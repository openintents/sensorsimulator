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

package org.openintents.tools.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.openintents.tools.simulator.controller.sensor.SensorController;
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

	private SensorSimulator mSensorSimulator;

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

	/**
	 * Constructor to start as thread.
	 * 
	 * @param newSensorSimulator
	 *            , SensorSimulator instance of simulator that starts thread
	 * @param newClientSocket
	 *            , Socket that is used in connecting
	 */
	public SensorServerThread(SensorSimulator newSensorSimulator,
			Socket newClientSocket) {
		mSensorSimulator = newSensorSimulator;
		mNextThread = null;
		mPreviousThread = null;
		mClientSocket = newClientSocket;
		mTalking = true;

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

			outputLine = "SensorSimulator";
			out.println(outputLine);

			mSensorSimulator.addMessage("Incoming connection opened.");

			while ((inputLine = in.readLine()) != null) {
				executeCommand(out, in, inputLine);
			}
			out.close();
			in.close();
			mClientSocket.close();

		} catch (IOException e) {
			if (mTalking) {
				System.err.println("IOException in SensorServerThread.");
				try {
					if (mClientSocket != null) {
						mClientSocket.close();
					}
				} catch (IOException e2) {
					System.err.println("Close failed as well.");
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
		mSensorSimulator.addMessage("Incoming connection closed.");
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
		if (cmd.compareTo("getSupportedSensors()") == 0) {
			String[] supportedSensors = mSensorSimulator.model
					.getSupportedSensors();
			out.println(supportedSensors.length);
			for (int i = 0; i < supportedSensors.length; i++) {
				out.println(supportedSensors[i]);
			}
		} else {
			String sensorName = in.readLine();
			SensorController sensorCtrl = mSensorSimulator.controller
					.getSensorCtrlFromName(sensorName);
			SensorModel sensorModel = mSensorSimulator.model
					.getSensorModelFromName(sensorName);
			// get number of components of sensor data value
			if (cmd.compareTo("getNumSensorValues()") == 0) {
				out.println(sensorModel.getNumSensorValues());
			}
			// get sensor update delay
			else if (cmd.compareTo("setSensorUpdateDelay()") == 0) {
				String args = in.readLine();
				if (sensorModel.isEnabled()) {
					int updateDelay = Integer.parseInt(args);
					sensorCtrl.setCurrentUpdateRate(updateDelay);
					out.println("" + sensorModel.getCurrentUpdateRate());
				} else {
					System.out.println(sensorName
							+ " throw IllegalArgumentException");
					out.println("throw IllegalArgumentException");
				}
			}
			// unset sensor update rate
			else if (cmd.compareTo("unsetSensorUpdateRate()") == 0) {
				if (sensorModel.isEnabled()) {
					sensorModel.resetCurrentUpdateDelay();
					sensorCtrl.setCurrentUpdateRate(sensorModel
							.getDefaultUpdateRate());
					out.println("OK");
				} else {
					// This sensor is currently disabled
					out.println("throw IllegalStateException");
				}
			}
			// read sensor
			else if (cmd.compareTo("readSensor()") == 0) {
				if (sensorCtrl != null) {
					if (sensorModel.isEnabled()) {
						out.println(sensorModel.printSensorData());
					} else {
						// This sensor is currently disabled
						out.println("throw IllegalStateException");
					}
					sensorCtrl.updateEmulatorRefresh(mSensorSimulator.view
							.getRefreshCount());
				} else {
					out.println("throw IllegalArgumentException");
					// ??? The client is violating the protocol.
					mSensorSimulator
							.addMessage("WARNING: Client sent unexpected command: "
									+ cmd);
				}
			}
			// unknown command
			else {
				out.println("throw IllegalArgumentException");
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
			System.err.println("Close failed.");
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
