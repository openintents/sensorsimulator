package org.openintents.samples.SensorSimulatorSimpleDemo;

import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.app.Activity;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SenSimDemoActivity extends Activity {

	protected static final String TAG = "SenSimDemoActivity";
	private SensorManagerSimulator mSensorManager;
	private TextView mAccTextView;
	private TextView mProxTextView;
	private TextView mAccTitle;
	private TextView mLightTextView;
	private TextView mLigTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate()");

		setContentView(R.layout.activity_sensimdemo);

		// set info
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();

		String ipString = String.format(
				"%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff),
				(ip >> 16 & 0xff), (ip >> 24 & 0xff));

		TextView infoView = (TextView) findViewById(R.id.sensimdemo_info);
		infoView.setText("Wifi IP address is: " + ipString);

		mAccTextView = (TextView) findViewById(R.id.sensimdemo_accval);
		mProxTextView = (TextView) findViewById(R.id.sensimdemo_proxval);
		mLightTextView = (TextView) findViewById(R.id.sensimdemo_ligval);
		mAccTitle = (TextView) findViewById(R.id.sensimdemo_acctitle);
		mAccTitle.setText("Accelerometer");
		mAccTitle = (TextView) findViewById(R.id.sensimdemo_protitle);
		mAccTitle.setText("Proximity");
		mLigTitle = (TextView) findViewById(R.id.sensimdemo_ligtitle);
		mLigTitle.setText("Light");

		// //////////////////////////////////////////////////////////////
		// INSTRUCTIONS
		// ============

		// 1) Include sensimmocklib.jar in your project. Put that file
		// into the 'libs' folder. In Eclipse, right-click on your project in
		// the Package Explorer, select Properties > Java Build Path > (tab)
		// Libraries then click Add JARs to add this jar.

		// 2) You need the permission
		// <uses-permission android:name="android.permission.INTERNET"/>
		// in your Manifest file!

		// 3) Instead of calling the system service to obtain the Sensor
		// manager, you should obtain it from the SensorManagerSimulator:
		mSensorManager = SensorManagerSimulator.getSystemService(this,
				SENSOR_SERVICE);

		// 4) Connect to the sensor simulator, using the settings that have been
		// set previously with SensorSimulatorSettings
		mSensorManager.connectSimulator();

		// The rest of your application can stay unmodified.
		// //////////////////////////////////////////////////////////////
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(mListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(mListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mListener);
	}

	SensorEventListener mListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(final SensorEvent event) {

			if (event.type == Sensor.TYPE_ACCELEROMETER) {
				mAccTextView.setText(event.values[0] + "\n" + event.values[1]
						+ "\n" + event.values[2]);
			} else if (event.type == Sensor.TYPE_LIGHT) {
				mLightTextView.setText(event.values[0] + "");
			} else if (event.type == Sensor.TYPE_PROXIMITY) {
				mProxTextView.setText(event.values[0] + "");
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
	};
}
