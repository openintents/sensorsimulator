package com.openintents.sensorsimulator.testlibrary;

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

	private SensorDataSender mDataSender;
	private SequenceLoader mSequenceLoader;

	public SensorTester() {
		mDataSender = new SensorDataSender();
		mSequenceLoader = new SequenceLoader();
	}

	/**
	 * Start internal server and do some initializing work.
	 */
	public void connect() {
		mDataSender.connect();
	}

	public void disconnect() {
		mDataSender.disconnect();
	}

	/**
	 * Send a shake event, consisting of a sequence of sensor events, which were
	 * recorded during shaking a real device.
	 */
	public boolean shake() {
		// read shake sensor data from somewhere
		List<SensorEventContainer> shakeSequence = mSequenceLoader
				.loadFromFile("lesequence");
		// send to client
		if (mDataSender.sendSensorEvents(shakeSequence))
			return true;

		return false;
	}
}
