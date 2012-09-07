/*
 * Copyright (C) 2008 - 2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.tools.simulator.comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.openintents.tools.simulator.logging.Logg;

/**
 * Listens for incoming connections from an Android phone or emulator.
 * 
 * Connections are then passed to the {@link SensorServerThread}.
 * 
 * @author Peli
 * @author donat3llo
 * 
 */
public class SensorServer implements Runnable {
	private static final String TAG = SensorServer.class.getName();

	/**
	 * linked list of all successors, so that we can destroy them when needed.
	 */
	private Thread mThread;
	private SensorServerThread mFirstThread;
	private SensorServerThread mLastThread;

	private ServerSocket mServerSocket;
	private SensorServerThreadListener mThreadListener;

	private int mPort;
	private boolean mListening;

	/**
	 * Constructor to start as server that listens for connections.
	 * 
	 * @param sensorServerThreadListener
	 *            implements the simulator command api
	 * @param port
	 *            the port on which to listen
	 */
	public SensorServer(SensorServerThreadListener sensorServerThreadListener,
			int port) {
		mPort = port;
		if (mPort == 0)
			return;

		mThreadListener = sensorServerThreadListener;
		mFirstThread = null;
		mLastThread = null;
		mListening = true;
		
		// start ourselves:
		mThread = new Thread(this);
		mThread.start();
	}

	/**
	 * Method that is called when starting a thread for network connection.
	 */
	public void run() {
		listenServer();
	}

	/**
	 * Method that starts thread for network connection.
	 */
	public void listenServer() {

		mServerSocket = null;
		try {
			mServerSocket = new ServerSocket(mPort);
		} catch (IOException e) {
			Logg.e(TAG, "Could not listen on port: " + mPort);
			// e.printStackTrace();
			return;
		}

		Socket clientSocket = null;
		try {
			while (mListening) {
				clientSocket = mServerSocket.accept();

				// Start again new thread:
				SensorServerThread newThread = new SensorServerThread(
						mThreadListener, clientSocket);

				// set the linking:
				if (mFirstThread == null) {
					// this is the first thread:
					mFirstThread = newThread;
					mLastThread = newThread;
				} else {
					// link into chain:
					mLastThread.setNextThread(newThread);
					newThread.setPreviousThread(mLastThread);
					mLastThread = newThread;
				}
			}

		} catch (IOException e) {
			if (mListening) {
				Logg.e(TAG, "Accept failed.");
			} else {
				// everything ok, socket closed by user.
			}
		}

		try {
			mServerSocket.close();
		} catch (IOException e) {
			Logg.e(TAG, "Close failed");
			System.exit(1);
		}
	}

	/**
	 * Stop all active threads and then oneself.
	 */
	public void stop() {

		// go through the list and kill in turn
		SensorServerThread sst;
		SensorServerThread ssthelp;
		for (sst = mFirstThread; sst != null; sst = ssthelp) {
			// first remember next pointer before it is gone:
			ssthelp = sst.getNextThread();
			// sst.mThread.interrupt();
			sst.stop();
		}
		// finally kill ourselves:
		mListening = false;

		try {
			if (mServerSocket != null) {
				Logg.i(TAG, "Closing listening server...");
				mServerSocket.close();
			}
		} catch (IOException e) {
			Logg.e(TAG, "Close failed.");
			System.exit(1);
		}
	}

}
