package org.openintents.tags.content;

import java.util.List;

import org.openintents.R;
import org.openintents.provider.ContentIndex;
import org.openintents.provider.Tag;
import org.openintents.provider.Location.Locations;
import org.openintents.provider.Tag.Tags;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Displays a row of content with image and content description.
 * 
 */
public class ContentListRow extends RelativeLayout {

	private static final int CONTENT_ICON = 1;
	private static final int CONTENT_URI = 2;
	private static final int CONTENT_TYPE = 3;
	private static final int TAGS = 4;

	private ImageView mIcon;
	private TextView mName;
	private ImageView mType;
	private ContentIndex mContentIndex;
	private Drawable mIconDrawable;
	private String mNameString;
	private int mTypeString;
	boolean mHide;
	private String mUri;
	private int mTypeDrawable = 0;

	private Handler mHandler = new Handler();

	private Runnable mUpdateViews = new Runnable() {

		public void run() {
			if (mNameString == null) {
				mIcon.setImageDrawable(null);
				mName.setText(mUri);
				// First convert text to 'spannable'
				mName.setText(mName.getText(), TextView.BufferType.SPANNABLE);
				Spannable str = (Spannable) mName.getText();

				// Strikethrough
				str.setSpan(new StrikethroughSpan(), 0, str.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				deleteRow();
			} else {
				mName.setText(mNameString);
				if (mTypeDrawable > 0) {
					mType.setImageResource(mTypeDrawable);
					mType.setOnClickListener(new OnClickListener() {

						public void onClick(View view) {
							new AlertDialog.Builder(ContentListRow.this.getContext()).setTitle("info").setIcon(mTypeDrawable). 
							setMessage(getResources()
									.getString(mTypeString)).show();	

						}
					});
				}
				mIcon.setImageDrawable(mIconDrawable);
				mTags.setText(mTagsString);
			}
		}

	};
	private TextView mTags;
	private Tag mTagHelper;
	private String mTagsString;

	public ContentListRow(Context context) {
		super(context);

		mContentIndex = new ContentIndex(context.getContentResolver());
		mTagHelper = new Tag(context);

		mIcon = new ImageView(context);
		mIcon.setPadding(2, 2, 2, 2);
		mIcon.setId(CONTENT_ICON);

		mName = new TextView(context);
		mName.setGravity(RelativeLayout.CENTER_VERTICAL);
		mName.setId(CONTENT_URI);

		// TODO The following does not seem to work?
		mName.setTextAppearance(context, android.R.attr.textAppearanceLarge);

		// so we give some explicit values for now
		mName.setTextSize(24);
		mName.setTextColor(0xFFFFFFFF);

		mType = new ImageView(context);
		mType.setId(CONTENT_TYPE);

		mTags = new TextView(context);
		mTags.setId(TAGS);
		mTags.setTextSize(15);
		mTags.setTextColor(0xFFFFFFFF);

		RelativeLayout.LayoutParams icon = new RelativeLayout.LayoutParams(64,
				64);
		icon.addRule(ALIGN_PARENT_LEFT);
		addView(mIcon, icon);

		RelativeLayout.LayoutParams name = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 64);
		name.addRule(ALIGN_RIGHT, CONTENT_ICON);
		addView(mName, name);

		RelativeLayout.LayoutParams type = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 64);
		type.addRule(ALIGN_PARENT_RIGHT);
		addView(mType, type);

		RelativeLayout.LayoutParams tags = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		tags.addRule(ALIGN_PARENT_LEFT);
		tags.addRule(BELOW, CONTENT_ICON);
		addView(mTags, tags);

	}

	protected void deleteRow() {
		mHide = true;
		if (getContext() instanceof ContentBrowserView) {
			((ContentBrowserView) getContext()).delete(mUri);
		}
	}

	public void bindCursor(Cursor cursor) {
		mUri = cursor.getString(cursor.getColumnIndex(Tags.URI_2));
		updateContentFrom(mUri);
	}

	public void updateContentFrom(final String uri) {
		Thread t = new Thread() {

			@Override
			public void run() {
				updateContentFromInternal(uri);
				mHandler.post(mUpdateViews);
			}

		};
		t.start();
	}

	public void updateContentFromInternal(String uri) {

		Uri contentUri = null;
		String type = null;
		Intent intent = null;

		try {
			contentUri = Uri.parse(uri);
		} catch (NullPointerException e1) {
			Log.i("ContentListRowNP", e1.getMessage());
			setUnparsableUri();
		}

		if (contentUri != null) {
			intent = new Intent(Intent.ACTION_VIEW, contentUri);
			if (getContext().getPackageManager().resolveActivity(intent, 0) == null) {
				intent = null;
			} else {
				try {
					type = getContext().getContentResolver()
							.getType(contentUri);
				} catch (SecurityException e) {
					Log.i("ContentListRowSec", e.getMessage());
					e.printStackTrace();
					setSecurity();
				}
			}
		}

		Drawable icon = getIconForUri(contentUri, type, intent);
		mIconDrawable = icon;

		String text = getTextForUri(contentUri, type, intent, uri);
		if (text == null && contentUri != null) {
			mHide = true;
		}
		mNameString = text;

		if (contentUri != null && !mHide) {
			mTagsString = mTagHelper.findTags(uri, ", ");
		}

	}

	private String getTextForUri(Uri uri, String type, Intent intent,
			String uri2) {

		String result;
		if (uri == null) {
			if (uri2 == null) {
				result = getResources().getString(R.string.nothing_selected);
			} else {
				result = uri2;
			}
		} else if ("geo".equals(uri.getScheme())) {
			// deal with geo
			result = uri.getPath();
		} else if ("http".equals(uri.getScheme())) {
			// deal with http
			result = uri2;
		} else if ("https".equals(uri.getScheme())) {
			// deal with https
			result = uri2;
		} else if ("mailto".equals(uri.getScheme())) {
			// deal with mailto
			result = uri2;
		} else {
			if (uri.getScheme() != null) {
				Cursor cursor = mContentIndex.getContentBody(uri);

				if (cursor == null) {
					result = uri.toString();
				} else if (cursor.getCount() < 1) {
					// deleted content
					result = null;
				} else {
					cursor.moveToNext();
					if (uri.toString().startsWith(Locations.CONTENT_URI.toString())){
						result = cursor.getString(0) + "," + cursor.getString(1);
					} else {
						result = cursor.getString(0);
					}
				}
			} else {
				result = uri2;
			}
		}

		return result;
	}

	private Drawable getIconForUri(Uri uri, String type, Intent intent) {
		Drawable icon = null;

		PackageManager pm = getContext().getPackageManager();

		if (intent != null) {

			try {
				icon = pm.getActivityIcon(intent);
			} catch (NameNotFoundException e1) {
				Log.i("ContentListRowIcon", e1.getMessage());
				setUnknownName();
			}

			if (icon == null) {
				List<ResolveInfo> providerInfo = null;
				try {
					providerInfo = pm.queryIntentActivities(intent, 0);
				} catch (SecurityException e2) {
					Log.i("ContentListRowIcon", e2.getMessage());
					setSecurity();
				}

				if (providerInfo != null && providerInfo.size() > 0) {
					try {
						icon = pm
								.getApplicationIcon(providerInfo.get(0).activityInfo.applicationInfo.packageName);
					} catch (NameNotFoundException e) {
						Log.e("ContentListRowIcon", "bindCursor", e);
						setUnknownName();
					}
				}
			}
		}

		if (icon == null) {
			icon = pm.getDefaultActivityIcon();
		}

		return icon;
	}

	private void setSecurity() {
		mTypeString = R.string.tags_security;
		mTypeDrawable = R.drawable.lock001a;
	}

	private void setUnparsableUri() {
		mTypeString = R.string.tags_unparsable_uri;
		mTypeDrawable = R.drawable.question001a;

	}

	private void setUnknownName() {
		mTypeString = R.string.tags_unknown_content;
		mTypeDrawable = R.drawable.question001a;
	}

}
