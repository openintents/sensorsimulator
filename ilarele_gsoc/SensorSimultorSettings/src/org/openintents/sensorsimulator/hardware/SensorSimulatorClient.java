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
package org.openintents.sensorsimulator.hardware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import org.openintents.sensorsimulator.db.SensorSimulator;
import org.openintents.sensorsimulator.db.SensorSimulatorConvenience;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

/**
 * SensorSimulatorClient is responsible for connecting and disconnecting our
 * SensorManagerSimulator. It also handles all the outputs and inputs. TCP
 * connection is created between this class and our java GUI responsible for
 * simulating sensors. This class contains all the methods needed for sending
 * and also receiving inputs, as well outputs.
 * 
 * @author Peli
 * @author Josip Balic
 */
final class SensorSimulatorClient {

	/**
	 * TAG for logging.
	 */
	static final String TAG = "Hardware";

	/**
	 * Whether to log sensor protocol data (send and receive) through LogCat.
	 * This is very instructive, but time-consuming.
	 */
	private static final boolean LOG_PROTOCOL = false;

	private Context mContext;
	private SensorSimulatorConvenience mSensorSimulatorConvenience;

	protected boolean connected;

	Socket mSocket;
	PrintWriter mOut;
	BufferedReader mIn;

	private ArrayList<Listener> mListeners = new ArrayList<Listener>();

	@SuppressWarnings("unused")
	private SensorManagerSimulator mSensorManager;

	/**
	 * Constructor for our SensorSimulatorClient.
	 * 
	 * @param context
	 *            , Context of our application
	 * @param sensorManager
	 *            , SensorManagerSimulator that created this client
	 */
	protected SensorSimulatorClient(Context context,
			SensorManagerSimulator sensorManager) {
		connected = false;
		mContext = context;
		mSensorManager = sensorManager;
		mSensorSimulatorConvenience = new SensorSimulatorConvenience(mContext);
	}

	/**
	 * Method used to connect our application with SensorSimulator GUI.
	 */
	protected void connect() {

		mSocket = null;
		mOut = null;
		mIn = null;

		Log.i(TAG, "Starting connection...");

		// get Info from ContentProvider
		String ipaddress = mSensorSimulatorConvenience
				.getPreference(SensorSimulator.KEY_IPADDRESS);
		String socket = mSensorSimulatorConvenience
				.getPreference(SensorSimulator.KEY_SOCKET);

		Log.i(TAG, "Connecting to " + ipaddress + " : " + socket);

		try {
			mSocket = new Socket(ipaddress, Integer.parseInt(socket));

			mOut = new PrintWriter(mSocket.getOutputStream(), true);
			mIn = new BufferedReader(new InputStreamReader(
					mSocket.getInputStream()));
		} catch (UnknownHostException e) {
			Log.e(TAG, "Don't know about host: " + ipaddress + " : " + socket);
			return;
		} catch (SocketTimeoutException e) {
			Log.e(TAG, "Connection time out: " + ipaddress + " : " + socket);
			return;
		} catch (IOException e) {
			Log.e(TAG, "Couldn't get I/O for the connection to: " + ipaddress
					+ " : " + socket);
			Log.e(TAG,
					"---------------------------------------------------------------");
			Log.e(TAG, "Do you have the following permission in your manifest?");
			Log.e(TAG,
					"<uses-permission android:name=\"android.permission.INTERNET\"/>");
			Log.e(TAG,
					"---------------------------------------------------------------");
			System.exit(1);
		}

		Log.i(TAG, "Read line...");

		String fromServer = "";
		try {
			fromServer = mIn.readLine();
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: x.x.x.x.");
			System.exit(1);
		}
		Log.i(TAG, "Received: " + fromServer);

		if (fromServer.equals("SensorSimulator")) {
			connected = true;
			Log.i(TAG, "Connected");
		} else {
			Log.i(TAG, "Problem connecting: Wrong string sent.");
			disconnect();
		}

	}

	/**
	 * Method used to disconnect our application and SensorSimulator GUI.
	 */
	protected void disconnect() {
		if (connected) {
			Log.i(TAG, "Disconnect()");

			try {
				mOut.close();
				mIn.close();
				mSocket.close();
			} catch (IOException e) {
				System.err
						.println("Couldn't get I/O for the connection to: x.x.x.x.");
				System.exit(1);
			}

			connected = false;
		} else {
			// already disconnected, nothing to do.
		}
	}

	/**
	 * Method used to get supported sensors from SensorSimulator GUI.
	 * 
	 * @return sensors, ArrayList<Integer> of supported sensors.
	 */
	protected ArrayList<Integer> getSensors() {
		// Get String array of supported sensors from SensorSimulator GUI.
		String[] sensornames = getSupportedSensors();
		// Convert that array to ArrayList of integers.
		ArrayList<Integer> sensors = SensorNames
				.getSensorsFromNames(sensornames);
		return sensors;
	}

	/** Delay in milliseconds */
	private int DELAY_MS_FASTEST = 0;
	private int DELAY_MS_GAME = 20;
	private int DELAY_MS_UI = 60;
	private int DELAY_MS_NORMAL = 200;

	/**
	 * Method that registers listener for specific sensor. All sensors can't be
	 * registered through this method like they can on real device, so
	 * registration of each sensor must be done.
	 * 
	 * @param listener
	 *            , SensorEventListener for the sensor we are registering
	 * @param sensor
	 *            , Sensor we are registering
	 * @param rate
	 *            , integer rate of updates
	 * @return boolean, true of false if registration was successful
	 */
	protected boolean registerListener(SensorEventListener listener,
			Sensor sensor, int rate) {
		int delay = -1;
		// here we check the sensor rate that is going to be applied to listener
		switch (rate) {
		case SensorManager.SENSOR_DELAY_FASTEST:
			delay = DELAY_MS_FASTEST;
			break;

		case SensorManager.SENSOR_DELAY_GAME:
			delay = DELAY_MS_GAME;
			break;

		case SensorManager.SENSOR_DELAY_UI:
			delay = DELAY_MS_UI;
			break;

		case SensorManager.SENSOR_DELAY_NORMAL:
			delay = DELAY_MS_NORMAL;
			break;

		default:
			return false;
		}
		boolean result;
		// and here we check our listeners Array, for every sensor we create new
		// listener
		// and add it to our array
		synchronized (mListeners) {
			Listener l = null;
			Iterator<Listener> iter = mListeners.iterator();
			while (iter.hasNext()) {
				Listener i = iter.next();
				if (i.mSensorListener == listener) {
					l = i;
					break;
				}
			}

			if (mListeners.isEmpty()) {
				l = new Listener(listener, sensor, delay);
				result = enableSensor(sensor, delay);
				if (result) {
					mListeners.add(l);
					sensor.addSensorToList(sensor.sensorToRegister);
					sensor.addSensor(0);
					mListeners.notify();
				}
			} else {
				l = new Listener(listener, sensor, delay);
				result = enableSensor(sensor, delay);
				if (result) {
					l.addSensors(sensor, delay);
					mListeners.add(l);
					sensor.addSensor(0);
					mListeners.notify();
				}
			}
		}

		return result;
	}

	/**
	 * Called to unregister specific sensor.
	 * 
	 * @param listener
	 *            , SensorEventListener of the sensor
	 * @param sensor
	 *            , Sensor we want to unregister
	 */
	protected void unregisterListener(SensorEventListener listener,
			Sensor sensor) {
		synchronized (mListeners) {
			Iterator<Listener> itr = mListeners.iterator();
			do {

				Listener element = itr.next();

				if (element.hasSensor(sensor.sensorToRemove))

				{
					// Line below is used to disable sensor
					boolean result = enableSensor(sensor, -1);

					if (result) {

						if (mListeners.size() == 1) {
							mListeners = new ArrayList<Listener>();
						} else {
							mListeners.remove(element);
						}

						sensor.removeSensorFromList(sensor.sensorToRemove);
						sensor.removeSensor(0);
						break;
					}
				}

			} while (itr.hasNext());

		}
	}

	/**
	 * Called when we want to unregister listener and all of it's sensors.
	 * 
	 * @param listener
	 *            , SensorEventListener of listener and it's sensors we want to
	 *            unregister
	 */
	protected void unregisterListener(SensorEventListener listener) {

		if (mListeners.size() != 0) {

			Iterator<Listener> itr = mListeners.iterator();
			Sensor mSensor = itr.next().mSensor;
			ArrayList<Integer> sensors = mSensor.getList();

			int[] sensor = new int[sensors.size()];
			for (int i = 0; i < sensors.size(); i++) {
				if (sensors.get(i) != null) {
					sensor[i] = (sensors.get(i).intValue());
				}
			}

			for (int i = 0; i < sensor.length; i++) {
				mSensor.removeSensor(sensor[i]);
				unregisterListener(listener, mSensor);
			}
		}

	}

	// ///////////////////////////////////////////////////
	// Internal functions
	/**
	 * SensorSimulatorClient holds also a private Listener class. Every time new
	 * sensor is enabled, new instance of this class is created and it's stored
	 * in SensorSimulatorClient. That way we know which sensors have registered
	 * listeners and which listener is representing which sensor.
	 * 
	 * @author Peli
	 * @author Josip Balic
	 */
	private class Listener {

		private SensorEventListener mSensorListener;
		private Sensor mSensor;
		private ArrayList<Integer> mSensors;
		private int mDelay;
		private long mNextUpdateTime;

		/**
		 * addSensors is used to add sensor to listener it creates.
		 * 
		 * @param sensor
		 *            Sensor, Sensor object in which sensor we want to enable is
		 *            located
		 * @param delay
		 *            , integer that represents delay for this sensor
		 * @return mSensors, Sensor object
		 */
		ArrayList<Integer> addSensors(Sensor sensor, int delay) {
			int sensors = sensor.sensorToRegister;
			sensor.addSensorToList(sensors);
			mSensors.add(sensors);
			if (delay < mDelay) {
				mDelay = delay;
				mNextUpdateTime = 0;
			}
			return mSensors;
		}

		/**
		 * Method used to find if this Listener contains particular sensor.
		 * 
		 * @param sensor
		 *            , integer of the sensor
		 * @return true or false, true if this sensor created this listener,
		 *         otherwise false
		 */
		boolean hasSensor(int sensor) {
			for (int i = 0; i < mSensors.size(); i++) {
				if (mSensors.get(i) == sensor)
					return true;
			}
			return false;
		}

		/**
		 * Constructor of Listener class
		 * 
		 * @param listener
		 *            , SensorEventListener that needs to be saved in
		 *            SensorSimulatorClient.
		 * @param sensor
		 *            , Sensor that created SensorEventListener
		 * @param delay
		 *            , integer that represents delay of sensor
		 */
		Listener(SensorEventListener listener, Sensor sensor, int delay) {

			mSensorListener = listener;
			mSensors = new ArrayList<Integer>();
			mSensors.add(sensor.sensorToRegister);
			mDelay = delay;
			mNextUpdateTime = 0;
			mSensor = sensor;
		}
	}

	/**
	 * Enables the sensor.
	 * 
	 * @param sensorAdd
	 *            , Sensor object that contains sensor to be added
	 * @param delay
	 *            , integer delay of sensor values
	 * @return True, if sensor is enabled
	 */
	private boolean enableSensor(Sensor sensorAdd, int delay) {
		String sensorString = null;
		boolean result = false;
		if (delay == -1) {
			sensorString = SensorNames.getSensorName(sensorAdd.sensorToRemove);
		} else {
			sensorString = SensorNames
					.getSensorName(sensorAdd.sensorToRegister);
		}

		try {
			setSensorUpdateDelay(sensorString, delay);
			result = true;
		} catch (IllegalArgumentException e) {
			Log.d(TAG, "Sensor " + sensorString + " not supported");
		}

		if (!mHandler.hasMessages(MSG_UPDATE_SENSORS)) {
			mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SENSORS));
		}
		sensorString = null;
		return result;
	}

	private static final int MSG_UPDATE_SENSORS = 1;

	// Increase needed if new sensor is added
	private static int MAX_SENSOR = 12;
	private float[][] mValues = new float[MAX_SENSOR][];
	private boolean[] mValuesCached = new boolean[MAX_SENSOR];

	// String that is used for barcode output
	private String barcode;

	/** Handle the process of updating sensors */
	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE_SENSORS) {

				// Let us go through all sensors,
				// and only read out the data if it is time for
				// that particular sensor.

				long current = SystemClock.uptimeMillis();
				long nextTime = current + DELAY_MS_NORMAL; // Largest time

				// Clear the sensor values' cache
				for (int i = 0; i < MAX_SENSOR; i++) {
					mValuesCached[i] = false;
					if (mValues[i] == null) {
						Log.d(TAG, "Create cache for sensor " + i);
						mValues[i] = new float[3];
					}
				}

				for (Listener l : mListeners) {
					if (current >= l.mNextUpdateTime) {
						// do the update:

						// Go through all sensors for this listener
						int sensorbit = 1;
						for (int i = 0; i < MAX_SENSOR; i++) {
							if (hasSensor(l.mSensors, sensorbit)) {
								// Get current sensor values (if not yet cached)
								if (!mValuesCached[i]) {
									readSensor(sensorbit, mValues[i], barcode);
									mValuesCached[i] = true;
								}
								// Check if input received is for barcode sensor
								// or for other
								// sensor and create appropriate
								if (barcode != null) {
									SensorEvent event = new SensorEvent(
											mContext, barcode, sensorbit);
									barcode = null;
									l.mSensorListener.onSensorChanged(event);
								} else {
									SensorEvent event = new SensorEvent(
											mContext, mValues[i], sensorbit);
									l.mSensorListener.onSensorChanged(event);
								}
							}

							// switch to next sensor
							sensorbit++;
						}

						// Set next update time:
						l.mNextUpdateTime += l.mDelay;

						// Now in case we are already behind the schedule,
						// do the following update as soon as possible
						// (but don't drag behind schedule forever -
						// i.e. skip the updates that are already past.)
						if (l.mNextUpdateTime < current) {
							l.mNextUpdateTime = current;
						}
					}
					if (l.mNextUpdateTime < nextTime) {
						nextTime = l.mNextUpdateTime;
					}
				}

				// readAllSensorsUpdate();

				if (mListeners.size() > 0) {
					// Autoupdate
					sendMessageAtTime(obtainMessage(MSG_UPDATE_SENSORS),
							nextTime);
				}
			}

		}
	};

	// ///////////////////////////////////////////////////////////
	// Bridge to old API

	static boolean hasSensor(ArrayList<Integer> sensors, int sensor) {
		for (int i = 0; i < sensors.size(); i++) {
			if (sensors.get(i) == sensor)
				return true;
		}
		return false;
	}

	// Here we added to original method a barcode2 String also so that we can
	// get incoming barcode variables as a String and not float[] values
	private void readSensor(int sensorbit, float[] sensorValues, String barcode2) {
		String sensorname = SensorNames.getSensorName(sensorbit);
		try {
			readSensor(sensorname, sensorValues, barcode2);
		} catch (IllegalStateException e) {
			// Sensor is currently not enabled.
			// For the new API (SDK 0.9 and higher) we catch this exception
			// and do not pass it further.
			Log.d(TAG, "Sensor not enabled -> enable it now");
		}
	}

	protected int getNumSensorValues(int sensorbit) {
		String sensorname = SensorNames.getSensorName(sensorbit);
		return getNumSensorValues(sensorname);
	}

	// ////////////////////////////////////////////////////////////////
	// DEPRECATED FUNCTIONS FOLLOW

	protected String[] getSupportedSensors() {
		mOut.println("getSupportedSensors()");
		String[] sensors = { "" };
		int num = 0;

		try {
			String numstr = mIn.readLine();
			num = Integer.parseInt(numstr);

			sensors = new String[num];
			for (int i = 0; i < num; i++) {
				sensors[i] = mIn.readLine();
			}
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: x.x.x.x.");
			System.exit(1);
		}

		return sensors;
	}

	protected int getNumSensorValues(String sensor) {

		if (LOG_PROTOCOL) {
			Log.i(TAG, "Send: " + sensor);
		}
		mOut.println(sensor);
		if (LOG_PROTOCOL) {
			Log.i(TAG, "Send: getNumSensorValues()");
		}
		mOut.println("getNumSensorValues()");

		int num = 0;

		try {
			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0)
				throw new IllegalArgumentException("Sensor '" + sensor
						+ "' is not supported.");
			if (LOG_PROTOCOL) {
				Log.i(TAG, "Received: " + numstr);
			}

			num = Integer.parseInt(numstr);
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: x.x.x.x.");
			System.exit(1);
		}

		return num;
	}

	protected void readSensor(String sensor, float[] sensorValues,
			String barcode2) {
		if (sensorValues == null)
			throw new NullPointerException("readSensor for '" + sensor
					+ "' called with sensorValues == null.");
		if (LOG_PROTOCOL) {
			Log.i(TAG, "Send: getNumSensorValues()");
		}
		mOut.println("readSensor()\n" + sensor);
		int num = 0;

		try {
			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0)
				throw new IllegalArgumentException("Sensor '" + sensor
						+ "' is not supported.");
			else if (numstr.compareTo("throw IllegalStateException") == 0)
				throw new IllegalStateException("Sensor '" + sensor
						+ "' is currently not enabled.");
			if (LOG_PROTOCOL) {
				Log.i(TAG, "Received: " + numstr);
			}
			num = Integer.parseInt(numstr);

			if (sensorValues.length < num)
				throw new ArrayIndexOutOfBoundsException(
						"readSensor for '"
								+ sensor
								+ "' called with sensorValues having too few elements ("
								+ sensorValues.length
								+ ") to hold the sensor values (" + num + ").");

			for (int i = 0; i < num; i++) {
				String val = mIn.readLine();
				if (LOG_PROTOCOL) {
					Log.i(TAG, "Received: " + val);
				}

				sensorValues[i] = Float.parseFloat(val);
				if (val.length() == 13) {
					barcode2 = val;
					barcode = barcode2;
				}
			}
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: x.x.x.x.");
			System.exit(1);
		} catch (NullPointerException e2) {
			Log.e(TAG, "Error reading sensors: Is the client running?");
			System.exit(1);
		}
	}

	protected void setSensorUpdateDelay(String sensor, int updateDelay) {
		if (updateDelay == -1) {
			unsetSensorUpdateRate(sensor);
			return;
		}

		mOut.println("setSensorUpdateDelay()");
		mOut.println(sensor);
		mOut.println("" + updateDelay);

		try {
			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0)
				throw new IllegalArgumentException("Sensor '" + sensor
						+ "' is not supported.");
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: x.x.x.x.");
			System.exit(1);
		}
	}

	protected void unsetSensorUpdateRate(String sensor) {
		try {
			mOut.println("unsetSensorUpdateRate()");
			mOut.println(sensor);

			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0)
				throw new IllegalArgumentException("Sensor '" + sensor
						+ "' is not supported.");
		} catch (Exception e) {
			System.err
					.println("Couldn't get I/O for the connection to: x.x.x.x.");
			System.exit(1);
		}
	}

}
