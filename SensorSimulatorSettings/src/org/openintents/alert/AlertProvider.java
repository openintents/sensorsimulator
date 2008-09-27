package org.openintents.alert;

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

import java.util.HashMap;

import org.openintents.provider.Alert;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class AlertProvider extends ContentProvider {

	private static final String DATABASE_NAME = "alerts.db";
	private static final int DATABASE_VERSION = 2;

	private SQLiteOpenHelper mOpenHelper;

	private static final String TABLE_ALERTS = "alerts";
	private static final String TABLE_SERVICES = "services";

	private static final String TAG = "org.openintents.alert.AlertProvider";

	private static final int ALERT_GENERIC = 100;
	private static final int ALERT_GENERIC_ID = 101;
	private static final int ALERT_LOCATION = 102;
	private static final int ALERT_LOCATION_ID = 103;
	private static final int ALERT_COMBINED = 104;
	private static final int ALERT_COMBINED_ID = 105;
	private static final int ALERT_SENSOR = 106;
	private static final int ALERT_SENSOR_ID = 106;
	private static final int ALERT_DATE_TIME = 107;
	private static final int ALERT_DATE_TIME_ID = 108;

	private static final int MANAGED_SERVICE = 200;
	private static final int MANAGED_SERVICE_ID = 201;

	private static final UriMatcher URL_MATCHER;
	private static final HashMap<String, String> GENERIC_PROJECTION_MAP;
	private static final HashMap<String, String> SERVICE_PROJECTION_MAP;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "Creating table " + TABLE_ALERTS);
			db.execSQL("CREATE TABLE " + TABLE_ALERTS + " ("
					+ Alert.Generic._ID + " INTEGER PRIMARY KEY,"
					+ Alert.Generic._COUNT + " INTEGER,"
					+ Alert.Generic.CONDITION1 + " STRING,"
					+ Alert.Generic.CONDITION2 + " STRING,"
					+ Alert.Generic.TYPE + " STRING," + Alert.Generic.RULE
					+ " STRING," + Alert.Generic.NATURE + " STRING,"
					+ Alert.Generic.INTENT + " STRING,"
					+ Alert.Generic.INTENT_CATEGORY + " STRING,"
					+ Alert.Generic.INTENT_URI + " STRING,"
					+ Alert.Generic.INTENT_MIME_TYPE + " STRING,"
					+ Alert.Generic.ACTIVE + " INTEGER,"
					+ Alert.Generic.ACTIVATE_ON_BOOT + " INTEGER" + ");");

			db.execSQL("CREATE TABLE " + TABLE_SERVICES + " ("
					+ Alert.ManagedService._ID + " INTEGER PRIMARY KEY,"
					+ Alert.ManagedService._COUNT + " INTEGER,"
					+ Alert.ManagedService.SERVICE_CLASS + " STRING,"
					+ Alert.ManagedService.TIME_INTERVALL + " STRING,"
					+ Alert.ManagedService.DO_ROAMING + " STRING,"
					+ Alert.ManagedService.LAST_TIME + " STRING" +

					");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Log.w(TAG, "upgrade not supported");
			// Log.v(TAG, "");
			if (newVersion > 1) {
				db.execSQL("CREATE TABLE " + TABLE_SERVICES + " ("
						+ Alert.ManagedService._ID + " INTEGER PRIMARY KEY,"
						+ Alert.ManagedService._COUNT + " INTEGER,"
						+ Alert.ManagedService.SERVICE_CLASS + " STRING,"
						+ Alert.ManagedService.TIME_INTERVALL + " STRING,"
						+ Alert.ManagedService.DO_ROAMING + " STRING,"
						+ Alert.ManagedService.LAST_TIME + " STRING" +

						");");
			}

		}

	}// class helper

	/*
	 * public AlertProvider() { super(DATABASE_NAME, DATABASE_VERSION); }
	 */
	@Override
	public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
	}

	/*
	 * @Override protected void upgradeDatabase(int oldVersion, int newVersion) {
	 * AlertDBHelper dbHelper = new AlertDBHelper();
	 * dbHelper.onUpgrade(db, oldVersion, newVersion); }
	 */

	/*
	 * @Override protected void bootstrapDatabase() { super.bootstrapDatabase();
	 * AlertDBHelper dbHelper = new AlertDBHelper();
	 * dbHelper.onCreate(db); }
	 */

	@Override
	public String getType(Uri uri) {
		switch (URL_MATCHER.match(uri)) {
		case MANAGED_SERVICE:
			return "vnd.android.cursor.dir/vnd.openintents.alert.managedservice";

		case MANAGED_SERVICE_ID:
			return "vnd.android.cursor.item/vnd.openintents.alert.managedservice";

		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	public static void test() {
		Log.d(TAG, "TEST CALL ACCEPTED");
	}

	public static void test(Uri u) {
		int match = URL_MATCHER.match(u);
		Log.d(TAG, "TEST CALL ACCEPTED");
		Log.d(TAG, "uri>>" + u + "<< matched >>" + match + "<<");
	}

	/*
	 * @author Zero
	 * 
	 * @version 1.0 @argument uri ContentURI NOT NULL @argument values
	 *          ContentValues NOT NULL @return uri of the new item.
	 * 
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = URL_MATCHER.match(uri);
		Log.d(this.TAG, "INSERT,URI MATCHER RETURNED >>" + match + "<<");
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowID = 0;

		switch (match) {
		case ALERT_GENERIC:

			// if nature is not given, it's user.
			if (!values.containsKey(Alert.Generic.NATURE)) {
				values.put(Alert.Generic.NATURE, Alert.NATURE_USER);
			}

			rowID = db.insert(TABLE_ALERTS, "", values);
			if (rowID > 0) {
				Uri nUri = ContentUris.withAppendedId(
						Alert.Generic.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(nUri, null);
				return nUri;
			}
			throw new SQLException("Failed to insert row into " + uri);

		case ALERT_LOCATION:

			// if nature is not given, it's user.
			if (!values.containsKey(Alert.Generic.NATURE)) {
				values.put(Alert.Generic.NATURE, Alert.NATURE_USER);
			}

			if (!values.containsKey(Alert.Location.TYPE)) {
				values.put(Alert.Location.TYPE, Alert.TYPE_LOCATION);

			}

			rowID = db.insert(TABLE_ALERTS, "", values);
			if (rowID > 0) {
				Uri nUri = ContentUris.withAppendedId(
						Alert.Location.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(nUri, null);
				return nUri;
			}
			throw new SQLException("Failed to insert row into " + uri);

		case ALERT_DATE_TIME:

			// if nature is not given, it's user.
			if (!values.containsKey(Alert.Generic.NATURE)) {
				values.put(Alert.Generic.NATURE, Alert.NATURE_USER);
			}

			if (!values.containsKey(Alert.Location.TYPE)) {
				values.put(Alert.Location.TYPE, Alert.TYPE_LOCATION);

			}

			rowID = db.insert(TABLE_ALERTS, "", values);
			if (rowID > 0) {
				Uri nUri = ContentUris.withAppendedId(
						Alert.DateTime.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(nUri, null);
				return nUri;
			}
			throw new SQLException("Failed to insert row into " + uri);

		case MANAGED_SERVICE:
			rowID = db.insert(TABLE_SERVICES, "", values);
			if (rowID > 0) {
				Uri nUri = ContentUris.withAppendedId(
						Alert.ManagedService.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(nUri, null);
				return nUri;
			}
			throw new SQLException("Failed to insert row into " + uri);

		}

		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = URL_MATCHER.match(uri);
		Log.d(this.TAG, "query,URI is >>" + uri + "<<");
		Log.d(this.TAG, "query,URI MATCHER RETURNED >>" + match + "<<");
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String orderBy = null;

		// actually all alerts share one id space.
		// so we just have to match any url set sort order
		// then query
		boolean didMatch = false;
		long rowID = 0;
		switch (match) {

		case ALERT_GENERIC:
			qb.setTables(TABLE_ALERTS);
			qb.setProjectionMap(GENERIC_PROJECTION_MAP);

			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Generic.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case ALERT_LOCATION:
			qb.setTables(TABLE_ALERTS);
			qb.setProjectionMap(GENERIC_PROJECTION_MAP);

			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Generic.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case ALERT_COMBINED:
			qb.setTables(TABLE_ALERTS);
			qb.setProjectionMap(GENERIC_PROJECTION_MAP);

			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Generic.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case ALERT_DATE_TIME:
			qb.setTables(TABLE_ALERTS);
			qb.setProjectionMap(GENERIC_PROJECTION_MAP);

			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Generic.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case ALERT_GENERIC_ID:
			qb.setTables(TABLE_ALERTS);
			qb.setProjectionMap(GENERIC_PROJECTION_MAP);

			qb.appendWhere("_id=" + uri.getLastPathSegment());
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Location.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case ALERT_LOCATION_ID:
			qb.setTables(TABLE_ALERTS);
			qb.setProjectionMap(GENERIC_PROJECTION_MAP);

			qb.appendWhere("_id=" + uri.getLastPathSegment());
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Location.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case ALERT_DATE_TIME_ID:
			qb.setTables(TABLE_ALERTS);
			qb.setProjectionMap(GENERIC_PROJECTION_MAP);

			qb.appendWhere("_id=" + uri.getLastPathSegment());
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Location.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case ALERT_COMBINED_ID:

			qb.setTables(TABLE_ALERTS);
			qb.setProjectionMap(GENERIC_PROJECTION_MAP);

			qb.appendWhere("_id=" + uri.getLastPathSegment());
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Location.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case MANAGED_SERVICE:
			qb.setTables(TABLE_SERVICES);
			qb.setProjectionMap(SERVICE_PROJECTION_MAP);

			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Location.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case MANAGED_SERVICE_ID:
			qb.setTables(TABLE_SERVICES);
			qb.setProjectionMap(SERVICE_PROJECTION_MAP);

			qb.appendWhere("_id=" + uri.getLastPathSegment());
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = Alert.Location.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs,
				null, null, orderBy);
		Log
				.v(
						TAG,
						"query result for "
								+ selection
								+ " "
								+ (selectionArgs != null
										&& selectionArgs.length > 0 ? selectionArgs[0]
										: selectionArgs) + ": " + c.getCount());
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
		String alertID = "";
		int match = URL_MATCHER.match(uri);
		Log.d(this.TAG, "UPDATE,URI MATCHER RETURNED >>" + match + "<<");
		String rowID = "";

		switch (match) {

		case ALERT_GENERIC:
			count = db.update(TABLE_ALERTS, values, selection,
					selectionArgs);
			// getContext().getContentResolver().notifyChange(nUri, null);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		case ALERT_GENERIC_ID:
			alertID = uri.getPathSegments().get(1);
			count = db.update(TABLE_ALERTS, values, "_id="
					+ alertID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);

			getContext().getContentResolver().notifyChange(uri, null);
			break;
		case ALERT_LOCATION:
			count = db.update(TABLE_ALERTS, values, selection,
					selectionArgs);
			// getContext().getContentResolver().notifyChange(nUri, null);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		case ALERT_LOCATION_ID:
			alertID = uri.getPathSegments().get(1);
			count = db.update(TABLE_ALERTS, values, "_id="
					+ alertID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);

			getContext().getContentResolver().notifyChange(uri, null);
			break;

		case ALERT_DATE_TIME:
			count = db.update(TABLE_ALERTS, values, selection,
					selectionArgs);
			// getContext().getContentResolver().notifyChange(nUri, null);
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		case ALERT_DATE_TIME_ID:
			alertID = uri.getPathSegments().get(1);
			count = db.update(TABLE_ALERTS, values, "_id="
					+ alertID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);

			getContext().getContentResolver().notifyChange(uri, null);
			break;

		case MANAGED_SERVICE:
			alertID = uri.getPathSegments().get(1);
			count = db.update(TABLE_SERVICES, values, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			break;

		case MANAGED_SERVICE_ID:
			alertID = uri.getPathSegments().get(1);
			count = db.update(TABLE_ALERTS, values, "_id="
					+ alertID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);

			getContext().getContentResolver().notifyChange(uri, null);
			break;

		}
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
		String alertID = "";
		int match = URL_MATCHER.match(uri);
		Log.d(this.TAG, "INSERT,URI MATCHER RETURNED >>" + match + "<<");
		long rowID = 0;
		switch (match) {

		case ALERT_GENERIC:
			count = db.delete(TABLE_ALERTS, selection, selectionArgs);
			break;
		case ALERT_GENERIC_ID:
			alertID = uri.getPathSegments().get(1);
			count = db.delete(TABLE_ALERTS, "_id="
					+ alertID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		case ALERT_LOCATION:
			count = db.delete(TABLE_ALERTS, selection, selectionArgs);
			break;
		case ALERT_LOCATION_ID:
			alertID = uri.getPathSegments().get(1);
			count = db.delete(TABLE_ALERTS, "_id="
					+ alertID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		case ALERT_DATE_TIME:
			count = db.delete(TABLE_ALERTS, selection, selectionArgs);
			break;
		case ALERT_DATE_TIME_ID:
			alertID = uri.getPathSegments().get(1);
			count = db.delete(TABLE_ALERTS, "_id="
					+ alertID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;

		case MANAGED_SERVICE:
			count = db.delete(TABLE_SERVICES, selection, selectionArgs);
			break;
		case MANAGED_SERVICE_ID:
			alertID = uri.getPathSegments().get(1);
			count = db.delete(TABLE_SERVICES, "_id="
					+ alertID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;

		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	public boolean isSyncable() {
		return false;
	}

	static {

		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

		URL_MATCHER.addURI("org.openintents.alert", "generic/", ALERT_GENERIC);
		URL_MATCHER.addURI("org.openintents.alert", "generic/#",
				ALERT_GENERIC_ID);
		URL_MATCHER.addURI("org.openintents.alert", "location", ALERT_LOCATION);
		URL_MATCHER.addURI("org.openintents.alert", "location/#",
				ALERT_LOCATION_ID);
		URL_MATCHER.addURI("org.openintents.alert", "combined", ALERT_COMBINED);
		URL_MATCHER.addURI("org.openintents.alert", "combined/#",
				ALERT_COMBINED_ID);
		URL_MATCHER
				.addURI("org.openintents.alert", "datetime", ALERT_DATE_TIME);
		URL_MATCHER.addURI("org.openintents.alert", "datetime/#",
				ALERT_DATE_TIME_ID);
		URL_MATCHER.addURI("org.openintents.alert", "managedservice",
				MANAGED_SERVICE);
		URL_MATCHER.addURI("org.openintents.alert", "managedservice/#",
				MANAGED_SERVICE_ID);

		URL_MATCHER.addURI("org.openintents.alert", "", 6000);
		URL_MATCHER.addURI("org.openintents.alert", "/", 6001);

		GENERIC_PROJECTION_MAP = new HashMap<String, String>();
		GENERIC_PROJECTION_MAP.put(Alert.Generic._ID, Alert.Generic._ID);
		GENERIC_PROJECTION_MAP.put(Alert.Generic._COUNT, Alert.Generic._COUNT);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.TYPE, Alert.Generic.TYPE);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.CONDITION1,
				Alert.Generic.CONDITION1);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.CONDITION2,
				Alert.Generic.CONDITION2);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.NATURE, Alert.Generic.NATURE);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.ACTIVE, Alert.Generic.ACTIVE);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.ACTIVATE_ON_BOOT,
				Alert.Generic.ACTIVATE_ON_BOOT);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.RULE, Alert.Generic.RULE);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.INTENT, Alert.Generic.INTENT);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.INTENT_CATEGORY,
				Alert.Generic.INTENT_CATEGORY);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.INTENT_URI,
				Alert.Generic.INTENT_URI);
		GENERIC_PROJECTION_MAP.put(Alert.Generic.INTENT_MIME_TYPE,
				Alert.Generic.INTENT_MIME_TYPE);

		SERVICE_PROJECTION_MAP = new HashMap<String, String>();
		SERVICE_PROJECTION_MAP.put(Alert.ManagedService._ID,
				Alert.ManagedService._ID);
		SERVICE_PROJECTION_MAP.put(Alert.ManagedService._COUNT,
				Alert.ManagedService._COUNT);
		SERVICE_PROJECTION_MAP.put(Alert.ManagedService.SERVICE_CLASS,
				Alert.ManagedService.SERVICE_CLASS);
		SERVICE_PROJECTION_MAP.put(Alert.ManagedService.TIME_INTERVALL,
				Alert.ManagedService.TIME_INTERVALL);
		SERVICE_PROJECTION_MAP.put(Alert.ManagedService.DO_ROAMING,
				Alert.ManagedService.DO_ROAMING);
		SERVICE_PROJECTION_MAP.put(Alert.ManagedService.LAST_TIME,
				Alert.ManagedService.LAST_TIME);

	}

}/* eoc */