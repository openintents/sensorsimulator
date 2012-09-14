/*
 * Copyright (C) 2008 - 2011 OpenIntents.org
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

package org.openintents.tools.simulator.model.sensor.sensors;

import java.util.Random;

/**
 * SensorModel keeps the internal data model behind a Sensor, common to all
 * sensors. It also contains abstract methods for sensor specific actions (that
 * must be implemented for each sensor).
 * 
 * @author ilarele
 * 
 */
public abstract class SensorModel {
	public static final int POZ_ACCELEROMETER = 0;
	public static final int POZ_MAGNETIC_FIELD = 1;
	public static final int POZ_ORIENTATION = 2;
	public static final int POZ_TEMPERATURE = 3;
	public static final int POZ_BARCODE_READER = 4;
	public static final int POZ_LIGHT = 5;
	public static final int POZ_PROXIMITY = 6;
	public static final int POZ_PRESSURE = 7;
	public static final int POZ_LINEAR_ACCELERATION = 8;
	public static final int POZ_GRAVITY = 9;
	public static final int POZ_ROTATION = 10;
	public static final int POZ_GYROSCOPE = 11;

	// Action Commands:
	public static String ACTION_YAW_PITCH = "yaw & pitch";
	public static String ACTION_ROLL_PITCH = "roll & pitch";
	public static String ACTION_MOVE = "move";

	// Sensors Type

	public static final int TYPE_ACCELEROMETER = 1;
	public static final int TYPE_MAGNETIC_FIELD = 1 + TYPE_ACCELEROMETER;
	public static final int TYPE_ORIENTATION = 1 + TYPE_MAGNETIC_FIELD;
	public static final int TYPE_GYROSCOPE = 1 + TYPE_ORIENTATION;
	public static final int TYPE_LIGHT = 1 + TYPE_GYROSCOPE;
	public static final int TYPE_PRESSURE = 1 + TYPE_LIGHT;
	public static final int TYPE_TEMPERATURE = 1 + TYPE_PRESSURE;
	public static final int TYPE_PROXIMITY = 1 + TYPE_TEMPERATURE;
	public static final int TYPE_LINEAR_ACCELERATION = 1 + TYPE_PROXIMITY;
	public static final int TYPE_GRAVITY = 1 + TYPE_LINEAR_ACCELERATION;
	public static final int TYPE_ROTATION_VECTOR = 1 + TYPE_GRAVITY;
	public static final int TYPE_BARCODE = 1 + TYPE_ROTATION_VECTOR;

	// Supported sensors
	public static final String ORIENTATION = "orientation";
	public static final String ACCELEROMETER = "accelerometer";
	public static final String GRAVITY = "gravity";
	public static final String LINEAR_ACCELERATION = "linear acceleration";
	public static final String TEMPERATURE = "temperature";
	public static final String MAGNETIC_FIELD = "magnetic field";
	public static final String LIGHT = "light";
	public static final String PROXIMITY = "proximity";
	public static final String BARCODE_READER = "barcode reader";
	public static final String PRESSURE = "pressure";
	public static final String ROTATION_VECTOR = "rotation vector";
	public static final String GYROSCOPE = "gyroscope";

	public static final String SHOW_ACCELERATION = "show acceleration";
	public static final String BINARY_PROXIMITY = "binary proximity";

	public static final String DISABLED = "DISABLED";

	public static final String SENSOR_DELAY_FASTEST = "FASTEST (0 ms)";
	public static final String SENSOR_DELAY_GAME = "GAME(20 ms)";
	public static final String SENSOR_DELAY_UI = "UI(60 ms)";
	public static final String SENSOR_DELAY_NORMAL = "NORMAL(200 ms)";

	/** Delay in milliseconds */
	public static final int DELAY_MS_FASTEST = 0;
	public static final int DELAY_MS_GAME = 20;
	public static final int DELAY_MS_UI = 60;
	public static final int DELAY_MS_NORMAL = 200;

	// Constant giving the unicode value of degrees symbol.
	final static public String DEGREES = "\u00B0";
	final static public String MICRO = "\u00b5";
	final static public String PLUSMINUS = "\u00b1";
	final static public String SQUARED = "\u00b2"; // superscript two

	private static Random mRandomGenerator = new Random();

	/** Whether the sensor is enable or not. */
	protected boolean mEnabled;

	/** Whether to form an average at each update */
	protected boolean mUpdateAverage;

	// Random contribution
	protected float mRandom;

	/**
	 * Duration (in milliseconds) between two updates. This is the inverse of
	 * the update rate.
	 */
	protected long mUpdateDelay;
	/**
	 * Whether to form the average over the last duration when reading out
	 * sensors. Alternative is to just take the current value.
	 */
	protected boolean mAverage;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	protected long mNextUpdate;

	public SensorModel() {
		mEnabled = false;

		mUpdateDelay = 200;
	}

	/**
	 * Returns the number of parameters of the sensor values.
	 * 
	 * @param out
	 */
	public abstract int getNumSensorValues();

	/**
	 * It is used in communication with the emulator, when sending sensor
	 * values.
	 * 
	 * @param out
	 */
	public abstract String printSensorData();

	/**
	 * 
	 * @return The Standard Unit of measurement for sensors values.
	 */
	public abstract String getSI();

	/**
	 * Sets the next values for the sensor (if the time for next update was
	 * reached), by making the average or keeping the current value.
	 */
	public abstract void updateSensorReadoutValues();

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enable) {
		mEnabled = enable;
	}

	public double getRandom() {
		return mRandom;
	}

	public abstract String getName();

	public abstract int getType();

	public String getAverageName() {
		return "average " + getName();
	}

	/**
	 * get a random number in the range -random to +random
	 * 
	 * @param random
	 *            range of random number
	 * @return random number
	 */
	public static double getRandom(double random) {
		double val;
		val = mRandomGenerator.nextDouble();
		return (2 * val - 1) * random;
	}

	/**
	 * Sets the delay between two sensor updates.
	 * 
	 * @param updateDelay
	 *            the delay between two sensor updates
	 */
	public void setCurrentUpdateDelay(int updateDelay) {
		mUpdateDelay = updateDelay;
	}

	/**
	 * Sets the sensor update delay to the DELAY_MS_NORMAL delay
	 */
	public void resetCurrentUpdateDelay() {
		mUpdateDelay = DELAY_MS_NORMAL;
	}
}
