package org.openintents.sensorsimulator.hardware;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * Wrapper class for fake implementation of <code>SensorEventListener</code>
 * which registers itself with real sensor API to forward real sensor events
 * when in "real mode".
 * <p>
 * <code>realAPI()</code> and <code>fakeAPI</code> should be called when there
 * is or is no connection to a fake sensor data provider respectively.
 * 
 * @author Qui Don Ho
 * 
 */
public class SensorEventListenerWrapper implements
		android.hardware.SensorEventListener {

	private static final String TAG = "SensorEventListener";
	private SensorEventListener listener;
	private android.hardware.SensorManager sensorManager;
	private Map<Sensor, Integer> sensors;
	private SensorManagerSimulator sensorManagerSimulator;

	private boolean mIsFakeMode = false;

	public SensorEventListenerWrapper(SensorEventListener listener,
			android.hardware.SensorManager sensorManager,
			SensorManagerSimulator sensorManagerSimulator) {
		this.listener = listener;
		this.sensorManager = sensorManager;
		this.sensorManagerSimulator = sensorManagerSimulator;
		sensors = new HashMap<Sensor, Integer>();
	}

	// /////////////////////////////////////////////////////////////////////////
	// switch between real and fake API
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Switches to "real mode": Registers itself with the real sensor API and
	 * starts forwarding all incoming <code>SensorEvent</code>s to the fake
	 * <code>SensorEventListener</code>.
	 * 
	 * @return true, if all sensors could be successfully registered, false if
	 *         at least one could not
	 */
	public boolean realAPI() {
		Log.v(TAG, "Registering to real sensor API");

		mIsFakeMode = false;

		boolean success = true;

		// reset
		sensorManager.unregisterListener(this);

		// register this to all sensors
		for (Map.Entry<Sensor, Integer> entry : sensors.entrySet()) {
			success &= success
					& sensorManager.registerListener(this, sensorManager
							.getDefaultSensor(entry.getKey().getType()), entry
							.getValue());
		}

		return success;
	}

	/**
	 * Switches to "fake mode": Unregisters itself with from real sensor API.
	 */
	public void fakeAPI() {
		Log.v(TAG, "Unregistering from real sensor API");

		mIsFakeMode = true;

		// unregister this from all sensors
		sensorManager.unregisterListener(this);
	}

	// /////////////////////////////////////////////////////////////////////////
	// register and unregister from real API if necessary
	// /////////////////////////////////////////////////////////////////////////

	public boolean registerListener(Sensor sensor, int delay) {
		sensors.put(sensor, delay);

		if (mIsFakeMode)
			return true;
		else
			return realAPI();
	}

	public int unregisterListener(Sensor sensor) {
		// remove sensor from the list of sensors to register to
		sensors.remove(sensor);

		if (!mIsFakeMode) {
			realAPI();
		}

		return sensors.size();
	}

	public void unregisterListener() {
		sensors.clear();

		if (!mIsFakeMode) {
			realAPI();
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// SensorEventListener methods which just forward data to our implementation
	// /////////////////////////////////////////////////////////////////////////
	@Override
	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
		listener.onAccuracyChanged(
				sensorManagerSimulator.getDefaultSensor(sensor.getType()),
				accuracy);
	}

	@Override
	public void onSensorChanged(android.hardware.SensorEvent event) {
		listener.onSensorChanged(new SensorEvent(event.sensor.getType(),
				event.accuracy, event.timestamp, event.values));
	}
}
