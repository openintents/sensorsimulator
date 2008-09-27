package org.openintents.alert;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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
/**
 * This class will print the current location to the logfile every 30seconds.
 * DON'T USE THIS ON A REAL DEVICE! your battery will drain in now time! you
 * have been warned! ;) This is for debugging purposes only
 */
public class DebugGPSService extends Service implements Runnable {

	private boolean alive = false;

	private static final String _TAG = "DebugGPSService";
	LocationManager locMan;

	public void onCreate() {

		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.
		Toast.makeText(this, "DebugGPSService started", Toast.LENGTH_SHORT)
				.show();

		locMan = (LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);
		this.alive = true;
		Thread thr = new Thread(null, this, _TAG);
		thr.start();
	}

	public void run() {
		// Log.d(_TAG,"BEFORE LOOPER BLOCK");
		Looper.prepare();

		Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				// process incoming messages here
			}
		};

		FileOutputStream fos = null;
		try {
			fos = openFileOutput("locations.sql", Context.MODE_WORLD_READABLE
					& Context.MODE_WORLD_WRITEABLE);
		} catch (FileNotFoundException e1) {
			Log.e(_TAG, e1.getMessage(), e1);
		}
		OutputStreamWriter writer = new OutputStreamWriter(fos);

		// Looper.loop();
		// Log.d(_TAG,"AFERT LOOPER BLOCK");
		while (this.alive) {

			try {
				Thread.sleep(30 * 1000);

				Location l = locMan.getLastKnownLocation("gps");
				if (l == null) {
					// Toast.makeText(this, "Current Position>> null <<",
					// Toast.LENGTH_SHORT).show();
					Log.d(_TAG, "Current Position>> null <<");
				} else {
					// Toast.makeText(this, "Current
					// Position>>"+l.getLatitude()+":"+l.getLongitude()+"<<",
					// Toast.LENGTH_SHORT).show();
					Log.d(_TAG, "Current Position>>" + l.getLatitude() + ":"
							+ l.getLongitude() + "<<");

					try {
						writer
								.append(String
										.format(
												"INSERT INTO locations (latitude, longitude) VALUES ( %f, %f);\n",
												l.getLatitude(), l
														.getLongitude()));
					} catch (IOException e) {
						Log.e(_TAG, e.getMessage(), e);
					}
				}
			} catch (Exception e) {

				Log.e(_TAG, "Error:" + e.getMessage());
				e.printStackTrace();
			}

		}
		try {
			writer.close();
		} catch (IOException e) {
			Log.e(_TAG, e.getMessage(), e);
		}
		// finished/stopp called. cleanup & exit
		this.cleanup();
	}

	private void cleanup() {
		Log.d(_TAG, "Cleaning up...");
	}

	public IBinder onBind(android.content.Intent i) {
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, _TAG + " stoping..", Toast.LENGTH_SHORT).show();
		this.alive = false;
		// mNM.notifyWithText(1, "thread stopping",
		// NotificationManager.LENGTH_SHORT,null);
	}

}