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

package org.openintents.sensorsimulator.hardware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import org.openintents.sensorsimulator.db.SensorSimulator;
import org.openintents.sensorsimulator.db.SensorSimulatorConvenience;

import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

final class SensorSimulatorClient {
	
	/**
	 * TAG for logging.
	 */
	static final String TAG = "Hardware";
	private static final String TAG2 = "Hardware2";
	
	/**
	 * Whether to log sensor protocol data 
	 * (send and receive) through LogCat.
	 * This is very instructive, but time-consuming.
	 */
	private static final boolean LOG_PROTOCOL = false;
	
	private Context mContext;
	private SensorSimulatorConvenience mSensorSimulatorConvenience;
	
	protected boolean connected;
	
	Socket mSocket;
    PrintWriter mOut;
    BufferedReader mIn;
    
    private /*static final*/ ArrayList<Listener> mListeners = new ArrayList<Listener>();
	
	protected SensorSimulatorClient(Context context) {
		connected = false;
		mContext = context;
		mSensorSimulatorConvenience = new SensorSimulatorConvenience(mContext);
	}
	
	protected void connect() {
		
		// if (connected) return;

        mSocket = null;
        mOut = null;
        mIn = null;

        Log.i(TAG, "Starting connection...");
        
        // get Info from ContentProvider
        
        String ipaddress = mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_IPADDRESS);
        String socket = mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_SOCKET);
        
        Log.i(TAG, "Connecting to " + ipaddress + " : " + socket);
        
        try {
            mSocket = new Socket(ipaddress, Integer.parseInt(socket));
        	/*
        	 * !!!!! Socket with timeout does not work due to bug in Android SDK:
        	 * http://groups.google.com/group/android-developers/browse_thread/thread/bd9a4057713d9f50/c76645b31078445a
            
            // socket with timeout:
        	mSocket = new Socket();
        	SocketAddress sockaddr = new InetSocketAddress(ipaddress, Integer.parseInt(socket));
            
        	int timeoutMs = 3000; // 3 seconds
        	mSocket.connect(sockaddr, timeoutMs);
        	*/
        	
            mOut = new PrintWriter(mSocket.getOutputStream(), true);
            mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        } catch (UnknownHostException e) {
            //System.err.println("Don't know about host: ");
            //System.exit(1);
        	Log.e(TAG, "Don't know about host: " + ipaddress + " : " + socket);
        	return;
        } catch (SocketTimeoutException e) {
        	Log.e(TAG, "Connection time out: " + ipaddress + " : " + socket);
        	return;
        } catch (IOException e) {
            Log.e(TAG, "Couldn't get I/O for the connection to: " + ipaddress + " : " + socket);
            Log.e(TAG, "---------------------------------------------------------------");
            Log.e(TAG, "Do you have the following permission in your manifest?");
            Log.e(TAG, "<uses-permission android:name=\"android.permission.INTERNET\"/>");
            Log.e(TAG, "---------------------------------------------------------------");
            System.exit(1);
        }

        Log.i(TAG, "Read line...");
        
        String fromServer = "";
        try {
        	fromServer = mIn.readLine();
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
		Log.i(TAG, "Received: " + fromServer);
        
		if (fromServer.equals("SensorSimulator")) {
			// OK
			connected = true;
			Log.i(TAG, "Connected");
		} else {
			// Who is that???
			Log.i(TAG, "Problem connecting: Wrong string sent.");
			disconnect();
		}
               
	}
	
	protected void disconnect() {
		if (connected) {
			Log.i(TAG, "Disconnect()");
	        
			try {
				mOut.close();
		        mIn.close();
		        mSocket.close();
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
	            System.exit(1);
			}
			
			connected = false;
		} else {
			// aldready disconnected. Nothing to do.
		}
	}
	
	protected int getSensors() {
		Log.i(TAG, "getSensors");
		String[] sensornames = getSupportedSensors();
		Log.i(TAG, "sensornames: " + sensornames.length);
		int sensors = SensorNames.getSensorsFromNames(sensornames);
		Log.i(TAG, "sensors: " + sensors);
		return sensors;
	}

	/** Delay in milliseconds */
	private int DELAY_MS_FASTEST = 0;
	private int DELAY_MS_GAME = 20;
	private int DELAY_MS_UI = 60;
	private int DELAY_MS_NORMAL = 200;
	
	protected boolean registerListener(SensorListener listener, int sensors, int rate) {

        int delay = -1;
        switch(rate)
        {
        case SensorManager.SENSOR_DELAY_FASTEST:
            delay = DELAY_MS_FASTEST;
            break;

        case SensorManager.SENSOR_DELAY_GAME:
            delay = DELAY_MS_GAME;
            break;

        case SensorManager.SENSOR_DELAY_UI:
            delay = DELAY_MS_UI;
            break;

        case SensorManager.SENSOR_DELAY_NORMAL:
            delay = DELAY_MS_NORMAL;
            break;

        default:
            return false;
        }
        boolean result;
        synchronized(mListeners)
        {
            Listener l = null;
            Iterator<Listener> iter = mListeners.iterator();
            while (iter.hasNext())
            {
                Listener i = iter.next();
                if(i.mSensorListener == listener) {
                	l = i;
                    break;
                }
            }
            if(l == null)
            {
                l = new Listener(listener, sensors, delay);
                result = enableSensor(sensors, delay);
                if(result)
                {
                    mListeners.add(l);
                    mListeners.notify();
                }
            } else
            {
                result = enableSensor(sensors, delay);
                if(result) {
                    l.addSensors(sensors, delay);
                }
            }
        }

        return result;
	}

	protected boolean registerListener(SensorListener listener, int sensors) {
		return registerListener(listener, sensors, SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void unregisterListener(SensorListener listener, int sensors) {
	    synchronized(mListeners)
        {
            int size = mListeners.size();
            int i = 0;
            do
            {
                if(i >= size)
                    break;
                Listener l = (Listener)mListeners.get(i);
                if(l.mSensorListener == listener)
                {
                    enableSensor(sensors, -1);
                    if(l.removeSensors(sensors) == 0)
                        mListeners.remove(i);
                    break;
                }
                i++;
            } while(true);
        }
	}

	protected void unregisterListener(SensorListener listener) {
        unregisterListener(listener, SensorManager.SENSOR_ALL);
	}


	/////////////////////////////////////////////////////
	// Internal functions
	
    private class Listener
    {
        private SensorListener mSensorListener;
        private int mSensors;
        private int mDelay;
        private long mNextUpdateTime;
        
        int addSensors(int sensors, int delay)
        {
            mSensors |= sensors;
            if (delay < mDelay) {
            	mDelay = delay;
            	mNextUpdateTime = 0;
            }
            return mSensors;
        }

        int removeSensors(int sensors)
        {
            mSensors &= ~sensors;
            return mSensors;
        }

        boolean hasSensor(int sensor)
        {
            return (mSensors & sensor) != 0;
        }

        Listener(SensorListener listener, int sensors, int delay)
        {
        	mSensorListener = listener;
        	mSensors = sensors;
        	mDelay = delay;
        	mNextUpdateTime = 0;
        }
    }
    
    /**
     * Enables the sensors.
     * 
     * @param sensors
     * @param delay
     * @return True if at least one of the sensors could be enabled.
     */
    private boolean enableSensor(int sensors, int delay) {
    	String[] sensornames = SensorNames.getSensorNames(sensors);
    	boolean result = false;
    	for (String sensor : sensornames) {
    		try {
    			if (delay == -1) {
    				disableSensor(sensor);
    			} else {
    				enableSensor(sensor);
    				float updatesPerSecond = 1000;
    				if (delay > 0) {
    					updatesPerSecond = 1000 / delay;
    				}
    				setSensorUpdateRate(sensor, updatesPerSecond);
    			}
    			result = true;
    		} catch (IllegalArgumentException e) {
    			Log.d(TAG, "Sensor " + sensor + " not supported");
    		}
    	}
    	
    	if (!mHandler.hasMessages(MSG_UPDATE_SENSORS)) {
    		mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SENSORS));
    	}
    	return result;
    }
    
    
    private static final int MSG_UPDATE_SENSORS = 1;
    
    private static int MAX_SENSOR = 5;
    private float[][] mValues = new float[MAX_SENSOR][];
    private boolean[] mValuesCached = new boolean[MAX_SENSOR];
    
	/** Handle the process of updating sensors */
    protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE_SENSORS) {
				
            	// Let us go through all sensors,
            	// and only read out the data if it is time for
            	// that particular sensor.
            	
            	long current = SystemClock.uptimeMillis();
            	long nextTime = current + DELAY_MS_NORMAL; // Largest time
            	
            	// Clear the sensor values' cache
            	for (int i = 0; i < MAX_SENSOR; i++) {
            		mValuesCached[i] = false;
            		if (mValues[i] == null) {
            			Log.d(TAG, "Create cache for sensor " + i);
            			mValues[i] = new float[3];
            		}
            	}

            	for (Listener l : mListeners) {
            		if (current >= l.mNextUpdateTime) {
            			// do the update:
            			
            			// Go through all sensors for this listener
            			int sensorbit = 1;
            			for (int i = 0; i < MAX_SENSOR; i++) {
            				if (hasSensor(l.mSensors, sensorbit)) {
            					// Get current sensor values (if not yet cached)
            					if (!mValuesCached[i]) {
            						readSensor(sensorbit, mValues[i]);
            						mValuesCached[i] = true;
            					}
            					// Send the current sensor values to this listener
            					l.mSensorListener.onSensorChanged(sensorbit, mValues[i]);
            				}
            				
            				// switch to next sensor
            				sensorbit <<= 1;
            			}
            			
            			// Set next update time:
            			l.mNextUpdateTime += l.mDelay;
            			
            			// Now in case we are already behind the schedule,
            			// do the following update as soon as possible
            			// (but don't drag behind schedule forever -
            			//  i.e. skip the updates that are already past.)
            			if (l.mNextUpdateTime < current) {
            				l.mNextUpdateTime = current;
            			}
            		}
            		if (l.mNextUpdateTime < nextTime) {
            			nextTime = l.mNextUpdateTime;
            		}
            	}
            	
				
                // readAllSensorsUpdate();
            	
                if (mListeners.size() > 0) {
                	// Autoupdate
                	sendMessageAtTime(obtainMessage(MSG_UPDATE_SENSORS), nextTime);
                }
            }

		}
	};
	

	/////////////////////////////////////////////////////////////
	// Bridge to old API

	static boolean hasSensor(int sensors, int sensor) {
    	return (sensors & sensor) != 0;
    }
    
    private void readSensor(int sensorbit, float[] sensorValues) {
    	String sensorname = SensorNames.getSensorName(sensorbit);
    	try {
    		readSensor(sensorname, sensorValues);
    	} catch (IllegalStateException e) {
    		// Sensor is currently not enabled.
    		// For the new API (SDK 0.9 and higher) we catch this exception
    		// and do not pass it further.
    		Log.d(TAG, "Sensor not enabled -> enable it now");
    		try {
    			enableSensor(sensorname);
    		} catch (IllegalArgumentException e2) {
    			// Sensor is not supported
        		// For the new API (SDK 0.9 and higher) we catch this exception
        		// and do not pass it further.
        		Log.d(TAG, "Sensor not supported.");
    		}
    	}
	}

    protected int getNumSensorValues(int sensorbit) {
		String sensorname = SensorNames.getSensorName(sensorbit);
    	return getNumSensorValues(sensorname);
	}
	
	//////////////////////////////////////////////////////////////////
	// DEPRECATED FUNCTIONS FOLLOW
	

	protected void disableSensor(String sensor) {
		Log.i(TAG2, "disableSensor()");
		mOut.println("disableSensor()");
		Log.i(TAG2, "Send: " + sensor);
        mOut.println(sensor);
        
		try {
			String answer = mIn.readLine();
			if (answer.compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException(
						"Sensor '" + sensor
						+ "' is not supported.");
			}
			Log.i(TAG2, "Received: " + answer);
			
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
	}

    protected void enableSensor(String sensor) {
		Log.i(TAG2, "enableSensor()");
		mOut.println("enableSensor()");
		Log.i(TAG2, "Send: " + sensor);
        mOut.println(sensor);
		
		try {
			String answer = mIn.readLine();
			if (answer.compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException(
						"Sensor '" + sensor
						+ "' is not supported.");
			}
			Log.i(TAG2, "Received: " + answer);
			
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
	}
	
    protected String[] getSupportedSensors() {
		Log.i(TAG, "getSupportedSensors()");
        
		mOut.println("getSupportedSensors()");
		String[] sensors = {""};
		int num = 0;
		
		try {
			String numstr = mIn.readLine();
			Log.i(TAG, "Received: " + numstr);
	        
			num = Integer.parseInt(numstr);
			
			sensors = new String[num];
			for (int i=0; i<num; i++) {
				sensors[i] = mIn.readLine();
				Log.i(TAG, "Received: " + sensors[i]);
		        
			}
			
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
		
		return sensors;
	}
	
    protected int getNumSensorValues(String sensor) {
		if (LOG_PROTOCOL) Log.i(TAG, "Send: getNumSensorValues()");
		mOut.println("getNumSensorValues()");
		
		if (LOG_PROTOCOL) Log.i(TAG, "Send: " + sensor);
        mOut.println(sensor);
        
		int num = 0;
		
		try {
			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException(
						"Sensor '" + sensor
						+ "' is not supported.");
			}
			if (LOG_PROTOCOL) Log.i(TAG, "Received: " + numstr);
	        
			num = Integer.parseInt(numstr);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
		
		return num;
	}
	
    protected void readSensor(String sensor, float[] sensorValues) {
		if (sensorValues == null) {
			throw new NullPointerException (
					"readSensor for '" + sensor
					+ "' called with sensorValues == null.");
		}
		if (LOG_PROTOCOL) Log.i(TAG, "Send: getNumSensorValues()");
		//mOut.println("readSensor()");
		//if (LOG_PROTOCOL) Log.i(TAG, "Send: " + sensor);
        //mOut.println(sensor);
		// For performance reasons, send these commands together
		mOut.println("readSensor()\n" + sensor);
		int num = 0;
		
		try {
			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException(
						"Sensor '" + sensor
						+ "' is not supported.");
			} else if (numstr.compareTo("throw IllegalStateException") == 0){
				throw new IllegalStateException(
						"Sensor '" + sensor
						+ "' is currently not enabled.");
			}
			if (LOG_PROTOCOL) Log.i(TAG, "Received: " + numstr);
	        num = Integer.parseInt(numstr);
	        
	        if (sensorValues.length < num) {
	        	throw new ArrayIndexOutOfBoundsException (
						"readSensor for '" + sensor
						+ "' called with sensorValues having too few elements ("
						+ sensorValues.length + ") to hold the sensor values ("
						+ num + ").");
	        }
			
			//sensorValues = new float[num];
			for (int i=0; i<num; i++) {
				String val = mIn.readLine();
				if (LOG_PROTOCOL) Log.i(TAG, "Received: " + val);
		        
				sensorValues[i] = Float.parseFloat(val);
			}
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
	}
	
    protected float[] getSensorUpdateRates(String sensor) {
		if (LOG_PROTOCOL) Log.i(TAG, "getSensorUpdateRates()");
        
		mOut.println("getSensorUpdateRates()");
		float[] rates = null;
		if (LOG_PROTOCOL) Log.i(TAG, "Send: " + sensor);
        mOut.println(sensor);
        int num = 0;
		
		try {
			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException(
						"Sensor '" + sensor
						+ "' is not supported.");
			}
			if (LOG_PROTOCOL) Log.i(TAG, "Received: " + numstr);
	        
			num = Integer.parseInt(numstr);
			
			if (num > 0) {
				rates = new float[num];
				for (int i=0; i<num; i++) {
					rates[i] = Float.parseFloat(mIn.readLine());
					if (LOG_PROTOCOL) Log.i(TAG, "Received: " + rates[i]);
				}
			} else {
				rates = null;
			}
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
		
		return rates;
			
	}
	
    protected float getSensorUpdateRate(String sensor) {
		Log.i(TAG, "getSensorUpdateRate()");
        
		mOut.println("getSensorUpdateRate()");
		float rate = 0;
		Log.i(TAG, "Send: " + sensor);
        mOut.println(sensor);
        
		try {
			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException(
						"Sensor '" + sensor
						+ "' is not supported.");
			} else if (numstr.compareTo("throw IllegalStateException") == 0){
				throw new IllegalStateException(
						"Sensor '" + sensor
						+ "' is currently not enabled.");
			}
			
			Log.i(TAG, "Received: " + numstr);
	        
			rate = Float.parseFloat(numstr);
			Log.i(TAG, "Received: " + rate);
			
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
		
		return rate;		
	}
	
    protected void setSensorUpdateRate(String sensor, float updatesPerSecond) {
		Log.i(TAG, "setSensorUpdateRate()");
        
		mOut.println("setSensorUpdateRate()");
		
		Log.i(TAG, "Send: " + sensor);
        mOut.println(sensor);
        
		try {
			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException(
						"Sensor '" + sensor
						+ "' is not supported.");
			}			
			Log.i(TAG, "Received: " + numstr); // should be numstr=="OK"
	        
			Log.i(TAG, "Send: " + updatesPerSecond);
	        mOut.println("" + updatesPerSecond);			
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
	}
	
    protected void unsetSensorUpdateRate(String sensor) {
		Log.i(TAG, "unsetSensorUpdateRate()");
        
		mOut.println("unsetSensorUpdateRate()");
		
		Log.i(TAG, "Send: " + sensor);
        mOut.println(sensor);
        
		try {
			String numstr = mIn.readLine();
			if (numstr.compareTo("throw IllegalArgumentException") == 0) {
				throw new IllegalArgumentException(
						"Sensor '" + sensor
						+ "' is not supported.");
			}
			Log.i(TAG, "Received: " + numstr); // should be numstr=="OK"
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: x.x.x.x.");
            System.exit(1);
		}
	}
	
}
