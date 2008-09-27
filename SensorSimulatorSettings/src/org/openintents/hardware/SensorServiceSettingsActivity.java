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

package org.openintents.hardware;

import org.openintents.R;
import org.openintents.hardware.SensorEventListener.OnSensorListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class SensorServiceSettingsActivity extends Activity {
	
	/**
	 * TAG for logging.
	 */
	private static final String TAG = "SensorServiceSettingsActivity";

	SensorEventListener mSensorListener;
	
    TextView mCallbackText;


    /**
     * Standard initialization of this activity.  Set up the UI, then wait
     * for the user to poke it before doing anything.
     */
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.sensor_service_settings);
        
        mSensorListener = new SensorEventListener(this);

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.bind);
        button.setOnClickListener(mBindListener);
        button = (Button)findViewById(R.id.unbind);
        button.setOnClickListener(mUnbindListener);
        
        mCallbackText = (TextView)findViewById(R.id.callback);
        mCallbackText.setText("Not attached.");
    }

    private OnClickListener mBindListener = new OnClickListener() {
        public void onClick(View v) {
            mSensorListener.bindService();
            mSensorListener.setOnSensorListener(mOnSensorListener);
            mCallbackText.setText("Binding.");
        }
    };

    private OnClickListener mUnbindListener = new OnClickListener() {
        public void onClick(View v) {
        	mSensorListener.unbindService();
        	mSensorListener.setOnSensorListener(null);
        	mCallbackText.setText("Unbinding.");
        }
    };
    
    private OnSensorListener mOnSensorListener = new OnSensorListener() {
		
		public boolean onSensorEvent(final SensorEvent event) {
			Log.i(TAG, "onSensorEvent: " + event);
            int action = event.getAction();
			switch (action) {
			case SensorEvent.ACTION_MOVE:
				mCallbackText.setText("Received from service: " + event.getX());
				return true;
			case SensorEvent.ACTION_SHAKE:
				mCallbackText.setText("SHAKE: " + event.getX() + ", " + event.getEventTime());
				return true;
			default:
				assert false;
			}
			return false;
		}
    	
    };

}


