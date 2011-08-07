package org.openintents.tools.simulator.model;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

public class StateModel {

	private float mTime;
	// sensors supported for interpolation
	private float mTemperature;
	private float mLight;
	private float mProximity;
	private float mPressure;
	private float[] mGravity;
	private float[] mLinearAcceleration;

	// not supported for interpolation
	// yaw, pitch, roll
	private float[] mOrientation;

	// x, y, z
	// private float[] mAccelerometer;

	// mLight
	// mProximity
	// mTemperature
	// mGravity

	public StateModel(StateModel model) {
		this.mTime = 0.5f;
		this.mTemperature = model.getTemperature();
		this.mLight = model.getLight();
		this.mProximity = model.getProximity();
		this.mPressure = model.getPressure();
		this.mGravity = model.getGravity().clone();
		this.mLinearAcceleration = model.getLinearAcceleration().clone();

		// not supported - used for drawing the device
		this.mOrientation = model.getOrientation().clone();
		// this.mAccelerometer = model.getAccelerometer().clone();
	}

	public StateModel(SensorSimulatorModel simulatorModel) {
		this();
		// mAccelerometer = new float[4];
		copyState(simulatorModel);
	}

	/**
	 * Empty contructor
	 */
	public StateModel() {
		mTime = 0.5f;
		mGravity = new float[4];
		mLinearAcceleration = new float[4];
		mOrientation = new float[4];
	}

	/**
	 * 
	 * @return lax, lay, laz
	 */
	public float[] getLinearAcceleration() {
		return mLinearAcceleration;
	}

	/**
	 * 
	 * @return gx, gy, gz
	 */
	public float[] getGravity() {
		return mGravity;
	}

	public float getTemperature() {
		return mTemperature;
	}

	public float getLight() {
		return mLight;
	}

	public float getProximity() {
		return mProximity;
	}

	public float getPressure() {
		return mPressure;
	}

	/**
	 * 
	 * @return yaw, pitch, roll
	 */
	public float[] getOrientation() {
		return mOrientation;
	}

	// /**
	// *
	// * @return x, y, z
	// */
	// public float[] getAccelerometer() {
	// return mAccelerometer;
	// }

	/**
	 * Saves in the StateModel format all sesnsors data (a lighter format).
	 */
	public void copyState(SensorSimulatorModel simulatorModel) {
		mTemperature = (float) simulatorModel.getTemperature().getReadTemp();
		mLight = (float) simulatorModel.getLight().getReadLight();
		mProximity = (float) simulatorModel.getProximity().getReadProximity();
		mPressure = (float) simulatorModel.getPressure().getReadPressure();

		LinearAccelerationModel linearAcceleration = simulatorModel
				.getLinearAcceleration();
		mLinearAcceleration[0] = (float) linearAcceleration
				.getReadLinearAccelerationX();
		mLinearAcceleration[1] = (float) linearAcceleration
				.getReadLinearAccelerationY();
		mLinearAcceleration[2] = (float) linearAcceleration
				.getReadLinearAccelerationZ();

		GravityModel gravity = simulatorModel.getGravity();
		mGravity[0] = (float) gravity.getReadGravityX();
		mGravity[1] = (float) gravity.getReadGravityY();
		mGravity[2] = (float) gravity.getReadGravityZ();

		OrientationModel orientation = simulatorModel.getOrientation();
		mOrientation[0] = (float) orientation.getReadYaw();
		mOrientation[1] = (float) orientation.getReadPitch();
		mOrientation[2] = (float) orientation.getReadRoll();

		// AccelerometerModel accelerometer = simulatorModel.getAccelerometer();
		// mAccelerometer[0] = accelerometer.getReadAccelerometerX();
		// mAccelerometer[1] = accelerometer.getReadAccelerometerY();
		// mAccelerometer[2] = accelerometer.getReadAccelerometerZ();
	}

	/**
	 * 
	 * @return A string containing all sensors values from the current state.
	 */

	public String getSensorsValues() {

		StringBuffer sb = new StringBuffer();
		// sb.append(SensorModel.ACCELEROMETER + ":"
		// + Global.TWO_DECIMAL_FORMAT.format(mAccelerometer[0]) + ", "
		// + Global.TWO_DECIMAL_FORMAT.format(mAccelerometer[1]) + ", "
		// + Global.TWO_DECIMAL_FORMAT.format(mAccelerometer[2]) + "\n");
		sb.append(SensorModel.ORIENTATION + ":"
				+ Global.TWO_DECIMAL_FORMAT.format(mOrientation[0]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mOrientation[1]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mOrientation[2]) + "\n");
		sb.append(SensorModel.TEMPERATURE + ":"
				+ Global.TWO_DECIMAL_FORMAT.format(mTemperature) + "\n");
		sb.append(SensorModel.LIGHT + ":"
				+ Global.TWO_DECIMAL_FORMAT.format(mLight) + "\n");
		sb.append(SensorModel.PROXIMITY + ":"
				+ Global.TWO_DECIMAL_FORMAT.format(mProximity) + "\n");
		sb.append(SensorModel.PRESSURE + ":"
				+ Global.TWO_DECIMAL_FORMAT.format(mPressure) + "\n");
		sb.append(SensorModel.LINEAR_ACCELERATION + ":"
				+ Global.TWO_DECIMAL_FORMAT.format(mLinearAcceleration[0])
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mLinearAcceleration[1])
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mLinearAcceleration[2])
				+ "\n");
		sb.append(SensorModel.GRAVITY + ":"
				+ Global.TWO_DECIMAL_FORMAT.format(mGravity[0]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mGravity[1]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mGravity[2]));
		return sb.toString();
	}

	public void fillSensor(String sensorName, float[] sensorValues) {
		if (sensorName.equals(SensorModel.TEMPERATURE)) {
			mTemperature = sensorValues[0];
		} else if (sensorName.equals(SensorModel.LIGHT)) {
			mLight = sensorValues[0];
		} else if (sensorName.equals(SensorModel.PROXIMITY)) {
			mProximity = sensorValues[0];
		} else if (sensorName.equals(SensorModel.PRESSURE)) {
			mPressure = sensorValues[0];
		} else if (sensorName.equals(SensorModel.LINEAR_ACCELERATION)) {
			mLinearAcceleration = sensorValues;
		} else if (sensorName.equals(SensorModel.GRAVITY)) {
			mGravity = sensorValues;
		}
	}

	public HashMap<String, float[]> getStateSensors() {
		HashMap<String, float[]> stateSensors = new HashMap<String, float[]>();
		stateSensors.put(SensorModel.TEMPERATURE, new float[] { mTemperature });
		stateSensors.put(SensorModel.LIGHT, new float[] { mLight });
		stateSensors.put(SensorModel.PROXIMITY, new float[] { mProximity });
		stateSensors.put(SensorModel.PRESSURE, new float[] { mPressure });
		stateSensors.put(SensorModel.LINEAR_ACCELERATION, mLinearAcceleration);
		stateSensors.put(SensorModel.GRAVITY, mGravity);
		return stateSensors;
	}

	public void fillLinearValues(StateModel s1, StateModel s2, int k, int n) {
		int n_k = n - k;
		mTemperature = (n_k * s1.getTemperature() + k * s2.getTemperature())
				/ n;
		mLight = (n_k * s1.getLight() + k * s2.getLight()) / n;
		mProximity = (n_k * s1.getProximity() + k * s2.getProximity()) / n;
		mPressure = (n_k * s1.getPressure() + k * s2.getPressure()) / n;
	}

	public float getTime() {
		return mTime;
	}

	public void setTime(float value) {
		mTime = value;
	}

	public static StateModel fromHashTable(Hashtable<Integer, float[]> sensors) {
		if (sensors.size() == 0)
			return null;
		StateModel result = new StateModel();
		for (Entry<Integer, float[]> sensor : sensors.entrySet()) {
			fillSensor(result, sensor.getKey(), sensor.getValue());
		}
		return result;
	}

	private static void fillSensor(StateModel state, int sensorType,
			float[] values) {
		switch (sensorType) {
		case SensorModel.TYPE_TEMPERATURE:
			state.mTemperature = values[0];
			break;
		case SensorModel.TYPE_LIGHT:
			state.mLight = values[0];
			break;
		case SensorModel.TYPE_PROXIMITY:
			state.mProximity = values[0];
			break;
		case SensorModel.TYPE_PRESSURE:
			state.mPressure = values[0];
			break;
		case SensorModel.TYPE_LINEAR_ACCELERATION:
			state.mLinearAcceleration = values;
			break;
		case SensorModel.TYPE_GRAVITY:
			state.mGravity = values;
			break;
		default:
			break;
		}
	}
}
