/* 
 * Copyright (C) 2008 OpenIntents.org
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

package org.openintents.provider;

import org.openintents.R;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * Definition for content provider related to hardware.
 * Stores hardware abstraction and hardware simulator related data.
 *
 */
public abstract class Hardware {
	
	/**
	 * TAG for logging.
	 */
	private static final String TAG = "Hardware";
	
	/**
	 * Hardware preferences.
	 * Simple table to store name-value pairs.
	 */
	public static final class Preferences implements BaseColumns {
		/**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI
                = Uri.parse(
                		"content://org.openintents.hardware/preferences");

        /**
         * The default sort order for this table.
         */
        public static final String DEFAULT_SORT_ORDER = "_id ASC";
        
       
        /**
         * The name of the item.
         * <P>Type: TEXT</P>
         */
        public static final String NAME = "name";

        /**
         * An image of the item (uri).
         * <P>Type: TEXT</P>
         */
        public static final String VALUE = "value";

    }
	
	////////////////////////////////////////////////////////
	// Convenience functions:
	
	/**
	 * The content resolver has to be set before accessing 
	 * any of these functions.
	 */
	public static ContentResolver mContentResolver;
	
	public static final String[] mProjectionPreferencesFilter = 
		new String[] { Preferences._ID, Preferences.NAME, Preferences.VALUE};
	public static final int mProjectionPreferencesID = 0;
	public static final int mProjectionPreferencesNAME = 1;
	public static final int mProjectionPreferencesVALUE = 2;
	
	// Some default preference values
	public static final String IPADDRESS = "IP address";
	public static final String SOCKET = "Socket";
	public static final String DEFAULT_SOCKET = "8010";
	
	/**
	 * Obtains the 'value' for preferenceID, 
	 * or returns "" if not existent.
	 * 
	 * @param name The name of the preference.
	 * @return The value for preference 'name'.
	 */
	public static String getPreference(final String name) {
		try {
			Log.i(TAG, "getPreference()");
			Cursor c = mContentResolver.query(Preferences.CONTENT_URI, 
					mProjectionPreferencesFilter, 
					Preferences.NAME + "= '" + name + "'",
					null,
					Preferences.DEFAULT_SORT_ORDER);
			if (c.getCount() >= 1) {
				c.moveToFirst();
				return c.getString(mProjectionPreferencesVALUE);
			} else if (c.getCount() == 0) {
				// This value does not exist yet!
				return "";
			} else {
				Log.e(TAG, "table 'preferences' corrupt. Multiple NAME!");
				return "";
			}
		} catch (Exception e) {
			Log.e(TAG, "insert into table 'contains' failed", e);
			return "Preferences table corrupt!";
		}
	}
	
	/**
	 * Updates the 'value' for the preferenceID.
	 * 
	 * @param name The name of the preference.
	 * @param value The value to set.
	 */
	public static void setPreference(final String name, final String value) {
		/*
		// This value does not exist yet. Let's insert it:
		ContentValues values2 = new ContentValues(2);
		values2.put(Preferences.NAME, name);
		values2.put(Preferences.VALUE, value);
		mContentResolver.insert(Preferences.CONTENT_URI, values2);
		*/
		
		Log.i(TAG, "setPreference");
		try {
			Log.i(TAG, "get Cursor.");
			if (mContentResolver == null)
				Log.i(TAG, "Panic!.");
			Cursor c = mContentResolver.query(Preferences.CONTENT_URI, 
					mProjectionPreferencesFilter, 
					Preferences.NAME + "= '" + name + "'",
					null,
					Preferences.DEFAULT_SORT_ORDER);
			Log.i(TAG, "got Cursor.");
			//Log.i(TAG, "Cursor: " + c.toString());
			
			if (c == null) {
				Log.e(TAG, "missing hardware provider");
				return;
			}
			
			
			if (c == null || c.getCount() < 1) {
				Log.i(TAG, "Insert");
				
				// This value does not exist yet. Let's insert it:
				ContentValues values = new ContentValues(2);
				values.put(Preferences.NAME, name);
				values.put(Preferences.VALUE, value);
				mContentResolver.insert(Preferences.CONTENT_URI, values);
			} else if (c.getCount() >= 1) {
				Log.i(TAG, "Update");
				
				// This is the key, so we can update it:
				c.moveToFirst();
				String id = c.getString(mProjectionPreferencesID);
				ContentValues cv = new ContentValues();
				cv.put(Preferences.VALUE, value);
				mContentResolver.update(Uri.withAppendedPath(Preferences.CONTENT_URI, id), cv, null, null );
				
				// c.requery();
				c.getString(mProjectionPreferencesVALUE);
			} else {
				Log.e(TAG, "table 'preferences' corrupt. Multiple NAME!");
			}
		} catch (Exception e) {
			Log.i(TAG, "setPreference() failed", e);
			
		}
	}
}
