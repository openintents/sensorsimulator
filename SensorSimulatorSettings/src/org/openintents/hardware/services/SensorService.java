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

package org.openintents.hardware.services;

import org.openintents.R;
import org.openintents.hardware.SensorEvent;
import org.openintents.hardware.SensorServiceSettingsActivity;
import org.openintents.hardware.Sensors;
import org.openintents.provider.Hardware;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * A service that watches sensors and sends high-level notifications 
 * to applications.
 * 
 * 
 */
public class SensorService extends Service {
	
	private static final int MSG_UPDATE_SENSOR = 1;
    
    /**
     * List of callbacks that have been registered with the
     * service.  Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    final RemoteCallbackList<ISensorServiceCallback> mCallbacks
            = new RemoteCallbackList<ISensorServiceCallback>();
    
    int mValue = 0;
    NotificationManager mNM;
    
    //////////////////////////////////////////////////////
    // Public settings that are read from preferences.
    
    public boolean use_sensor_simulator = true;
    
    /** 
     * Shake detection threshold.
     */
    public float accelerometer_shake_threshold = 1.2f;
    
    //////////////////////////////////////////////////////
    // Internal constants.
    
    /**
     * Internal conversion factor for faster processing.
     */
    static final int ACCELEROMETER_FLOAT_TO_INT = 1024;
    
    //////////////////////////////////////////////////////
    // Internal variables.
    
    int mACCELEROMETER_SHAKE_THRESHOLD_SQUARE;
    
    @Override
    public void onCreate() {

        // For Sensors:
		Hardware.mContentResolver = getContentResolver();
		
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification.
        showNotification();
        
        readPreferences();
        
        calculateConstants();
        
        // Connect to Sensor simulator
        if (use_sensor_simulator) {
        	Sensors.connectSimulator();
        }
        
        // Enable sensor
        Sensors.enableSensor(Sensors.SENSOR_ACCELEROMETER);
        
        
        
        // While this service is running, it will continually increment a
        // number.  Send the first message that is used to perform the
        // increment.
        mSensorHandler.sendEmptyMessage(MSG_UPDATE_SENSOR);
    }

    @Override
    public void onDestroy() {
    	
    	// Disable sensor
    	Sensors.disableSensor(Sensors.SENSOR_ACCELEROMETER);
    	
    	// Disconnect from Sensor simulator
    	if (use_sensor_simulator) {
            Sensors.disconnectSimulator();
    	}
    	
        // Cancel the persistent notification.
        mNM.cancel(R.string.sensor_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.sensor_service_stopped, Toast.LENGTH_SHORT).show();
        
        // Unregister all callbacks.
        mCallbacks.kill();
        
        // Remove the next pending message to increment the counter, stopping
        // the increment loop.
        mSensorHandler.removeMessages(MSG_UPDATE_SENSOR);
    }
    

    @Override
    public IBinder onBind(Intent intent) {
        // Select the interface to return.  If your service only implements
        // a single interface, you can just return it here without checking
        // the Intent.
        if (ISensorService.class.getName().equals(intent.getAction())) {
            return mBinder;
        }
        return null;
    }

    /**
     * The IRemoteInterface is defined through IDL
     */
    private final ISensorService.Stub mBinder = new ISensorService.Stub() {
        public void registerCallback(ISensorServiceCallback cb) {
            if (cb != null) mCallbacks.register(cb);
        }
        public void unregisterCallback(ISensorServiceCallback cb) {
            if (cb != null) mCallbacks.unregister(cb);
        }
    };
    
    /**
     * Reads preferences from common database.
     */
    void readPreferences() {
    	// TODO: Read from preferences file.
    }
    
    /**
     * Calculates constants.
     */
    void calculateConstants() {
    	int t = (int)(ACCELEROMETER_FLOAT_TO_INT * accelerometer_shake_threshold);
    	mACCELEROMETER_SHAKE_THRESHOLD_SQUARE = t * t;
    }
    
    /**
     * Called periodically to analyze sensor data.
     */
    private final Handler mSensorHandler = new Handler() {

        int action = SensorEvent.ACTION_VOID;
        float x = 0;
        float y = 0;
        float z = 0;
        long eventTime = 0;
        
        @Override public void handleMessage(Message msg) {

            
            switch (msg.what) {
                
                // It is time to bump the value!
                case MSG_UPDATE_SENSOR:
                	// clearAction();
                	
                    testAccelerometerShake();
                    
                    // Repeat every 1 second.
                    sendMessageDelayed(obtainMessage(MSG_UPDATE_SENSOR), 1*100);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        private void clearAction() {
            action = SensorEvent.ACTION_VOID;
            x = 0;
            y = 0;
            z = 0;
            eventTime = 0;
        }
        
        private void testAccelerometerShake() {
        	// Read out sensor values
            int num = Sensors.getNumSensorValues(Sensors.SENSOR_ACCELEROMETER);
			float[] val = new float[num];
			Sensors.readSensor(Sensors.SENSOR_ACCELEROMETER, val);
			
			int ax = (int)(ACCELEROMETER_FLOAT_TO_INT * val[0]);
			int ay = (int)(ACCELEROMETER_FLOAT_TO_INT * val[1]);
			int az = (int)(ACCELEROMETER_FLOAT_TO_INT * val[2]);
			
			int len2 = ax * ax + ay * ay + az * az;
			
			if (len2 > mACCELEROMETER_SHAKE_THRESHOLD_SQUARE) {
				// Shaking
				action = SensorEvent.ACTION_SHAKE;
				x = val[0];
				y = val[1];
				z = val[2];
				eventTime = SystemClock.uptimeMillis();
				
				broadcastAction();
			}
        }
        
		private void broadcastAction() {
			// Broadcast to all clients the new value.
			final int N = mCallbacks.beginBroadcast();
			for (int i=0; i<N; i++) {
			    try {
			        mCallbacks.getBroadcastItem(i).onISensorEvent(action, x, y, z, eventTime);
			    } catch (DeadObjectException e) {
			        // The RemoteCallbackList will take care of removing
			        // the dead object for us.
			    } catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mCallbacks.finishBroadcast();
		}
    };

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // This is who should be launched if the user selects our notification.
        Intent contentIntent = new Intent(this, SensorServiceSettingsActivity.class);

        // This is who should be launched if the user selects the app icon in the notification,
        // (in this case, we launch the same activity for both)
        Intent appIntent = new Intent(this, SensorServiceSettingsActivity.class);

        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.sensor_service_started);

        // TODO ???
        
        /*
        mNM.notify(R.string.sensor_service_started, // we use a string id because it is a unique
                                                    // number.  we use it later to cancel the
                                                    // notification
                   new Notification(
                       this,                        // our context
                       R.drawable.mobile_shake001a_32,      // the icon for the status bar
                       text,                        // the text to display in the ticker
                       System.currentTimeMillis(),  // the timestamp for the notification
                       getText(R.string.sensor_service), // the title for the notification
                       text,                        // the details to display in the notification
                       contentIntent,               // the contentIntent (see above)
                       R.drawable.mobile_shake_application001a,  // the app icon
                       getText(R.string.sensor_service_settings), // the name of the app
                       appIntent));                 // the appIntent (see above)
                       */
    }
}
