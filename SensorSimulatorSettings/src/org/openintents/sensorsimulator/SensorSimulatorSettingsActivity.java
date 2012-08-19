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

package org.openintents.sensorsimulator;

import java.text.DecimalFormat;

import org.openintents.sensorsimulator.db.SensorSimulator;
import org.openintents.sensorsimulator.db.SensorSimulatorConvenience;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;
import org.openintents.sensorsimulator.hardware.SensorNames;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

/**
 * Lets user set connection settings to the SensorSimulator and test them.
 * 
 * The connection is outbound to the SensorSimulator.
 * 
 * @author Peli
 *
 */
public class SensorSimulatorSettingsActivity extends Activity implements SensorListener {
	/**
	 * TAG for logging.
	 */
	private static final String TAG = "SensorSimulatorSettingsActivity";

    private SensorManagerSimulator mSensorManager;
    
	private EditText mEditTextIP;
	private EditText mEditTextSocket;
	
	// Indicators whether real device or sensor simulator is connected.
	private TextView mTextSensorType;
	
	private Button mButtonConnect;
	private Button mButtonDisconnect;
	
	private LinearLayout mSensorsList;
	
	DecimalFormat mDecimalFormat;
	
	String[] mSupportedSensors;
	
	/**
	 * Number of supported sensors.
	 */
	int mNumSensors;
	
	/**
	 * The list of currently enabled sensors.
	 * (Needed to determine which of the sensors
	 *  have to be updated regularly by new sensor data).
	 */
	boolean[] mSensorEnabled;
	
	/**
	 * Keep pointers to SingleSensorView
	 * so that we can update sensor data regularly.
	 */
	SingleSensorView[] mSingleSensorView;
	
	String[] mDelayTypes = new String[] { "Fastest", "Game", "UI", "Normal" };
	int[] mDelayValue = new int[] {
			SensorManager.SENSOR_DELAY_FASTEST,
			SensorManager.SENSOR_DELAY_GAME, 
			SensorManager.SENSOR_DELAY_UI, 
			SensorManager.SENSOR_DELAY_NORMAL};
	
	private TabHost mTabHost;
	
	private SensorSimulatorConvenience mSensorSimulatorConvenience;
    
	/**
	 * Called when activity starts.
	 * 
	 * This can either be the first time, or the user navigates back
	 * after the activity has been killed.
	 * 
	 * We do not automatically reconnect to the SensorSimulator,
	 * as it may not be available in the mean-time anymore or 
	 * the IP address may have changed.
	 * 
	 * (We do not know how long the activity had been dormant).
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		
		setContentView(R.layout.sensorsimulator);
		// SensorSimulatorConvenience.mContentResolver = getContentResolver();
		
		// Start with Android's sensor manager
		//mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
		
		mSensorSimulatorConvenience = new SensorSimulatorConvenience(this);
		
		Context context = this;
        // Get the Resources object from our context
        Resources res = context.getResources();
    
		mTabHost = (TabHost)findViewById(R.id.tabhost);
		mTabHost.setup();
		
		TabSpec tabspec = mTabHost.newTabSpec("settings");
		tabspec.setIndicator(res.getString(R.string.settings), res.getDrawable(R.drawable.settings001a_32));
		tabspec.setContent(R.id.content1);
		mTabHost.addTab(tabspec);
		
		tabspec = mTabHost.newTabSpec("testing");
		tabspec.setIndicator(res.getString(R.string.testing), res.getDrawable(R.drawable.mobile_shake001a_32));
		tabspec.setContent(R.id.content2);
		mTabHost.addTab(tabspec);
		
		mTabHost.setCurrentTab(0);
	
//		mEditText = (EditText) findViewById(R.id.edittext);
		mEditTextIP = (EditText) findViewById(R.id.ipaddress);
		mEditTextSocket = (EditText) findViewById(R.id.socket);
		
		mButtonConnect = (Button) findViewById(R.id.buttonconnect);
		mButtonConnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				connect();
			}
		});

		mButtonDisconnect = (Button) findViewById(R.id.buttondisconnect);
		mButtonDisconnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				disconnect();
			}
		});
		
		setButtonState();
		
		
		mEditTextIP.setText(mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_IPADDRESS));
		String s = mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_SOCKET);
		if (s.contentEquals("")) {
			s = SensorSimulator.DEFAULT_SOCKET;
		}
		mEditTextSocket.setText(s);
		
		mTextSensorType = (TextView) findViewById(R.id.datatype);
		
		// Format for output of data
		mDecimalFormat = new DecimalFormat("#0.00");
		
		
		readAllSensors(); // Basic sensor information
		
		mSensorsList = (LinearLayout) findViewById(R.id.sensordatalist);

		fillSensorList(); // Fills the sensor list manually, giving us more control
		
	}
	
	/**
	 * Called when activity comes to foreground.
	 */
    @Override
    protected void onResume() {
        super.onResume();
        
        mSensorManager.registerListener(this, 
                SensorManager.SENSOR_ACCELEROMETER | 
                SensorManager.SENSOR_MAGNETIC_FIELD | 
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }
    
    /**
     * Called when another activity is started.
     */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState (outState);
    }

	/**
	 * Called when the user leaves.
	 * Here we store the IP address and port.
	 */
    @Override
    protected void onPause() {
        super.onPause();

        String newIP = mEditTextIP.getText().toString();
		String newSocket = mEditTextSocket.getText().toString();
		String oldIP = mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_IPADDRESS);
		String oldSocket = mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_SOCKET);
		
		if (! (newIP.contentEquals(oldIP) && newSocket.contentEquals(oldSocket)) ) {
			// new values
	        mSensorManager.unregisterListener(this);
			mSensorManager.disconnectSimulator();
			
			// Save the values
			mSensorSimulatorConvenience.setPreference(SensorSimulator.KEY_IPADDRESS, newIP);
			mSensorSimulatorConvenience.setPreference(SensorSimulator.KEY_SOCKET, newSocket);
		
		}
	}
	
    ////////////////////////////////////////////////////////
    // The menu

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
				
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		}
		return super.onOptionsItemSelected(item);		
	}

	///////////////////////////////////////
	// Menu related functions

	///////////////////////////////////////
	
	public void connect() {
		Log.i(TAG, "Connect");
		String newIP = mEditTextIP.getText().toString();
		String newSocket = mEditTextSocket.getText().toString();
		String oldIP = mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_IPADDRESS);
		String oldSocket = mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_SOCKET);
		
		if (! (newIP.contentEquals(oldIP) && newSocket.contentEquals(oldSocket)) ) {
			// new values
	        mSensorManager.unregisterListener(this);
			mSensorManager.disconnectSimulator();
			
			// Save the values
			mSensorSimulatorConvenience.setPreference(SensorSimulator.KEY_IPADDRESS, newIP);
			mSensorSimulatorConvenience.setPreference(SensorSimulator.KEY_SOCKET, newSocket);
		}
		
		if (! mSensorManager.isConnectedSimulator() ) {
			Log.i(TAG, "Not connected yet -> Connect");
			mSensorManager.connectSimulator();
		}
		
		readAllSensors();
		
		setButtonState();
		
 		if (mSensorManager.isConnectedSimulator()) {
			mTextSensorType.setText(R.string.sensor_simulator_data);
		} else {
			mTextSensorType.setText(R.string.real_device_data);
		}
 		
 		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 		// TODO Is this an Android bug?? notifyDataSetChanged()
 		// call results in performance loss later.
 		// 
 		// I've asked about this here:
 		// http://groups.google.com/group/android-developers/browse_frm/thread/ad4a386116f2e915
 		//
 		// Keeping the line below works, but has really 
 		// slow performance, because for each small text change
 		// the whole row is recreated.
 		//
		// Now notify the ListAdapter of the changes:
//		mSensorListAdapter.notifyDataSetChanged();
		//mSensorListAdapter.notifyDataSetInvalidated();
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 		
 		fillSensorList();
		
	}
	
	public void disconnect() {
		mSensorManager.unregisterListener(this);
		mSensorManager.disconnectSimulator();
		
		readAllSensors();
		
		setButtonState();
		
 		if (mSensorManager.isConnectedSimulator()) {
			mTextSensorType.setText(R.string.sensor_simulator_data);
		} else {
			mTextSensorType.setText(R.string.real_device_data);
		}

 		fillSensorList();
	}
	
	public void setButtonState() {
		boolean connected = mSensorManager.isConnectedSimulator();
		mButtonConnect.setEnabled(!connected);
		
		mButtonDisconnect.setEnabled(connected);
		
		mButtonConnect.invalidate();
		mButtonDisconnect.invalidate();
	}
    
    /**
     * Reads information about which sensors are supported and what their rates and current rates are.
     */
	public void readAllSensors() {
		
        Log.d(TAG, "Sensors: " + mSensorManager.getSensors());
        Log.d(TAG, "Connected: " + mSensorManager.isConnectedSimulator());
        
        int sensors = mSensorManager.getSensors();
        Log.d(TAG, "sensors: " + sensors);
        
        mSupportedSensors = SensorNames.getSensorNames(sensors);
        Log.d(TAG, "mSupportedSensors: " + mSupportedSensors);
        
		// Now set values that are related to sensor updates:
		mNumSensors = mSupportedSensors.length;
        
	}
	
	
	public void onSensorChanged(int sensor, float[] values) {
        //Log.d(TAG, "onSensorChanged: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);
        
		// T-mobile G1 patch
		/*
		if (sensor == SensorManager.SENSOR_ORIENTATION) {
			if (values[1] > 90 || values[1] < -90) {
				values[2] = - values[2];
				//values[0] += 180;
				//if (values[0] > 360) {
				//	values[0] -= 360;
				//}
			}
		}
		*/
		
		
        // Update the display
        for (int i = 0; i < mNumSensors; i++) {
        	if (mSingleSensorView[i].mSensorBit == sensor) {
        		// Update this view
        		String data = "";
        		int num = SensorNames.getNumSensorValues(sensor);
        		//int num = 3;
        		
        		for (int j = 0; j < num; j++) {
        			data += mDecimalFormat.format(values[j]);
        			if (j < num-1) data += ", ";
        		}
        		//Log.d(TAG, "onSensorChanged - setText: " + i + ": " + data);
                	
        		mSingleSensorView[i].mTextView.setText(data);
        		break;
        	}
        }
        
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
		
	}

	/** 
     * Fills the sensor list with currently active sensors.
     */
	
    void fillSensorList() {
    	// First clean the list
    	mSensorsList.removeAllViews();
    	
    	// Now we fill the list, one by one:
    	int max = mSupportedSensors.length;
    	mSingleSensorView = new SingleSensorView[max];
    	
    	Log.i(TAG, "fillSensorList: " + max);
    	for (int i=0; i < max; i++) {
    		int sensorbit = SensorNames.getSensorsFromNames(new String[] {mSupportedSensors[i]});
    		SingleSensorView ssv = 
    			new SingleSensorView(this, 
    					mSupportedSensors[i], 
    					sensorbit,
    					i);
    		ssv.setLayoutParams(new LinearLayout.LayoutParams(
    				LinearLayout.LayoutParams.FILL_PARENT,
    				LinearLayout.LayoutParams.WRAP_CONTENT));
    		mSensorsList.addView(ssv, i);
    		mSingleSensorView[i] = ssv;
    	}
    }
    
    /**
     * Layout for displaying single sensor.
     */
    private class SingleSensorView extends LinearLayout {

        private TextView mTitle;
        
        LinearLayout mL1;
        LinearLayout mL1a;
        LinearLayout mL1b;
        LinearLayout mL1c;
        
        CheckBox  mCheckBox;
        TextView mTextView;
        Spinner mSpinner;
        
        ArrayAdapter<String> mUpdateRateAdapter;
        
        Context mContext;
        
        int mSensorId;
        String mSensor;
        int mSensorBit;
        
        /**
         * Index of the default value in the list (spinner) for the 
         * sensor update rate.
         * (-1 for no default index).
         */
        int mDefaultValueIndex;
		
    	public SingleSensorView(Context context, String sensor, int sensorbit, /*String[] updateRates, */int sensorId) {
    		super(context);
    		Log.i(TAG, "SingleSensorView - constructor");
    		
    		mContext = context;
    		mSensorId = sensorId;
    		mSensor = sensor;
    		mSensorBit = sensorbit;
    		mDefaultValueIndex = -1;  // -1 means there is no default index.
    		
    		// Build child view from resource:
    		LayoutInflater inf = 
    			(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE); 
    		View rowView = inf.inflate(R.layout.sensorsimulator_row, null); 
    		
    		// We set a tag, so that Handler can find this view
    		rowView.setTag(mSensor);
    		
    		// Assign widgets
    		mCheckBox = (CheckBox) rowView.findViewById(R.id.enabled);
    		mCheckBox.setText(sensor);
    		
    		mTextView = (TextView) rowView.findViewById(R.id.sensordata);
    		mTextView.setText(sensor);
    		
    		mSpinner = (Spinner) rowView.findViewById(R.id.updaterate);
    		mUpdateRateAdapter = new ArrayAdapter<String>(
                    context, android.R.layout.simple_spinner_item, mDelayTypes);
            mUpdateRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(mUpdateRateAdapter);
            
    		addView(rowView, new LinearLayout.LayoutParams(
        			LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		
    		updateSensorStateInformation();   

    	}
    	
    	public void updateSensorStateInformation() {
    		// We add a listener for the CheckBox:
    		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    				
    				if (isChecked) {
    					int pos = mSpinner.getSelectedItemPosition();
    					int updateRate = mDelayValue[pos];
    					mSensorManager.registerListener(SensorSimulatorSettingsActivity.this, mSensorBit, updateRate);
    				} else {
    					mSensorManager.unregisterListener(SensorSimulatorSettingsActivity.this, mSensorBit);
    				}
    				
    				updateSensorStateInformation();
    				
    			}    			
    		});
    	}
    }
	
}
