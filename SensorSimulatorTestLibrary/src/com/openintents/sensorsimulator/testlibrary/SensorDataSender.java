package com.openintents.sensorsimulator.testlibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Observable;
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
public class SensorDataSender extends Observable {

	private static final int PORT = 8010;
	private EmptyListener mEmptyListener;
	private Thread mSendingThread;
	private BlockingQueue<SensorEventContainer> mSensorEvents;

	private boolean mClientConnected = false;

	// network stuff
	private ServerSocket mServerSocket = null;
	private Socket client;
	private DataOutputStream clientOut = null;
	private DataInputStream clientIn = null;

	public SensorDataSender() {
		mSensorEvents = new LinkedBlockingQueue<SensorEventContainer>();
		mSendingThread = new Thread(mSending);
	}

	/**
	 * Start internal thread.
	 */
	public void start() {
		mSendingThread.start();
	}

	/**
	 * Stop internal thread and clean up.
	 * 
	 * TODO internal state machine so this cannot be called in wrong state
	 */
	public void stop() {
		try {
			// say goodbye to device
			clientOut.writeInt(-1);
			clientOut.flush();
			clientOut.close();
			client.close();
			mServerSocket.close();
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
	public boolean sendSensorEvents(Collection<SensorEventContainer> events) {
		Log.v("SimpleTest", "sendSensorEvents()");
		mSensorEvents.addAll(events);
		try {
			// command for sequence
			clientOut.writeInt(0);

			// how many events?
			int size = mSensorEvents.size();
			clientOut.writeInt(size);

			// process queue
			for (int i = 0; i < size; i++) {
				SensorEventContainer event = mSensorEvents.take();
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
			Log.v("SimpleTest", ok == 2 ? "ok" : "not ok");
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

	private Runnable mSending = new Runnable() {

		@Override
		public void run() {

			System.out.println("run started");

			try {
				// open server socket
				mServerSocket = new ServerSocket(PORT);

				// for testing
				if (mEmptyListener != null)
					mEmptyListener.notifyEmpty();

				// wait for clients
				client = mServerSocket.accept();

				mClientConnected = true;
				clientOut = new DataOutputStream(new BufferedOutputStream(
						client.getOutputStream()));
				clientIn = new DataInputStream(new BufferedInputStream(
						client.getInputStream()));

				// tell everybody
				setChanged();
				notifyObservers();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Notify that queue is empty
	 * 
	 * @param recordTester
	 */
	public void setEmptyListener(EmptyListener recordTester) {
		mEmptyListener = recordTester;
	}

	public boolean isClientConnected() {
		return mClientConnected;
	}
}
