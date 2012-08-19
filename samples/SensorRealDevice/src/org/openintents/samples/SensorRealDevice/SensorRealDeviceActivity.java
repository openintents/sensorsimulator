package org.openintents.samples.SensorRealDevice;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorRealDeviceActivity extends Activity {

	protected static final long DELTA = 1000;
	protected long lastUpdateAcc = System.currentTimeMillis();
	protected long lastUpdateLinearAcc = lastUpdateAcc;
	protected long lastUpdateGravity = lastUpdateAcc;

	// private SensorManagerSimulator mSensorManager;
	private SensorManager mSensorManager;

	private TextView mTextViewAccelerometer;
	private TextView mTextViewGravity;
	private TextView mTextViewLinearAcceleration;
	private TextView mTextViewLight;
	private TextView mTextViewTemperature;
	private TextView mTextViewOrientation;
	private TextView mTextViewMagneticField;
	private TextView mTextViewPressure;
	private TextView mTextViewRotationVector;

	private SensorEventListener mEventListenerAccelerometer;
	private SensorEventListener mEventListenerGravity;
	private SensorEventListener mEventListenerLinearAcceleration;
	private SensorEventListener mEventListenerLight;
	private SensorEventListener mEventListenerTemperature;
	private SensorEventListener mEventListenerOrientation;
	private SensorEventListener mEventListenerMagneticField;
	private SensorEventListener mEventListenerPressure;
	private SensorEventListener mEventListenerRotationVector;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTextViewAccelerometer = (TextView) findViewById(R.id.text_accelerometer);
		mTextViewGravity = (TextView) findViewById(R.id.text_gravity);
		mTextViewLinearAcceleration = (TextView) findViewById(R.id.text_linear_acceleration);
		mTextViewLight = (TextView) findViewById(R.id.text_light);
		mTextViewTemperature = (TextView) findViewById(R.id.text_temperature);
		mTextViewOrientation = (TextView) findViewById(R.id.text_orientation);
		mTextViewMagneticField = (TextView) findViewById(R.id.text_magnetic_field);
		mTextViewPressure = (TextView) findViewById(R.id.text_pressure);
		mTextViewRotationVector = (TextView) findViewById(R.id.text_rotation_vector);
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
				mTextViewAccelerometer.setText("Accelerometer: " + values[0]
						+ ", " + values[1] + ", " + values[2]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerLinearAcceleration = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextViewLinearAcceleration.setText("Linear Acceleration: "
						+ values[0] + ", " + values[1] + ", " + values[2]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerGravity = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextViewGravity.setText("Gravity: " + values[0] + ", "
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
				mTextViewMagneticField.setText("Compass: " + values[0] + ", "
						+ values[1] + ", " + values[2]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerOrientation = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextViewOrientation.setText("Orientation: " + values[0] + ", "
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
				mTextViewTemperature.setText("Temperature: " + values[0]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerLight = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextViewLight.setText("Light: " + values[0]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerPressure = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextViewPressure.setText("Pressure: " + values[0]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mEventListenerRotationVector = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] values = event.values;
				mTextViewRotationVector.setText("RotationVector: " + values[0]
						+ ", " + values[1] + ", " + values[2]);
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
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mEventListenerLinearAcceleration,
				mSensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mEventListenerGravity,
				mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mEventListenerMagneticField,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mEventListenerOrientation,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mEventListenerTemperature,
				mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE),
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mEventListenerLight,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mEventListenerPressure,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mEventListenerRotationVector,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mEventListenerAccelerometer);
		mSensorManager.unregisterListener(mEventListenerLinearAcceleration);
		mSensorManager.unregisterListener(mEventListenerGravity);
		mSensorManager.unregisterListener(mEventListenerMagneticField);
		mSensorManager.unregisterListener(mEventListenerOrientation);
		mSensorManager.unregisterListener(mEventListenerTemperature);
		mSensorManager.unregisterListener(mEventListenerLight);
		mSensorManager.unregisterListener(mEventListenerPressure);
		mSensorManager.unregisterListener(mEventListenerRotationVector);
		super.onStop();
	}
}