/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2011 OpenIntents.org
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.openintents.tools.simulator.controller.sensor.SensorController;
import org.openintents.tools.simulator.logging.Logg;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

/**
 * Handles the communication with the SensorClient from the Android phone or
 * emulator.
 * 
 * This class is invoked from the {@link SensorServer}.
 * 
 * @author Peli
 * @author Josip Balic
 * @author ilarele
 * @author donat3llo
 */
public class SensorServerThread implements Runnable {

	private static final String TAG = SensorServerThread.class.getName();

	/**
	 * linked list of all successors, so that we can destroy them when needed.
	 */
	private Thread mThread;
	private SensorServerThread mNextThread;
	private SensorServerThread mPreviousThread;

	private Socket mClientSocket;

	/**
	 * Whether thread is supposed to be continuing work.
	 */
	private boolean mTalking;

	private SensorServerThreadListener mServerThreadListener;

	/**
	 * Constructor to start as thread.
	 * 
	 * @param serverThreadListener
	 *            , delegate for all operations
	 * @param newClientSocket
	 *            , Socket that is used in connecting
	 */
	public SensorServerThread(SensorServerThreadListener serverThreadListener,
			Socket newClientSocket) {
		mNextThread = null;
		mPreviousThread = null;
		mClientSocket = newClientSocket;
		mTalking = true;
		mServerThreadListener = serverThreadListener;


		// start ourselves:
		mThread = new Thread(this);
		mThread.start();
	}

	/**
	 * Handles communication with the client.
	 * 
	 * In a simple protocol, all Android Sensors class methods are received and
	 * answered. If necessary, exceptions are thrown as specified in the Sensors
	 * class.
	 */
	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(mClientSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					mClientSocket.getInputStream()));
			String inputLine, outputLine;

			// say hi to client
			outputLine = "SensorSimulator";
			out.println(outputLine);

			Logg.i(TAG, "Incoming connection opened.");

			// handle commands
			while ((inputLine = in.readLine()) != null) {
				executeCommand(out, in, inputLine);
			}
			
			// clean up
			out.close();
			in.close();
			mClientSocket.close();

		} catch (IOException e) {
			if (mTalking) {
				Logg.e(TAG, "IOException in SensorServerThread.");
				try {
					if (mClientSocket != null) {
						mClientSocket.close();
					}
				} catch (IOException e2) {
					Logg.e(TAG, "Close failed as well.");
				}
			} else {
				// everything fine. Our mouth was shut deliberately.
			}
		}

		// Here we finish program execution and we take ourselves out of the
		// chained list:
		if (mPreviousThread != null) {
			mPreviousThread.mNextThread = mNextThread;
		}
		if (mNextThread != null) {
			mNextThread.mPreviousThread = mPreviousThread;
		}
		Logg.i(TAG, "Incoming connection closed.");
	}

	/**
	 * Here each command is executed following the protocol between server and
	 * client application.
	 * 
	 * @param out
	 *            OutputStream (to write in)
	 * @param in
	 *            InputStream (to read from)
	 * @param cmd
	 *            command to be executed
	 * @throws IOException
	 */
	private void executeCommand(PrintWriter out, BufferedReader in, String cmd) 
			throws IOException {
		
		// get list of supported sensors
		if (cmd.compareTo("getSupportedSensors()") == 0) {
			String[] supportedSensors = mServerThreadListener.getSupportedSensors();
			out.println(supportedSensors.length);
			for (int i = 0; i < supportedSensors.length; i++) {
				out.println(supportedSensors[i]);
			}
		} else {
			try {
				String sensorName = in.readLine();

				// get number of components of sensor data value
				if (cmd.compareTo("getNumSensorValues()") == 0) {
					// out.println(sensorModel.getNumSensorValues());
					out.println(mServerThreadListener.getNumSensorValues(sensorName));
				}
				// get sensor update delay
				else if (cmd.compareTo("setSensorUpdateDelay()") == 0) {
					String args = in.readLine();
					int updateDelay = Integer.parseInt(args);

					mServerThreadListener.setSensorUpdateDelay(sensorName, updateDelay);
					out.println("OK");
				}
				// unset sensor update rate
				else if (cmd.compareTo("unsetSensorUpdateRate()") == 0) {
					mServerThreadListener.unsetSensorUpdateRate(sensorName);
					out.println("OK");
				}
				// read sensor
				else if (cmd.compareTo("readSensor()") == 0) {
					out.println(mServerThreadListener.readSensor(sensorName));
				}
				// unknown command
				else {
					throw new IllegalArgumentException();
				}
			} catch (IllegalArgumentException iae) {
				out.println("throw IllegalArgumentException");
			} catch (IllegalStateException ise) {
				out.println("throw IllegalStateException");
			}
		}
	}

	/**
	 * Closes the socket.
	 */
	public void stop() {
		try {
			mTalking = false;
			mClientSocket.close();
		} catch (IOException e) {
			Logg.e(TAG, "Close failed.");
			System.exit(1);
		}
	}

	public void setNextThread(SensorServerThread newThread) {
		mNextThread = newThread;
	}

	public void setPreviousThread(SensorServerThread mLastThread) {
		mPreviousThread = mLastThread;
	}

	public SensorServerThread getNextThread() {
		return mNextThread;
	}
}
