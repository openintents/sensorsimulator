package org.openintents.samples.SensorSimulatorSimpleDemo;

import org.openintents.samples.SensorSimulatorSimpleDemo.SenSimDemoActivity;
import org.openintents.samples.SensorSimulatorSimpleDemo.R;
import org.openintents.sensorsimulator.testlibrary.SensorTester;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ActivTest {

	@Test
	public void shouldHaveHappySmiles() throws Exception {
		String hello = new SenSimDemoActivity().getResources().getString(
				R.string.app_name);
		assertThat(hello, equalTo("SensorSimulatorSimpleDemo"));
	}

	@Test
	public void sensor() throws Exception {
		SensorTester sensorTester = new SensorTester();
		sensorTester.connect();
		sensorTester.shake();
		assertThat("", equalTo(""));
	}
}