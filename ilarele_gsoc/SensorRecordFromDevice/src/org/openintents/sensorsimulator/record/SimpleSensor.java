package org.openintents.sensorsimulator.record;

public class SimpleSensor {
	// similars to android.hardware.Sensor values
	// for <= 2.1 API - 8 sensors
	// for > 2.1 API - 11 sensors
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

	// not quite a sensor
	public static final int TYPE_BARCODE_READER = 1 + TYPE_ROTATION_VECTOR;

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

	// not quite a sensor
	public static final String NAME_BARCODE_READER = "barcode reader";

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
		case TYPE_LINEAR_ACCELERATION:
			return NAME_LINEAR_ACCELERATION;
		case TYPE_GRAVITY:
			return NAME_GRAVITY;
		case TYPE_ROTATION_VECTOR:
			return NAME_ROTATION_VECTOR;
		case TYPE_BARCODE_READER:
			return NAME_BARCODE_READER;
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
