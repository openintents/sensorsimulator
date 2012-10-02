package org.openintents.sensorsimulator.hardware;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

/**
 * Client-side receiver which receives sensor events from sensor data provider
 * and sends them to the respective Listeners.
 * <p>
 * This class is responsible for all network related stuff.
 * 
 * @author Qui Don Ho
 * 
 */
public class DataReceiver implements SensorDataReceiver {

	// sensor dispatchers
	private Dispatcher mTemperatureDispatcher;
	private boolean mConnected;

	public DataReceiver(Context context) {
		mTemperatureDispatcher = new TimestampDispatcher(context);

		mConnected = false;
	}

	@Override
	public void connect() {
		mConnected = true;
		new Thread(fakeData).start();
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		mTemperatureDispatcher.stop();
		mConnected = false;
	}

	@Override
	public boolean isConnected() {
		return mConnected;
	}

	@Override
	public ArrayList<Integer> getSensors() {
		// TODO Auto-generated method stub

		String[] sensornames = new String[] { "temperature" };
		// Convert that array to ArrayList of integers.
		ArrayList<Integer> sensors = SensorNames
				.getSensorsFromNames(sensornames);

		return sensors;
	}

	@Override
	public boolean registerListener(SensorEventListener listener,
			Sensor sensor, int rate) {
		int interval; // in ms

		// TODO define intervals as constants somewhere else
		switch (rate) {
		case SensorManagerSimulator.SENSOR_DELAY_FASTEST:
			interval = 10;
			break;
		case SensorManagerSimulator.SENSOR_DELAY_GAME:
			interval = 20;
			break;
		case SensorManagerSimulator.SENSOR_DELAY_NORMAL:
			interval = 40;
			break;
		case SensorManagerSimulator.SENSOR_DELAY_UI:
			interval = 80;
			break;
		default:
			interval = rate; // as per Android Spec
		}

		// check sensor type and add to correct dispatcher
		if (sensor.sensorToRegister == Sensor.TYPE_TEMPERATURE) {
			mTemperatureDispatcher.addListener(listener, interval);

			return true;
		}
		return false;
	}

	@Override
	public void unregisterListener(SensorEventListener listener, Sensor sensor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterListener(SensorEventListener listener) {
		// TODO Auto-generated method stub

	}

	// TODO remove after testing
	private Runnable fakeData = new Runnable() {

		@Override
		public void run() {
			List<SensorEvent> events = new LinkedList<SensorEvent>();
			float[] values = new float[3];
			values[1] = 0f;
			values[2] = 0f;

			Random random = new Random(System.currentTimeMillis());

			for (int i = 0; i < 200; i++) {
				try {
					values[0] = random.nextFloat();
					events.add(new SensorEvent(values.clone(),
							Sensor.TYPE_TEMPERATURE));
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			mTemperatureDispatcher.putEvents(events);
			mTemperatureDispatcher.start();
		}
	};
}
