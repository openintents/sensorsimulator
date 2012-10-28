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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import org.openintents.sensorsimulator.db.SensorSimulatorConvenience;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * Replaces SensorManager with possibility to connect to SensorSimulator.
 * 
 * Note: This class does not extend SensorManager directly, because its
 * constructor is not public.
 * 
 * @author Peli
 * @author Josip Balic
 */

public class SensorManagerSimulator implements Observer {

	private static SensorManagerSimulator instance;

	/**
	 * TAG for logging.
	 */
	@SuppressWarnings("unused")
	private static final String TAG = "SensorManagerSimulator";

	/**
	 * Client that communicates with the SensorSimulator application.
	 */
	private static SensorDataReceiver mSensorDataReceiver;

	private SensorManager mSensorManager = null;
	private Context mContext;

	public static final int SENSOR_DELAY_FASTEST = 0;
	public static final int SENSOR_DELAY_GAME = 1;
	public static final int SENSOR_DELAY_NORMAL = 3;
	public static final int SENSOR_DELAY_UI = 2;

	private static Sensor sensors;

	private Map<SensorEventListener, SensorEventListenerWrapper> mWrapperMap;

	/**
	 * Constructor.
	 * 
	 * If the SensorManagerSimulator is not connected, the default SensorManager
	 * is used. This is obtained through (SensorManager)
	 * getSystemService(Context.SENSOR_SERVICE), but can be overwritten using
	 * setDefaultSensorManager().
	 * 
	 * @param context
	 *            Context of the activity.
	 */
	private SensorManagerSimulator(Context context,
			SensorManager systemsensormanager) {
		mContext = context;
		mSensorManager = systemsensormanager;

		// new way
		SensorSimulatorConvenience prefs = new SensorSimulatorConvenience(
				context);

		Log.d(TAG, "Create MAP");
		mWrapperMap = new HashMap<SensorEventListener, SensorEventListenerWrapper>();

		mSensorDataReceiver = new DataReceiver();
		mSensorDataReceiver.addObserver(this);
	}

	/**
	 * This method is used in our program to make a new sensor manager simulator
	 * through which we emulate our sensor input.
	 * 
	 * @param context
	 *            , Context of the activity
	 * @param sensorManager
	 *            , String that is used for checking in if loop
	 * @return instance, instance of SensorManagerSimulator
	 */
	public static SensorManagerSimulator getSystemService(Context context,
			String sensorManager) {
		if (instance == null) {

			if (sensorManager.equals(Context.SENSOR_SERVICE)) {
				if (SensorManagerSimulator.isRealSensorsAvailable()) {
					instance = new SensorManagerSimulator(context,
							(SensorManager) context
									.getSystemService(sensorManager));
				} else {
					instance = new SensorManagerSimulator(context, null);
					Toast.makeText(
							context,
							"Android SensorManager disabled, 1.5 SDK emulator crashes when using it... Make sure to connect SensorSimulator",
							Toast.LENGTH_LONG).show();
				}
			}
		}
		return instance;
	}

	/**
	 * Set the SensorManager if the SensorManagerSimulator is not connected to
	 * the SensorSimulator.
	 * 
	 * By default, it is set to (SensorManager)
	 * getSystemService(Context.SENSOR_SERVICE).
	 * 
	 * @param sensormanager
	 */
	public void setDefaultSensorManager(SensorManager sensormanager) {
		mSensorManager = sensormanager;
	}

	/**
	 * Returns the available sensors.
	 * 
	 * @return available sensors as ArrayList<Integer>
	 */
	public ArrayList<Integer> getSensors() {
		if (mSensorDataReceiver.isConnected()) {
			return mSensorDataReceiver.getSensors();
		} else {
			if (mSensorManager != null) {
				return null;
			}
			return null;
		}
	}

	/**
	 * Method that checks for the 1.5 SDK Emulator bug...
	 * 
	 * @return boolean true or false
	 */
	private static boolean isRealSensorsAvailable() {
		if (Build.VERSION.SDK.equals("3")) {
			// We are on 1.5 SDK
			if (Build.MODEL.contains("sdk")) {
				// We are on Emulator
				return false;
			}
		}
		return true;
	}

	/**
	 * Registers a listener for given sensors.
	 * 
	 * @param listener
	 *            , SensorEventListener object
	 * @param sensor
	 *            , Sensor object that represents sensor we want to register
	 * @param rate
	 *            , rate of events. This is only a hint to the system. events
	 *            may be received faster or slower than the specified rate.
	 *            Usually events are received faster
	 * @return boolean, true or false if registering was successful or not
	 */
	public boolean registerListener(SensorEventListener listener,
			Sensor sensor, int rate) {
		// TODO test if sensor is supported and can be enabled
		mSensorDataReceiver.registerListener(listener, sensor, rate);
		// register with wrapper
		Log.d(TAG, "checking if key exists...");
		if (!mWrapperMap.containsKey(listener)) {
			Log.d(TAG, "it does not...");
			SensorEventListenerWrapper wrapper = new SensorEventListenerWrapper(
					listener, mSensorManager);
			wrapper.registerListener(sensor, rate);
			mWrapperMap.put(listener, wrapper);
			Log.d(TAG, "map has now " + mWrapperMap.size() + " entries");
		}
		return true;
	}

	/**
	 * Unregisters a listener for the sensors with which it is registered.
	 * 
	 * @param listener
	 *            , a SensorEventListener object
	 * @param sensors
	 *            , Sensor object that represent sensor we want to unregister
	 */
	public void unregisterListener(SensorEventListener listener, Sensor sensor) {
		Log.d(TAG, "unregistering listener, map has " + mWrapperMap.size()
				+ " entries");

		mSensorDataReceiver.unregisterListener(listener, sensor);
		if (mWrapperMap.containsKey(listener)) {
			Log.d(TAG, "unregistering wrapper...");
			mWrapperMap.get(listener).unregisterListener(sensor);
			// TODO check if wrapper has to be deleted from map
		}
	}

	/**
	 * Unregisters a listener for the sensors with which it is registered.
	 * 
	 * @param listener
	 *            a SensorListener object
	 */
	public void unregisterListener(SensorEventListener listener) {
		Log.d(TAG, "unregistering listener, map has " + mWrapperMap.size()
				+ " entries");
		mSensorDataReceiver.unregisterListener(listener);
		if (mWrapperMap.containsKey(listener)) {
			Log.d(TAG, "unregistering wrapper...");
			mWrapperMap.get(listener).unregisterListener();
			mWrapperMap.remove(listener);
		}
	}

	// Member function extensions:
	/**
	 * Connect to the Sensor Simulator. (All the settings should have been set
	 * already.)
	 */
	public void connectSimulator() {
		if (!mSensorDataReceiver.isConnected())
			mSensorDataReceiver.connect();
	};

	/**
	 * Disconnect from the Sensor Simulator.
	 */
	public void disconnectSimulator() {
		mSensorDataReceiver.disconnect();
	}

	/**
	 * Returns whether the Sensor Simulator is currently connected.
	 */
	public boolean isConnectedSimulator() {
		return mSensorDataReceiver.isConnected();
	}

	/**
	 * When we register Sensor we use
	 * SensorManager.getDefaultSensor(SENSOR.TYPE). This method simulates that
	 * command. If it's first time we are registering new sensor, new sensor
	 * object is created. Otherwise we check if sensor is already registered. If
	 * it's not, we add it to our Sensor object so that it can be registered. If
	 * it's already registered, than this method was called in
	 * unregisterListener method and we add that sensor to our Sensor object to
	 * be unregistered.
	 * 
	 * @param type
	 *            , integer number of sensor to be registered or unregistered.
	 * @return sensors, Sensor object that is used in our methods.
	 */
	public Sensor getDefaultSensor(int type) {
		if (sensors == null) {
			sensors = new Sensor(mContext, type);
			return sensors;
		} else if (sensors.checkList(type)) {
			sensors.addSensor(type);
			return sensors;
		} else {
			sensors.removeSensor(type);
			return sensors;
		}
	}

	/*
	 * DataReceiver notifies, when a fake sensor data provider has connected.
	 * Use this to switch between SensorSimulator and real API.
	 */
	@Override
	public void update(Observable observable, Object data) {
		if (mSensorDataReceiver.isConnected()) {
			Iterator<Entry<SensorEventListener, SensorEventListenerWrapper>> it = mWrapperMap
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<SensorEventListener, SensorEventListenerWrapper> pair = (Map.Entry<SensorEventListener, SensorEventListenerWrapper>) it
						.next();
				pair.getValue().fakeAPI();
			}
		} else {
			Iterator<Entry<SensorEventListener, SensorEventListenerWrapper>> it = mWrapperMap
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<SensorEventListener, SensorEventListenerWrapper> pair = (Map.Entry<SensorEventListener, SensorEventListenerWrapper>) it
						.next();
				pair.getValue().realAPI();
			}
		}
	}
}
