package org.openintents.media;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

public class MediaHelper {

	/** Specifies the relevant columns. */
	static final String[] mProjection = new String[] {
			android.provider.BaseColumns._ID,
			android.provider.MediaStore.MediaColumns.TITLE,
			android.provider.MediaStore.MediaColumns.DATA };

	public static String getFilenameForUri(Activity activity, String uriString) {
		Uri mURI = Uri.parse(uriString);

		Cursor mCursor = activity.managedQuery(mURI, mProjection, null, null, null);
		String mFilename = null;
		if (mCursor != null && mCursor.moveToFirst()) {
			int indexDATA = mCursor
					.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.DATA);
			mFilename = mCursor.getString(indexDATA);

			int indexTitle = mCursor
					.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.TITLE);
			String mTitle = mCursor.getString(indexTitle);
		}
		return mFilename;
	}
}
