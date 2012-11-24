package org.openintents.sensorsimulator.testlibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

/**
 * Server which can take a sequence of sensor events and send them to a client.
 * Manages an internal blocking queue which can be filled with events.
 * 
 * @author Qui Don Ho
 * 
 */
public class SequenceDataSender {

	private static final int PORT = 8111;
	protected static final String TAG = "SensorDataSender";
	private BlockingQueue<SensorEvent> mSensorEvents;

	private boolean mConnected = false;

	// network stuff
	private Socket client;
	private DataOutputStream clientOut = null;
	private DataInputStream clientIn = null;

	public SequenceDataSender() {
		mSensorEvents = new LinkedBlockingQueue<SensorEvent>();
	}

	/**
	 * Connect to app-under-test
	 * 
	 * @return true, if connection could be established, false otherwise.
	 */
	public boolean connect() {
		try {
			Log.d(TAG, "sensimtest  Trying to connect to server...");
			// connect to app under test
			client = new Socket("127.0.0.1", PORT);

			mConnected = true;
			clientOut = new DataOutputStream(new BufferedOutputStream(
					client.getOutputStream()));
			clientIn = new DataInputStream(new BufferedInputStream(
					client.getInputStream()));

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Connect to app-under-test
	 */
	public boolean connect(String ipAddress) {
		try {
			// Log.d(TAG, "Trying to connect to server...");
			// connect to app under test
			client = new Socket(ipAddress, PORT);

			mConnected = true;
			clientOut = new DataOutputStream(new BufferedOutputStream(
					client.getOutputStream()));
			clientIn = new DataInputStream(new BufferedInputStream(
					client.getInputStream()));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return mConnected;
	}

	/**
	 * Stop internal thread and clean up.
	 * 
	 * TODO internal state machine so this cannot be called in wrong state
	 */
	public void disconnect() {
		try {
			// say goodbye to device
			clientOut.writeInt(-1);
			clientOut.flush();
			clientOut.close();
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Load a chunk of sensor events in internal queue and send it to client.
	 * 
	 * @param events
	 *            the events to send
	 */
	public boolean sendSensorEvents(Collection<SensorEvent> events) {
		mSensorEvents.addAll(events);
		try {
			// command for sequence
			clientOut.writeInt(0);

			// how many events?
			int size = mSensorEvents.size();
			clientOut.writeInt(size);

			// process queue
			for (int i = 0; i < size; i++) {
				SensorEvent event = mSensorEvents.take();
				// send event to client
				clientOut.writeInt(event.type);
				clientOut.writeInt(event.accuracy);
				clientOut.writeLong(event.timestamp);
				clientOut.writeInt(event.values.length);
				for (float value : event.values) {
					clientOut.writeFloat(value);
				}

			}
			clientOut.flush();
			int ok = clientIn.readInt();
			if (ok == 2)
				return true;
		} catch (InterruptedException e) {
			// shouldn't happen because we send as many events as we counted
			// with size(), at least i hope so :)
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public boolean isConnected() {
		return mConnected;
	}
}
