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

package org.openintents.sensorsimulator;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.openintents.sensorsimulator.db.SensorSimulator;
import org.openintents.sensorsimulator.db.SensorSimulatorConvenience;
import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;
import org.openintents.sensorsimulator.hardware.SensorNames;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Lets user set connection settings to the SensorSimulator and test them.
 * 
 * The connection is outbound to the SensorSimulator.
 * 
 * Important to connect at least once using this activity, so that IP and port
 * can be stored and used later in another application. This activity is used to
 * see our simulations effects and to test them.
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class SensorSimulatorSettingsActivity extends Activity {
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

	ArrayList<String> mSupportedSensors = new ArrayList<String>();

	/**
	 * Number of supported sensors.
	 */
	int mNumSensors;

	/**
	 * The list of currently enabled sensors. (Needed to determine which of the
	 * sensors have to be updated regularly by new sensor data).
	 */
	boolean[] mSensorEnabled;

	/**
	 * Keep pointers to SingleSensorView so that we can update sensor data
	 * regularly.
	 */
	SingleSensorView[] mSingleSensorView;

	String[] mDelayTypes = new String[] { "Fastest", "Game", "UI", "Normal" };

	private TabHost mTabHost;

	private SensorSimulatorConvenience mSensorSimulatorConvenience;

	/**
	 * Called when activity starts.
	 * 
	 * This can either be the first time, or the user navigates back after the
	 * activity has been killed.
	 * 
	 * We do not automatically reconnect to the SensorSimulator, as it may not
	 * be available in the mean-time anymore or the IP address may have changed.
	 * 
	 * (We do not know how long the activity had been dormant).
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);

		setContentView(R.layout.sensorsimulator);

		// add as sensor manager our's sensor manager simulator
		mSensorManager = SensorManagerSimulator.getSystemService(this,
				SENSOR_SERVICE);

		// set convenience for storing and loading IP and port for connection
		mSensorSimulatorConvenience = new SensorSimulatorConvenience(this);

		Context context = this;
		// Get the Resources object from our context
		Resources res = context.getResources();

		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup();

		TabSpec tabspec = mTabHost.newTabSpec("settings");
		tabspec.setIndicator(res.getString(R.string.settings),
				res.getDrawable(R.drawable.settings001a_32));
		tabspec.setContent(R.id.content1);
		mTabHost.addTab(tabspec);

		tabspec = mTabHost.newTabSpec("testing");
		tabspec.setIndicator(res.getString(R.string.testing),
				res.getDrawable(R.drawable.mobile_shake001a_32));
		tabspec.setContent(R.id.content2);
		mTabHost.addTab(tabspec);

		mTabHost.setCurrentTab(0);

		mEditTextIP = (EditText) findViewById(R.id.ipaddress);
		mEditTextSocket = (EditText) findViewById(R.id.socket);

		mButtonConnect = (Button) findViewById(R.id.buttonconnect);
		mButtonConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				connect();
			}
		});

		mButtonDisconnect = (Button) findViewById(R.id.buttondisconnect);
		mButtonDisconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnect();
			}
		});

		setButtonState();

		String ipAddress = mSensorSimulatorConvenience
				.getPreference(SensorSimulator.KEY_IPADDRESS);
		if (!ipAddress.contentEquals("")) {
			mEditTextIP.setText(ipAddress);
		}
		String s = mSensorSimulatorConvenience
				.getPreference(SensorSimulator.KEY_SOCKET);
		if (s.contentEquals("")) {
			s = SensorSimulator.DEFAULT_SOCKET;
		}
		mEditTextSocket.setText(s);

		mTextSensorType = (TextView) findViewById(R.id.datatype);

		// Format for output of data
		mDecimalFormat = new DecimalFormat("#0.00");

		readAllSensors(); // Basic sensor information

		mSensorsList = (LinearLayout) findViewById(R.id.sensordatalist);

		fillSensorList(); // Fills the sensor list manually, giving us more
							// control

		// if we use simulation of GPS, here we register location manager for
		// the GPS
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mlocListener);

	}

	/**
	 * Inner class that represents our Location Listener. Each time we receive
	 * GPS input, Toast message is made and it contains variables which are send
	 * to the emulator. In order for Toast message to display, emulator should
	 * be connected to internet.
	 * 
	 * @author Josip Balic
	 */
	public class MyLocationListener implements LocationListener {

		/**
		 * Method that gets location changes. Once location is received, Toast
		 * message with Latitude, Longitude and Altitude is made. This is just
		 * an example how LocationListener should be used in applications.
		 */
		@Override
		public void onLocationChanged(Location location) {
			location.getLatitude();
			location.getLongitude();
			location.getAltitude();

			String Text = "My current location is: " + "Longitude = "
					+ location.getLongitude() + " Latitude = "
					+ location.getLatitude() + " Altitude = "
					+ location.getAltitude();

			Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT)
					.show();

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Called when activity comes to foreground. In onResume() method we
	 * register our sensors listeners, important - sensor listeners are not and
	 * should not be registered before, but in on resume method.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(listener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(listener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(listener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(listener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(listener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(listener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(listener, mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(listener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(listener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	/**
	 * Called when activity is stopped. Here we unregister all of currently
	 * existing listeners.
	 */
	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(listener);
		super.onStop();
	}

	/**
	 * Called when another activity is started.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * Called when the user leaves. Here we store the IP address and port,
	 * unregister existing listeners and close the connection with sensor
	 * simulator.
	 */
	@Override
	protected void onPause() {
		super.onPause();

		String newIP = mEditTextIP.getText().toString();
		String newSocket = mEditTextSocket.getText().toString();
		String oldIP = mSensorSimulatorConvenience
				.getPreference(SensorSimulator.KEY_IPADDRESS);
		String oldSocket = mSensorSimulatorConvenience
				.getPreference(SensorSimulator.KEY_SOCKET);

		if (!(newIP.contentEquals(oldIP) && newSocket.contentEquals(oldSocket))) {
			// new values, unregister existing listeners and disconnect from
			// simulator
			mSensorManager.unregisterListener(listener);
			mSensorManager.disconnectSimulator();

			// Save the values
			mSensorSimulatorConvenience.setPreference(
					SensorSimulator.KEY_IPADDRESS, newIP);
			mSensorSimulatorConvenience.setPreference(
					SensorSimulator.KEY_SOCKET, newSocket);

		}
	}

	// //////////////////////////////////////////////////////
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

	// /////////////////////////////////////
	// Menu related functions

	// /////////////////////////////////////

	/**
	 * This method is used to connect our activity with sensor simulator. If we
	 * already have opened connection, than we unregister all the listeners,
	 * close existing connection and we create new connection.
	 */
	public void connect() {
		Log.i(TAG, "Connect");
		String newIP = mEditTextIP.getText().toString();
		String newSocket = mEditTextSocket.getText().toString();
		String oldIP = mSensorSimulatorConvenience
				.getPreference(SensorSimulator.KEY_IPADDRESS);
		String oldSocket = mSensorSimulatorConvenience
				.getPreference(SensorSimulator.KEY_SOCKET);

		if (!(newIP.contentEquals(oldIP) && newSocket.contentEquals(oldSocket))) {
			// new values
			mSensorManager.unregisterListener(listener);
			mSensorManager.disconnectSimulator();

			// Save the values
			mSensorSimulatorConvenience.setPreference(
					SensorSimulator.KEY_IPADDRESS, newIP);
			mSensorSimulatorConvenience.setPreference(
					SensorSimulator.KEY_SOCKET, newSocket);
		}

		if (!mSensorManager.isConnectedSimulator()) {
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

		fillSensorList();

	}

	/**
	 * Method disconnect() is used to close existing connection with sensor
	 * simulator. When disconnect is called, all registered sensors are getting
	 * unregistered.
	 */
	public void disconnect() {
		mSensorManager.unregisterListener(listener);
		mSensorManager.disconnectSimulator();

		readAllSensors();

		setButtonState();

		if (mSensorManager.isConnectedSimulator()) {
			mTextSensorType.setText(R.string.sensor_simulator_data);
		} else {
			mTextSensorType.setText(R.string.real_device_data);
		}

		mSensorsList.removeAllViews();
	}

	/**
	 * This method is used to set button states. If we are connected with sensor
	 * simulator, than we can't click on button connect anymore and vice versa.
	 */
	public void setButtonState() {
		boolean connected = mSensorManager.isConnectedSimulator();
		mButtonConnect.setEnabled(!connected);

		mButtonDisconnect.setEnabled(connected);

		mButtonConnect.invalidate();
		mButtonDisconnect.invalidate();
	}

	/**
	 * Reads information about which sensors are supported and what their rates
	 * and current rates are.
	 */
	public void readAllSensors() {

		ArrayList<Integer> sensors = mSensorManager.getSensors();

		if (sensors != null) {
			mSupportedSensors = SensorNames.getSensorNames(sensors);
		}

		// Now set values that are related to sensor updates:
		if (mSupportedSensors != null) {
			mNumSensors = mSupportedSensors.size();
		}
	}

	/**
	 * Our listener for this application. This listener holds all sensors we
	 * enable. If we are developing application which is using 2 or more
	 * sensors, it's advisable to use only one listener per one sensor.
	 */
	private SensorEventListener listener = new SensorEventListener() {

		/**
		 * onAccuracyChanged must be added, but it doesn't need any editing.
		 */
		@Override
		public void onAccuracyChanged(Sensor sensor, int acc) {
		}

		/**
		 * onSensorChanged method is used to write events of our enabled sensors
		 * in our application.
		 */
		@Override
		public void onSensorChanged(SensorEvent event) {
			int sensor = event.type;
			float[] values = event.values;
			for (int i = 0; i < mNumSensors; i++) {
				if ((mSingleSensorView[i].mSensorBit == sensor) && sensor != 9) {
					// Update this view
					String data = "";
					int num = SensorNames.getNumSensorValues(sensor);

					for (int j = 0; j < num; j++) {
						data += mDecimalFormat.format(values[j]);
						if (j < num - 1) {
							data += ", ";
						}
					}

					mSingleSensorView[i].mSensorDataTextView.setText(data);
					break;
				}
			}

			// If Barcode is enabled, this method is used only for Barcode
			// output
			for (int i = 0; i < mNumSensors; i++) {
				if ((mSingleSensorView[i].mSensorBit == sensor) && sensor == 9) {
					// Update this view
					String data = "";
					data = event.barcode;

					mSingleSensorView[i].mSensorDataTextView.setText(data);
					break;
				}
			}
		}

	};

	/**
	 * Fills the sensor list with currently active sensors.
	 */

	void fillSensorList() {
		if (mSupportedSensors != null) {
			mSensorsList.removeAllViews();

			// Now we fill the list, one by one:
			int max = mSupportedSensors.size();

			mSingleSensorView = new SingleSensorView[max];

			for (int i = 0; i < max; i++) {
				String[] sensorsNames = new String[mSupportedSensors.size()];
				ArrayList<Integer> sensorbit = SensorNames
						.getSensorsFromNames(mSupportedSensors
								.toArray(sensorsNames));
				SingleSensorView ssv = new SingleSensorView(this,
						mSupportedSensors.get(i), sensorbit.get(i), i);
				ssv.setLayoutParams(new LinearLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.FILL_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				mSensorsList.addView(ssv, i);
				mSingleSensorView[i] = ssv;
			}
		}
	}

	/**
	 * Layout for displaying single sensor.
	 * 
	 * @author Peli
	 * @author Josip Balic
	 */
	private class SingleSensorView extends LinearLayout {

		@SuppressWarnings("unused")
		private TextView mTitle;

		@SuppressWarnings("unused")
		LinearLayout mL1;
		@SuppressWarnings("unused")
		LinearLayout mL1a;
		@SuppressWarnings("unused")
		LinearLayout mL1b;
		@SuppressWarnings("unused")
		LinearLayout mL1c;

		TextView mSensorNameTextView;
		TextView mSensorDataTextView;

		@SuppressWarnings("unused")
		Context mContext;

		@SuppressWarnings("unused")
		int mSensorId;
		String mSensor;
		int mSensorBit;

		/**
		 * Index of the default value in the list (spinner) for the sensor
		 * update rate. (-1 for no default index).
		 */
		@SuppressWarnings("unused")
		int mDefaultValueIndex;

		public SingleSensorView(Context context, String sensor, int sensorbit,
				int sensorId) {
			super(context);

			mContext = context;
			mSensorId = sensorId;
			mSensor = sensor;
			mSensorBit = sensorbit;
			mDefaultValueIndex = -1; // -1 means there is no default index.

			// Build child view from resource:
			LayoutInflater inf = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View rowView = inf.inflate(R.layout.sensorsimulator_row, null);

			// We set a tag, so that Handler can find this view
			rowView.setTag(mSensor);

			mSensorNameTextView = (TextView) rowView
					.findViewById(R.id.sensor_name);
			mSensorNameTextView.setText(sensor);
			mSensorDataTextView = (TextView) rowView
					.findViewById(R.id.sensor_data);

			addView(rowView, new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

			mSensorManager.registerListener(listener,
					mSensorManager.getDefaultSensor(mSensorBit),
					SensorManager.SENSOR_DELAY_FASTEST); // TODO
		}
	}
}
