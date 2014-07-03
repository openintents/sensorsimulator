package org.openintents.tools.simulator.comm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.openintents.sensorsimulator.testlibrary.ContinuousDataSender;
import org.openintents.sensorsimulator.testlibrary.Sensor;
import org.openintents.sensorsimulator.testlibrary.SensorEvent;
import org.openintents.sensorsimulator.testlibrary.SensorEventProducer;

/**
 * Produces sensor events (data), by sampling them from the underlying data
 * source. This is a temporary solution until the data source
 * (SensorSimulatorModel) is refactored so that it can generate Events by itself
 * and transfer them to the device-under-test.
 * <p>
 * Sampling is done through an internal TimerTask.
 * 
 * @author Qui Don Ho
 * 
 */
public class PollProducer implements SensorEventProducer {

	// default sampling speed values
	private static final int SAMPLING_DELAY_FASTEST = 50;
	private static final int SAMPLING_DELAY_GAME = 100;
	private static final int SAMPLING_DELAY_NORMAL = 200;
	private static final int SAMPLING_DELAY_UI = 400;

	private static PollProducer mInstance;

	private Timer mTimer;
	private SensorDataSource mDataSource;
	private ContinuousDataSender mSender;

	// sensor-to-sensorrate map
	private Map<Sensor.Type, Integer> mRegisteredSensorRates = new HashMap<Sensor.Type, Integer>();
	// should contain the fastest sensor rate

	private Map<Integer, Integer> mSamplingDelays;

	/**
	 * Create a new instance and start producing.
	 * 
	 * @param sensorDataSource
	 */
	public static PollProducer startProducing(SensorDataSource sensorDataSource) {

		if (mInstance == null) {

			mInstance = new PollProducer(sensorDataSource);
			return mInstance;
		} else {
			throw new IllegalStateException("Already started!");
		}
	}

	private PollProducer(SensorDataSource sensorDataSource) {

		mSender = new ContinuousDataSender(this);
		mTimer = new Timer();
		mDataSource = sensorDataSource;
		mSamplingDelays = new HashMap<Integer, Integer>();

		// default values
		mSamplingDelays.put(0, SAMPLING_DELAY_FASTEST);
		mSamplingDelays.put(1, SAMPLING_DELAY_GAME);
		mSamplingDelays.put(2, SAMPLING_DELAY_UI);
		mSamplingDelays.put(3, SAMPLING_DELAY_NORMAL);
	}

	public void connect(String ipAddress) {
		mSender.connect(ipAddress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openintents.tools.simulator.comm.SensorEventProducer#registerSensors
	 * (java.util.Map)
	 */
	@Override
	public Map<Integer, Integer> registerSensors(
			Map<Sensor.Type, Integer> sensorRates) {
		mRegisteredSensorRates = sensorRates;

		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Entry<Sensor.Type, Integer> entry : mRegisteredSensorRates
				.entrySet()) {
			result.put(Sensor.typeToInt(entry.getKey()),
					mSamplingDelays.get(entry.getValue()));

			// start sampling
			mTimer.schedule(new SensorQuery(entry.getKey()), 0,
					mSamplingDelays.get(entry.getValue()));
		}
		return result;
	}

	/**
	 * Queries the data source for sensor data. Each sensor has its own sampling
	 * rate, depending on how the <code>SensorEventListener</code>s on the
	 * connected device have registered to the
	 * <code>SensorManagerSimulator</code>.
	 */
	private class SensorQuery extends TimerTask {

		private Sensor.Type mSensorType;

		public SensorQuery(Sensor.Type sensorType) {
			mSensorType = sensorType;
		}

		@Override
		public void run() {
			String reading;
			try {
				reading = mDataSource.readSensor(mSensorType);
			}
			// TODO activate sensor
			catch (IllegalStateException e) {
				reading = "1\n0";
			}
			float[] sensorData = convertSensorData(reading);

			// create sensorevent
			SensorEvent sEvent = new SensorEvent(
					Sensor.typeToInt(mSensorType), 1, sensorData);

			mSender.push(sEvent);
		}
	}

	/**
	 * Helper method to convert sensor data from String to float Array.
	 * 
	 * @param data
	 *            the sensor data in String form
	 * @return the sensor data in float array form
	 */
	private float[] convertSensorData(String data) {
		String[] dataArray = data.split("\n");

		float[] result = new float[dataArray.length - 1];
		for (int i = 1; i < dataArray.length; i++) {
			result[i - 1] = Float.parseFloat(dataArray[i]);
		}
		return result;
	}
}
