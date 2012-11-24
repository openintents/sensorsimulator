package com.openintents.sensorsimulator.testlibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>SensorTester</code> provides a Test Project with the ability to send
 * sensor sequences (e.g. 'shake') to the SensorSimulator powered
 * app-under-test.
 * <p>
 * Usage:
 * <ol>
 * <li>connect
 * <li>send events
 * </ol>
 * <p>
 * 
 * @author Qui Don Ho
 * 
 */
public class SensorTester {

	private SequenceDataSender mDataSender;
	private SequenceLoader mSequenceLoader;
	private boolean mConnected;

	public SensorTester() {
		mDataSender = new SequenceDataSender();
		mSequenceLoader = new SequenceLoader();

		mConnected = false;
	}

	/**
	 * Start internal server and do some initializing work.
	 */
	public boolean connect() {
		mConnected = mDataSender.connect();
		return mConnected;
	}

	/**
	 * Start internal server and do some initializing work.
	 * 
	 * @return
	 */
	public boolean connect(String ip) {
		mConnected = mDataSender.connect(ip);
		return mConnected;
	}

	public void disconnect() {
		mDataSender.disconnect();
		mConnected = false;
	}

	/**
	 * Send a shake event, consisting of a sequence of sensor events, which were
	 * recorded during shaking a real device.
	 */
	public boolean shake() {
		// read shake sensor data from somewhere
		List<SensorEvent> shakeSequence = mSequenceLoader
				.loadGesture("shake");
		// send to client
		if (mDataSender.sendSensorEvents(shakeSequence))
			return true;

		return false;
	}

	/**
	 * Send a sequence loaded from a file.
	 */
	public boolean sendSequenceFile(String fileName) {
		// read shake sensor data from somewhere
		List<SensorEvent> sequence = mSequenceLoader
				.loadFile(fileName);
		// send to client
		if (mDataSender.sendSensorEvents(sequence))
			return true;

		return false;
	}

	public boolean isConnected() {
		return mConnected;
	}

	/**
	 * Set a sensor value.
	 * 
	 * @param sensor
	 * @param values
	 */
	public void setSensor(Sensor.Type sensor, float[] values) {
		ArrayList<SensorEvent> c;
		c = new ArrayList<SensorEvent>(1);
		c.add(new SensorEvent(Sensor.typeToInt(sensor), 1, System
				.currentTimeMillis(),
				values));
		mDataSender.sendSensorEvents(c);
	}
}
