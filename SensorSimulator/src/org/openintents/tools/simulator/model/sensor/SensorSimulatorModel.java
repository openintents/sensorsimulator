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
import java.util.HashMap;
import java.util.Map;

import org.openintents.tools.simulator.SensorSimulator;
import org.openintents.tools.simulator.SensorsScenario;
import org.openintents.tools.simulator.comm.SensorServer;
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
import org.openintents.tools.simulator.model.sensor.sensors.SensorType;
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

	// sensors
	private Map<SensorType, SensorModel> mSensors;

	// Simulation delay:
	private SensorSimulator mSensorSimulator;

	public SensorSimulatorModel(SensorSimulator sensorSimulator) {
		mSensorSimulator = sensorSimulator;

		// new sensors
		mSensors = new HashMap<SensorType, SensorModel>();
		mSensors.put(SensorType.ACCELEROMETER, new AccelerometerModel());
		mSensors.put(SensorType.MAGNETIC_FIELD, new MagneticFieldModel());
		mSensors.put(SensorType.ORIENTATION, new OrientationModel());
		mSensors.put(SensorType.TEMPERATURE, new TemperatureModel());
		mSensors.put(SensorType.BARCODE_READER, new BarcodeReaderModel());
		mSensors.put(SensorType.LIGHT, new LightModel());
		mSensors.put(SensorType.PROXIMITY, new ProximityModel());
		mSensors.put(SensorType.PRESSURE, new PressureModel());
		mSensors.put(SensorType.LINEAR_ACCELERATION, new LinearAccelerationModel());
		mSensors.put(SensorType.GRAVITY, new GravityModel());
		mSensors.put(SensorType.ROTATION, new RotationVectorModel());
		mSensors.put(SensorType.GYROSCOPE, new GyroscopeModel());
	}

	public Map <SensorType, SensorModel> getSensors() {
		return mSensors;
	}

	public SensorsScenario getScenario() {
		return mSensorSimulator.scenario;
	}

	public void loadState(StateModel state) {
		// simple
		TemperatureModel temperatureModel = (TemperatureModel) getSensorModelFromName(SensorType.TEMPERATURE);
		temperatureModel.setTemp(state.getTemperature());

		LightModel lightModel = (LightModel) getSensorModelFromName(SensorType.LIGHT);
		lightModel.setLight(state.getLight());

		ProximityModel proximityModel = (ProximityModel) getSensorModelFromName(SensorType.PROXIMITY);
		proximityModel.setProximity(state.getProximity());

		PressureModel pressureModel = (PressureModel) getSensorModelFromName(SensorType.PRESSURE);
		pressureModel.setPressure(state.getPressure());

		// complex
		GravityModel gravityModel = (GravityModel) getSensorModelFromName(SensorType.GRAVITY);
		gravityModel.setGravity(state.getGravity());

		LinearAccelerationModel linearAccModel = (LinearAccelerationModel) getSensorModelFromName(SensorType.LINEAR_ACCELERATION);
		linearAccModel.setLinearAcceleration(state.getLinearAcceleration());

		OrientationModel orientationModel = (OrientationModel) getSensorModelFromName(SensorType.ORIENTATION);
		orientationModel.setOrientation(state.getOrientation());

		AccelerometerModel accelerometerModel = (AccelerometerModel) getSensorModelFromName(SensorType.ACCELEROMETER);
		accelerometerModel.setAccelerometer(state.getAccelerometer());

		MagneticFieldModel magneticFieldModel = (MagneticFieldModel) getSensorModelFromName(SensorType.MAGNETIC_FIELD);
		magneticFieldModel.setMagneticField(state.getMagneticField());

		RotationVectorModel rotationVectorModel = (RotationVectorModel) getSensorModelFromName(SensorType.ROTATION);
		rotationVectorModel.setRotationVector(state.getRotationVector());

		GyroscopeModel gyroscopeModel = (GyroscopeModel) getSensorModelFromName(SensorType.GYROSCOPE);
		gyroscopeModel.setGyroscope(state.getGyroscope());
	}

	/**
	 * Returns the model component of a sensor by name.
	 * 
	 * @param sensorName
	 *            name of the model component to be returned
	 * @return model component of the specified sensor
	 */
	public SensorModel getSensorModelFromName(SensorType sensorType) {
		return mSensors.get(sensorType);
	}

	/**
	 * Method used to get currently String[] of currently supported sensors.
	 * 
	 * @return String[] filled with names of currently supported sensors.
	 */
	public String[] getSupportedSensors() {
		ArrayList<String> resultArray = new ArrayList<String>();
		for (Map.Entry<SensorType, SensorModel> sensorEntry : getSensors().entrySet()) {
			if (sensorEntry.getValue().isEnabled()) {
				resultArray.add(sensorEntry.getValue().getName());
			}
		}
		return resultArray.toArray(new String[resultArray.size()]);
	}
}
