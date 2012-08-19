/*
 * Copyright (C) 2011 OpenIntents.org
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
package org.openintents.tools.simulator.model;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensor.sensors.GyroscopeModel;
import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.MagneticFieldModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.RotationVectorModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.util.Interpolate;

/**
 * Represents the model for a scenario state.
 * Contains all sensors values in float or float[] format.
 * 
 * Supported sensors: temperature, light, proximity, pressure, gravity,
 * linear acceleration, orientation, accelerometer, magnetic field,
 * rotation vector, gyroscope.
 * @author ilarele
 *
 */
public class StateModel {

	// sensors supported for interpolation
	private float mTemperature;
	private float mLight;
	private float mProximity;
	private float mPressure;
	private float[] mGravity;
	private float[] mLinearAcceleration;
	// yaw, pitch, roll
	private float[] mOrientation;
	private float[] mAccelerometer;
	private float[] mMagneticField;
	private float[] mRotationVector;
	private float[] mGyroscope;

	public StateModel(StateModel model) {
		mTemperature = model.getTemperature();
		mLight = model.getLight();
		mProximity = model.getProximity();
		mPressure = model.getPressure();
		mGravity = model.getGravity().clone();
		mLinearAcceleration = model.getLinearAcceleration().clone();

		// not supported - used for drawing the device
		mOrientation = model.getOrientation().clone();
		mAccelerometer = model.getAccelerometer().clone();
		mMagneticField = model.getMagneticField().clone();
		mRotationVector = model.getRotationVector().clone();
		mGyroscope = model.getGyroscope().clone();
	}

	public StateModel(SensorSimulatorModel simulatorModel) {
		this();
		copyState(simulatorModel);
	}

	/**
	 * Empty contructor
	 */
	public StateModel() {
		mGravity = new float[3];
		mLinearAcceleration = new float[3];
		mOrientation = new float[3];
		mAccelerometer = new float[3];
		mMagneticField = new float[3];
		mRotationVector = new float[3];
		mGyroscope = new float[3];
	}

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

		AccelerometerModel accelerometer = simulatorModel.getAccelerometer();
		mAccelerometer[0] = (float) accelerometer.getReadAccelerometerX();
		mAccelerometer[1] = (float) accelerometer.getReadAccelerometerY();
		mAccelerometer[2] = (float) accelerometer.getReadAccelerometerZ();

		MagneticFieldModel magneticField = simulatorModel.getMagneticField();
		mMagneticField[0] = (float) magneticField.getReadCompassX();
		mMagneticField[1] = (float) magneticField.getReadCompassY();
		mMagneticField[2] = (float) magneticField.getReadCompassZ();

		RotationVectorModel rotationVector = simulatorModel.getRotationVector();
		mRotationVector[0] = (float) rotationVector.getReadRotationVectorX();
		mRotationVector[1] = (float) rotationVector.getReadRotationVectorY();
		mRotationVector[2] = (float) rotationVector.getReadRotationVectorZ();

		GyroscopeModel gyroscope = simulatorModel.getGyroscope();
		mGyroscope[0] = (float) gyroscope.getReadGyroscopePitch();
		mGyroscope[1] = (float) gyroscope.getReadGyroscopeYaw();
		mGyroscope[2] = (float) gyroscope.getReadGyroscopeRoll();
	}

	public void copyState(StateModel mOwnState) {
		mTemperature = mOwnState.getTemperature();
		mLight = mOwnState.getLight();
		mProximity = mOwnState.getProximity();
		mPressure = mOwnState.getPressure();

		mLinearAcceleration = mOwnState.getLinearAcceleration().clone();
		mGravity = mOwnState.getGravity().clone();
		mOrientation = mOwnState.getOrientation().clone();
		mAccelerometer = mOwnState.getAccelerometer().clone();
		mMagneticField = mOwnState.getMagneticField().clone();
		mRotationVector = mOwnState.getRotationVector().clone();
		mGyroscope = mOwnState.getGyroscope().clone();
	}

	/**
	 * 
	 * @return A string containing all sensors values from the current state.
	 */

	public String getSensorsValues() {
		StringBuffer sb = new StringBuffer();
		sb.append(SensorModel.ACCELEROMETER + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mAccelerometer[0]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mAccelerometer[1]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mAccelerometer[2]) + "\n");
		sb.append(SensorModel.MAGNETIC_FIELD + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mMagneticField[0]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mMagneticField[1]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mMagneticField[2]) + "\n");
		sb.append(SensorModel.ORIENTATION + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mOrientation[0]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mOrientation[1]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mOrientation[2]) + "\n");
		sb.append(SensorModel.TEMPERATURE + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mTemperature) + "\n");
		sb.append(SensorModel.LIGHT + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mLight) + "\n");
		sb.append(SensorModel.PROXIMITY + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mProximity) + "\n");
		sb.append(SensorModel.PRESSURE + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mPressure) + "\n");
		sb.append(SensorModel.LINEAR_ACCELERATION + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mLinearAcceleration[0])
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mLinearAcceleration[1])
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mLinearAcceleration[2])
				+ "\n");
		sb.append(SensorModel.GRAVITY + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mGravity[0]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mGravity[1]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mGravity[2]) + "\n");

		sb.append(SensorModel.ROTATION_VECTOR + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mRotationVector[0]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mRotationVector[1]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mRotationVector[2]) + "\n");
		sb.append(SensorModel.GYROSCOPE + ": "
				+ Global.TWO_DECIMAL_FORMAT.format(mGyroscope[0]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mGyroscope[1]) + ", "
				+ Global.TWO_DECIMAL_FORMAT.format(mGyroscope[2]));
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
		} else if (sensorName.equals(SensorModel.ACCELEROMETER)) {
			mAccelerometer = sensorValues;
		} else if (sensorName.equals(SensorModel.ORIENTATION)) {
			mOrientation = sensorValues;
		} else if (sensorName.equals(SensorModel.MAGNETIC_FIELD)) {
			mMagneticField = sensorValues;
		} else if (sensorName.equals(SensorModel.ROTATION_VECTOR)) {
			mRotationVector = sensorValues;
		} else if (sensorName.equals(SensorModel.GYROSCOPE)) {
			mGyroscope = sensorValues;
		}
	}

	public HashMap<String, float[]> getStateSensors() {
		HashMap<String, float[]> stateSensors = new HashMap<String, float[]>();
		stateSensors.put(SensorModel.TEMPERATURE, new float[] { mTemperature });
		stateSensors.put(SensorModel.LIGHT, new float[] { mLight });
		stateSensors.put(SensorModel.PROXIMITY, new float[] { mProximity });
		stateSensors.put(SensorModel.PRESSURE, new float[] { mPressure });
		stateSensors.put(SensorModel.ORIENTATION, mOrientation);
		stateSensors.put(SensorModel.ACCELEROMETER, mAccelerometer);
		stateSensors.put(SensorModel.LINEAR_ACCELERATION, mLinearAcceleration);
		stateSensors.put(SensorModel.GRAVITY, mGravity);
		stateSensors.put(SensorModel.MAGNETIC_FIELD, mMagneticField);
		stateSensors.put(SensorModel.ROTATION_VECTOR, mRotationVector);
		stateSensors.put(SensorModel.GYROSCOPE, mGyroscope);
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

	public void fillNonLinearValues(StateModel s1, StateModel s2, int k, int n) {
		float[] rightAxis = Interpolate.interpolate(
				s1.getRightAxisOrientation(), s2.getRightAxisOrientation(), k,
				n);
		mOrientation[0] = rightAxis[1]; // yaw
		mOrientation[1] = rightAxis[0]; // pitch
		mOrientation[2] = rightAxis[2]; // roll

		mAccelerometer = Interpolate.interpolate(s1.getAccelerometer(),
				s2.getAccelerometer(), k, n);
		mGravity = Interpolate.interpolate(s1.getGravity(), s2.getGravity(), k,
				n);
		mLinearAcceleration = Interpolate.interpolate(
				s1.getLinearAcceleration(), s2.getLinearAcceleration(), k, n);
		mMagneticField = Interpolate.interpolate(s1.getMagneticField(),
				s2.getMagneticField(), k, n);
		mRotationVector = Interpolate.interpolate(s1.getRotationVector(),
				s2.getRotationVector(), k, n);

		mGyroscope = Interpolate.interpolate(s1.getGyroscope(),
				s2.getGyroscope(), k, n);
	}

	public static StateModel getStateFromRecordedSensors(
			Hashtable<Integer, float[]> sensors) {
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
		case SensorModel.TYPE_ACCELEROMETER:
			state.mAccelerometer = values;
			break;
		case SensorModel.TYPE_ORIENTATION:
			state.mOrientation = values;
			break;
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
		case SensorModel.TYPE_MAGNETIC_FIELD:
			state.mMagneticField = values;
			break;
		case SensorModel.TYPE_ROTATION_VECTOR:
			state.mRotationVector = values;
			break;
		case SensorModel.TYPE_GYROSCOPE:
			state.mGyroscope = values;
			break;
		default:
			break;
		}
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
	 * @return pitch, yaw, roll
	 */
	public float[] getGravity() {
		return mGravity;
	}

	/**
	 * 
	 * @return lax, lay, laz
	 */
	public float[] getLinearAcceleration() {
		return mLinearAcceleration;
	}

	/**
	 * Orientation (in degree) Yaw = Rotation about the Y-Axis;Pitch = Rotation
	 * about the X-Axis; Roll = Rotation about the Z-Axis
	 * 
	 * @return yaw, pitch, roll
	 */
	public float[] getOrientation() {
		return mOrientation;
	}

	/**
	 * Orientation (in degree) Pitch = Rotation about the X-Axis; Yaw = Rotation
	 * about the Y-Axis; Roll = Rotation about the Z-Axis
	 * 
	 * @return pitch, yaw, roll
	 */
	public float[] getRightAxisOrientation() {
		return new float[] { mOrientation[1], mOrientation[0], mOrientation[2] };
	}

	/**
	 * 
	 * @return x, y, z
	 */
	public float[] getAccelerometer() {
		return mAccelerometer;
	}

	public float[] getMagneticField() {
		return mMagneticField;
	}

	public float[] getRotationVector() {
		return mRotationVector;
	}

	/**
	 * 
	 * @return pitch, yaw, roll
	 */
	public float[] getGyroscope() {
		return mGyroscope;
	}

}
