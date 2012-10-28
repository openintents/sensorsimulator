package org.openintents.sensorsimulator.hardware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.util.Log;
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
public class DataReceiver implements SensorDataReceiver, Observer {

	private static final String TAG = "DataReceiver";
	protected static final int PORT = 8111;
	private SparseArray<Dispatcher> mDispatchers;
	private boolean mConnected;
	private Thread mReceivingThread;

	public DataReceiver() {
		mDispatchers = new SparseArray<Dispatcher>(12);
		mDispatchers.put(Sensor.TYPE_ACCELEROMETER, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_GYROSCOPE, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_LIGHT, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_MAGNETIC_FIELD, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_ORIENTATION, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_PRESSURE, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_PROXIMITY, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_TEMPERATURE, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_BARCODE_READER, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_LINEAR_ACCELERATION,
				new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_GRAVITY, new SequenceDispatcher());
		mDispatchers.put(Sensor.TYPE_ROTATION_VECTOR, new SequenceDispatcher());

		for (int i = 0; i < mDispatchers.size(); i++) {
			((SequenceDispatcher) mDispatchers.valueAt(i)).addObserver(this);
		}

		mConnected = false;
	}

	@Override
	public void connect() {
		// because SensorSimulatorSettingsActivity checks connected state
		// immediately after calling connect()
		// TODO should be MVC (Observer) instead
		mConnected = true;
		for (int i = 0; i < mDispatchers.size(); i++) {
			if (!mDispatchers.valueAt(i).hasStarted())
				mDispatchers.valueAt(i).start();
		}
		mReceivingThread = new Thread(mReceiving);
		mReceivingThread.start();
	}

	@Override
	public void disconnect() {

		// ignore call if not yet started
		if (mConnected) {
			// explained in connect()
			mConnected = false;
			for (int i = 0; i < mDispatchers.size(); i++)
				mDispatchers.valueAt(i).stop();
			mReceivingThread.interrupt();
		}
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
		Dispatcher dispatcher = mDispatchers.get(sensor.sensorToRegister);
		if (dispatcher != null) {
			dispatcher.removeListener(listener);
		}
	}

	@Override
	public void unregisterListener(SensorEventListener listener) {
		for (int i = 0; i < mDispatchers.size(); i++) {
			mDispatchers.valueAt(i).removeListener(listener);
		}
	}

	int mDispatcherCount = 0;

	@Override
	public synchronized void update(Observable observable, Object data) {
		mDispatcherCount++;
		if (mDispatcherCount == mDispatchers.size()) {
			this.notify();
		}
	}

	private Runnable mReceiving = new Runnable() {

		@Override
		public void run() {
			ServerSocket serverSocket = null;
			Socket connection = null;
			DataInputStream in = null;
			DataOutputStream out = null;

			try {
				serverSocket = new ServerSocket(PORT);
				serverSocket.setSoTimeout(100);

				// wait for clients to connect and start sending events
				while (!Thread.interrupted()) {

					try {
						connection = serverSocket.accept();
						connection.setSoTimeout(100);

						mConnected = true;

						in = new DataInputStream(connection.getInputStream());
						out = new DataOutputStream(connection.getOutputStream());

						boolean quit = false;

						// read commands from client and execute them
						while (!quit && !Thread.interrupted()) {

							try {
								int command = in.readInt();

								// play sequence
								if (command == 0) {

									int eventCount = in.readInt();

									// read sensor events
									for (int j = 0; j < eventCount
											&& !Thread.interrupted(); j++) {
										int type = in.readInt();
										int accuracy = in.readInt();
										long timestamp = in.readLong();
										int valLength = in.readInt();
										float[] values = new float[valLength];
										for (int i = 0; i < valLength; i++) {
											values[i] = in.readFloat();
										}

										mDispatchers.get(type).putEvent(
												new SensorEvent(type, accuracy,
														timestamp, values));
									}

									// start dispatching
									for (int i = 0; i < mDispatchers.size(); i++)
										((SequenceDispatcher) mDispatchers
												.valueAt(i)).play();

									// wait till all dispatchers finish
									synchronized (DataReceiver.this) {
										DataReceiver.this.wait();
										// reset
										mDispatcherCount = 0;
									}

									// tell server that its done
									out.writeInt(2);
								} else if (command == -1) {
									quit = true;
								}
							} catch (SocketTimeoutException e) {
								// just check if thread was interrupted in while
								// condition
							}
						}

						// clean up client
						if (out != null)
							out.close();
						if (in != null)
							in.close();
						if (connection != null)
							connection.close();
					} catch (SocketTimeoutException e) {
						// just check if thread was interrupted in while
						// condition
					}
				}

				// clean up
				for (int i = 0; i < mDispatchers.size(); i++)
					mDispatchers.valueAt(i).stop();

				if (serverSocket != null)
					serverSocket.close();

				mConnected = false;
			} catch (InterruptedException e) {
				// interrupted while dispatching
				e.printStackTrace();

			} catch (IOException e) {
				// some other crap happened
				e.printStackTrace();
			}
		}
	};
}
