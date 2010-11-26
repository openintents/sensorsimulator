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

package org.openintents.sensorsimulator.hardware;

import android.content.Context;

/**
 * This class provides sensor functions as they have been used in an earlier SDK beta.
 * These functions are still used to communicate to the stand-alone SensorSimulator.
 * 
 * @author Peli
 * @deprecated
 */
final class Sensors /*extends android.hardware.Sensors*/ {

	/**
	 * TAG for logging.
	 */
	private static final String TAG = "Hardware";
	
	/**
	 * Client that communicates with the SensorSimulator application.
	 */
	private SensorSimulatorClient mClient;
	
	private Context mContext;
	
	protected Sensors(Context context) {
		super();
		mContext = context;
		mClient = new SensorSimulatorClient(mContext);
	}

	/**
	 * Disables the given sensor for this VM. 
	 * Following this call, calls to readSensor or getSensorUpdateRate 
	 * for the given sensor type are disallowed.
	 * 
	 * @param sensor the name of the sensor to be enabled 
	 * @throws IllegalArgumentException if the sensor is not supported 
	 */
	protected void disableSensor(String sensor) {
		if (mClient.connected) {
			mClient.disableSensor(sensor);
		} else {
			//android.hardware.Sensors.disableSensor(sensor);
		}
	}

	/**
	 * Enables the given sensor for this VM. 
	 * Following this call, calls to readSensor for the given sensor type are allowed. 
	 * @param sensor the name of the sensor to be enabled 
	 * @throws IllegalArgumentException if the sensor is not supported
	 */
	protected void enableSensor(String sensor) {
		if (mClient.connected) {
			mClient.enableSensor(sensor);
		} else {
			//android.hardware.Sensors.enableSensor(sensor);
		}
	}

	/**
	 * Returns the number of sensor values returned by the given sensor. 
	 * The returned value is 1 for temperature and pedometer values, 
	 * and 3 for accelerator or compass values. 
	 * @param sensor a string indicating the sensor type
	 * @return number of sensor values
	 * @throws IllegalArgumentException if the sensor is not supported 
	 */
	protected int getNumSensorValues(String sensor) {
		if (mClient.connected) {
			return mClient.getNumSensorValues(sensor);
		} else {
			return 0;//android.hardware.Sensors.getNumSensorValues(sensor);
		}
	}
	
	/**
	 * Returns the current update rate for the given sensor, in updates per second. 
	 * If the sensor does not allow external control over its update rate, 
	 * or does not have a fixed update rate, 0 is returned. 
	 * @param sensor the name of the sensor to be queried 
	 * @return the current update rate for the sensor, or 0
	 * @throws IllegalArgumentException  if the sensor is not supported
	 * @throws IllegalStateException  if the sensor is not enabled  
	 */
	protected float getSensorUpdateRate(String sensor) {
		if (mClient.connected) {
			return mClient.getSensorUpdateRate(sensor);
		} else {
			return 0;//android.hardware.Sensors.getSensorUpdateRate(sensor);
		}
	}

	/**
	 * Returns a list of supported update rates for the given sensor, in updates per second. 
	 * If no information is available, null is returned. 
	 * @param sensor the name of the sensor to be queried 
	 * @return an array of floats, or null is nor information is available.
	 * @throws IllegalArgumentException if the sensor is not supported
	 */
	protected float[] getSensorUpdateRates(String sensor) {
		if (mClient.connected) {
			return mClient.getSensorUpdateRates(sensor);
		} else {
			return null; //android.hardware.Sensors.getSensorUpdateRates(sensor);
		}
	}
	
	/**
	 * Returns an array of Strings containing the supported sensor types.
	 * @return array of Strings containing the supported sensor types
	 */
	protected String[] getSupportedSensors() {
		if (mClient.connected) {
			return mClient.getSupportedSensors();
		} else {
			return new String[] {}; //android.hardware.Sensors.getSupportedSensors();
		}
	}

	/**
	 * Reads the sensor indicated by sensorType, storing the vector of returned values into the entries of sensorValues. 
	 * Values whose values are not given in defined units are normalized to the range [-1, 1]. 
	 * 
	 * For sensors that return a spatial vector, the axes are oriented as follows: 
	 * with the device lying flat on a horizontal surface in front of the user, 
	 * oriented so the screen is readable by the user in the normal fashion, 
	 * the X axis goes from left to right, the Y axis goes from the user toward the device, 
	 * and the Z axis goes upwards perpendicular to the surface.
	 *
	 * @param sensor the name of the sensor to read from
	 * @param sensorValues an array of floats to hold the returned value(s) 
	 * @throws IllegalArgumentException if the sensor is not supported 
	 * @throws IllegalStateException if the sensor is not enabled 
	 * @throws NullPointerException if sensorValues is null 
	 * @throws ArrayIndexOutOfBoundsException if sensorValues has too few elements to hold the sensor values  
	 */
	protected void readSensor(String sensor, float[] sensorValues) {
		if (mClient.connected) {
			mClient.readSensor(sensor, sensorValues);
		} else {
			//android.hardware.Sensors.readSensor(sensor, sensorValues);
		}
	}
	
	/**
	 * Sets the desired update rate for the given sensor, in updates per second. 
	 * The supported update rate closest to the given rate is used. 
	 * If the sensor is not enabled at the time of the call, 
	 * the change will take effect when it becomes enabled. 
	 * If the sensor does not allow external control over its update rate, nothing happens. 
	 * @param sensor the name of the sensor to be queried
	 * @param updatesPerSecond the desired update rate for the sensor
	 * @throws IllegalArgumentException  if the sensor is not supported  
	 */
	protected void setSensorUpdateRate(String sensor, float updatesPerSecond) {
		if (mClient.connected) {
			mClient.setSensorUpdateRate(sensor, updatesPerSecond);
		} else {
			//android.hardware.Sensors.setSensorUpdateRate(sensor, updatesPerSecond);
		}
	}
	
	/**
	 * Unsets the desired update rate for the given sensor. 
	 * The sensor will use its default rate. 
	 * If the sensor is not enabled at the time of the call, 
	 * the change will take effect when it becomes enabled. 
	 * @param sensor the name of the sensor to be updated
	 * @throws IllegalArgumentException if the sensor is not supported  
	 */
	protected void unsetSensorUpdateRate(String sensor) {
		if (mClient.connected) {
			mClient.unsetSensorUpdateRate(sensor);
		} else {
			//android.hardware.Sensors.unsetSensorUpdateRate(sensor);
		}
	}
	
	//  Member function extensions:
	/**
	 * Connect to the Sensor Simulator.
	 * (All the settings should have been set already.)
	 */
	protected void connectSimulator() {
		mClient.connect();
	};
	
	/**
	 * Disconnect from the Sensor Simulator.
	 */
	protected void disconnectSimulator() {
		mClient.disconnect();
	}
}
