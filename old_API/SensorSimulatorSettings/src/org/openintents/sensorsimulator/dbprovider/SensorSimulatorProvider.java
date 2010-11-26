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

package org.openintents.sensorsimulator.dbprovider;

import java.util.HashMap;

import org.openintents.sensorsimulator.R;
import org.openintents.sensorsimulator.db.SensorSimulator;
import org.openintents.sensorsimulator.db.SensorSimulator.Settings;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to a preferences related to hardware or 
 * hardware simulation. 
 * 
 */
public class SensorSimulatorProvider extends ContentProvider {

	private SQLiteOpenHelper mOpenHelper;

	/**
	 * TAG for logging.
	 */
	private static final String TAG = "SensorSimulatorProvider";
	private static final String DATABASE_NAME = "sensorsimulator.db";
	private static final int DATABASE_VERSION = 1;

	/** 
	 * Name of the table
	 */
	private static final String DATABASE_TABLE_SETTINGS = "settings";
	
	private static HashMap<String, String> PREFERENCES_PROJECTION_MAP;
	
	// Basic tables
	private static final int SETTINGS = 1;
	private static final int SETTING_ID = 2;
	
	private static final UriMatcher URL_MATCHER;

	/**
	 * HardwareProvider maintains the following tables:
	 *  * preferences: preferences related to hardware abstraction
	 *                 or hardware simulation.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		/**
		 * Creates tables "settings".
		 */
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + SensorSimulatorProvider.DATABASE_TABLE_SETTINGS + " ("
					+ "_id INTEGER PRIMARY KEY," // Database Version 1
					+ SensorSimulator.Settings.KEY + " VARCHAR," // V1
					+ SensorSimulator.Settings.VALUE + " VARCHAR" // V1
					+ ");");
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SensorSimulatorProvider.DATABASE_TABLE_SETTINGS);
			onCreate(db);
		}
	}

	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
        return true;
	}

	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs,String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		Log.i(TAG, "Query for URL: " + url);
		
		String defaultOrderBy = null;
		switch (URL_MATCHER.match(url)) {
		case SETTINGS:
			qb.setTables(SensorSimulatorProvider.DATABASE_TABLE_SETTINGS);
			qb.setProjectionMap(PREFERENCES_PROJECTION_MAP);
			defaultOrderBy = Settings.DEFAULT_SORT_ORDER;
			break;

		case SETTING_ID:
			qb.setTables(SensorSimulatorProvider.DATABASE_TABLE_SETTINGS);
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		// If no sort order is specified use the default

		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = defaultOrderBy;
		} else {
			orderBy = sort;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs,null,null,orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	public Uri insert(Uri url, ContentValues initialValues) {
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		
		// insert is supported for items or lists
		switch (URL_MATCHER.match(url)) {
		case SETTINGS:
			return insertPreferences(url, values);
		
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}
	
	public Uri insertPreferences(Uri url, ContentValues values) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowID;
		
		Resources r = Resources.getSystem();
		

		// Make sure that the fields are all set
		if (!values.containsKey(Settings.KEY)) {
			values.put(Settings.KEY, r.getString(R.string.new_item));
		}
		
		if (!values.containsKey(Settings.VALUE)) {
			values.put(Settings.VALUE, "");
		}
		
		
		// TODO: Here we should check, whether item exists already. 
		// (see TagsProvider)
		// insert the item. 
		rowID = db.insert(SensorSimulatorProvider.DATABASE_TABLE_SETTINGS, SensorSimulator.Settings.KEY, values);
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(Settings.CONTENT_URI,rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		
		// If everything works, we should not reach the following line:
		throw new SQLException("Failed to insert row into " + url);
	}

	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
		//long rowId;
		switch (URL_MATCHER.match(url)) {
		case SETTINGS:
			count = db.delete(SensorSimulatorProvider.DATABASE_TABLE_SETTINGS, where, whereArgs);
			break;

		case SETTING_ID:
			String segment = url.getPathSegments().get(1); // contains rowId
			//rowId = Long.parseLong(segment);
			String whereString;
			if (!TextUtils.isEmpty(where)) {
				whereString = " AND (" + where + ')';
			} else {
				whereString = "";
			}

			count = db
					.delete(SensorSimulatorProvider.DATABASE_TABLE_SETTINGS, "_id=" + segment + whereString, whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		Log.i(TAG, "update called for: " + url);
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
		//long rowId;
		switch (URL_MATCHER.match(url)) {
		case SETTINGS:
			count = db.update(SensorSimulatorProvider.DATABASE_TABLE_SETTINGS, values, where, whereArgs);
			break;

		case SETTING_ID:
			String segment = url.getPathSegments().get(1); // contains rowId
			//rowId = Long.parseLong(segment);
			String whereString;
			if (!TextUtils.isEmpty(where)) {
				whereString = " AND (" + where + ')';
			} else {
				whereString = "";
			}

			count = db
					.update(SensorSimulatorProvider.DATABASE_TABLE_SETTINGS, values, 
							"_id=" + segment + whereString, whereArgs);
			break;

		default:
			Log.e(TAG, "Update received unknown URL: " + url);
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	public String getType(Uri url) {
		switch (URL_MATCHER.match(url)) {
		case SETTINGS:
			return "vnd.android.cursor.dir/vnd.openintents.sensorsimulator.setting";

		case SETTING_ID:
			return "vnd.android.cursor.item/vnd.openintents.sensorsimulator.setting";

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}

	static {
		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URL_MATCHER.addURI("org.openintents.sensorsimulator", "settings", SETTINGS);
		URL_MATCHER.addURI("org.openintents.sensorsimulator", "settings/#", SETTING_ID);
		
		PREFERENCES_PROJECTION_MAP = new HashMap<String, String>();
		PREFERENCES_PROJECTION_MAP.put(Settings._ID, "settings._id");
		PREFERENCES_PROJECTION_MAP.put(Settings.KEY, "settings." + Settings.KEY);
		PREFERENCES_PROJECTION_MAP.put(Settings.VALUE, "settings." + Settings.VALUE);
		
	}
}
