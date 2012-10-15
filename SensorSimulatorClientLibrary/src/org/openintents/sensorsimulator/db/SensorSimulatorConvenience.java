/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 * 
 * Copyright (C) 2008-2010 OpenIntents.org
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

package org.openintents.sensorsimulator.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Class that defines our Convenience and it's methods. It is used to insert and
 * get our IP address and socket from our Content Provider.
 * 
 * @author Peli
 */
public class SensorSimulatorConvenience {

	/**
	 * TAG for logging.
	 */
	private static final String TAG = "SensorSimulatorConvenience";

	private Context mContext;
	private ContentResolver mContentResolver;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The activity context.
	 */
	public SensorSimulatorConvenience(Context context) {
		mContext = context;
		mContentResolver = mContext.getContentResolver();
	}

	/**
	 * Updates the 'value' for the preferenceID.
	 * 
	 * @param name
	 *            The name of the preference.
	 * @param value
	 *            The value to set.
	 */
	public void setPreference(final String name, final String value) {

		try {

			if (mContentResolver == null)
				Log.i(TAG, "Panic!.");
			Cursor c = mContentResolver.query(
					SensorSimulator.Settings.CONTENT_URI,
					SensorSimulator.SENSORSIMULATOR_PROJECTION,
					SensorSimulator.Settings.KEY + "= '" + name + "'", null,
					SensorSimulator.Settings.DEFAULT_SORT_ORDER);

			if (c == null) {
				Log.e(TAG, "missing hardware provider");
				return;
			}

			if (c == null || c.getCount() < 1) {

				ContentValues values = new ContentValues(2);
				values.put(SensorSimulator.Settings.KEY, name);
				values.put(SensorSimulator.Settings.VALUE, value);
				mContentResolver.insert(SensorSimulator.Settings.CONTENT_URI,
						values);
			} else if (c.getCount() >= 1) {

				// This is the key, so we can update it:
				c.moveToFirst();
				String id = c.getString(c
						.getColumnIndexOrThrow(SensorSimulator.Settings._ID));
				ContentValues cv = new ContentValues();
				cv.put(SensorSimulator.Settings.VALUE, value);
				mContentResolver.update(Uri.withAppendedPath(
						SensorSimulator.Settings.CONTENT_URI, id), cv, null,
						null);

				// c.requery();
				c.getString(c
						.getColumnIndexOrThrow(SensorSimulator.Settings.VALUE));
			} else {
				Log.e(TAG, "table 'settings' corrupt. Multiple KEY!");
			}
		} catch (Exception e) {
			Log.e(TAG, "setPreference() failed", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Obtains the 'value' for preferenceID, or returns "" if not existent.
	 * 
	 * @param name
	 *            The name of the preference.
	 * @return The value for preference 'name'.
	 */
	public String getPreference(final String name) {
		try {
			// Log.i(TAG, "getPreference()");
			Cursor c = mContentResolver.query(
					SensorSimulator.Settings.CONTENT_URI,
					SensorSimulator.SENSORSIMULATOR_PROJECTION,
					SensorSimulator.Settings.KEY + "= '" + name + "'", null,
					SensorSimulator.Settings.DEFAULT_SORT_ORDER);
			if (c.getCount() >= 1) {
				c.moveToFirst();
				return c.getString(c
						.getColumnIndexOrThrow(SensorSimulator.Settings.VALUE));
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

}
