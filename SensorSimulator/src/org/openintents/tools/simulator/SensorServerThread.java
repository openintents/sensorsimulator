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
import java.util.ArrayList;

import org.openintents.tools.simulator.controller.SensorSimulatorController;
import org.openintents.tools.simulator.controller.sensor.SensorController;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
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
	 * Method to call only once thread.
	 */
	@Override
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

			mSensorSimulator.addMessage("Incoming connection opened.");

			// here we treat different getSupportedSensors command from others
			// (others have the name of the sensor in the input stream)
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.compareTo("getSupportedSensors()") == 0) {
					String[] supportedSensors = getSupportedSensors();
					out.println(supportedSensors.length);
					for (int i = 0; i < supportedSensors.length; i++) {
						out.println(supportedSensors[i]);
					}
				} else {
					executeCommand(out, in, inputLine);
				}
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
		String sensorName = in.readLine();
		SensorController sensorCtrl = getSensorCtrlFromName(sensorName);
		SensorModel sensorModel = getSensorModelFromName(sensorName);
		if (cmd.compareTo("getNumSensorValues()") == 0) {
			sensorModel.getNumSensorValues(out);
		} else if (cmd.compareTo("setSensorUpdateDelay()") == 0) {
			String args = in.readLine();
			if (sensorModel.isEnabled()) {
				int updateDelay = Integer.parseInt(args);
				sensorCtrl.setCurrentUpdateRate(updateDelay);
				sensorModel.setSensorUpdateRate(out);
			} else {
				System.out.println(sensorName
						+ " throw IllegalArgumentException");
				out.println("throw IllegalArgumentException");
			}
		} else if (cmd.compareTo("unsetSensorUpdateRate()") == 0) {
			sensorModel.unsetSensorUpdateRate(out);
			sensorCtrl.setCurrentUpdateRate(sensorModel.getDefaultUpdateRate());
		} else if (cmd.compareTo("readSensor()") == 0) {
			if (sensorCtrl != null) {
				sensorCtrl.readSensor(out);
				sensorCtrl.updateEmulatorRefresh(mSensorSimulator.view
						.getRefreshCount());
			} else {
				out.println("throw IllegalArgumentException");
				// ??? The client is violating the protocol.
				mSensorSimulator
						.addMessage("WARNING: Client sent unexpected command: "
								+ cmd);
			}
		} else {
			out.println("throw IllegalArgumentException");
		}
	}

	/**
	 * Returns the controller component of a sensor by name. (by accessing
	 * mSensorSimulator.controller list with all controllers)
	 * 
	 * @param sensorName
	 * @return
	 */

	private SensorController getSensorCtrlFromName(String sensorName) {
		SensorSimulatorController ctrl = mSensorSimulator.controller;
		if (sensorName.compareTo(SensorModel.ACCELEROMETER) == 0)
			return ctrl.getAccelerometer();
		else if (sensorName.compareTo(SensorModel.MAGNETIC_FIELD) == 0)
			return ctrl.getMagneticField();
		else if (sensorName.compareTo(SensorModel.ORIENTATION) == 0)
			return ctrl.getOrientation();
		else if (sensorName.compareTo(SensorModel.TEMPERATURE) == 0)
			return ctrl.getTemperature();
		else if (sensorName.compareTo(SensorModel.BARCODE_READER) == 0)
			return ctrl.getBarcodeReader();
		else if (sensorName.compareTo(SensorModel.LIGHT) == 0)
			return ctrl.getLight();
		else if (sensorName.compareTo(SensorModel.PROXIMITY) == 0)
			return ctrl.getProximity();
		else if (sensorName.compareTo(SensorModel.PRESSURE) == 0)
			return ctrl.getPressure();
		else if (sensorName.compareTo(SensorModel.LINEAR_ACCELERATION) == 0)
			return ctrl.getLinearAcceleration();
		else if (sensorName.compareTo(SensorModel.GRAVITY) == 0)
			return ctrl.getGravity();
		else if (sensorName.compareTo(SensorModel.ROTATION_VECTOR) == 0)
			return ctrl.getRotationVector();
		else if (sensorName.compareTo(SensorModel.GYROSCOPE) == 0)
			return ctrl.getGyroscope();
		return null;
	}

	/**
	 * Returns the model component of a sensor by name. (by accessing
	 * mSensorSimulator.modellist with all models)
	 * 
	 * @param sensorName
	 * @return
	 */
	private SensorModel getSensorModelFromName(String sensorName) {
		SensorSimulatorModel model = mSensorSimulator.model;
		if (sensorName.compareTo(SensorModel.ACCELEROMETER) == 0)
			return model.getAccelerometer();
		else if (sensorName.compareTo(SensorModel.MAGNETIC_FIELD) == 0)
			return model.getMagneticField();
		else if (sensorName.compareTo(SensorModel.ORIENTATION) == 0)
			return model.getOrientation();
		else if (sensorName.compareTo(SensorModel.TEMPERATURE) == 0)
			return model.getTemperature();
		else if (sensorName.compareTo(SensorModel.BARCODE_READER) == 0)
			return model.getBarcodeReader();
		else if (sensorName.compareTo(SensorModel.LIGHT) == 0)
			return model.getLight();
		else if (sensorName.compareTo(SensorModel.PROXIMITY) == 0)
			return model.getProximity();
		else if (sensorName.compareTo(SensorModel.PRESSURE) == 0)
			return model.getPressure();
		else if (sensorName.compareTo(SensorModel.LINEAR_ACCELERATION) == 0)
			return model.getLinearAcceleration();
		else if (sensorName.compareTo(SensorModel.GRAVITY) == 0)
			return model.getGravity();
		else if (sensorName.compareTo(SensorModel.ROTATION_VECTOR) == 0)
			return model.getRotationVector();
		else if (sensorName.compareTo(SensorModel.GYROSCOPE) == 0)
			return model.getGyroscope();
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
