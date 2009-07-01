package org.openintents.samples.SensorDemo;

import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.app.Activity;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SensorDemoActivity extends Activity implements SensorListener {

    private SensorManagerSimulator mSensorManager;
    
    TextView mTextView1;
    TextView mTextView2;
    TextView mTextView3;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTextView1 = (TextView) findViewById(R.id.text1);
        mTextView2 = (TextView) findViewById(R.id.text2);
        mTextView3 = (TextView) findViewById(R.id.text3);

        ////////////////////////////////////////////////////////////////
        // INSTRUCTIONS
        // ============

        // 1) Use the separate application SensorSimulatorSettings
        //    to enter the correct IP address of the SensorSimulator.
        //    This should work before you proceed, because the same
        //    settings are used for your custom sensor application.

        // 2) Include sensorsimulator-lib.jar in your project.
        //    Put that file into the 'lib' folder.
        //    In Eclipse, right-click on your project in the 
        //    Package Explorer, select
        //    Properties > Java Build Path > (tab) Libraries
        //    then click Add JARs to add this jar.

        // 3) You need the permission
        //    <uses-permission android:name="android.permission.INTERNET"/>
        //    in your Manifest file!

        // 4) Instead of calling the system service to obtain the Sensor manager,
        //    you should obtain it from the SensorManagerSimulator:

        //mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);

        // 5) Connect to the sensor simulator, using the settings
        //    that have been set previously with SensorSimulatorSettings
        mSensorManager.connectSimulator();

        // The rest of your application can stay unmodified.
        ////////////////////////////////////////////////////////////////

    }
    

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER
				| SensorManager.SENSOR_MAGNETIC_FIELD
				| SensorManager.SENSOR_ORIENTATION,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(this);
		super.onStop();
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
	}

	public void onSensorChanged(int sensor, float[] values) {
		switch(sensor) {
		case SensorManager.SENSOR_ACCELEROMETER:
			mTextView1.setText("Accelerometer: " 
					+ values[0] + ", " 
					+ values[1] + ", "
					+ values[2]);
			break;
		case SensorManager.SENSOR_MAGNETIC_FIELD:
			mTextView2.setText("Compass: " 
					+ values[0] + ", " 
					+ values[1] + ", "
					+ values[2]);
			break;
		case SensorManager.SENSOR_ORIENTATION:
			mTextView3.setText("Orientation: " 
					+ values[0] + ", " 
					+ values[1] + ", "
					+ values[2]);
			break;
		}
	}
}