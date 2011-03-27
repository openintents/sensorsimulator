package org.openintents.samples.SensorRealDevice;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorRealDeviceActivity extends Activity {

	// private SensorManagerSimulator mSensorManager;
	private SensorManager mSensorManager;

	TextView mTextView1;
	TextView mTextView2;
	TextView mTextView3;
	TextView mTextView4;
	TextView mTextView5;

	private SensorEventListener mEventListenerAccelerometer;
	private SensorEventListener mEventListenerLight;
	private SensorEventListener mEventListenerTemperature;
	private SensorEventListener mEventListenerOrientation;
	private SensorEventListener mEventListenerMagneticField;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTextView1 = (TextView) findViewById(R.id.text1);
		mTextView2 = (TextView) findViewById(R.id.text2);
		mTextView3 = (TextView) findViewById(R.id.text3);
		mTextView4 = (TextView) findViewById(R.id.text4);
		mTextView5 = (TextView) findViewById(R.id.text5);

		// //////////////////////////////////////////////////////////////
		// INSTRUCTIONS
		// ============

		// 1) Use the separate application SensorSimulatorSettings
		// to enter the correct IP address of the SensorSimulator.
		// This should work before you proceed, because the same
		// settings are used for your custom sensor application.

		// 2) Include sensorsimulator-lib.jar in your project.
		// Put that file into the 'lib' folder.
		// In Eclipse, right-click on your project in the
		// Package Explorer, select
		// Properties > Java Build Path > (tab) Libraries
		// then click Add JARs to add this jar.

		// 3) You need the permission
		// <uses-permission android:name="android.permission.INTERNET"/>
		// in your Manifest file!

		// 4) Instead of calling the system service to obtain the Sensor
		// manager,
		// you should obtain it from the SensorManagerSimulator:

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// mSensorManager = SensorManagerSimulator.getSystemService(this,
		// SENSOR_SERVICE);

		// 5) Connect to the sensor simulator, using the settings
		// that have been set previously with SensorSimulatorSettings
		// mSensorManager.connectSimulator();

		// The rest of your application can stay unmodified.
		// //////////////////////////////////////////////////////////////

		initListeners();

	}

	private void initListeners() {
		mEventListenerAccelerometer = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextView1.setText("Accelerometer: " + values[0] + ", "
						+ values[1] + ", " + values[2]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerMagneticField = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextView2.setText("Compass: " + values[0] + ", " + values[1]
						+ ", " + values[2]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerOrientation = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextView3.setText("Orientation: " + values[0] + ", "
						+ values[1] + ", " + values[2]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerTemperature = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextView4.setText("Temperature: " + values[0] + ", "
						+ values[1] + ", " + values[2]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerLight = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextView5.setText("Light: " + values[0]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mEventListenerAccelerometer,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mEventListenerMagneticField,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mEventListenerOrientation,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mEventListenerTemperature,
				mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mEventListenerLight,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mEventListenerAccelerometer);
		mSensorManager.unregisterListener(mEventListenerMagneticField);
		mSensorManager.unregisterListener(mEventListenerOrientation);
		mSensorManager.unregisterListener(mEventListenerTemperature);
		mSensorManager.unregisterListener(mEventListenerLight);
		super.onStop();
	}
}