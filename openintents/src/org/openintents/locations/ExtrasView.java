package org.openintents.locations;

import org.openintents.provider.Location;
import org.openintents.provider.Location.Extras;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * View to manage extras for a location.
 * 
 * @author friedger
 * 
 */
public class ExtrasView extends ListActivity {

	protected static final int MENU_REMOVE = 1;
	private static final int MENU_ADD = 2;

	private Location mLocation;
	private Cursor mExtrasCursor;
	private long mLocationId;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mLocationId = getIntent().getLongExtra(Extras.LOCATION_ID, 0);
		if (mLocationId == 0L) {
			// early exit
			finish();
		}

		mLocation = new Location(getContentResolver());
		mExtrasCursor = mLocation.queryExtras(mLocationId);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				org.openintents.R.layout.locations_extras, mExtrasCursor,
				new String[] { Extras._ID, Extras.KEY, Extras.VALUE },
				new int[] { org.openintents.R.id.text,
						org.openintents.R.id.edittext,
						org.openintents.R.id.edittext2 });
		adapter.setViewBinder(new ViewBinder() {

			public boolean setViewValue(View view, Cursor cursor, int i) {
				//TODO new SDK setText with cursor
				//((TextView) view).setText(cursor, i);
				return true;
			}

		});
		setListAdapter(adapter);
		getListView().setOnCreateContextMenuListener(
				new View.OnCreateContextMenuListener() {

					public void onCreateContextMenu(ContextMenu contextmenu,
							View view, ContextMenu.ContextMenuInfo obj) {
						contextmenu
								.add(
										0,
										MENU_REMOVE,
										0,
										org.openintents.R.string.locations_delete_extra);

					}

				});

	}

	@Override
	protected void onPause() {
		super.onPause();
		updateExtra(false);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case MENU_REMOVE:
			long extraId = ((Cursor) getListAdapter()
					.getItem(menuInfo.position)).getLong(0);
			mLocation.deleteExtra(extraId);
			mExtrasCursor.requery();
			break;
		}

		return true;
	}

	public void updateExtra(boolean requery) {
		for (int i = 0; i < getListView().getChildCount(); i++) {

			mExtrasCursor.moveToPosition(i);
			LinearLayout row = (LinearLayout) getListView().getChildAt(i);
			ContentValues values = new ContentValues();
			values.put(Extras.KEY, ((EditText) row.getChildAt(1))
					.getText().toString());
			values.put(Extras.VALUE, ((EditText) row.getChildAt(2))
					.getText().toString());
			mLocation.updateExtras(mLocationId, mExtrasCursor.getLong(mExtrasCursor.getColumnIndexOrThrow(Extras._ID)), values);
		}
		
		if (requery){
			mExtrasCursor.requery();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ADD, 0, org.openintents.R.string.locations_add_extra);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADD:
			updateExtra(true);
			mLocation.addExtra(mLocationId);
			mExtrasCursor.requery();
			break;

		default:
			break;
		}
		return true;
	}

}
