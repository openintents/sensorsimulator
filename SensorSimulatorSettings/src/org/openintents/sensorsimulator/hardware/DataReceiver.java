package org.openintents.sensorsimulator.hardware;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.content.Context;

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
		new Thread(mReceiving).start();
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

		String[] sensornames = new String[] { "accelerometer" };
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
		if (sensor.sensorToRegister == Sensor.TYPE_ACCELEROMETER) {
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

	private Runnable mReceiving = new Runnable() {

		@Override
		public void run() {
			try {
				mTemperatureDispatcher.start();

				Socket connection = new Socket("192.168.2.199", 8010);
				DataInputStream in = new DataInputStream(
						connection.getInputStream());

				while (true) {
					// read sensor event
					int type = in.readInt();
					int accuracy = in.readInt();
					long timestamp = in.readLong();
					int valLength = in.readInt();
					float[] values = new float[valLength];
					for (int i = 0; i < valLength; i++) {
						values[i] = in.readFloat();
					}

					mTemperatureDispatcher.putEvent(new SensorEvent(values,
							type));
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
