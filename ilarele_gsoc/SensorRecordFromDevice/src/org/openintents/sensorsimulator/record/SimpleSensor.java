package org.openintents.sensorsimulator.record;

import android.util.Log;

public class SimpleSensor {
	public static final int TYPE_ACCELEROMETER = 1;
	public static final int TYPE_MAGNETIC_FIELD = 2;
	public static final int TYPE_ORIENTATION = 3;
	public static final int TYPE_GYROSCOPE = 4;
	public static final int TYPE_LIGHT = 5;
	public static final int TYPE_PRESSURE = 6;
	public static final int TYPE_TEMPERATURE = 7;
	public static final int TYPE_PROXIMITY = 8;
	public static final int TYPE_BARCODE_READER = 9;
	public static final int TYPE_LINEAR_ACCELERATION = 10;
	public static final int TYPE_GRAVITY = 11;
	public static final int TYPE_ROTATION_VECTOR = 12;
	
	public static final String NAME_ACCELEROMETER = "accelerometer";
	public static final String NAME_MAGNETIC_FIELD = "magnetic field";
	public static final String NAME_ORIENTATION = "orientation";
	public static final String NAME_GYROSCOPE = "gyroscope";
	public static final String NAME_LIGHT = "light";
	public static final String NAME_PRESSURE = "pressure";
	public static final String NAME_TEMPERATURE = "temperature";
	public static final String NAME_PROXIMITY = "proximity";
	public static final String NAME_BARCODE_READER = "barcode reader";
	public static final String NAME_LINEAR_ACCELERATION = "linear acceleration";
	public static final String NAME_GRAVITY = "gravity";
	public static final String NAME_ROTATION_VECTOR = "rotation vector";
	protected static final int MAX_SENSORS = 12;
	
	private int mType;
	private String mName;
	private boolean mIsEnable;
	
	public SimpleSensor(int type) {
		mType = type;
		mName = getName(type);
	}

	private String getName(int type) {
		switch (type) {
		case TYPE_ACCELEROMETER:
			return NAME_ACCELEROMETER;
		case TYPE_MAGNETIC_FIELD:
			return NAME_MAGNETIC_FIELD;
		case TYPE_ORIENTATION:
			return NAME_ORIENTATION;
		case TYPE_GYROSCOPE:
			return NAME_GYROSCOPE;
		case TYPE_LIGHT:
			return NAME_LIGHT;
		case TYPE_PRESSURE:
			return NAME_PRESSURE;
		case TYPE_TEMPERATURE:
			return NAME_TEMPERATURE;
		case TYPE_PROXIMITY:
			return NAME_PROXIMITY;
		case TYPE_BARCODE_READER:
			return NAME_BARCODE_READER;
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
