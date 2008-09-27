package org.openintents.alert.services;

import org.openintents.provider.Alert;

import android.app.Service;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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

public class RegisterAlertsService extends Service implements Runnable {

	private boolean alive = false;

	private static final String _TAG = "RegisterAlertsService";
	LocationManager locMan;

	Cursor mCursor;
	private int cond1Row = 0;
	private int cond2Row = 0;
	private int intentRow = 0;
	private int intentCatRow = 0;
	private int intentUriRow = 0;
	private int typeRow = 0;

	public void onCreate() {

		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.
		// Toast.makeText(this, "DebugGPSService started",
		// Toast.LENGTH_SHORT).show();
		Log.i(_TAG, "RegisterAlertsService started");
		locMan = (LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);
		Alert.init(this);
		Thread thr = new Thread(null, this, "NewsReaderService");
		thr.start();
	}

	public void run() {
		String mType = "";

		mCursor = Alert.mContentResolver.query(Alert.Generic.CONTENT_URI,
				Alert.Generic.PROJECTION, Alert.Generic.ACTIVE + " = 1 AND "
						+ Alert.Generic.ACTIVATE_ON_BOOT + " = 1", null, null);
		if (mCursor != null && mCursor.getCount() > 0) {

			cond1Row = mCursor.getColumnIndex(Alert.Generic.CONDITION1);
			cond2Row = mCursor.getColumnIndex(Alert.Generic.CONDITION2);
			intentRow = mCursor.getColumnIndex(Alert.Generic.INTENT);
			intentCatRow = mCursor
					.getColumnIndex(Alert.Generic.INTENT_CATEGORY);
			intentUriRow = mCursor.getColumnIndex(Alert.Generic.INTENT_URI);
			typeRow = mCursor.getColumnIndex(Alert.Generic.TYPE);

			mCursor.moveToFirst();
			while (!mCursor.isAfterLast()) {
				mType = mCursor.getString(typeRow);
				if (mType.equals(Alert.TYPE_LOCATION)) {
					String geo = mCursor.getString(cond1Row);
					String dist = mCursor.getString(cond2Row);
					ContentValues cv = new ContentValues();
					cv.put(Alert.Location.POSITION, geo);
					cv.put(Alert.Location.DISTANCE, dist);
					Alert.registerLocationAlert(cv);

				} else if (mType.equals(Alert.TYPE_DATE_TIME)) {
					// TODO: register timed alerts.
				}

				mCursor.moveToNext();
			}

		} else if (mCursor == null) {
			Log.e(_TAG, "Cursor was null. no alerts activated");
		} else {
			Log
					.w(_TAG,
							"Cursor was null or had zero rows. no alerts activated");
		}
		this.cleanup();
	}

	private void cleanup() {
		Log.d(_TAG, "Cleaning up...");
		if (mCursor != null) {
			mCursor.close();
		}
	}

	public IBinder onBind(android.content.Intent i) {
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, _TAG + " stoping..", Toast.LENGTH_SHORT).show();
		// mNM.notifyWithText(1, "thread stopping",
		// NotificationManager.LENGTH_SHORT,null);
	}

}