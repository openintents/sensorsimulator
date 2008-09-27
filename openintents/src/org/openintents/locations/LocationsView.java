/* 
 * Copyright (C) 2007-2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.locations;

import java.util.List;

import org.openintents.OpenIntents;
import org.openintents.R;
import org.openintents.provider.Alert;
import org.openintents.provider.Intents;
import org.openintents.provider.Location.Extras;
import org.openintents.provider.Location.Locations;
import org.openintents.provider.Tag.Tags;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * Simple activity to show a list of locations and add the current location to
 * the list.
 * 
 * 
 */
public class LocationsView extends Activity {

	private static final int MENU_ADD_CURRENT_LOCATION = 1;
	private static final int MENU_VIEW = 2;
	private static final int MENU_TAG = 3;
	private static final int MENU_DELETE = 4;
	private static final int MENU_ADD_ALERT = 5;
	protected static final int MENU_MANAGE_EXTRAS = 6;

	private static final int TAG_ACTIVITY = 1;
	private static final int REQUEST_PICK_INTENT = 2;

	private org.openintents.provider.Location mLocation;
	private Cursor c;

	/** tag for logging */
	private static final String TAG = "locationsView";

	private static final String MLAST = "mlast";

	private ListView mList;
	private int mlastPosition;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.locations);

		mLocation = new org.openintents.provider.Location(this
				.getContentResolver());
		mList = (ListView) findViewById(R.id.locations);

		mList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				if (getCallingActivity() != null
						&& Intent.ACTION_PICK.equals(getIntent().getAction())) {

					Cursor cursor = (Cursor) mList.getAdapter().getItem(
							position);
					String geo = getGeoString(cursor);
					Intent resultIntent = new Intent();
					resultIntent.setData(ContentUris.withAppendedId(
							Locations.CONTENT_URI, id));
					resultIntent.putExtra(Locations.EXTRA_GEO, geo);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				} else {
					viewLocationWithMapView(position);
				}
			}

		});

		mList
				.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

					public void onCreateContextMenu(ContextMenu contextmenu,
							View view, ContextMenu.ContextMenuInfo obj) {
						contextmenu
								.add(0, MENU_VIEW, 0, R.string.view_location)
								.setIcon(R.drawable.locations_view001a);
						contextmenu
								.add(0, MENU_TAG, 0, R.string.tag_location)
								.setIcon(
										R.drawable.locations_favorite_application001a);
						contextmenu.add(0, MENU_DELETE, 0,
								R.string.delete_location).setIcon(
								R.drawable.locations_delete001a);
						contextmenu.add(0, MENU_ADD_ALERT, 0,
								R.string.add_alert).setIcon(
								R.drawable.locations_add_alert001a);
						contextmenu.add(0, MENU_MANAGE_EXTRAS, 0,
								R.string.locations_manage_extras).setIcon(
								R.drawable.locations_application001a);

					}

				});

		ListAdapter adapter = new ArrayAdapter<String>(this,
				R.layout.location_row, new String[0]);
		mList.setAdapter(adapter);
		// init Alertprovider convenicen functions (zero)
		Alert.init(this);

		fillData();
	}

	private String getGeoString(Cursor cursor) {
		String latitude = String.valueOf(cursor.getDouble(cursor
				.getColumnIndexOrThrow(Locations.LATITUDE)));
		String longitude = String.valueOf(cursor.getString(cursor
				.getColumnIndexOrThrow(Locations.LONGITUDE)));
		return "geo:" + latitude + "," + longitude;
	}

	private void fillData() {

		if (c == null) {
			c = getContentResolver().query(
					Locations.CONTENT_URI,
					new String[] { Locations._ID, Locations.LATITUDE,
							Locations.LONGITUDE }, null, null,
					Locations.DEFAULT_SORT_ORDER);

		} else {
			c.requery();
		}

		// Get a cursor for all locations
		startManagingCursor(c);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		// Use a template that displays a text view
				R.layout.location_row,
				// Give the cursor to the list adapter
				c,
				// Map the LATITUDE and LONGITUDE columns in the
				// database to...
				new String[] { Locations.LATITUDE, Locations.LONGITUDE,
						Locations._ID, Locations._ID },
				// The view defined in the XML template
				new int[] { R.id.latitude, R.id.longitude, R.id.tags,
						R.id.distance });
		Location curLocation = getCurrentLocation();
		ViewBinder viewBinder = new TagsViewBinder(this, curLocation);
		adapter.setViewBinder(viewBinder);
		mList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu
				.add(0, MENU_ADD_CURRENT_LOCATION, 0,
						R.string.add_current_location).setIcon(
						R.drawable.locations_add001a);
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		super.onContextItemSelected(item);

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case MENU_VIEW:
			Cursor cursor = (Cursor) mList.getAdapter().getItem(
					menuInfo.position);
			if (cursor != null) {
				viewLocation(cursor);
			}
			break;
		case MENU_TAG:
			long id = menuInfo.id;
			if (id >= 0) {
				tagLocation(id);
			}
			break;
		case MENU_DELETE:
			id = menuInfo.id;
			if (id >= 0) {
				deleteLocation(id);
			}
			break;
		case MENU_ADD_ALERT:
			id = menuInfo.id;
			if (id >= 0) {
				mlastPosition = menuInfo.position;
				Intent intent = new Intent(Intent.ACTION_PICK,
						Intents.CONTENT_URI);
				intent.putExtra(Intents.EXTRA_ACTION_LIST, Intent.ACTION_VIEW);
				startActivityForResult(intent, REQUEST_PICK_INTENT);
			}
			break;
		case MENU_MANAGE_EXTRAS:
			Intent intent = new Intent(this, ExtrasView.class);
			long locationId = ((Cursor) mList.getAdapter().getItem(
					menuInfo.position)).getLong(0);
			if (locationId != 0L) {
				intent.putExtra(Extras.LOCATION_ID, locationId);
				startActivity(intent);
			}
			break;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_ADD_CURRENT_LOCATION:
			addCurrentLocation();
			break;
		}
		return true;
	}

	private void addAlert(String locationUri, String data, String actionName,
			String type, String uri) {

		ContentValues values = new ContentValues();
		values.put(Alert.Location.ACTIVE, Boolean.TRUE);
		values.put(Alert.Location.ACTIVATE_ON_BOOT, Boolean.TRUE);
		values.put(Alert.Location.DISTANCE, 100L);
		values.put(Alert.Location.POSITION, locationUri);
		values.put(Alert.Location.INTENT, actionName);
		values.put(Alert.Location.INTENT_URI, uri);
		// TODO convert type to uri (?) or add INTENT_MIME_TYPE column
		// getContentResolver().insert(Alert.Location.CONTENT_URI, values);
		// using alert.insert will register alerts automatically.
		Uri result = Alert.insert(Alert.Location.CONTENT_URI, values);
		int textId;
		if (uri != null) {
			textId = R.string.alert_added;
		} else {
			textId = R.string.alert_not_added;
		}
		Toast.makeText(this, textId, Toast.LENGTH_SHORT).show();

	}

	private void deleteLocation(long id) {
		mLocation.deleteLocation(id);

	}

	private void addCurrentLocation() {
		Location location = getCurrentLocation();

		if (location == null) {
			new AlertDialog.Builder(this).setTitle("info").setMessage(
					"curr. location could not be determined").show();
		} else {
			mLocation.addLocation(location);
			fillData();
		}

	}

	private Location getCurrentLocation() {

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null) {
			List<String> providers = locationManager.getProviders(true);
			if (providers != null && providers.size() > 0) {
				LocationProvider locationProvider = locationManager
						.getProvider(providers.get(0));
				Log.v(TAG, "using location provider '"
						+ locationProvider.getName() + "'");
				Location location = locationManager
						.getLastKnownLocation(locationProvider.getName());
				return location;
			} else {
				Log.v(TAG, "no location provider found." + providers
						+ providers.size());
				return null;

			}
		} else {

			Log.v(TAG, "no location provider found." + locationManager);
			return null;
		}

	}

	private void tagLocation(long id) {
		Uri uriToTag = ContentUris.withAppendedId(Locations.CONTENT_URI, id);

		Intent intent = new Intent(OpenIntents.TAG_ACTION, Tags.CONTENT_URI);
		intent.putExtra(Tags.QUERY_URI, uriToTag.toString());

		try {
			startActivityForResult(intent, TAG_ACTIVITY);
		} catch (Exception e) {
			e.printStackTrace();
			new AlertDialog.Builder(this).setTitle("info").setMessage(
					"tag action failed: " + e.toString()).show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent resultIntent) {
		super.onActivityResult(requestCode, resultCode, resultIntent);

		if (requestCode == TAG_ACTIVITY && resultCode == Activity.RESULT_OK) {
			fillData();
		} else if (requestCode == REQUEST_PICK_INTENT
				&& resultCode == Activity.RESULT_OK) {

			Cursor cursor = (Cursor) mList.getAdapter().getItem(mlastPosition);
			if (cursor == null) {
				c.requery();
				cursor = (Cursor) mList.getAdapter().getItem(mlastPosition);
			}

			String locationUri = getGeoString(cursor);
			addAlert(locationUri, resultIntent.getAction(), resultIntent
					.getStringExtra(Intents.EXTRA_ACTION), resultIntent
					.getStringExtra(Intents.EXTRA_TYPE), resultIntent
					.getStringExtra(Intents.EXTRA_URI));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(MLAST, mList.getSelectedItemPosition());
		stopManagingCursor(c);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startManagingCursor(c);
	}

	private void viewLocation(Cursor cursor) {
		String geoString = getGeoString(cursor);
		Uri uri;
		// try {
		uri = Uri.parse(geoString);
		// } catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// uri = null;
		// }

		if (uri != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivityForResult(intent, 0);
		}

	}

	private void viewLocationWithMapView(int position) {
		Double longitude = 0.0, latitude = 0.0;

		c.moveToPosition(position);
		latitude = c.getDouble(c.getColumnIndex(Locations.LATITUDE)) * 1E6;
		longitude = c.getDouble(c.getColumnIndex(Locations.LONGITUDE)) * 1E6;

		Bundle bundle = new Bundle();
		bundle.putInt("latitude", latitude.intValue());
		bundle.putInt("longitude", longitude.intValue());
		bundle.putLong("_id", c.getLong(c.getColumnIndex(Locations._ID)));

		Intent intent = new Intent();
		intent.setClass(this, LocationsMapView.class);
		intent.putExtras(bundle);

		startActivityForResult(intent, 0);
	}

}