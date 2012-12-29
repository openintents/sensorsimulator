package org.openintents.samples.SensorSimulatorSimpleDemo.test;

import org.openintents.samples.SensorSimulatorSimpleDemo.SenSimDemoActivity;

import org.openintents.sensorsimulator.testlibrary.SensorTester;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TextView;


public class SimpleTest extends
		ActivityInstrumentationTestCase2<SenSimDemoActivity> {
	private static final String TAG = "SimpleTest";

	private SenSimDemoActivity mActivity;
	private TextView tv;

	private SensorTester sensorTester;

	public SimpleTest() {
		super(SenSimDemoActivity.class);

		setActivityInitialTouchMode(false);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// activity must be started before SensorTester
		mActivity = getActivity();

		sensorTester = new SensorTester();
		sensorTester.connect();

		tv = (TextView) mActivity
				.findViewById(org.openintents.samples.SensorSimulatorSimpleDemo.R.id.sensimdemo_accval);
	}

	@Override
	protected void tearDown() throws Exception {
		sensorTester.disconnect();

		super.tearDown();
	}

	public void testShake() {
		if (!sensorTester.shake())
			assertTrue(false);

		String s = (String) tv.getText();
		Log.d(TAG, "s = " + s);
		assertTrue(s.contains("-0.05746084"));
	}
}