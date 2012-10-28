package org.openintents.sensorsimulator.hardware;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorEventListenerWrapper implements
		android.hardware.SensorEventListener {

	private static final String TAG = "SensorEventListener";
	private SensorEventListener listener;
	private SensorManager sensorManager;
	private Sensor sensor;

	public SensorEventListenerWrapper(SensorEventListener listener,
			SensorManager sensorManager) {
		this.listener = listener;
		this.sensorManager = sensorManager;
		sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	public void realAPI() {
		Log.d(TAG, "switching to real API");
		// register this to sensorManager
	}

	public void fakeAPI() {
		Log.d(TAG, "switching to fake API");
		// unregister this from sensorManager
	}

	public boolean registerListener(
			org.openintents.sensorsimulator.hardware.Sensor sensor, int delay) {
		// add sensor to the list of sensors to register to
		return false;
	}

	public void unregisterListener(
			org.openintents.sensorsimulator.hardware.Sensor sensor) {
		// remove sensor from the list of sensors to register to
	}

	public void unregisterListener() {
		// TODO Auto-generated method stub

	}

	public SensorEventListener getListener() {
		return listener;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// forward to listener
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// forward to listener
	}

}
