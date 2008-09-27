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

import java.util.HashMap;

import org.openintents.provider.Location;
import org.openintents.provider.Location.Extras;

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
 * Provides access to a database of locations. Each location has a latitude and
 * longitude, a creation date and a modified data.
 * 
 * supports urls of the format content://org.openintents.locations/locations
 * content://org.openintents.locations/locations/23
 * 
 */
public class LocationsProvider extends ContentProvider {

	private SQLiteOpenHelper mOpenHelper;

	private static final String TAG = "LocationsProvider";
	private static final String DATABASE_NAME = "locations.db";
	private static final int DATABASE_VERSION = 4;

	private static HashMap<String, String> LOCATION_PROJECTION_MAP;

	private static final int LOCATIONS = 1;
	private static final int LOCATION_ID = 2;
	private static final int LOCATIONSEXTRA = 3;
	private static final int LOCATIONSEXTRA_ID = 4;
	
	private static final UriMatcher URL_MATCHER;

	

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE locations (_id INTEGER PRIMARY KEY,"
					+ "latitude DOUBLE," + "longitude DOUBLE,"
					+ "created INTEGER," + "modified INTEGER" + ");");
			db.execSQL("CREATE TABLE locations_extra (_id INTEGER PRIMARY KEY,"
					+ "location_id INTEGER," + "key STRING," + "value STRING"+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS locations");
			db.execSQL("DROP TABLE IF EXISTS locations_extra");
			onCreate(db);
		}
	}

	@Override
	public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
	}

	@Override
	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (URL_MATCHER.match(url)) {
		case LOCATIONS:
			qb.setTables("locations");
			qb.setProjectionMap(LOCATION_PROJECTION_MAP);
			break;

		case LOCATION_ID:
			qb.setTables("locations");
			qb.setProjectionMap(LOCATION_PROJECTION_MAP);
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;

		case LOCATIONSEXTRA:
			qb.setTables("locations_extra");
			String segment = url.getPathSegments().get(1);
			qb.appendWhere("location_id=" + segment);
			sort = Extras.KEY;
			break;			
		case LOCATIONSEXTRA_ID:
			qb.setTables("locations_extra");
			qb.appendWhere("_id=" + url.getLastPathSegment());
			sort = Extras.KEY;
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = Location.Locations.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sort;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	@Override
	public String getType(Uri url) {
		switch (URL_MATCHER.match(url)) {
		case LOCATIONS:
			return "vnd.android.cursor.dir/vnd.openintents.location";

		case LOCATION_ID:
			return "vnd.android.cursor.item/vnd.openintents.location";

		case LOCATIONSEXTRA:			
			return "vnd.android.cursor.dir/vnd.openintents.locationextra";

		case LOCATIONSEXTRA_ID:			
			return "vnd.android.cursor.item/vnd.openintents.locationextra";

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}

	@Override
	public Uri insert(Uri url, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowID;
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		
		switch (URL_MATCHER.match(url)){
		case LOCATIONS:
			Long now = Long.valueOf(System.currentTimeMillis());
			Resources r = Resources.getSystem();

			// Make sure that the fields are all set
			if (!values.containsKey(Location.Locations.CREATED_DATE)) {
				values.put(Location.Locations.CREATED_DATE, now);
			}

			if (!values.containsKey(Location.Locations.MODIFIED_DATE)) {
				values.put(Location.Locations.MODIFIED_DATE, now);
			}

			rowID = db.insert("locations", "location", values);
			if (rowID > 0) {
				Uri uri = ContentUris.withAppendedId(
						Location.Locations.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(uri, null);
				return uri;
			}

			throw new SQLException("Failed to insert row into " + url);
			
		case LOCATIONSEXTRA:	
			String locationId = url.getPathSegments().get(1);
			long id = Long.parseLong(locationId);
			values.put("location_id", id);
			rowID = db.insert("locations_extra", "locationextra", values);
			if (rowID > 0) {
				Uri uri = ContentUris.withAppendedId(
						Location.Extras.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(uri, null);
				return uri;
			}
			throw new SQLException("Failed to insert row into " + url);
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}


	}

	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
		long rowId = 0;
		switch (URL_MATCHER.match(url)) {
		case LOCATIONS:
			count = db.delete("locations", where, whereArgs);
			break;

		case LOCATION_ID:
			String segment = url.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			String whereString;
			if (!TextUtils.isEmpty(where)) {
				whereString = " AND (" + where + ')';
			} else {
				whereString = "";
			}

			count = db.delete("locations", "_id=" + segment + whereString,
					whereArgs);
			break;
			
		case LOCATIONSEXTRA:
			segment = url.getPathSegments().get(2);
			long locationId = Long.parseLong(segment);
			count = db.delete("locations_extra", "location_id = " + locationId, null);
			break;			
		case LOCATIONSEXTRA_ID:
			segment = url.getLastPathSegment();
			long extraId = Long.parseLong(segment);
			count = db.delete("locations_extra", "_id = " + extraId, null);
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	@Override
	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
		switch (URL_MATCHER.match(url)) {
		case LOCATIONS:
			count = db.update("locations", values, where, whereArgs);
			break;

		case LOCATION_ID:
			String segment = url.getPathSegments().get(1);

			String whereString;
			if (!TextUtils.isEmpty(where)) {
				whereString = " AND (" + where + ')';
			} else {
				whereString = "";
			}
			
			count = db.update("locations", values, "_id=" + segment
					+ whereString, whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	static {
		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URL_MATCHER.addURI("org.openintents.locations", "locations", LOCATIONS);
		URL_MATCHER.addURI("org.openintents.locations", "locations/#",
				LOCATION_ID);
		URL_MATCHER.addURI("org.openintents.locations", "locations/#/*",
				LOCATIONSEXTRA);
		URL_MATCHER.addURI("org.openintents.locations", "locations/#/*/#",
				LOCATIONSEXTRA_ID);
		URL_MATCHER.addURI("org.openintents.locations", "extras/#",
				LOCATIONSEXTRA_ID);

		LOCATION_PROJECTION_MAP = new HashMap<String, String>();
		LOCATION_PROJECTION_MAP.put(Location.Locations._ID, "_id");
		LOCATION_PROJECTION_MAP.put(Location.Locations.LATITUDE, "latitude");
		LOCATION_PROJECTION_MAP.put(Location.Locations.LONGITUDE, "longitude");
		LOCATION_PROJECTION_MAP.put(Location.Locations.CREATED_DATE, "created");
		LOCATION_PROJECTION_MAP.put(Location.Locations.MODIFIED_DATE,
				"modified");
	}
}
