package com.openintents.sensorsimulator.testlibrary;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * <code>SensorTester</code> provides a Test Project with the ability to send
 * sensor sequences (e.g. 'shake') to the SensorSimulator powered
 * app-under-test.
 * <p>
 * Usage:
 * <ol>
 * <li>initialize
 * <li>wait for callback (connection established)
 * <li>send events
 * </ol>
 * <p>
 * 
 * @author Qui Don Ho
 * 
 */
public class SensorTester implements Observer {

	private SensorDataSender mDataSender;
	private boolean connected;
	private SequenceLoader mSequenceLoader;

	/**
	 * Start internal server and do some initializing work.
	 */
	public void start() {
		mDataSender = new SensorDataSender();
		mDataSender.addObserver(this);
		mSequenceLoader = new SequenceLoader();
		mDataSender.start();
	}

	public void stop() {
		mDataSender.stop();
	}

	/**
	 * Wait for client to connect.
	 */
	public void waitUntilConnected() {
		synchronized (this) {
			try {
				while (!connected) {
					this.wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	@Override
	public synchronized void update(Observable observable, Object data) {
		if (observable == mDataSender) {
			connected = true;
			this.notifyAll();
		}
	}
}
