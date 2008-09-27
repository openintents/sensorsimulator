package org.openintents.tags.content;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Displays a row in the package list.
 * 
 */
public class PackageListRow extends RelativeLayout {

	private static final int PACKAGE_ICON = 1;

	private static final int PACKAGE_NAME = 2;

	private static final int PACKAGE_PACKAGE_NAME = 3;

	private ImageView mIcon;

	private TextView mName;

	private TextView mPackageName;

	/** Small text to display info if no Pick Activity is available. */
	private TextView mAlertInfo;

	protected Drawable mIconDrawable;

	protected String mAlertinfoString;

	protected Handler mHandler = new Handler();

	protected Runnable updateViews = new Runnable() {
		public void run() {
			mIcon.setImageDrawable(mIconDrawable);
			mAlertInfo.setText(mAlertinfoString);
		}
	};

	public PackageListRow(Context context) {
		super(context);

		setLayoutParams(new AbsListView.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		mIcon = new ImageView(context);
		mIcon.setPadding(2, 2, 2, 2);
		mIcon.setId(PACKAGE_ICON);

		mName = new TextView(context);
		mName.setGravity(RelativeLayout.CENTER_VERTICAL);
		mName.setId(PACKAGE_NAME);

		// set some explicit values for now
		mName.setTextSize(24);
		mName.setTextColor(0xFFFFFFFF);
		mName.setPadding(5, 5, 0, 0);

		mPackageName = new TextView(context);
		mPackageName.setGravity(RelativeLayout.CENTER_VERTICAL);
		mPackageName.setId(PACKAGE_PACKAGE_NAME);

		// set some explicit values for now
		mPackageName.setTextSize(12);
		mPackageName.setTextColor(0xFFAAAAAA);
		mPackageName.setPadding(5, 0, 0, 0);

		mAlertInfo = new TextView(context);
		mAlertInfo.setGravity(RelativeLayout.CENTER_VERTICAL);		

		// set some explicit values for now
		mAlertInfo.setTextSize(12);
		mAlertInfo.setTextColor(0xFFFF0000);
		mAlertInfo.setPadding(5, 0, 0, 0);

		RelativeLayout.LayoutParams icon = new RelativeLayout.LayoutParams(64,
				64);
		icon.addRule(ALIGN_PARENT_LEFT);
		icon.addRule(ALIGN_PARENT_TOP);
		// for now remove icon until the relative rules are setup correctly.
		//addView(mIcon, icon);

		RelativeLayout.LayoutParams name = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 40);
		name.addRule(ALIGN_RIGHT, PACKAGE_ICON);
		name.addRule(ALIGN_PARENT_TOP);
		addView(mName, name);

		RelativeLayout.LayoutParams packagename = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 24);
		packagename.addRule(BELOW, PACKAGE_NAME);
		packagename.addRule(ALIGN_RIGHT, PACKAGE_ICON);
		addView(mPackageName, packagename);

		RelativeLayout.LayoutParams alertinfo = new RelativeLayout.LayoutParams(
				64, 64);
		alertinfo.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		alertinfo.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(mAlertInfo, alertinfo);
	}

	public void bindCursor(final Cursor cursor) {
		final String packageName = cursor.getString(cursor
				.getColumnIndexOrThrow("package"));
		String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
		mAlertinfoString = "";
		mIconDrawable = null;

		new Thread() {
			@Override
			public void run() {

				PackageManager pm = getContext().getPackageManager();
				try {
					ProviderInfo providerInfo = pm.resolveContentProvider(
							packageName, 0);
					if (providerInfo != null) {
						// packageName is a sub path of application (like locations in org.openintents)
						mIconDrawable = providerInfo.loadIcon(pm);
					} else {
						// packageName = application name
						mIconDrawable = pm.getApplicationIcon(packageName);
					}
				} catch (Exception e) {
					Log.e("PackageListRow", "bindView", e);
					mIconDrawable = pm.getDefaultActivityIcon();
				}

				// data is the picked content directory
				String data = cursor.getString(2);
				Uri uri = Uri.parse(data);
				Intent intent = new Intent(Intent.ACTION_PICK, uri);
				mAlertinfoString = "";
				if (pm.resolveActivity(intent, 0) == null) {
					intent.setAction(Intent.ACTION_GET_CONTENT);
					if (pm.resolveActivity(intent, 0) == null) {
						mAlertinfoString = "no pick/get activity available";
					}
				}
				;
				mHandler.post(updateViews);
			}
		}.start();

		mName.setText(name);
		mPackageName.setText(packageName);
		mAlertInfo.setText(mAlertinfoString);
	}

}
