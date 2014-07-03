package org.openintents.sensorsimulator.hardware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;

import android.util.Log;
import android.util.SparseArray;

/**
 * DataReceiver defines the methods for a client-side message broker,
 * which is responsible for accepting sensor data from a sensor data provider.
 * The data provider can be the SensorSimulator server component, or the JUnit
 * Testcases.
 * <p>
 * This class is responsible for all network related stuff.
 * 
 * @author Qui Don Ho
 */
class DataReceiver extends Observable {

	private static final String TAG = "DataReceiver";
	protected static final int PORT = 8111;

	private SparseArray<Dispatcher> mDispatchers;
	private Map<SensorEventListener, Map<Sensor, Integer>> mListenerMap;

	private boolean mConnected;
	private Thread mReceivingThread;

	// indicates if app wants to receive fake sensor data
	private boolean mHasStarted;

	public DataReceiver() {

		// map for listeners
		mListenerMap = new HashMap<SensorEventListener, Map<Sensor, Integer>>();

		mConnected = false;
	}

	private void setupSequenceDispatchers() {
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
	}

	private void setupContinuousDispatchers() {
		mDispatchers = new SparseArray<Dispatcher>(12);
		mDispatchers.put(Sensor.TYPE_ACCELEROMETER, new ContinuousDispatcher(
				500, 0));
		mDispatchers.put(Sensor.TYPE_GYROSCOPE,
				new ContinuousDispatcher(500, 0));
		mDispatchers.put(Sensor.TYPE_LIGHT, new ContinuousDispatcher(500, 0));
		mDispatchers.put(Sensor.TYPE_MAGNETIC_FIELD, new ContinuousDispatcher(
				500, 0));
		mDispatchers.put(Sensor.TYPE_ORIENTATION, new ContinuousDispatcher(500,
				0));
		mDispatchers
				.put(Sensor.TYPE_PRESSURE, new ContinuousDispatcher(500, 0));
		mDispatchers.put(Sensor.TYPE_PROXIMITY,
				new ContinuousDispatcher(500, 0));
		mDispatchers.put(Sensor.TYPE_TEMPERATURE, new ContinuousDispatcher(500,
				0));
		mDispatchers.put(Sensor.TYPE_BARCODE_READER, new ContinuousDispatcher(
				500, 0));
		mDispatchers.put(Sensor.TYPE_LINEAR_ACCELERATION,
				new ContinuousDispatcher(500, 0));
		mDispatchers.put(Sensor.TYPE_GRAVITY, new ContinuousDispatcher(500, 0));
		mDispatchers.put(Sensor.TYPE_ROTATION_VECTOR, new ContinuousDispatcher(
				500, 0));

		for (int i = 0; i < mDispatchers.size(); i++) {
			ContinuousDispatcher disp = (ContinuousDispatcher) mDispatchers
					.valueAt(i);
			disp.start();
		}
	}

	private void addAllListeners() {

		for (Entry<SensorEventListener, Map<Sensor, Integer>> listenerMapping : mListenerMap
				.entrySet()) {
			for (Entry<Sensor, Integer> sensorRateMapping : listenerMapping
					.getValue().entrySet()) {
				Dispatcher dispatcher = mDispatchers.get(sensorRateMapping
						.getKey().getType());
				if (dispatcher != null) {
					dispatcher.addListener(listenerMapping.getKey(),
							sensorRateMapping.getValue());
				}
			}
		}

	}

	public void connect() {
		mHasStarted = true;
		mReceivingThread = new Thread(mReceiving);
		mReceivingThread.start();
	}

	public void disconnect() {
		mHasStarted = false;
		mReceivingThread.interrupt();
	}

	public boolean isConnected() {
		return mConnected;
	}

	public boolean hasStarted() {
		return mHasStarted;
	}

	public boolean registerListener(SensorEventListener listener,
			Sensor sensor, int rate) {

		// put in map
		if (mListenerMap.containsKey(listener)) {
			mListenerMap.get(listener).put(sensor, rate);
		} else {
			Map<Sensor, Integer> sensorRateMap = new HashMap<Sensor, Integer>();
			sensorRateMap.put(sensor, rate);
			mListenerMap.put(listener, sensorRateMap);
		}

		// register to dispatchers
		if (mDispatchers != null) {
			Dispatcher dispatcher = mDispatchers.get(sensor.getType());
			if (dispatcher != null) {
				dispatcher.addListener(listener, rate);
			}
		}

		return true;
	}

	public void unregisterListener(SensorEventListener listener, Sensor sensor) {
		// remove from map
		if (mListenerMap.containsKey(listener))
			mListenerMap.get(listener).remove(sensor);

		// remove from dispatchers
		if (mDispatchers != null) {
			Dispatcher dispatcher = mDispatchers.get(sensor.getType());
			if (dispatcher != null) {
				dispatcher.removeListener(listener);
			}
		}
	}

	public void unregisterListener(SensorEventListener listener) {
		// remove from map
		mListenerMap.remove(listener);

		if (mDispatchers != null) {
			for (int i = 0; i < mDispatchers.size(); i++) {
				mDispatchers.valueAt(i).removeListener(listener);
			}
		}
	}

	private Runnable mReceiving = new Runnable() {

		private Thread mContinuousThread = null;

		@Override
		public void run() {

			// listen on a port for clients to send fake sensor data
			while (mHasStarted) {
				ServerSocket serverSocket = null;
				Socket connection = null;
				DataInputStream in = null;
				DataOutputStream out = null;

				try {
					serverSocket = new ServerSocket(PORT);
					serverSocket.setSoTimeout(100);

					// wait for clients to connect and start sending events
					while (mHasStarted && !Thread.interrupted()) {

						try {
							connection = serverSocket.accept();
//							connection.setSoTimeout(100);

							mConnected = true;
							setChanged();
							notifyObservers();

							in = new DataInputStream(
									connection.getInputStream());
							out = new DataOutputStream(
									connection.getOutputStream());

							boolean quit = false;

							// read commands from client and execute them
							while (!quit && !Thread.interrupted()) {

								try {
									int command = in.readInt();

									// play sequence ///////////////////////////
									if (command == 0) {
										// switch to sequence dispatchers
										setupSequenceDispatchers();
										addAllListeners();

										// read sensor event count
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
													new SensorEvent(type,
															accuracy,
															timestamp, values));
										}

										// start dispatching
										for (int i = 0; i < mDispatchers.size(); i++) {
											mDispatchers.valueAt(i).start();
										}

										// wait for dispatchers to finish
										for (int i = 0; i < mDispatchers.size(); i++) {
											((SequenceDispatcher) mDispatchers
													.valueAt(i)).join();
										}

										// tell server that its done
										out.writeInt(2);
									}
									// read continuous data ////////////////////
									else if (command == 1) {
										setupContinuousDispatchers();
										addAllListeners();

										// get fastest registered rates for each
										// sensor
										Map<Integer, Integer> fastestRegistration = new HashMap<Integer, Integer>();
										for (Entry<SensorEventListener, Map<Sensor, Integer>> entry : mListenerMap
												.entrySet()) {
											for (Entry<Sensor, Integer> sensorRateMapping : entry
													.getValue().entrySet()) {
												Sensor sensor = sensorRateMapping
														.getKey();
												Integer rate = sensorRateMapping
														.getValue();
												if (fastestRegistration
														.containsKey(sensor
																.getType())) {
													if (fastestRegistration
															.get(sensor
																	.getType()) > rate)
														fastestRegistration
																.put(sensor
																		.getType(),
																		rate);
												} else {
													fastestRegistration.put(
															sensor.getType(),
															rate);
												}
											}
										}

										// write sensor registration count
										out.writeInt(fastestRegistration.size());
										// write sensor registrations
										for (Entry<Integer, Integer> entry : fastestRegistration
												.entrySet()) {
											out.writeInt(entry.getKey());
											out.writeInt(entry.getValue()
													.intValue());
										}
										
										out.flush();

										// read and set event delivering speed
										for (int i = 0; i < fastestRegistration
												.size(); i++) {
											int sensorType = in.readInt();
											ContinuousDispatcher disp = (ContinuousDispatcher) mDispatchers
													.get(sensorType);
											disp.configProducer(in.readInt(),
													fastestRegistration
															.get(sensorType));
										}

										mSocket2 = new ServerSocket(8112);
										
										out.writeInt(8112);
										
										out.flush();
										
										mContinuousThread = new Thread(
												mContinuousReceiving);
										mContinuousThread.start();

										
										// configure listeners and rates
									}
									// quit ////////////////////////////////////
									else if (command == -1) {
										quit = true;
										if (mContinuousThread != null)
											mContinuousThread.interrupt();
									}
								} catch (SocketTimeoutException e) {
									// TODO: just check if thread was
									// interrupted in
									// while condition

									// Check if registered listeners changed
									// (which
									// ones, rates...)
								}
							}

							// clean up client
							if (out != null)
								out.close();
							if (in != null)
								in.close();
							if (connection != null)
								connection.close();

							mConnected = false;
							setChanged();
							notifyObservers();
						} catch (SocketTimeoutException e) {
							// just check if thread was interrupted in while
							// condition
						} catch (EOFException e) {
							// client disconnected without saying bye :'(
							if (mContinuousThread != null)
								mContinuousThread.interrupt();
							mReceivingThread.interrupt();
						}
					}

					// clean up
					for (int i = 0; i < mDispatchers.size(); i++)
						mDispatchers.valueAt(i).stop();

					if (serverSocket != null)
						serverSocket.close();
				} catch (IOException e) {
					// some other crap with the serverSocket happened
					e.printStackTrace();
				} finally {
					// make sure sensormanagersimulator can switch to real api
					// again, even if client disconnected abruptly
					// (EOFException)
					mConnected = false;
					setChanged();
					notifyObservers();
				}
			}
		}

		private Runnable mContinuousReceiving = new Runnable() {

			@Override
			public void run() {
				try {
					Socket conn = mSocket2.accept();
					conn.setSoTimeout(1000);
					DataInputStream input = new DataInputStream(
							conn.getInputStream());

					while (!Thread.interrupted()) {

						try {
							int type = input.readInt();
							int accuracy = input.readInt();
							int valLength = input.readInt();
							float[] values = new float[valLength];
							for (int j = 0; j < valLength; j++) {
								values[j] = input.readFloat();
							}

							// put into dispatcher
							mDispatchers.get(type).putEvent(
									new SensorEvent(type, accuracy, System
											.nanoTime(), values));
						} catch (SocketTimeoutException e) {
							// just try again
						} catch (EOFException e) {
							Thread.currentThread().interrupt();
						}
					}

					conn.close();
					mSocket2.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					Log.d(TAG, "mContinuousReceiving stopped.");
				}
			}
		};
		private ServerSocket mSocket2;
	};
}
