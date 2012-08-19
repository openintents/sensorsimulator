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
import java.util.Iterator;

import android.content.Context;
import android.util.Log;

/**
 * This class simulates SENSOR object used in android's sensor methods. In
 * android's methods, sensor object is generated for each sensor. This class is
 * generated only once, and it has a list of currently enabled sensors.
 * 
 * This class simulates the class android.hardware.Sensor.
 * 
 * @author Josip Balic
 */

public class Sensor {

	public static final int TYPE_ACCELEROMETER = 1;
	public static final int TYPE_ALL = -1;
	public static final int TYPE_GYROSCOPE = 4;
	public static final int TYPE_LIGHT = 5;
	public static final int TYPE_MAGNETIC_FIELD = 2;
	public static final int TYPE_ORIENTATION = 3;
	public static final int TYPE_PRESSURE = 6;
	public static final int TYPE_PROXIMITY = 8;
	public static final int TYPE_TEMPERATURE = 7;
	public static final int TYPE_BARCODE_READER = 9;
	public static final int TYPE_LINEAR_ACCELERATION = 10;
	public static final int TYPE_GRAVITY = 11;
	public static final int TYPE_ROTATION_VECTOR = 12;
	public int sensorToRegister = 0;
	public int sensorToRemove = 0;

	private ArrayList<Integer> currentSensors = new ArrayList<Integer>();
	/**
	 * Client that communicates with the SensorSimulator application.
	 */
	@SuppressWarnings("unused")
	private SensorSimulatorClient mClient;

	@SuppressWarnings("unused")
	private Context mContext;

	/**
	 * Constructor for Sensor object, it's called first time when we are
	 * registering first Sensor.
	 * 
	 * @param context
	 *            , context of the application
	 * @param type
	 *            , integer number of sensor we want to register.
	 */
	protected Sensor(Context context, int type) {
		super();
		mContext = context;
		sensorToRegister = type;
	}

	/**
	 * This method adds integer number of sensor we want to enable.
	 * 
	 * @param type
	 *            , integer number of sensor to be enabled.
	 */
	protected void addSensor(int type) {
		sensorToRegister = type;
	}

	/**
	 * This method adds integer number of sensor we want to remove.
	 * 
	 * @param type
	 *            , integer number of sensor to be removed.
	 */
	protected void removeSensor(int type) {
		sensorToRemove = type;
	}

	/**
	 * Method that checks list of enabled sensors. Returns true if sensor is
	 * enabled, or false if it isn't yet enabled.
	 * 
	 * @param type
	 *            , integer number of sensor we want to check.
	 * @return boolean true or false, returns true if sensor is enabled or false
	 *         if it isn't yet enabled.
	 */
	protected boolean checkList(int type) {
		Iterator<Integer> iter = currentSensors.iterator();
		while (iter.hasNext()) {
			if (iter.next() == type) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Once sensor is enabled, this method is called to add sensor to list of
	 * currently enabled ones.
	 * 
	 * @param type
	 *            , integer number of enabled sensor.
	 */
	protected void addSensorToList(int type) {
		currentSensors.add(type);
	}

	/**
	 * Once sensor is disabled, this method is called to remove sensor from the
	 * list. If sensor we want to remove is the only sensor in list, than we
	 * create a new empty arrayList. This must be done to avoid list and memory
	 * problems in program.
	 * 
	 * @param type
	 *            , integer number of sensor to be removed from list.
	 */
	protected void removeSensorFromList(int type) {
		if (currentSensors.size() == 1) {
			currentSensors = new ArrayList<Integer>();
		} else {
			for (int i = 0; i < currentSensors.size(); i++) {
				if (currentSensors.get(i) == type) {
					currentSensors.remove(i);
				}
			}
		}

	}

	/**
	 * Method that returns the list that contains currently enabled sensors.
	 * 
	 * @return currentSensors, ArrayList<Integer> of currently enabled sensors.
	 */
	protected ArrayList<Integer> getList() {
		return currentSensors;
	}
}
