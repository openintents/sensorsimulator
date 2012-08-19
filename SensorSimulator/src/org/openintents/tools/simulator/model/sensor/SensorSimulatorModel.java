/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2011 OpenIntents.org
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

/*
 * 09/Apr/08 Dale Thatcher <openintents at dalethatcher dot com>
 *           Added wii-mote data collection.
 */

package org.openintents.tools.simulator.model.sensor;

import java.util.ArrayList;

import org.openintents.tools.simulator.SensorServer;
import org.openintents.tools.simulator.SensorSimulator;
import org.openintents.tools.simulator.SensorsScenario;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.BarcodeReaderModel;
import org.openintents.tools.simulator.model.sensor.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensor.sensors.GyroscopeModel;
import org.openintents.tools.simulator.model.sensor.sensors.LightModel;
import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.MagneticFieldModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.PressureModel;
import org.openintents.tools.simulator.model.sensor.sensors.ProximityModel;
import org.openintents.tools.simulator.model.sensor.sensors.RotationVectorModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.TemperatureModel;

/**
 * SensorSimulatorModel keeps the internal data model behind SensorSimulator.
 * 
 * SensorSimulator simulates various sensors. An Android application can connect
 * through TCP/IP with the settings shown to the SensorSimulator to simulate
 * accelerometer, compass, orientation sensor, and thermometer.
 * 
 * @author Peli
 * @author Josip Balic
 */
public class SensorSimulatorModel {
	private int mSensorsPort;

	private float mUpdate;
	private long mRefreshCount;

	@SuppressWarnings("unused")
	private double mRefreshSensors;

	// for measuring updates:
	private int mUpdateSensorCount;
	private long mUpdateSensorTime;

	/**
	 * Duration in milliseconds until user setting changes are read out.
	 */
	private long mUserSettingsDuration;

	/**
	 * Time of next update for reading user settings from widgets. The time is
	 * compared to System.currentTimeMillis().
	 */
	private long mUserSettingsNextUpdate;

	// sensors
	private ArrayList<SensorModel> mSensors;

	// Server for sending out sensor data
	private SensorServer mSensorServer;
	private int mIncomingConnections;

	// Simulation delay:
	private int mDelay;
	private SensorSimulator mSensorSimulator;

	public SensorSimulatorModel(SensorSimulator sensorSimulator) {
		mSensorSimulator = sensorSimulator;
		// Initialize variables
		mSensorsPort = 8010;
		mDelay = 500;

		mIncomingConnections = 0;
		mUpdate = 10;

		// sensors
		mSensors = new ArrayList<SensorModel>();
		mSensors.add(new AccelerometerModel());
		mSensors.add(new MagneticFieldModel());
		mSensors.add(new OrientationModel());
		mSensors.add(new TemperatureModel());
		mSensors.add(new BarcodeReaderModel());
		mSensors.add(new LightModel());
		mSensors.add(new ProximityModel());
		mSensors.add(new PressureModel());
		mSensors.add(new LinearAccelerationModel());
		mSensors.add(new GravityModel());
		mSensors.add(new RotationVectorModel());
		mSensors.add(new GyroscopeModel());

		mSensorServer = new SensorServer(sensorSimulator);

		mUserSettingsDuration = 500; // Update every half second. This should
		// be enough.
		mUserSettingsNextUpdate = System.currentTimeMillis(); // First update
		// is now.

		// Variables for timing:
		mUpdateSensorCount = 0;
		mUpdateSensorTime = System.currentTimeMillis();
	}

	/**
	 * This method is called by SensorServerThread when a new client connects.
	 */
	public void newClient() {
		mIncomingConnections++;
	}

	public MagneticFieldModel getMagneticField() {
		return (MagneticFieldModel) mSensors
				.get(SensorModel.POZ_MAGNETIC_FIELD);
	}

	public TemperatureModel getTemperature() {
		return (TemperatureModel) mSensors.get(SensorModel.POZ_TEMPERATURE);
	}

	public BarcodeReaderModel getBarcodeReader() {
		return (BarcodeReaderModel) mSensors
				.get(SensorModel.POZ_BARCODE_READER);
	}

	public LightModel getLight() {
		return (LightModel) mSensors.get(SensorModel.POZ_LIGHT);
	}

	public ProximityModel getProximity() {
		return (ProximityModel) mSensors.get(SensorModel.POZ_PROXIMITY);
	}

	public AccelerometerModel getAccelerometer() {
		return (AccelerometerModel) mSensors.get(SensorModel.POZ_ACCELEROMETER);
	}

	public OrientationModel getOrientation() {
		return (OrientationModel) mSensors.get(SensorModel.POZ_ORIENTATION);
	}

	public ArrayList<SensorModel> getSensors() {
		return mSensors;
	}

	public int getSimulationPort() {
		return mSensorsPort;
	}

	public void stopSensorServer() {
		mSensorServer.stop();
	}

	public void restartSensorServer() {
		mSensorServer.stop();
		mSensorServer = new SensorServer(mSensorSimulator);
	}

	public double getUpdateSensors() {
		return mUpdate;
	}

	public int getDelay() {
		return mDelay;
	}

	public int incUpdateSensorCount() {
		return ++mUpdateSensorCount;
	}

	public long getRefreshCount() {
		return mRefreshCount;
	}

	public long getUpdateSensorTime() {
		return mUpdateSensorTime;
	}

	public void setUpdateSensorTime(long newVal) {
		mUpdateSensorTime = newVal;
	}

	public void setUpdateSensorCount(int newVal) {
		mUpdateSensorCount = newVal;
	}

	public void setRefreshSensors(double ms) {
		mRefreshSensors = ms;
	}

	public long getNextUpdate() {
		return mUserSettingsNextUpdate;
	}

	public double getDuration() {
		return mUserSettingsDuration;
	}

	public void addNextUpdate(double duration) {
		mUserSettingsNextUpdate += duration;
	}

	public void setNextUpdate(long ms) {
		mUserSettingsNextUpdate = ms;
	}

	public void setDelay(int newdelay) {
		mDelay = newdelay;
	}

	public PressureModel getPressure() {
		return (PressureModel) mSensors.get(SensorModel.POZ_PRESSURE);
	}

	public LinearAccelerationModel getLinearAcceleration() {
		return (LinearAccelerationModel) mSensors
				.get(SensorModel.POZ_LINEAR_ACCELERATION);
	}

	public GravityModel getGravity() {
		return (GravityModel) mSensors.get(SensorModel.POZ_GRAVITY);
	}

	public RotationVectorModel getRotationVector() {
		return (RotationVectorModel) mSensors.get(SensorModel.POZ_ROTATION);
	}

	public GyroscopeModel getGyroscope() {
		return (GyroscopeModel) mSensors.get(SensorModel.POZ_GYROSCOPE);
	}

	public SensorsScenario getScenario() {
		return mSensorSimulator.scenario;
	}

	public void loadState(StateModel state) {
		// simple
		TemperatureModel temperatureModel = getTemperature();
		temperatureModel.setTemp(state.getTemperature());

		LightModel lightModel = getLight();
		lightModel.setLight(state.getLight());

		ProximityModel proximityModel = getProximity();
		proximityModel.setProximity(state.getProximity());

		PressureModel pressureModel = getPressure();
		pressureModel.setPressure(state.getPressure());

		// complex
		GravityModel gravityModel = getGravity();
		gravityModel.setGravity(state.getGravity());

		LinearAccelerationModel linearAccModel = getLinearAcceleration();
		linearAccModel.setLinearAcceleration(state.getLinearAcceleration());

		OrientationModel orientationModel = getOrientation();
		orientationModel.setOrientation(state.getOrientation());

		AccelerometerModel accelerometerModel = getAccelerometer();
		accelerometerModel.setAccelerometer(state.getAccelerometer());

		MagneticFieldModel magneticFieldModel = getMagneticField();
		magneticFieldModel.setMagneticField(state.getMagneticField());

		RotationVectorModel rotationVectorModel = getRotationVector();
		rotationVectorModel.setRotationVector(state.getRotationVector());

		GyroscopeModel gyroscopeModel = getGyroscope();
		gyroscopeModel.setGyroscope(state.getGyroscope());
	}
}
