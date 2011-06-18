/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2010 OpenIntents.org
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

package org.openintents.tools.simulator.model.sensor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.openintents.tools.simulator.SensorSimulator;
import org.openintents.tools.simulator.controller.SensorSimulatorController;
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
 */
public class SensorServerThread implements Runnable {

	public SensorSimulator mSensorSimulator;

	/**
	 * linked list of all successors, so that we can destroy them when needed.
	 */
	public Thread mThread;
	public SensorServerThread nextThread;
	public SensorServerThread previousThread;

	public Socket mClientSocket;

	/**
	 * Whether thread is supposed to be continuing work.
	 */
	boolean talking;

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
		nextThread = null;
		previousThread = null;
		mClientSocket = newClientSocket;
		talking = true;

		mSensorSimulator.fixEnabledSensors();
		
		// start ourselves:
		mThread = new Thread(this);
		mThread.start();
	}

	/**
	 * Method to call only once thread.
	 */
	public void run() {
		listenThread();
	}

	/**
	 * Handles communication with the client.
	 * 
	 * In a simple protocol, all Android Sensors class methods are received and
	 * answered. If necessary, exceptions are thrown as specified in the Sensors
	 * class.
	 */
	public void listenThread() {
		try {
			PrintWriter out = new PrintWriter(mClientSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					mClientSocket.getInputStream()));
			String inputLine, outputLine;

			outputLine = "SensorSimulator";
			out.println(outputLine);

			// mSensorSimulator.addMessage("Incoming connection opened.");

			while ((inputLine = in.readLine()) != null) {
				if (inputLine.compareTo("getSupportedSensors()") == 0) {
					String[] supportedSensors = getSupportedSensors();
					out.println(supportedSensors.length);
					for (int i = 0; i < supportedSensors.length; i++) {
						out.println(supportedSensors[i]);
					}
				} else if (inputLine.compareTo("readSensor()") == 0) {
					inputLine = in.readLine();
					SensorController sensorCtrl = getSensorCtrlFromName(inputLine);
					if (sensorCtrl != null) {
						sensorCtrl.readSensor(out);
						sensorCtrl.updateEmulatorRefresh(mSensorSimulator.view.getRefreshCount());
					} else
						out.println("throw IllegalArgumentException");
				} else {
					SensorModel sensor = getSensorModelFromName(inputLine);
					if (sensor != null) {
						inputLine = in.readLine();
						executeCommand(sensor, out, in, inputLine);
					} else {
						out.println("throw IllegalArgumentException");
						System.out
								.println("WARNING: Client sent unexpected command: "
										+ inputLine);
						// ??? The client is violating the protocol.
						// mSensorSimulator
						// .addMessage("WARNING: Client sent unexpected command: "
						// + inputLine);
					}
				}
			}
			mSensorSimulator.unfixEnabledSensors();
			out.close();
			in.close();
			mClientSocket.close();

		} catch (IOException e) {
			if (talking) {
				System.err.println("IOException in SensorServerThread.");
				try {
					if (mClientSocket != null)
						mClientSocket.close();
				} catch (IOException e2) {
					System.err.println("Close failed as well.");
				}
			} else {
				// everything fine. Our mouth was shut deliberately.
			}
		}

		// Here we finish program execution and we take ourselves out of the
		// chained list:
		if (previousThread != null) {
			previousThread.nextThread = nextThread;
		}
		if (nextThread != null) {
			nextThread.previousThread = previousThread;
		}
		// mSensorSimulator.addMessage("Incoming connection closed.");
	}

	private void executeCommand(SensorModel sensor, PrintWriter out, BufferedReader inArgs, String cmd) throws IOException {
		if (cmd.compareTo("getNumSensorValues()") == 0)
			sensor.getNumSensorValues(out);
		else if (cmd.compareTo("getSensorUpdateRates()") == 0)
			sensor.getSensorUpdateRates(out);
		else if (cmd.compareTo("setSensorUpdateRate()") == 0) {
			sensor.setSensorUpdateRate(out);
			String args = inArgs.readLine();
			float updatesPerSecond = Float.parseFloat(args);
			mSensorSimulator.model.getAccelerometer().setCurrentUpdateRate(updatesPerSecond);
		}
		else if (cmd.compareTo("unsetSensorUpdateRate()") == 0)
			sensor.unsetSensorUpdateRate(out);
		else
			out.println("throw IllegalArgumentException");
	}

	private SensorController getSensorCtrlFromName(String inputLine) {
		SensorSimulatorController ctrl = mSensorSimulator.ctrl;
		if (inputLine.compareTo(SensorModel.ACCELEROMETER) == 0)
			return ctrl.getAccelerometer();
		else if (inputLine.compareTo(SensorModel.MAGNETIC_FIELD) == 0)
			return ctrl.getMagneticField();
		else if (inputLine.compareTo(SensorModel.ORIENTATION) == 0)
			return ctrl.getOrientation();
		else if (inputLine.compareTo(SensorModel.TEMPERATURE) == 0)
			return ctrl.getTemperature();
		else if (inputLine.compareTo(SensorModel.BARCODE_READER) == 0)
			return ctrl.getBarcodeReader();
		else if (inputLine.compareTo(SensorModel.LIGHT) == 0)
			return ctrl.getLight();
		else if (inputLine.compareTo(SensorModel.PROXIMITY) == 0)
			return ctrl.getProximity();
		else if (inputLine.compareTo(SensorModel.PRESSURE) == 0)
			return ctrl.getPressure();
		return null;
	}

	private SensorModel getSensorModelFromName(String inputLine) {
		SensorSimulatorModel model = mSensorSimulator.model;
		if (inputLine.compareTo(SensorModel.ACCELEROMETER) == 0)
			return model.getAccelerometer();
		else if (inputLine.compareTo(SensorModel.MAGNETIC_FIELD) == 0)
			return model.getMagneticField();
		else if (inputLine.compareTo(SensorModel.ORIENTATION) == 0)
			return model.getOrientation();
		else if (inputLine.compareTo(SensorModel.TEMPERATURE) == 0)
			return model.getTemperature();
		else if (inputLine.compareTo(SensorModel.BARCODE_READER) == 0)
			return model.getBarcodeReader();
		else if (inputLine.compareTo(SensorModel.LIGHT) == 0)
			return model.getLight();
		else if (inputLine.compareTo(SensorModel.PROXIMITY) == 0)
			return model.getProximity();
		else if (inputLine.compareTo(SensorModel.PRESSURE) == 0)
			return model.getPressure();
		return null;
	}

	/**
	 * Method used to get currently String[] of currently supported sensors.
	 * 
	 * @return String[] filled with names of currently supported sensors.
	 */
	public String[] getSupportedSensors() {
		ArrayList<String> resultArray = new ArrayList<String>();
		for (SensorModel sensor : mSensorSimulator.model.getSensors()) {
			if (sensor.isEnabled()) {
				resultArray.add(sensor.getName());
			}
		}
		String[] result = new String[resultArray.size()];
		int i = 0;
		for (String sensor : resultArray) {
			result[i++] = sensor;
		}
		return result;
	}

	/**
	 * Closes the socket.
	 */
	public void stop() {
		try {
			talking = false;
			mClientSocket.close();
			mSensorSimulator.unfixEnabledSensors();
		} catch (IOException e) {
			System.err.println("Close failed.");
			System.exit(1);
		}
	}
}
