package org.openintents.hardware;

import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Looper;

public class SensorManagerSimulator {


	/**
	 * TAG for logging.
	 */
	private static final String TAG = "SensorManagerSimulator";
	
	/**
	 * Client that communicates with the SensorSimulator application.
	 */
	public static SensorSimulatorClient mClient = new SensorSimulatorClient();
	
	private SensorManager mSensorManager = null;
	
	public SensorManagerSimulator(SensorManager systemsensormanager) {
		mSensorManager = systemsensormanager;
	}
	
	
	public int getSensors() {
		if (mClient.connected) {
			return mClient.getSensors();
		} else {
			return mSensorManager.getSensors();
		}
	}

	
	public boolean registerListener(SensorListener listener, int sensors, int rate) {
		if (mClient.connected) {
			return mClient.registerListener(listener, sensors, rate);
		} else {
			return mSensorManager.registerListener(listener, sensors, rate);
		}
	}

	
	public boolean registerListener(SensorListener listener, int sensors) {
		if (mClient.connected) {
			return mClient.registerListener(listener, sensors);
		} else {
			return mSensorManager.registerListener(listener, sensors);
		}
	}

	
	public void unregisterListener(SensorListener listener, int sensors) {
		if (mClient.connected) {
			mClient.unregisterListener(listener, sensors);
		} else {
			mSensorManager.unregisterListener(listener, sensors);
		}
	}

	
	public void unregisterListener(SensorListener listener) {
		if (mClient.connected) {
			mClient.unregisterListener(listener);
		} else {
			mSensorManager.unregisterListener(listener);
		}
	}
	

	//  Member function extensions:
	/**
	 * Connect to the Sensor Simulator.
	 * (All the settings should have been set already.)
	 */
	public static void connectSimulator() {
		mClient.connect();
	};
	
	/**
	 * Disconnect from the Sensor Simulator.
	 */
	public static void disconnectSimulator() {
		mClient.disconnect();
	}

}
