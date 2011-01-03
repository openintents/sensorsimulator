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

package org.openintents.tools.sensorsimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles the communication with the SensorClient from the Android phone or emulator.
 *
 * This class is invoked from the {@link SensorServer}.
 *
 * @author Peli
 * @author Josip Balic
 */
public class SensorServerThread implements Runnable {

	public ISensorSimulator mSensorSimulator;

	/**
	 * linked list of all successors, so that we can destroy them
	 * when needed.
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
	 * @param newSensorSimulator, SensorSimulator instance of simulator that starts thread
	 * @param newClientSocket, Socket that is used in connecting
	 */
	public SensorServerThread(ISensorSimulator newSensorSimulator,
			Socket newClientSocket) {
		mSensorSimulator = newSensorSimulator;
		nextThread = null;
		previousThread = null;
		mClientSocket = newClientSocket;
		talking = true;

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
	 * In a simple protocol, all Android Sensors class methods are
	 * received and answered. If necessary, exceptions are thrown
	 * as specified in the Sensors class.
	 */
    public void listenThread() {
    	double updatesPerSecond;
        double[] updatesList;
        int len;

        try {
	        PrintWriter out = new PrintWriter(mClientSocket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(
					new InputStreamReader(
					mClientSocket.getInputStream()));
	        String inputLine, outputLine;

	        outputLine = "SensorSimulator";
	        out.println(outputLine);

	        mSensorSimulator.addMessage("Incoming connection opened.");


	        while ((inputLine = in.readLine()) != null) {
	        	//////////////////////////////////////////////////////////
	        	if (inputLine.compareTo("getSupportedSensors()") == 0) {
	        		String[] supportedSensors = getSupportedSensors();
	        		out.println("" + supportedSensors.length);
	        		for (int i=0; i<supportedSensors.length; i++) {
	        			out.println(supportedSensors[i]);
	        		}

	        	//////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("disableSensor()") == 0 ||
	        			inputLine.compareTo("enableSensor()") == 0) {
	        		boolean enable = (inputLine.compareTo("enableSensor()") == 0);

	        		inputLine = in.readLine();
	        		// Test which sensor is meant and whether that sensor is supported.
	        		if (inputLine.compareTo(ISensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.isSupportedAccelerometer()) {
	        			out.println("" + mSensorSimulator.isEnabledAccelerometer());
	        			mSensorSimulator.setEnabledAccelerometer(enable);
//	        			mSensorSimulator.mRefreshEmulatorAccelerometerLabel.setText("-");
	        		} else if (inputLine.compareTo(ISensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.isSupportedMagneticField()) {
	        			out.println("" + mSensorSimulator.isEnabledMagneticField());
	        			mSensorSimulator.setEnabledMagneticField(enable);
//	        			mSensorSimulator.mRefreshEmulatorCompassLabel.setText("-");
	        		} else if (inputLine.compareTo(ISensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.isSupportedOrientation()) {
	        			out.println("" + mSensorSimulator.isEnabledOrientation());
	        			mSensorSimulator.setEnabledOrientation(enable);
//	        			mSensorSimulator.mRefreshEmulatorOrientationLabel.setText("-");
	        		} else if (inputLine.compareTo(ISensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.isSupportedTemperature()) {
	        			out.println("" + mSensorSimulator.isEnabledTemperature());
	        			mSensorSimulator.setEnabledTemperature(enable);
//	        			mSensorSimulator.mRefreshEmulatorThermometerLabel.setText("-");
	        		} else if (inputLine.compareTo(ISensorSimulator.BARCODE_READER) == 0
	        				&& mSensorSimulator.isSupportedBarcodeReader()) {
	        			out.println("" + mSensorSimulator.isEnabledBarcodeReader());
	        			mSensorSimulator.setEnabledBarcodeReader(enable);
	        		} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}
	        	//////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("getNumSensorValues()") == 0) {
	    	        inputLine = in.readLine();
	        		if (inputLine.compareTo(ISensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.isSupportedAccelerometer()) {
	        			out.println("3");
	        		} else if (inputLine.compareTo(ISensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.isSupportedMagneticField()) {
	        			out.println("3");
	        		} else if (inputLine.compareTo(ISensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.isSupportedOrientation()) {
	        			out.println("3");
	        		} else if (inputLine.compareTo(ISensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.isSupportedTemperature()) {
	        			out.println("1");
	        		} else if (inputLine.compareTo(ISensorSimulator.BARCODE_READER) == 0
	        				&& mSensorSimulator.isSupportedBarcodeReader()) {
	        			out.println("1");
	        		} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}
	        	//////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("readSensor()") == 0) {
	        		inputLine = in.readLine();
	        		if (inputLine.compareTo(ISensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.isSupportedAccelerometer()) {
	        			if (mSensorSimulator.isEnabledAccelerometer()) {
	        				//out.println("3"); // number of data following
		        			//out.println(mSensorSimulator.mobile.read_accelx);
		        			//out.println(mSensorSimulator.mobile.read_accely);
		        			//out.println(mSensorSimulator.mobile.read_accelz);

	        				// For performance reasons, send these commands together
	        				String sensorData = "3\n"  // number of data following
	        					+ mSensorSimulator.getMobilePanel().getReadAccelerometerX() + "\n"
	        					+ mSensorSimulator.getMobilePanel().getReadAccelerometerY() + "\n"
	        					+ mSensorSimulator.getMobilePanel().getReadAccelerometerZ();
	        				out.println(sensorData);
		        			mSensorSimulator.updateEmulatorAccelerometerRefresh();
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.isSupportedMagneticField()) {
	        			if (mSensorSimulator.isEnabledMagneticField()) {
		        			//out.println("3"); // number of data following
		        			//out.println(mSensorSimulator.mobile.read_compassx);
		        			//out.println(mSensorSimulator.mobile.read_compassy);
		        			//out.println(mSensorSimulator.mobile.read_compassz);

		        			// For performance reasons, send these commands together
	        				String sensorData = "3\n"  // number of data following
	        					+ mSensorSimulator.getMobilePanel().getReadCompassX() + "\n"
	        					+ mSensorSimulator.getMobilePanel().getReadCompassY() + "\n"
	        					+ mSensorSimulator.getMobilePanel().getReadCompassZ();
	        				out.println(sensorData);
		        			mSensorSimulator.updateEmulatorCompassRefresh();
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.isSupportedOrientation()) {
	        			if (mSensorSimulator.isEnabledOrientation()) {
			        		//out.println("3"); // number of data following
		        			//out.println(mSensorSimulator.mobile.read_yaw);
		        			//out.println(mSensorSimulator.mobile.read_pitch);
		        			//out.println(mSensorSimulator.mobile.read_roll);

		        			// For performance reasons, send these commands together
	        				String sensorData = "3\n"  // number of data following
	        					+ mSensorSimulator.getMobilePanel().getReadYaw() + "\n"
	        					+ mSensorSimulator.getMobilePanel().getReadPitch() + "\n"
	        					+ mSensorSimulator.getMobilePanel().getReadRoll();
	        				out.println(sensorData);
		        			mSensorSimulator.updateEmulatorOrientationRefresh();
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.isSupportedTemperature()) {
	        			if (mSensorSimulator.isEnabledTemperature()) {
				        	//out.println("1"); // number of data following
				        	//out.println(mSensorSimulator.mobile.read_temperature);

				        	// For performance reasons, send these commands together
				        	// (yes, we need a fast thermometer)
	        				String sensorData = "1\n"  // number of data following
	        					+ mSensorSimulator.getMobilePanel().getReadTemperature();
	        				out.println(sensorData);
		        			mSensorSimulator.updateEmulatorThermometerRefresh();
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}

	        		} else if (inputLine.compareTo(ISensorSimulator.BARCODE_READER) == 0
	        				&& mSensorSimulator.isSupportedBarcodeReader()) {
	        			if (mSensorSimulator.isEnabledBarcodeReader()) {

	        				if(mSensorSimulator.getMobilePanel().getBarcode().length()==13){
	        				String sensorData = "1\n"  // number of data following
	        					+ mSensorSimulator.getMobilePanel().getBarcode();
	        				out.println(sensorData);
	        				}else{
	        					String sensorData = "1\n"
	        						+ "1";
	        					out.println(sensorData);
	        				}
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}
	        	//////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("getSensorUpdateRates()") == 0) {
	    	        inputLine = in.readLine();
	        		if (inputLine.compareTo(ISensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.isSupportedAccelerometer()) {
	        			updatesList = mSensorSimulator.getUpdateRatesAccelerometer();
	        			if (updatesList == null || updatesList.length < 1) {
	        				out.println("0");
	        			} else {
	        				len = updatesList.length;
		        			out.println("" + len);
	        				for (int i=0; i<len; i++) {
	        					out.println("" + updatesList[i]);
	        				}
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.isSupportedMagneticField()) {
	        			updatesList = mSensorSimulator.getUpdateRatesCompass();
	        			if (updatesList == null || updatesList.length < 1) {
	        				out.println("0");
	        			} else {
	        				len = updatesList.length;
		        			out.println("" + len);
	        				for (int i=0; i<len; i++) {
	        					out.println("" + updatesList[i]);
	        				}
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.isSupportedOrientation()) {
	        			updatesList = mSensorSimulator.getUpdateRatesOrientation();
	        			if (updatesList == null || updatesList.length < 1) {
	        				out.println("0");
	        			} else {
	        				len = updatesList.length;
		        			out.println("" + len);
	        				for (int i=0; i<len; i++) {
	        					out.println("" + updatesList[i]);
	        				}
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.isSupportedTemperature()) {
	        			updatesList = mSensorSimulator.getUpdateRatesThermometer();
	        			if (updatesList == null || updatesList.length < 1) {
	        				out.println("0");
	        			} else {
	        				len = updatesList.length;
		        			out.println("" + len);
	        				for (int i=0; i<len; i++) {
	        					out.println("" + updatesList[i]);
	        				}
	        			}
	        		} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}

	        	//////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("getSensorUpdateRate()") == 0) {
	    	        inputLine = in.readLine();
	    	        if (inputLine.compareTo(ISensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.isSupportedAccelerometer()) {
	        			if (mSensorSimulator.isEnabledAccelerometer()) {
	        				updatesPerSecond = mSensorSimulator.getCurrentUpdateRateAccelerometer();
	        				out.println("" + updatesPerSecond);
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.isSupportedMagneticField()) {
	        			if (mSensorSimulator.isEnabledMagneticField()) {
	        				updatesPerSecond = mSensorSimulator.getCurrentUpdateRateCompass();
	        				out.println("" + updatesPerSecond);
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.isSupportedOrientation()) {
	        			if (mSensorSimulator.isEnabledOrientation()) {
	        				updatesPerSecond = mSensorSimulator.getCurrentUpdateRateOrientation();
	        				out.println("" + updatesPerSecond);
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.isSupportedTemperature()) {
	        			if (mSensorSimulator.isEnabledTemperature()) {
	        				updatesPerSecond = mSensorSimulator.getCurrentUpdateRateThermometer();
	        				out.println("" + updatesPerSecond);
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}
	    	    //////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("setSensorUpdateRate()") == 0) {
	    	        inputLine = in.readLine();
	    	        if (inputLine.compareTo(ISensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.isSupportedAccelerometer()) {
        				out.println("OK");
        				inputLine = in.readLine();
        				updatesPerSecond = Float.parseFloat(inputLine);
        				mSensorSimulator.setCurrentUpdateRateAccelerometer(updatesPerSecond);
	        		} else if (inputLine.compareTo(ISensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.isSupportedMagneticField()) {
        				out.println("OK");
        				inputLine = in.readLine();
        				updatesPerSecond = Float.parseFloat(inputLine);
        				mSensorSimulator.setCurrentUpdateRateCompass(updatesPerSecond);
        			} else if (inputLine.compareTo(ISensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.isSupportedOrientation()) {
        				out.println("OK");
        				inputLine = in.readLine();
        				updatesPerSecond = Float.parseFloat(inputLine);
        				mSensorSimulator.setCurrentUpdateRateOrientation(updatesPerSecond);
        			} else if (inputLine.compareTo(ISensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.isSupportedTemperature()) {
        				out.println("OK");
        				inputLine = in.readLine();
        				updatesPerSecond = Float.parseFloat(inputLine);
        				mSensorSimulator.setCurrentUpdateRateThermometer(updatesPerSecond);

        			} else if (inputLine.compareTo(ISensorSimulator.BARCODE_READER) == 0
	        				&& mSensorSimulator.isSupportedBarcodeReader()) {
        				out.println("OK");
        				inputLine = in.readLine();
        				updatesPerSecond = Float.parseFloat(inputLine);
        			} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}

	    	    //////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("unsetSensorUpdateRate()") == 0) {
	    	        inputLine = in.readLine();
	    	        if (inputLine.compareTo(ISensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.isSupportedAccelerometer()) {
	        			if (mSensorSimulator.isEnabledAccelerometer()) {
	        				out.println("OK");
	        				mSensorSimulator.setCurrentUpdateRateAccelerometer(
	        						mSensorSimulator.getDefaultUpdateRateAccelerometer());
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.isSupportedMagneticField()) {
	        			if (mSensorSimulator.isEnabledMagneticField()) {
	        				out.println("OK");
	        				mSensorSimulator.setCurrentUpdateRateCompass(
	        						mSensorSimulator.getDefaultUpdateRateCompass());
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.isSupportedOrientation()) {
	        			if (mSensorSimulator.isEnabledOrientation()) {
	        				out.println("OK");
	        				mSensorSimulator.setCurrentUpdateRateOrientation(
	        						mSensorSimulator.getDefaultUpdateRateOrientation());
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(ISensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.isSupportedTemperature()) {
	        			if (mSensorSimulator.isEnabledTemperature()) {
	        				out.println("OK");
	        				mSensorSimulator.setCurrentUpdateRateThermometer(
	        						mSensorSimulator.getDefaultUpdateRateThermometer());
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}
	        	//////////////////////////////////////////////////////////
			    } else {
		        	// ??? The client is violating the protocol.
			        mSensorSimulator.addMessage("WARNING: Client sent unexpected command: " + inputLine);
		        }
	        }
	        out.close();
	        in.close();
	        mClientSocket.close();

        } catch (IOException e) {
        	if (talking) {
	        	System.err.println("IOException in SensorServerThread.");
	        	try {
	        		if (mClientSocket != null) mClientSocket.close();
	            } catch (IOException e2) {
	                System.err.println("Close failed as well.");
	            }
        	} else {
        		// everything fine. Our mouth was shut deliberately.
        	}
        }

        //  Here we finish program execution and we take ourselves out of the chained list:
        if (previousThread != null) {
        	previousThread.nextThread = nextThread;
        }
        if (nextThread != null) {
        	nextThread.previousThread = previousThread;
        }
        mSensorSimulator.addMessage("Incoming connection closed.");

    }

    /**
     * Method used to get currently String[] of currently supported sensors.
     *
     * @return String[] filled with names of currently supported sensors.
     */
    public String[] getSupportedSensors() {
    	String[] sensorList = new String[5]; // currently max. 5 possible!
		int sensorMax = 0;
		if (mSensorSimulator.isSupportedAccelerometer()) {
			sensorList[sensorMax] = ISensorSimulator.ACCELEROMETER;
			sensorMax++;
		}
		if (mSensorSimulator.isSupportedMagneticField()) {
			sensorList[sensorMax] = ISensorSimulator.MAGNETIC_FIELD;
			sensorMax++;
		}
		if (mSensorSimulator.isSupportedOrientation()) {
			sensorList[sensorMax] = ISensorSimulator.ORIENTATION;
			sensorMax++;
		}
		if (mSensorSimulator.isSupportedTemperature()) {
			sensorList[sensorMax] = ISensorSimulator.TEMPERATURE;
			sensorMax++;
		}
		if (mSensorSimulator.isSupportedBarcodeReader()) {
			sensorList[sensorMax] = ISensorSimulator.BARCODE_READER;
			sensorMax++;
		}
		String[] returnSensorList = new String[sensorMax];
		for (int i=0; i<sensorMax; i++)
			returnSensorList[i] = sensorList[i];

		return returnSensorList;
    }

    /**
     * Closes the socket.
     */
    public void stop() {
    	try {
    		talking = false;
    		mClientSocket.close();
        } catch (IOException e) {
            System.err.println("Close failed.");
            System.exit(1);
        }
    }
}
