/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2010 OpenIntents.org
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

import org.openintents.tools.simulator.SensorSimulator;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.BarcodeReaderModel;
import org.openintents.tools.simulator.model.sensor.sensors.LightModel;
import org.openintents.tools.simulator.model.sensor.sensors.MagneticFieldModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.PressureModel;
import org.openintents.tools.simulator.model.sensor.sensors.ProximityModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.TemperatureModel;

/**
 * Class of SensorSimulator.
 * 
 * The SensorSimulator is a Java stand-alone application.
 * 
 * It simulates various sensors. An Android application can connect through
 * TCP/IP with the settings shown to the SensorSimulator to simulate
 * accelerometer, compass, orientation sensor, and thermometer.
 * 
 * @author Peli
 * @author Josip Balic
 */
public class SensorSimulatorModel {
	static String sendGPS = "send gps";
	static String recordReplay = "replay Record";
	static String playbackReplay = "replay Playback";
	static String emulateBattery = "emulate battery";
	static String nextTimeEvent = "next Time Event";

	private int sensorsPort;

	private float mUpdate;
	private long mRefreshCount;

	private double mRefreshSensors;

	// for measuring updates:
	private int updateSensorCount;
	private long updateSensorTime;

	/**
	 * Duration in milliseconds until user setting changes are read out.
	 */
	private long userSettingsDuration;

	/**
	 * Time of next update for reading user settings from widgets. The time is
	 * compared to System.currentTimeMillis().
	 */
	private long userSettingsNextUpdate;

	// sensors
	private ArrayList<SensorModel> sensors;

	// Server for sending out sensor data
	private SensorServer mSensorServer;
	private int mIncomingConnections;

	// Simulation delay:
	private int delay;
	private SensorSimulator mSensorSimulator;

	public SensorSimulatorModel(SensorSimulator sensorSimulator) {
		mSensorSimulator = sensorSimulator;
		// Initialize variables
		sensorsPort = 8010;
		delay = 500;

		mIncomingConnections = 0;
		mUpdate = 10;

		// sensors
		sensors = new ArrayList<SensorModel>();
		sensors.add(new AccelerometerModel());
		sensors.add(new MagneticFieldModel());
		sensors.add(new OrientationModel());
		sensors.add(new TemperatureModel());
		sensors.add(new BarcodeReaderModel());
		sensors.add(new LightModel());
		sensors.add(new ProximityModel());
		sensors.add(new PressureModel());
		
		mSensorServer = new SensorServer(sensorSimulator);

		userSettingsDuration = 500; // Update every half second. This should
		// be enough.
		userSettingsNextUpdate = System.currentTimeMillis(); // First update
		// is now.
		

		// Variables for timing:
		updateSensorCount = 0;
		updateSensorTime = System.currentTimeMillis();
	}

	/**
	 * This method is called by SensorServerThread when a new client connects.
	 */
	public void newClient() {
		mIncomingConnections++;
	}

	public MagneticFieldModel getMagneticField() {
		return (MagneticFieldModel) sensors.get(SensorModel.POZ_MAGNETIC_FIELD);
	}

	public TemperatureModel getTemperature() {
		return (TemperatureModel) sensors.get(SensorModel.POZ_TEMPERATURE);
	}

	public BarcodeReaderModel getBarcodeReader() {
		return (BarcodeReaderModel) sensors.get(SensorModel.POZ_BARCODE_READER);
	}

	public LightModel getLight() {
		return (LightModel) sensors.get(SensorModel.POZ_LIGHT);
	}

	public ProximityModel getProximity() {
		return (ProximityModel) sensors.get(SensorModel.POZ_PROXIMITY);
	}

	public AccelerometerModel getAccelerometer() {
		return (AccelerometerModel) sensors.get(SensorModel.POZ_ACCELEROMETER);
	}

	public OrientationModel getOrientation() {
		return (OrientationModel) sensors.get(SensorModel.POZ_ORIENTATION);
	}

	public ArrayList<SensorModel> getSensors() {
		return sensors;
	}

	public int getSimulationPort() {
		return sensorsPort;
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
		return delay;
	}

	public int incUpdateSensorCount() {
		return ++updateSensorCount;
	}

	public long getRefreshCount() {
		return mRefreshCount;
	}

	public long getUpdateSensorTime() {
		return updateSensorTime;
	}

	public void setUpdateSensorTime(long newVal) {
		updateSensorTime = newVal;
	}

	public void setUpdateSensorCount(int newVal) {
		updateSensorCount = newVal;
	}

	public void setRefreshSensors(double ms) {
		mRefreshSensors = ms;
	}

	public long getNextUpdate() {
		return userSettingsNextUpdate;
	}

	public double getDuration() {
		return userSettingsDuration;
	}

	public void addNextUpdate(double duration) {
		userSettingsNextUpdate += duration;
	}

	public void setNextUpdate(long ms) {
		userSettingsNextUpdate = ms;
	}

	public void setDelay(int newdelay) {
		delay = newdelay;
	}

	public PressureModel getPressure() {
		return (PressureModel) sensors.get(SensorModel.POZ_PRESSURE);
	}

}
