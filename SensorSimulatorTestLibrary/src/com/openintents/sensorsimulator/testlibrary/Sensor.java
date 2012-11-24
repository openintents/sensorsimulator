package com.openintents.sensorsimulator.testlibrary;

public class Sensor {
	// TODO add Level 14 sensors
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

	public enum Type {
		ACCELEROMETER, MAGNETIC_FIELD, ORIENTATION, TEMPERATURE, BARCODE_READER, LIGHT, PROXIMITY, PRESSURE, LINEAR_ACCELERATION, GRAVITY, ROTATION, GYROSCOPE
	}

	public static int typeToInt(Type sensor) {
		switch (sensor) {
		case ACCELEROMETER:
			return TYPE_ACCELEROMETER;
		case BARCODE_READER:
			return TYPE_BARCODE_READER;
		case GRAVITY:
			return TYPE_GRAVITY;
		case GYROSCOPE:
			return TYPE_GYROSCOPE;
		case LIGHT:
			return TYPE_LIGHT;
		case LINEAR_ACCELERATION:
			return TYPE_LINEAR_ACCELERATION;
		case MAGNETIC_FIELD:
			return TYPE_MAGNETIC_FIELD;
		case ORIENTATION:
			return TYPE_ORIENTATION;
		case PRESSURE:
			return TYPE_PRESSURE;
		case PROXIMITY:
			return TYPE_PROXIMITY;
		case ROTATION:
			return TYPE_ROTATION_VECTOR;
		case TEMPERATURE:
			return TYPE_TEMPERATURE;
		default:
			throw new UnsupportedOperationException("Unknown sensor type!");
		}
	}

	public static Type intToType(int sensor) {
		switch (sensor) {
		case TYPE_ACCELEROMETER:
			return Type.ACCELEROMETER;
		case TYPE_BARCODE_READER:
			return Type.BARCODE_READER;
		case TYPE_GRAVITY:
			return Type.GRAVITY;
		case TYPE_GYROSCOPE:
			return Type.GYROSCOPE;
		case TYPE_LIGHT:
			return Type.LIGHT;
		case TYPE_LINEAR_ACCELERATION:
			return Type.LINEAR_ACCELERATION;
		case TYPE_MAGNETIC_FIELD:
			return Type.MAGNETIC_FIELD;
		case TYPE_ORIENTATION:
			return Type.ORIENTATION;
		case TYPE_PRESSURE:
			return Type.PRESSURE;
		case TYPE_PROXIMITY:
			return Type.PROXIMITY;
		case TYPE_ROTATION_VECTOR:
			return Type.ROTATION;
		case TYPE_TEMPERATURE:
			return Type.TEMPERATURE;
		default:
			throw new UnsupportedOperationException("Unknown sensor type!");
		}
	}
}
