package org.openintents.provider;

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

import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Provider for the Alert Framework, Make sure you call init(Context c) before
 * using any of the convenience functions. Location.Position is an Uri of the
 * format geo:long,lat example: geo:3.1472,567890 Location.Distance is distance
 * in meters as long.
 * 
 * @author Ronan 'Zero' Schwarz
 */
public class Alert {

	public static final String _TAG = "org.openintents.provider.Alert";

	public static final String TYPE_LOCATION = "location";
	public static final String TYPE_SENSOR = "sensor";
	public static final String TYPE_GENERIC = "generic";
	public static final String TYPE_COMBINED = "combined";
	public static final String TYPE_DATE_TIME = "datetime";

	public static final String NATURE_USER = "user";
	public static final String NATURE_SYSTEM = "system";

	public static ContentResolver mContentResolver;

	private static final UriMatcher URL_MATCHER;

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

	// ugly hack to make the mock provider work,
	// see [..]
	public static final long LOCATION_EXPIRES = 1000000;

	public static final String EXTRA_URI = "URI";

	protected static LocationManager locationManager;

	protected static AlarmManager alarmManager;

	protected static Context context;

	public static final class Generic implements BaseColumns {

		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.alert/generic");

		public static final String CONDITION1 = "condition1";

		public static final String CONDITION2 = "condition2";

		public static final String TYPE = "alert_type";

		public static final String RULE = "rule";

		public static final String NATURE = "nature";

		public static final String ACTIVE = "active";

		public static final String ACTIVATE_ON_BOOT = "activate_on_boot";

		public static final String INTENT = "intent";

		public static final String INTENT_CATEGORY = "intent_category";

		public static final String INTENT_URI = "intent_uri";

		public static final String INTENT_MIME_TYPE = "intent_mime_type";

		public static final String DEFAULT_SORT_ORDER = "";

		public static final String[] PROJECTION = { _ID, _COUNT, CONDITION1,
				CONDITION2, TYPE, RULE, NATURE, ACTIVE, ACTIVATE_ON_BOOT,
				INTENT, INTENT_CATEGORY, INTENT_URI, INTENT_MIME_TYPE };

	};

	/**
	 * location based alerts. you must at least specify a position
	 */
	public static final class Location implements BaseColumns {

		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.alert/location");

		/**
		 * Location.Position is an Uri of the format geo:long,lat example:
		 * geo:3.1472,567890
		 */
		public static final String POSITION = Generic.CONDITION1;

		/**
		 * Location.Distance is distance in meters as long.
		 */
		public static final String DISTANCE = Generic.CONDITION2;

		/**
		 * Type must always be of Alert.TYPE_LOCATION any other values will
		 * result in your alert not being processed.
		 */
		public static final String TYPE = Generic.TYPE;

		public static final String RULE = Generic.RULE;

		public static final String NATURE = Generic.NATURE;

		public static final String ACTIVE = Generic.ACTIVE;

		public static final String ACTIVATE_ON_BOOT = Generic.ACTIVATE_ON_BOOT;

		public static final String INTENT = Generic.INTENT;

		public static final String INTENT_CATEGORY = Generic.INTENT_CATEGORY;

		public static final String INTENT_URI = Generic.INTENT_URI;

		public static final String INTENT_MIME_TYPE = Generic.INTENT_MIME_TYPE;

		public static final String DEFAULT_SORT_ORDER = "";

		public static final String[] PROJECTION = { _ID, _COUNT, POSITION,
				DISTANCE, TYPE, RULE, NATURE, ACTIVE, ACTIVATE_ON_BOOT, INTENT,
				INTENT_CATEGORY, INTENT_URI, INTENT_MIME_TYPE };

	};

	public static final class DateTime implements BaseColumns {

		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.alert/datetime");

		/**
		 * The point in time for the alarm, in format time:epoch1234456 number
		 * is time in millisecond sice 1970, like you get from
		 * System.getCurrentMillis
		 */
		public static final String TIME = Generic.CONDITION1;

		/**
		 * the alert reocurs every n milliseconds, or not at all if set to 0.
		 * reouccreny should be at least 1 minute.
		 */
		public static final String REOCCURENCE = Generic.CONDITION2;

		public static final String TYPE = Generic.TYPE;

		public static final String RULE = Generic.RULE;

		public static final String NATURE = Generic.NATURE;

		public static final String ACTIVE = Generic.ACTIVE;

		public static final String ACTIVATE_ON_BOOT = Generic.ACTIVATE_ON_BOOT;

		public static final String INTENT = Generic.INTENT;

		public static final String INTENT_CATEGORY = Generic.INTENT_CATEGORY;

		public static final String INTENT_URI = Generic.INTENT_URI;

		public static final String INTENT_MIME_TYPE = Generic.INTENT_MIME_TYPE;

		public static final String DEFAULT_SORT_ORDER = "";

		public static final String[] PROJECTION = { _ID, _COUNT, TIME,
				REOCCURENCE, TYPE, RULE, NATURE, ACTIVE, ACTIVATE_ON_BOOT,
				INTENT, INTENT_CATEGORY, INTENT_URI, INTENT_MIME_TYPE };

	};

	public static final class ManagedService implements BaseColumns {

		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.alert/managedservice");
		public static final String SERVICE_CLASS = "service_class";

		public static final String TIME_INTERVALL = "time_intervall";

		public static final String DO_ROAMING = "do_roaming";

		public static final String LAST_TIME = "last_time";

		public static final String[] PROJECTION = { _ID, _COUNT, SERVICE_CLASS,
				TIME_INTERVALL, DO_ROAMING, LAST_TIME };
	};

	public static void registerManagedService(String serviceClassName,
			long timeIntervall, boolean useWhileRoaming) {
		long minTime = 0;
		ContentValues cv = null;

		Cursor c = mContentResolver.query(ManagedService.CONTENT_URI,
				ManagedService.PROJECTION, ManagedService.SERVICE_CLASS
						+ " like '" + serviceClassName + "'", null, null);

		if (c != null && c.getCount() > 0) {// update
			c.moveToFirst();
			String id = c
					.getString(c.getColumnIndexOrThrow(ManagedService._ID));
			ContentValues values = new ContentValues();
			values.put(ManagedService.TIME_INTERVALL, Long
					.toString(timeIntervall));
			values.put(ManagedService.DO_ROAMING, Boolean
					.toString(useWhileRoaming));
			mContentResolver.update(Uri.withAppendedPath(
					ManagedService.CONTENT_URI, id), values, null, null);
			c.close();
		} else {
			// insert
			cv = new ContentValues();
			cv.put(ManagedService.SERVICE_CLASS, serviceClassName);
			cv.put(ManagedService.TIME_INTERVALL, timeIntervall);
			cv.put(ManagedService.DO_ROAMING, useWhileRoaming);
			insert(ManagedService.CONTENT_URI, cv);

		}
		// get all entry && compute new minimum time intervall
		// TODO: make this in sql.
		c = mContentResolver.query(ManagedService.CONTENT_URI,
				ManagedService.PROJECTION, null, null, null);

		c.moveToFirst();
		minTime = c.getLong(c.getColumnIndex(ManagedService.TIME_INTERVALL));
		while (!c.isAfterLast()) {
			long l = c.getLong(c.getColumnIndex(ManagedService.TIME_INTERVALL));

			if (l < minTime) {
				minTime = l;
			}

			c.moveToNext();
		}
		c.close();

		c = mContentResolver.query(DateTime.CONTENT_URI, DateTime.PROJECTION,
				DateTime.INTENT + " like '"
						+ org.openintents.OpenIntents.SERVICE_MANAGER + "'",
				null, null);
		String now = "time:epoch," + System.currentTimeMillis();
		cv = new ContentValues();
		cv.put(DateTime.TIME, now);
		cv.put(DateTime.REOCCURENCE, minTime);
		cv.put(DateTime.INTENT, org.openintents.OpenIntents.SERVICE_MANAGER);
		cv.put(DateTime.NATURE, Alert.NATURE_SYSTEM);
		cv.put(DateTime.ACTIVATE_ON_BOOT, true);
		cv.put(DateTime.ACTIVE, true);
		cv.put(DateTime.TYPE, Alert.TYPE_DATE_TIME);
		if (c != null && c.getCount() > 0) {

			update(DateTime.CONTENT_URI, cv, DateTime.INTENT + " like '"
					+ org.openintents.OpenIntents.SERVICE_MANAGER + "'", null);

			// TODO new SDK cancle PendingIntent
			// alarmManager.cancel(new
			// Intent().setAction(org.openintents.OpenIntents.SERVICE_MANAGER));

			registerDateTimeAlert(cv);
			// registerDateTimeAlert
		} else {

			insert(DateTime.CONTENT_URI, cv);

		}
		Log.d(_TAG, "registerManagedService: finished");

	}

	public static void unregisterManagedService(String serviceClassName) {
		ContentValues cv = new ContentValues();
		long minTime = 0;

		delete(ManagedService.CONTENT_URI, ManagedService.SERVICE_CLASS
				+ " like '" + serviceClassName + "'", null);

		// get all entry && compute new minimum time intervall
		// TODO: make this in sql.
		Cursor c = mContentResolver.query(ManagedService.CONTENT_URI,
				ManagedService.PROJECTION, null, null, null);

		c.moveToFirst();
		minTime = c.getLong(c.getColumnIndex(ManagedService.TIME_INTERVALL));
		while (!c.isAfterLast()) {
			long l = c.getLong(c.getColumnIndex(ManagedService.TIME_INTERVALL));

			if (l < minTime) {
				minTime = l;
			}

			c.moveToNext();
		}
		c.close();

		c = mContentResolver.query(DateTime.CONTENT_URI, DateTime.PROJECTION,
				DateTime.INTENT + " like '"
						+ org.openintents.OpenIntents.SERVICE_MANAGER + "'",
				null, null);
		String now = "time:epoch," + System.currentTimeMillis();
		cv.put(DateTime.TIME, now);
		cv.put(DateTime.REOCCURENCE, minTime);
		cv.put(DateTime.INTENT, org.openintents.OpenIntents.SERVICE_MANAGER);
		cv.put(DateTime.NATURE, Alert.NATURE_SYSTEM);
		cv.put(DateTime.ACTIVATE_ON_BOOT, true);
		cv.put(DateTime.ACTIVE, true);
		cv.put(DateTime.TYPE, Alert.TYPE_DATE_TIME);
		if (c != null && c.getCount() > 0) {

			update(DateTime.CONTENT_URI, cv, DateTime.INTENT + " like '"
					+ org.openintents.OpenIntents.SERVICE_MANAGER + "'", null);

			// TODO new SDK cancle PendingIntent
			// alarmManager.cancel(new
			// Intent().setAction(org.openintents.OpenIntents.SERVICE_MANAGER));
			registerDateTimeAlert(cv);
			// registerDateTimeAlert
		}

	}

	/**
	 * @param uri
	 *            the content uri to insert to
	 * @param cv
	 *            the ContentValues that will be inserted to
	 */
	public static Uri insert(Uri uri, ContentValues cv) {
		Uri res = null;
		int type = URL_MATCHER.match(uri);

		res = mContentResolver.insert(uri, cv);
		Log.d(_TAG, " insert, result>>" + res + "<<");
		if (res != null) {// register alert
			Log.d(_TAG, "uri>>" + uri + "<< matched>>" + type + "<<");
			switch (type) {
			case ALERT_LOCATION:
				registerLocationAlert(cv);
				break;
			case ALERT_DATE_TIME:
				registerDateTimeAlert(cv);
				break;

			}

		}
		return res;
	}

	/**
	 * @param uri
	 *            the content uri to delete
	 * @param selection
	 *            the selection to check against
	 * @param selectionArgs
	 *            the arguments applied to selection string (optional)
	 * @return number of deleted rows
	 */
	public static int delete(Uri uri, String selection, String[] selectionArgs) {

		return mContentResolver.delete(uri, selection, selectionArgs);
	}

	/**
	 * @param uri
	 *            the content uri to update
	 * @param cv
	 *            the ContentValues that will be update in selected rows.
	 * @param selection
	 *            the selection to check against
	 * @param selectionArgs
	 *            the arguments applied to selection string (optional)
	 * @return number of updated rows
	 */
	public static int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return mContentResolver.update(uri, values, selection, selectionArgs);
	}

	public static void registerLocationAlert(ContentValues cv) {
		Uri gUri = null;
		String distStr = "";

		String geo = "";
		String loc[] = null;

		try {
			gUri = Uri.parse(cv.getAsString(Location.POSITION));
			// do this for easier debugging.
			distStr = cv.getAsString(Location.DISTANCE);
			float dist = Float.parseFloat(distStr);
			// float dist=cv.getAsFloat(Location.DISTANCE);
			geo = gUri.getSchemeSpecificPart();
			loc = geo.split(",");
			double latitude = Double.parseDouble(loc[0]);
			double longitude = Double.parseDouble(loc[1]);

			// TODO: find out how to handle this now
			/*
			 * PendingIntent i= new PendingIntent();
			 * //i.setClassName("org.openintents.alert"
			 * ,"LocationAlertDispatcher");
			 * i.setAction("org.openintents.action.LOCATION_ALERT_DISPATCH");
			 * //i.setData(gUri); i.putExtra(Location.POSITION,
			 * cv.getAsString(Location.POSITION));
			 * 
			 * locationManager.addProximityAlert( latitude, longitude, dist,
			 * LOCATION_EXPIRES, i );
			 * Log.d(_TAG,"Registerd alert geo:"+geo+" dist:"+dist);
			 * Log.d(_TAG,"Registered alert intent:" + i);
			 */
		} catch (ArrayIndexOutOfBoundsException aioe) {
			Log.e(_TAG, "Error parsing geo uri. not in format geo:lat,long");
		} catch (NumberFormatException nfe) {
			Log.e(_TAG,
					"Error parsing longitude/latitude. Not A Number (NAN)\n uri>>"
							+ gUri + "<< \n dist>>" + distStr + "<<");
		} catch (NullPointerException npe) {
			Log.e(_TAG, "Nullpointer occured. did you call init(context) ?");
			npe.printStackTrace();
		}

		// registerReceiver(org.openintents.alert.LocationAlertDispatcher,);
	}

	public static void init(Context c) {
		context = c;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		mContentResolver = context.getContentResolver();
		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
	}

	public static void registerDateTimeAlert(ContentValues cv) {

		String myDate = cv.getAsString(DateTime.TIME);
		String s[] = myDate.split(",");
		Log.d(_TAG, "registerDateTimeAlert: s[0]>>" + s[0] + "<< s[1]>>+"
				+ s[1] + "<<");
		long time = 0;
		long myReoccurence = cv.getAsLong(DateTime.REOCCURENCE);
		Intent i = new Intent();
		Bundle b = new Bundle();
		b.putString(DateTime.TIME, myDate);
		b.putLong(DateTime.REOCCURENCE, myReoccurence);
		i.setAction(org.openintents.OpenIntents.DATE_TIME_ALERT_DISPATCH);
		try {
			time = Long.parseLong(s[1]);
		} catch (NumberFormatException nfe) {
			Log.e(_TAG,
					"registerDateTimeAlert: Date/Time couldn't be parsed, check time format of >"
							+ myDate + "<");
			return;
		}

		if (myReoccurence == 0) {
			// TODO new SDK cancle PendingIntent
			// alarmManager.set(AlarmManager.RTC,time,i);
			Log.d(_TAG, "registerDateTimeAlert: registerd single @>>" + time
					+ "<<");

		} else {
			// TODO new SDK cancle PendingIntent
			// alarmManager.setRepeating(AlarmManager.RTC,time,myReoccurence,i);
			Log.d(_TAG, "registerDateTimeAlert: registerd reoccuirng @>>"
					+ time + "<< intervall>>" + myReoccurence + "<<");

		}
	}

	public static void unregisterDateTimeAlert(ContentValues cv) {
		/*
		 * String myDate=cv.getAsString(DateTime.TIME); String
		 * s[]=myDate.split(",");
		 * Log.d(_TAG,"registerDateTimeAlert: s[0]>>"+s[0]
		 * +"<< s[1]>>+"+s[1]+"<<"); long time=0; long
		 * myReoccurence=cv.getAsLong(DateTime.REOCCURENCE);
		 * 
		 * Cursor c=mContentResolver.query( DateTime.CONTENT_URI,
		 * DateTime.PROJECTION_MAP, DateTime.TIME+" like '"+myDate+"'", null
		 * null );
		 * 
		 * if (c==null||c.count()==0) {//alert has been deleted
		 * 
		 * }else if (c!=null&&c.count()==1) {//exactly out alert alarmManager. }
		 * //TODO: check if there are now other alerts at this time.
		 */

		// atm it would oly be possible to delete all dateTimeDispatch alerts
		// and register the need a new. so we just leave them be, means some
		// emtpy lookups, but hey! :/
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
		URL_MATCHER.addURI("org.openintents.alert", "", 6000);
		URL_MATCHER.addURI("org.openintents.alert", "/", 6001);
	}

}/* eoc */