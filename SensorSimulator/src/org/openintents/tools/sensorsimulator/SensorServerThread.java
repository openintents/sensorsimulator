/* 
 * Copyright (C) 2008 OpenIntents.org
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
 *
 */
public class SensorServerThread implements Runnable {

	public SensorSimulator mSensorSimulator;
	
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
	/*
	String[] mSupportedSensors = {
			SensorSimulator.ACCELEROMETER,
			SensorSimulator.COMPASS,
			SensorSimulator.ORIENTATION
		};
	*/
	
	/**
	 * Constructor to start as thread.
	 * @param newSensorSimulator
	 */
	public SensorServerThread(SensorSimulator newSensorSimulator,
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
	
	// Thread is called exactly once.
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
	             //outputLine = kkp.processInput(inputLine);
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
	        		if (inputLine.compareTo(SensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.mSupportedAccelerometer.isSelected()) {
	        			out.println("" + mSensorSimulator.mEnabledAccelerometer.isSelected());
	        			mSensorSimulator.mEnabledAccelerometer.setSelected(enable);
	        			mSensorSimulator.mRefreshEmulatorAccelerometerLabel.setText("-");
	        		} else if (inputLine.compareTo(SensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.mSupportedMagneticField.isSelected()) {
	        			out.println("" + mSensorSimulator.mEnabledMagneticField.isSelected());
	        			mSensorSimulator.mEnabledMagneticField.setSelected(enable);
	        			mSensorSimulator.mRefreshEmulatorCompassLabel.setText("-");
	        		} else if (inputLine.compareTo(SensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.mSupportedOrientation.isSelected()) {
	        			out.println("" + mSensorSimulator.mEnabledOrientation.isSelected());
	        			mSensorSimulator.mEnabledOrientation.setSelected(enable);
	        			mSensorSimulator.mRefreshEmulatorOrientationLabel.setText("-");
	        		} else if (inputLine.compareTo(SensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.mSupportedTemperature.isSelected()) {
	        			out.println("" + mSensorSimulator.mEnabledTemperature.isSelected());
	        			mSensorSimulator.mEnabledTemperature.setSelected(enable);
	        			mSensorSimulator.mRefreshEmulatorThermometerLabel.setText("-");
	        		} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}
	        	//////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("getNumSensorValues()") == 0) {
	    	        inputLine = in.readLine();
	        		if (inputLine.compareTo(SensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.mSupportedAccelerometer.isSelected()) {
	        			out.println("3");
	        		} else if (inputLine.compareTo(SensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.mSupportedMagneticField.isSelected()) {
	        			out.println("3");
	        		} else if (inputLine.compareTo(SensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.mSupportedOrientation.isSelected()) {
	        			out.println("3");
	        		} else if (inputLine.compareTo(SensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.mSupportedTemperature.isSelected()) {
	        			out.println("1");
	        		} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}
	        	//////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("readSensor()") == 0) {
	        		inputLine = in.readLine();
	        		if (inputLine.compareTo(SensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.mSupportedAccelerometer.isSelected()) {
	        			if (mSensorSimulator.mEnabledAccelerometer.isSelected()) {
	        				//out.println("3"); // number of data following
		        			//out.println(mSensorSimulator.mobile.read_accelx);
		        			//out.println(mSensorSimulator.mobile.read_accely);
		        			//out.println(mSensorSimulator.mobile.read_accelz);
	        				
	        				// For performance reasons, send these commands together
	        				String sensorData = "3\n"  // number of data following
	        					+ mSensorSimulator.mobile.read_accelx + "\n" 
	        					+ mSensorSimulator.mobile.read_accely + "\n" 
	        					+ mSensorSimulator.mobile.read_accelz; 
	        				out.println(sensorData);
		        			mSensorSimulator.updateEmulatorAccelerometerRefresh();
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.mSupportedMagneticField.isSelected()) {
	        			if (mSensorSimulator.mEnabledMagneticField.isSelected()) {
		        			//out.println("3"); // number of data following
		        			//out.println(mSensorSimulator.mobile.read_compassx);
		        			//out.println(mSensorSimulator.mobile.read_compassy);
		        			//out.println(mSensorSimulator.mobile.read_compassz);
		        			
		        			// For performance reasons, send these commands together
	        				String sensorData = "3\n"  // number of data following
	        					+ mSensorSimulator.mobile.read_compassx + "\n" 
	        					+ mSensorSimulator.mobile.read_compassy + "\n" 
	        					+ mSensorSimulator.mobile.read_compassz; 
	        				out.println(sensorData);
		        			mSensorSimulator.updateEmulatorCompassRefresh();
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.mSupportedOrientation.isSelected()) {
	        			if (mSensorSimulator.mEnabledOrientation.isSelected()) {
			        		//out.println("3"); // number of data following
		        			//out.println(mSensorSimulator.mobile.read_yaw);
		        			//out.println(mSensorSimulator.mobile.read_pitch);
		        			//out.println(mSensorSimulator.mobile.read_roll);
		        			
		        			// For performance reasons, send these commands together
	        				String sensorData = "3\n"  // number of data following
	        					+ mSensorSimulator.mobile.read_yaw + "\n" 
	        					+ mSensorSimulator.mobile.read_pitch + "\n" 
	        					+ mSensorSimulator.mobile.read_roll; 
	        				out.println(sensorData);
		        			mSensorSimulator.updateEmulatorOrientationRefresh();
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.mSupportedTemperature.isSelected()) {
	        			if (mSensorSimulator.mEnabledTemperature.isSelected()) {
				        	//out.println("1"); // number of data following
				        	//out.println(mSensorSimulator.mobile.read_temperature);
				        	
				        	// For performance reasons, send these commands together
				        	// (yes, we need a fast thermometer)
	        				String sensorData = "1\n"  // number of data following
	        					+ mSensorSimulator.mobile.read_temperature; 
	        				out.println(sensorData);
		        			mSensorSimulator.updateEmulatorThermometerRefresh();
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
	        		if (inputLine.compareTo(SensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.mSupportedAccelerometer.isSelected()) {
	        			updatesList = mSensorSimulator.getSafeDoubleList(mSensorSimulator.mUpdateRatesAccelerometerText);
	        			if (updatesList == null || updatesList.length < 1) {
	        				out.println("0");
	        			} else {
	        				len = updatesList.length;
		        			out.println("" + len);
	        				for (int i=0; i<len; i++) {
	        					out.println("" + updatesList[i]);
	        				}
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.mSupportedMagneticField.isSelected()) {
	        			updatesList = mSensorSimulator.getSafeDoubleList(mSensorSimulator.mUpdateRatesCompassText);
	        			if (updatesList == null || updatesList.length < 1) {
	        				out.println("0");
	        			} else {
	        				len = updatesList.length;
		        			out.println("" + len);
	        				for (int i=0; i<len; i++) {
	        					out.println("" + updatesList[i]);
	        				}
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.mSupportedOrientation.isSelected()) {
	        			updatesList = mSensorSimulator.getSafeDoubleList(mSensorSimulator.mUpdateRatesOrientationText);
	        			if (updatesList == null || updatesList.length < 1) {
	        				out.println("0");
	        			} else {
	        				len = updatesList.length;
		        			out.println("" + len);
	        				for (int i=0; i<len; i++) {
	        					out.println("" + updatesList[i]);
	        				}
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.mSupportedTemperature.isSelected()) {
	        			updatesList = mSensorSimulator.getSafeDoubleList(mSensorSimulator.mUpdateRatesThermometerText);
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
	    	        if (inputLine.compareTo(SensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.mSupportedAccelerometer.isSelected()) {
	        			if (mSensorSimulator.mEnabledAccelerometer.isSelected()) {
	        				updatesPerSecond = mSensorSimulator.getSafeDouble(
	        						mSensorSimulator.mCurrentUpdateRateAccelerometerText, 0);
	        				out.println("" + updatesPerSecond);
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.mSupportedMagneticField.isSelected()) {
	        			if (mSensorSimulator.mEnabledMagneticField.isSelected()) {
	        				updatesPerSecond = mSensorSimulator.getSafeDouble(
	        						mSensorSimulator.mCurrentUpdateRateCompassText, 0);
	        				out.println("" + updatesPerSecond);
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.mSupportedOrientation.isSelected()) {
	        			if (mSensorSimulator.mEnabledOrientation.isSelected()) {
	        				updatesPerSecond = mSensorSimulator.getSafeDouble(
	        						mSensorSimulator.mCurrentUpdateRateOrientationText, 0);
	        				out.println("" + updatesPerSecond);
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.mSupportedTemperature.isSelected()) {
	        			if (mSensorSimulator.mEnabledTemperature.isSelected()) {
	        				updatesPerSecond = mSensorSimulator.getSafeDouble(
	        						mSensorSimulator.mCurrentUpdateRateThermometerText, 0);
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
	    	        if (inputLine.compareTo(SensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.mSupportedAccelerometer.isSelected()) {
        				out.println("OK");
        				inputLine = in.readLine();
        				updatesPerSecond = Float.parseFloat(inputLine);
        				mSensorSimulator.mCurrentUpdateRateAccelerometerText.setText(inputLine);
	        		} else if (inputLine.compareTo(SensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.mSupportedMagneticField.isSelected()) {
        				out.println("OK");
        				inputLine = in.readLine();
        				updatesPerSecond = Float.parseFloat(inputLine);
        				mSensorSimulator.mCurrentUpdateRateCompassText.setText(inputLine);
        			} else if (inputLine.compareTo(SensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.mSupportedOrientation.isSelected()) {
        				out.println("OK");
        				inputLine = in.readLine();
        				updatesPerSecond = Float.parseFloat(inputLine);
        				mSensorSimulator.mCurrentUpdateRateOrientationText.setText(inputLine);
        			} else if (inputLine.compareTo(SensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.mSupportedTemperature.isSelected()) {
        				out.println("OK");
        				inputLine = in.readLine();
        				updatesPerSecond = Float.parseFloat(inputLine);
        				mSensorSimulator.mCurrentUpdateRateThermometerText.setText(inputLine);
        			} else {
	        			// This sensor is not supported
	        			out.println("throw IllegalArgumentException");
	        		}
	    	    //////////////////////////////////////////////////////////
		        } else if (inputLine.compareTo("unsetSensorUpdateRate()") == 0) {
	    	        inputLine = in.readLine();
	    	        if (inputLine.compareTo(SensorSimulator.ACCELEROMETER) == 0
	        				&& mSensorSimulator.mSupportedAccelerometer.isSelected()) {
	        			if (mSensorSimulator.mEnabledAccelerometer.isSelected()) {
	        				out.println("OK");
	        				mSensorSimulator.mCurrentUpdateRateAccelerometerText.setText(
	        						mSensorSimulator.mDefaultUpdateRateAccelerometerText.getText());
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.MAGNETIC_FIELD) == 0
	        				&& mSensorSimulator.mSupportedMagneticField.isSelected()) {
	        			if (mSensorSimulator.mEnabledMagneticField.isSelected()) {
	        				out.println("OK");
	        				mSensorSimulator.mCurrentUpdateRateCompassText.setText(
	        						mSensorSimulator.mDefaultUpdateRateCompassText.getText());
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.ORIENTATION) == 0
	        				&& mSensorSimulator.mSupportedOrientation.isSelected()) {
	        			if (mSensorSimulator.mEnabledOrientation.isSelected()) {
	        				out.println("OK");
	        				mSensorSimulator.mCurrentUpdateRateOrientationText.setText(
	        						mSensorSimulator.mDefaultUpdateRateOrientationText.getText());
	        			} else {
	        				// This sensor is currently disabled
	        				out.println("throw IllegalStateException");
	        			}
	        		} else if (inputLine.compareTo(SensorSimulator.TEMPERATURE) == 0
	        				&& mSensorSimulator.mSupportedTemperature.isSelected()) {
	        			if (mSensorSimulator.mEnabledTemperature.isSelected()) {
	        				out.println("OK");
	        				mSensorSimulator.mCurrentUpdateRateThermometerText.setText(
	        						mSensorSimulator.mDefaultUpdateRateThermometerText.getText());
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
	        	//outputLine = inputLine;
	        	//mSensorSimulator.yawSlider.setValue(Integer.parseInt(inputLine));
	             //out.println(outputLine);
	             //if (outputLine.equals("Bye."))
	                //break;
	        }
	        out.close();
	        in.close();
	        mClientSocket.close();
	        
        } catch (IOException e) {
        	if (talking) {
	        	System.err.println("IOException in SensorServerThread.");
	            // System.exit(1);
	        	try {
	        		if (mClientSocket != null) mClientSocket.close();
	            } catch (IOException e2) {
	                System.err.println("Close failed as well.");
	                // System.exit(1);
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

    public String[] getSupportedSensors() {
    	String[] sensorList = new String[4]; // currently max. 4 possible!
		int sensorMax = 0;
		if (mSensorSimulator.mSupportedAccelerometer.isSelected()) {
			sensorList[sensorMax] = SensorSimulator.ACCELEROMETER;
			sensorMax++;
		}
		if (mSensorSimulator.mSupportedMagneticField.isSelected()) {
			sensorList[sensorMax] = SensorSimulator.MAGNETIC_FIELD;
			sensorMax++;
		}
		if (mSensorSimulator.mSupportedOrientation.isSelected()) {
			sensorList[sensorMax] = SensorSimulator.ORIENTATION;
			sensorMax++;
		}
		if (mSensorSimulator.mSupportedTemperature.isSelected()) {
			sensorList[sensorMax] = SensorSimulator.TEMPERATURE;
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
    	// close the socket
    	try {
    		talking = false;
    		mClientSocket.close();
        } catch (IOException e) {
            System.err.println("Close failed.");
            System.exit(1);
        }
    }
}
