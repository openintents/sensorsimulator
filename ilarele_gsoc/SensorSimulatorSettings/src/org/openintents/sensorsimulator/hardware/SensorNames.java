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

/**
 * Convenience functions to extract sensor names from a sensor's integers, and
 * the other way round.
 * 
 * @author Peli
 * @author Josip Balic
 */

public class SensorNames {

	// TAG for logging.
	@SuppressWarnings("unused")
	private static final String TAG = "SensorNames";

	// SensorNames of our sensors.
	public static final String TYPE_ORIENTATION = "orientation";
	public static final String TYPE_ACCELEROMETER = "accelerometer";
	public static final String TYPE_TEMPERATURE = "temperature";
	public static final String TYPE_MAGNETIC_FIELD = "magnetic field";
	public static final String TYPE_LIGHT = "light";
	public static final String TYPE_GYROSCOPE = "gyroscope";
	public static final String TYPE_PROXIMITY = "proximity";
	public static final String TYPE_PRESSURE = "pressure";
	public static final String TYPE_BARCODE_READER = "barcode reader";
	public static final String TYPE_LINEAR_ACCELERATION = "linear acceleration";
	public static final String TYPE_GRAVITY = "gravity";
	public static final String TYPE_ROTATION_VECTOR = "rotation vector";

	/**
	 * Number of current sensors in our program. In android's specification
	 * there are 11 sensors, but we have added barcode reader, so the number is
	 * 12.
	 */
	public static final int SENSOR_MAX_BIT = 12;

	/**
	 * Convert a sensor integer number into a sensor name.
	 * 
	 * @param sensorInteger
	 *            , integer of sensor
	 * @return String, returns string name of our sensor
	 */
	public static String getSensorName(int sensorInteger) {
		switch (sensorInteger) {
		case Sensor.TYPE_ACCELEROMETER:
			return SensorNames.TYPE_ACCELEROMETER;
		case Sensor.TYPE_MAGNETIC_FIELD:
			return SensorNames.TYPE_MAGNETIC_FIELD;
		case Sensor.TYPE_ORIENTATION:
			return SensorNames.TYPE_ORIENTATION;
		case Sensor.TYPE_GYROSCOPE:
			return SensorNames.TYPE_GYROSCOPE;
		case Sensor.TYPE_LIGHT:
			return SensorNames.TYPE_LIGHT;
		case Sensor.TYPE_PRESSURE:
			return SensorNames.TYPE_PRESSURE;
		case Sensor.TYPE_TEMPERATURE:
			return SensorNames.TYPE_TEMPERATURE;
		case Sensor.TYPE_PROXIMITY:
			return SensorNames.TYPE_PROXIMITY;
		case Sensor.TYPE_BARCODE_READER:
			return SensorNames.TYPE_BARCODE_READER;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return SensorNames.TYPE_LINEAR_ACCELERATION;
		case Sensor.TYPE_GRAVITY:
			return SensorNames.TYPE_GRAVITY;
		case Sensor.TYPE_ROTATION_VECTOR:
			return SensorNames.TYPE_ROTATION_VECTOR;
		default:
			return null;
		}
	}

	/**
	 * Convert a sensor name into a sensor integer number.
	 * 
	 * @param sensorName
	 *            , String name of our sensor
	 * @return integer, returns integer number of our sensor
	 */
	public static int getSensorInteger(String sensorName) {
		if (sensorName.equalsIgnoreCase(TYPE_ACCELEROMETER)) {
			return Sensor.TYPE_ACCELEROMETER;
		} else if (sensorName.equalsIgnoreCase(TYPE_GYROSCOPE)) {
			return Sensor.TYPE_GYROSCOPE;
		} else if (sensorName.equalsIgnoreCase(TYPE_LIGHT)) {
			return Sensor.TYPE_LIGHT;
		} else if (sensorName.equalsIgnoreCase(TYPE_MAGNETIC_FIELD)) {
			return Sensor.TYPE_MAGNETIC_FIELD;
		} else if (sensorName.equalsIgnoreCase(TYPE_ORIENTATION)) {
			return Sensor.TYPE_ORIENTATION;
		} else if (sensorName.equalsIgnoreCase(TYPE_PRESSURE)) {
			return Sensor.TYPE_PRESSURE;
		} else if (sensorName.equalsIgnoreCase(TYPE_PROXIMITY)) {
			return Sensor.TYPE_PROXIMITY;
		} else if (sensorName.equalsIgnoreCase(TYPE_TEMPERATURE)) {
			return Sensor.TYPE_TEMPERATURE;
		} else if (sensorName.equalsIgnoreCase(TYPE_BARCODE_READER)) {
			return Sensor.TYPE_BARCODE_READER;
		} else if (sensorName.equalsIgnoreCase(TYPE_LINEAR_ACCELERATION)) {
			return Sensor.TYPE_LINEAR_ACCELERATION;
		} else if (sensorName.equalsIgnoreCase(TYPE_GRAVITY)) {
			return Sensor.TYPE_GRAVITY;
		} else if (sensorName.equalsIgnoreCase(TYPE_ROTATION_VECTOR)) {
			return Sensor.TYPE_ROTATION_VECTOR;
		} else {
			return 0;
		}
	}

	/**
	 * Returns a string of names from an ArrayList of sensors.
	 * 
	 * @param sensors
	 *            , ArrayList<Integer> that contains list of sensors represented
	 *            with integer numbers.
	 * @return ArrayList<String>, of sensor names
	 */

	public static ArrayList<String> getSensorNames(ArrayList<Integer> sensors) {
		ArrayList<String> sensorNames = new ArrayList<String>();
		for (int i = 0; i < sensors.size(); i++) {
			for (int type = 1; type <= SENSOR_MAX_BIT; type++) {
				if (type == sensors.get(i)) {
					sensorNames.add(i, getSensorName(sensors.get(i)));
				}
			}
		}
		return sensorNames;
	}

	/**
	 * Convert a list of sensor names into the ArrayList<Integer>.
	 * 
	 * @param sensornames
	 *            , String[] of sensors represented with their names.
	 * @return ArrayList<Integer>, arrayList that contains integer number of
	 *         sensors.
	 */

	public static ArrayList<Integer> getSensorsFromNames(String[] sensornames) {
		ArrayList<Integer> sensors = new ArrayList<Integer>();
		for (int i = 0; i < sensornames.length; i++) {
			for (int type = 1; type <= SENSOR_MAX_BIT; type++) {
				if (sensornames[i].equals(getSensorName(type))) {
					sensors.add(type);
				}
			}
		}
		return sensors;
	}

	/**
	 * Returns the number of values a specific sensor returns.
	 * 
	 * @param sensor
	 *            , integer Sensor ID.
	 * @return Number, integer number of values returned.
	 */
	public static int getNumSensorValues(int sensor) {
		switch (sensor) {
		case Sensor.TYPE_ORIENTATION:
		case Sensor.TYPE_ACCELEROMETER:
		case Sensor.TYPE_LINEAR_ACCELERATION:
		case Sensor.TYPE_GRAVITY:
		case Sensor.TYPE_MAGNETIC_FIELD:
		case Sensor.TYPE_GYROSCOPE:
		case Sensor.TYPE_ROTATION_VECTOR:
			return 3;
		case Sensor.TYPE_PRESSURE:
		case Sensor.TYPE_TEMPERATURE:
		case Sensor.TYPE_LIGHT:
		case Sensor.TYPE_PROXIMITY:
		case Sensor.TYPE_BARCODE_READER:
			return 1;
		default:
			return 0;
		}
	}

}
