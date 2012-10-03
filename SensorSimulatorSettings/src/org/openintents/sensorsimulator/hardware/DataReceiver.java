package org.openintents.sensorsimulator.hardware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.SparseArray;

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
	// private Dispatcher mAccelerometerDispatcher;
	private SparseArray<Dispatcher> mDispatchers;
	private boolean mConnected;
	private Thread mReceivingThread;
	private String mIpAdress;
	private int mPort;

	public DataReceiver(String ipAdress, int port) {
		mDispatchers = // new HashMap<Integer, Dispatcher>();
		new SparseArray<Dispatcher>(12);
		mDispatchers.put(Sensor.TYPE_ACCELEROMETER, new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_GYROSCOPE, new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_LIGHT, new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_MAGNETIC_FIELD, new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_ORIENTATION, new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_PRESSURE, new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_PROXIMITY, new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_TEMPERATURE, new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_BARCODE_READER, new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_LINEAR_ACCELERATION,
				new TimestampDispatcher());
		mDispatchers.put(Sensor.TYPE_GRAVITY, new TimestampDispatcher());
		mDispatchers
				.put(Sensor.TYPE_ROTATION_VECTOR, new TimestampDispatcher());

		mIpAdress = ipAdress;
		mPort = port;

		mConnected = false;
	}

	@Override
	public void connect() {
		// because SensorSimulatorSettingsActivity checks connected state
		// immediately after calling connect()
		// TODO should be MVC (Observer) instead
		mConnected = true;
		for (int i = 0; i < mDispatchers.size(); i++)
			mDispatchers.valueAt(i).start();
		mReceivingThread = new Thread(mReceiving);
		mReceivingThread.start();
	}

	@Override
	public void disconnect() {
		// explained in connect()
		mConnected = false;
		for (int i = 0; i < mDispatchers.size(); i++)
			mDispatchers.valueAt(i).stop();
		mReceivingThread.interrupt();
	}

	@Override
	public boolean isConnected() {
		return mConnected;
	}

	@Override
	public ArrayList<Integer> getSensors() {
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
		Dispatcher dispatcher = mDispatchers.get(sensor.sensorToRegister);
		if (dispatcher != null) {
			dispatcher.addListener(listener, interval);

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
			Socket connection = null;
			DataInputStream in = null;
			DataOutputStream out = null;

			try {
				connection = new Socket(mIpAdress, mPort);
				// only block for some time so we can check interrupts
				connection.setSoTimeout(100);
				// TODO check if Bufferedstream would be faster
				in = new DataInputStream(connection.getInputStream());
				out = new DataOutputStream(connection.getOutputStream());

				mConnected = true;

				// read sensor events
				while (!Thread.interrupted()) {
					try {
						int type = in.readInt();
						int accuracy = in.readInt();
						long timestamp = in.readLong();
						int valLength = in.readInt();
						float[] values = new float[valLength];
						for (int i = 0; i < valLength; i++) {
							values[i] = in.readFloat();
						}

						mDispatchers.get(type).putEvent(
								new SensorEvent(type, accuracy, timestamp,
										values));
					} catch (SocketTimeoutException e) {
						// just try again
					}
				}

				// say goodbye to server
				out.writeInt(1);
			} catch (UnknownHostException e) {
				// wrong ip or port or server not started
				for (int i = 0; i < mDispatchers.size(); i++)
					mDispatchers.valueAt(i).stop();
				mConnected = false;
			} catch (EOFException e) {
				// server shut down connection
				for (int i = 0; i < mDispatchers.size(); i++)
					mDispatchers.valueAt(i).stop();
				mConnected = false;
			} catch (IOException e) {
				// some other crap happened
				e.printStackTrace();
			} finally {
				// cleanup
				try {
					if (out != null)
						out.close();
					if (in != null)
						in.close();
					if (connection != null)
						connection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void setServerAdress(String ipAdress, int port) {
		mIpAdress = ipAdress;
		mPort = port;
	}
}
