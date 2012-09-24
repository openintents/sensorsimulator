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

package org.openintents.tools.simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openintents.tools.simulator.comm.SensorServerThreadListener;
import org.openintents.tools.simulator.model.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensors.BarcodeReaderModel;
import org.openintents.tools.simulator.model.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensors.GyroscopeModel;
import org.openintents.tools.simulator.model.sensors.LightModel;
import org.openintents.tools.simulator.model.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensors.MagneticFieldModel;
import org.openintents.tools.simulator.model.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensors.PressureModel;
import org.openintents.tools.simulator.model.sensors.ProximityModel;
import org.openintents.tools.simulator.model.sensors.RotationVectorModel;
import org.openintents.tools.simulator.model.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensors.SensorType;
import org.openintents.tools.simulator.model.sensors.TemperatureModel;

/**
 * {@code SensorSimulatorModel} keeps the internal data model behind
 * SensorSimulator. It keeps a set of sensor models, which can be accessed
 * individually. It also provides the ability to load an entire sensor state,
 * which is a set of concrete values for each sensor.
 * <p>
 * The {@code SensorServerThreadListener} interface is implemented, so that
 * queries from Clients can be directly delegated to the data model.
 * 
 * @author Peli
 * @author Josip Balic
 */
public class SensorSimulatorModel implements SensorServerThreadListener {

	// sensors
	private Map<SensorType, SensorModel> mSensors;

	public SensorSimulatorModel() {

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
		mSensors.put(SensorType.GRAVITY, new GravityModel((OrientationModel) mSensors.get(SensorType.ORIENTATION)));
		mSensors.put(SensorType.ROTATION, new RotationVectorModel((OrientationModel) mSensors.get(SensorType.ORIENTATION)));
		mSensors.put(SensorType.GYROSCOPE, new GyroscopeModel());
	}

	/**
	 * Loads a state into the data model. A state consists of a value set for
	 * all sensors.
	 * 
	 * @param state
	 *            the state to load
	 */
	public void loadState(SensorState state) {
		// simple
		TemperatureModel temperatureModel = (TemperatureModel) getSensorModel(SensorType.TEMPERATURE);
		temperatureModel.setTemp(state.getTemperature());

		LightModel lightModel = (LightModel) getSensorModel(SensorType.LIGHT);
		lightModel.setLight(state.getLight());

		ProximityModel proximityModel = (ProximityModel) getSensorModel(SensorType.PROXIMITY);
		proximityModel.setProximity(state.getProximity());

		PressureModel pressureModel = (PressureModel) getSensorModel(SensorType.PRESSURE);
		pressureModel.setPressure(state.getPressure());

		// complex
		GravityModel gravityModel = (GravityModel) getSensorModel(SensorType.GRAVITY);
		gravityModel.setGravity(state.getGravity());

		LinearAccelerationModel linearAccModel = (LinearAccelerationModel) getSensorModel(SensorType.LINEAR_ACCELERATION);
		linearAccModel.setLinearAcceleration(state.getLinearAcceleration());

		OrientationModel orientationModel = (OrientationModel) getSensorModel(SensorType.ORIENTATION);
		orientationModel.setOrientation(state.getOrientation());

		AccelerometerModel accelerometerModel = (AccelerometerModel) getSensorModel(SensorType.ACCELEROMETER);
		accelerometerModel.setAccelerometer(state.getAccelerometer());

		MagneticFieldModel magneticFieldModel = (MagneticFieldModel) getSensorModel(SensorType.MAGNETIC_FIELD);
		magneticFieldModel.setMagneticField(state.getMagneticField());

		RotationVectorModel rotationVectorModel = (RotationVectorModel) getSensorModel(SensorType.ROTATION);
		rotationVectorModel.setRotationVector(state.getRotationVector());

		GyroscopeModel gyroscopeModel = (GyroscopeModel) getSensorModel(SensorType.GYROSCOPE);
		gyroscopeModel.setGyroscope(state.getGyroscope());
	}

	/**
	 * Returns the model component of a sensor by name.
	 * 
	 * @param sensorName
	 *            name of the model component to be returned
	 * @return model component of the specified sensor
	 */
	public SensorModel getSensorModel(SensorType sensorType) {
		return mSensors.get(sensorType);
	}

	// ////////////////////////////////////////////////////
	// SensorServerThreadListener methods
	// ////////////////////////////////////////////////////
	@Override
	public String[] getSupportedSensors() {
		ArrayList<String> resultArray = new ArrayList<String>();
		for (Map.Entry<SensorType, SensorModel> sensorEntry : mSensors.entrySet()) {
			if (sensorEntry.getValue().isEnabled()) {
				resultArray.add(sensorEntry.getValue().getName());
			}
		}
		return resultArray.toArray(new String[resultArray.size()]);
	}

	@Override
	public int getNumSensorValues(SensorType sensorType) {
		return getSensorModel(sensorType).getNumSensorValues();
	}

	@Override
	public void setSensorUpdateDelay(SensorType sensorType, int updateDelay) throws IllegalArgumentException {
		SensorModel sensorModel = getSensorModel(sensorType);
		if (sensorModel.isEnabled())
			sensorModel.setCurrentUpdateDelay(updateDelay);
		else
			throw new IllegalArgumentException();		
	}

	@Override
	public void unsetSensorUpdateRate(SensorType sensorType) throws IllegalStateException {
		SensorModel sensorModel = getSensorModel(sensorType);
		if (sensorModel.isEnabled()) {
			sensorModel.setCurrentUpdateDelay(SensorModel.DELAY_MS_NORMAL);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String readSensor(SensorType sensorType) {
		SensorModel sensorModel = getSensorModel(sensorType);
		if (sensorModel.isEnabled()) {
			return sensorModel.printData();
		} else {
			throw new IllegalStateException();
		}
	}
}
