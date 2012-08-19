package org.openintents.sensorsimulator.record;

import android.hardware.Sensor;

public class SimpleSensor {
	// similar to android.hardware.Sensor values
	// for <= 2.1 API - 8 sensors
	// for > 2.1 API - 11 sensors
	protected static final int MAX_SENSORS = 11;
	public static final int TYPE_LINEAR_ACCELERATION = 9;
	public static final int TYPE_GRAVITY = 10;
	public static final int TYPE_ROTATION_VECTOR = 11;

	public static final String NAME_ACCELEROMETER = "accelerometer";
	public static final String NAME_MAGNETIC_FIELD = "magnetic field";
	public static final String NAME_ORIENTATION = "orientation";
	public static final String NAME_GYROSCOPE = "gyroscope";
	public static final String NAME_LIGHT = "light";
	public static final String NAME_PRESSURE = "pressure";
	public static final String NAME_TEMPERATURE = "temperature";
	public static final String NAME_PROXIMITY = "proximity";
	public static final String NAME_LINEAR_ACCELERATION = "linear acceleration";
	public static final String NAME_GRAVITY = "gravity";
	public static final String NAME_ROTATION_VECTOR = "rotation vector";

	private int mType;
	private String mName;
	private boolean mIsEnable = false;

	public SimpleSensor(int type) {
		mType = type;
		mName = getName(type);
	}

	public SimpleSensor(int type, boolean enabled) {
		this(type);
		mIsEnable = enabled;
	}

	private String getName(int type) {
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
			return NAME_ACCELEROMETER;
		case Sensor.TYPE_MAGNETIC_FIELD:
			return NAME_MAGNETIC_FIELD;
		case Sensor.TYPE_ORIENTATION:
			return NAME_ORIENTATION;
		case Sensor.TYPE_GYROSCOPE:
			return NAME_GYROSCOPE;
		case Sensor.TYPE_LIGHT:
			return NAME_LIGHT;
		case Sensor.TYPE_PRESSURE:
			return NAME_PRESSURE;
		case Sensor.TYPE_TEMPERATURE:
			return NAME_TEMPERATURE;
		case Sensor.TYPE_PROXIMITY:
			return NAME_PROXIMITY;
		case TYPE_LINEAR_ACCELERATION:
			return NAME_LINEAR_ACCELERATION;
		case TYPE_GRAVITY:
			return NAME_GRAVITY;
		case TYPE_ROTATION_VECTOR:
			return NAME_ROTATION_VECTOR;
		}
		return null;
	}

	public String getName() {
		return mName;
	}

	public boolean isEnabled() {
		return mIsEnable;
	}

	public void changeEnable() {
		mIsEnable = !mIsEnable;
	}

	public void setEnable(boolean isEnabled) {
		mIsEnable = isEnabled;

	}

	public int getType() {
		return mType;
	}
}
