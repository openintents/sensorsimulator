package org.openintents.tags.content;

import org.openintents.R;
import org.openintents.provider.ContentIndex;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PackageList extends ListActivity {

	private static final String TAG = "PackageList";

	public static final int PACKAGE_ADD_ID = Menu.FIRST;

	public static final int PACKAGE_DEL_ID = Menu.FIRST + 1;

	private Cursor mCursor;

	private TextView mLabel;

	private ContentIndex mContentIndex;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.package_list);

		mLabel = (TextView) findViewById(R.id.label);

		mContentIndex = new ContentIndex(getContentResolver());

		try {
			mCursor = mContentIndex.getPackageNames();
			ListAdapter adapter = new PackageListAdapter(mCursor, this);
			setListAdapter(adapter);
		} catch (Exception e) {
			Log.e(TAG, "onCreate", e);
			finish();
		}

		refreshPackages();
	}

	private void refreshPackages() {
		int n = mCursor.getCount();
		String label;
		if (getCallingActivity() != null) {
			label = getString(R.string.label_packages_pick, String.valueOf(n));
		} else {
			label = getString(R.string.label_packages, String.valueOf(n));
		}
		
		mLabel.setText(label);
	}

	@Override
	protected void onRestart() {
		mCursor.requery();
		refreshPackages();
		super.onRestart();
	}

	@Override
	protected void onResume() {
		mCursor.requery();
		refreshPackages();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, PACKAGE_ADD_ID, 0, R.string.menu_package_add)
			.setIcon(R.drawable.new_doc).setShortcut('3', 'i');
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		final boolean haveItems = mCursor.getCount() > 0;

		menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);

		if (haveItems) {
			menu.add(Menu.CATEGORY_ALTERNATIVE, PACKAGE_DEL_ID,
					R.string.menu_package_del, R.drawable.trash).setShortcut('5', 'u');
			//TODO new sdk
			//menu.setDefaultItem(PACKAGE_ADD_ID);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case PACKAGE_DEL_ID:
			deletePackage();
			return true;
		case PACKAGE_ADD_ID:
			addPackage();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		if (getCallingActivity() != null) {			
			setResult(RESULT_OK, new Intent(((Cursor) l.getItemAtPosition(position)).getString(2)));
			finish();
		}
	}

	private void deletePackage() {
		OnClickListener c1 = new OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				if (whichButton == AlertDialog.BUTTON2) {
					mCursor.moveToPosition(PackageList.this.getSelectedItemPosition());
					int packageIndex = mCursor.getColumnIndexOrThrow("package");
					String packageName = mCursor.getString(packageIndex);
					Log.d(TAG, "Delete package " + packageName);
					mContentIndex.deletePackage(packageName);
					mCursor.requery();
					refreshPackages();
				} else {
					Log.d(TAG, "Cancel");
				}
			}
		};
		OnCancelListener c2 = new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				Log.d(TAG, "onCancel");
			}
		};

		String ok = getString(R.string.dialog_ok);
		String cancel = getString(R.string.dialog_cancel);
		String title = getString(R.string.dialog_title_package_del);
		String msg = getString(R.string.dialog_message_package_del);
		new AlertDialog.Builder(this).setTitle(title). 
		setMessage(msg).show();				
		// TODO new sdk (is the cancle button shown?
	}

	private void addPackage() {
		startActivity(new Intent(this, PackageAdd.class));
	}
}
