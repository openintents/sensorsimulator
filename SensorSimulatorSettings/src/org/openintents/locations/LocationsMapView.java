package org.openintents.locations;

import org.openintents.R;
import org.openintents.lib.MultiWordAutoCompleteTextView;
import org.openintents.main.About;
import org.openintents.provider.Location;
import org.openintents.provider.Tag;
import org.openintents.provider.Location.Locations;
import org.openintents.provider.Tag.Contents;
import org.openintents.provider.Tag.Tags;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Projection;

public class LocationsMapView extends MapActivity {

	private static final int MENU_USE_CENTER = 1;
	private static final int MENU_RESTORE_VALUES = 2;
	private static final int MENU_ABOUT = 3;

	private GeoPoint point;
	private MapView view;
	private long pointId;
	private Tag mTag;
	private Location mLocations;
	private Cursor mIdTagCursor;
	private StringBuffer mOriginalTags;
	private MultiWordAutoCompleteTextView mEditTags;
	private GeoPoint orgPoint;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.locations_map_view);
		mTag = new Tag(LocationsMapView.this);
		mLocations = new Location(this.getContentResolver());

		// get point and pointId
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			point = new GeoPoint(bundle.getInt("latitude"), bundle
					.getInt("longitude"));
			pointId = bundle.getLong("_id");
		}

		if (pointId == 0L && getIntent() != null) {
			String id = getIntent().getData().getLastPathSegment();
			if (id != null && id.length() > 0) {
				try {
					pointId = Integer.parseInt(id);
					point = mLocations.getPoint(pointId);
				} catch (NumberFormatException e) {
					// ignore - no id found
				}
			}
		}
		// store orignal point.
		orgPoint = new GeoPoint(point.getLatitudeE6(), point.getLongitudeE6());

		// prepare controls
		view = (MapView) findViewById(R.id.mapview);
		MapController controller = view.getController();
		controller.setCenter(new GeoPoint(point.getLatitudeE6(), point
				.getLongitudeE6()));
		// controller.setZoom(9);
		controller.setZoom(15);

		mEditTags = (MultiWordAutoCompleteTextView) findViewById(R.id.tag);

		mIdTagCursor = mTag.findTags(ContentUris.withAppendedId(
				Locations.CONTENT_URI, pointId).toString());
		startManagingCursor(mIdTagCursor);
		StringBuffer idTags = new StringBuffer();
		while (mIdTagCursor.moveToNext()) {
			idTags.append(mIdTagCursor.getString(mIdTagCursor
					.getColumnIndexOrThrow(Tags.URI_1)));
			idTags.append(",");
		}
		// remove extract ","
		if (idTags.length() > 0) {
			idTags.deleteCharAt(idTags.length() - 1);
		}
		mOriginalTags = idTags;
		mEditTags.setText(idTags);

		Cursor allTagsCursor = mTag
				.findTagsForContentType(Locations.CONTENT_URI.toString());
		startManagingCursor(allTagsCursor);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.tag_row_simple, allTagsCursor,
				new String[] { Contents.URI }, new int[] { R.id.tag_tag });
		adapter.setStringConversionColumn(allTagsCursor
				.getColumnIndexOrThrow(Contents.URI));
		mEditTags.setAdapter(adapter);

		// old
		// view.getOverlayController().add(new LocationsMapOverlay(this), true);
		java.util.List<com.google.android.maps.Overlay> overlays = view
				.getOverlays();
		synchronized (overlays) {
			overlays.clear();
			overlays.add(new LocationsMapOverlay(this));
		}
	}

	public GeoPoint getPoint() {
		return point;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean superResult = super.onKeyDown(keyCode, event);

		int level;
		switch (keyCode) {
		case KeyEvent.KEYCODE_I:
			// Zoom In
			level = view.getZoomLevel();
			view.getController().setZoom(level + 1);
			return true;
		case KeyEvent.KEYCODE_O:
			// Zoom Out
			level = view.getZoomLevel();
			view.getController().setZoom(level - 1);
			return true;
		}
		return superResult;
	}

	@Override
	protected void onPause() {
		super.onPause();
		updateTags(true);
	}

	private void updateTags(boolean useCenterIfRequired) {

		GeoPoint p = LocationsMapView.this.view.getMapCenter();

		android.location.Location loc = new android.location.Location(
				(String) null);
		loc.setLatitude(p.getLatitudeE6() / 1E6);
		loc.setLongitude(p.getLongitudeE6() / 1E6);

		Uri contentUri;
		if (pointId != 0L) {
			contentUri = ContentUris.withAppendedId(Locations.CONTENT_URI,
					pointId);
		} else {
			if (useCenterIfRequired) {
				contentUri = mLocations.addLocation(loc);
			} else {
				contentUri = null;
			}
		}
		if (contentUri != null) {
			String content = contentUri.toString();

			String[] tags = mEditTags.getText().toString().split(
					mEditTags.getSeparator());

			for (int i = 0; i < tags.length; i++) {
				String s = tags[i].trim();
				if (!TextUtils.isEmpty(s)) {
					mTag.insertTag(s, content);
				}
			}

			// delete removed tags
			mIdTagCursor.requery();
			while (mIdTagCursor.moveToNext()) {
				String oldTag = mIdTagCursor.getString(mIdTagCursor
						.getColumnIndex(Tags.URI_1));
				boolean found = false;
				for (String newTag : tags) {
					if (oldTag.equals(newTag)) {
						found = true;
						break;
					}
				}
				if (!found) {
					String id = mIdTagCursor.getString(mIdTagCursor
							.getColumnIndex(Tags._ID));
					getContentResolver().delete(
							Uri.withAppendedPath(Tags.CONTENT_URI, id), null,
							null);
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		boolean superResult = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_USE_CENTER, 0, R.string.locations_use_center).setIcon(
				R.drawable.location_center001a);
		menu.add(0, MENU_RESTORE_VALUES, 0, R.string.locations_restore_values)
				.setIcon(R.drawable.restore001a);
		menu.add(0, MENU_ABOUT, 0, R.string.about)
				.setIcon(R.drawable.about001a);
		return superResult;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_USE_CENTER:
			GeoPoint p = view.getMapCenter();
			updateUsingPoint(p);

			break;
		case MENU_RESTORE_VALUES:
			mEditTags.setText(mOriginalTags);
			view.getController().setCenter(
					new GeoPoint(orgPoint.getLatitudeE6(), orgPoint
							.getLongitudeE6()));
			updateUsingPoint(orgPoint);
			break;
		case MENU_ABOUT:
			showAboutDialog();

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAboutDialog() {
		Intent intent = new Intent(this, About.class);
		intent.putExtra(About.TITLE_ID, R.string.locations_about);
		intent.putExtra(About.TEXT_ID, R.string.locations_about_text);
		startActivity(intent);
	};

	private void updateUsingPoint(GeoPoint p) {
		android.location.Location loc = new android.location.Location(
				(String) null);
		loc.setLatitude(p.getLatitudeE6() / 1E6);
		loc.setLongitude(p.getLongitudeE6() / 1E6);

		if (pointId != 0l) {
			ContentValues values = new ContentValues();
			values.put(Locations.LATITUDE, loc.getLatitude());
			values.put(Locations.LONGITUDE, loc.getLongitude());
			getContentResolver().update(
					ContentUris.withAppendedId(Locations.CONTENT_URI, pointId),
					values, null, null);
		} else {
			Uri uri = mLocations.addLocation(loc);
			pointId = Integer.parseInt(uri.getLastPathSegment());
		}
		point = p;

		// old
		// view.getOverlayController().add(new LocationsMapOverlay(this), true);
		java.util.List<com.google.android.maps.Overlay> overlays = view
				.getOverlays();
		synchronized (overlays) {
			overlays.clear();
			overlays.add(new LocationsMapOverlay(this));
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public Projection getProjection() {
		return view.getProjection();
	}
}
