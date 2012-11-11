package org.openintents.tools.simulator.comm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.openintents.tools.simulator.model.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensors.SensorType;

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
public class SensorEventProducer {

	private static final int SAMPLING_DELAY_DEFAULT = 20;

	private static SensorEventProducer mInstance;

	private Timer mTimer;
	private SensorDataSource mDataSource;
	private ContinuousDataSender mSender;

	// sensor-to-sensorrate map
	private Map<SensorType, Integer> mRegisteredSensorRates = new HashMap<SensorType, Integer>();
	// should contain the fastest sensor rate
	private int mSamplingDelay = SAMPLING_DELAY_DEFAULT;

	private Map<Integer, Integer> mSamplingDelays;

	/**
	 * Create a new instance and start producing.
	 * 
	 * @param sensorDataSource
	 */
	public static SensorEventProducer startProducing(
			SensorDataSource sensorDataSource) {

		if (mInstance == null) {

			mInstance = new SensorEventProducer(sensorDataSource);
			return mInstance;
		} else {
			throw new IllegalStateException("Already started!");
		}
	}

	private SensorEventProducer(SensorDataSource sensorDataSource) {

		mSender = new ContinuousDataSender(this);
		mTimer = new Timer();
		mDataSource = sensorDataSource;
		mSamplingDelays = new HashMap<Integer, Integer>();

		// default values
		mSamplingDelays.put(0, 20);
		mSamplingDelays.put(1, 40);
		mSamplingDelays.put(2, 80);
		mSamplingDelays.put(3, 160);
	}

	public void connect() {

		mSender.connect();
		// mTimer.schedule(mTimerTask, 0, mSamplingDelay);
	}

	/**
	 * Take sensor rate registrations and return the delays.
	 * 
	 * @param sensorRates
	 *            Map: SensorType - Rate (FASTEST, GAME...)
	 * @return Map: SensorType(int) - update speed(ms)
	 */
	public Map<Integer, Integer> registerSensors(
			Map<SensorType, Integer> sensorRates) {
		mRegisteredSensorRates = sensorRates;

		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Entry<SensorType, Integer> entry : mRegisteredSensorRates
				.entrySet()) {
			result.put(sentypeToInt(entry.getKey()),
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

		private SensorType mSensorType;

		public SensorQuery(SensorType sensorType) {
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
			SensorEventContainer sEvent = new SensorEventContainer(
					sentypeToInt(mSensorType), 1, sensorData);

			mSender.push(sEvent);
			// control TODO remove
			System.out.println(sEvent);
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

	public static int sentypeToInt(SensorType sType) {
		switch (sType) {
		case ACCELEROMETER:
			return SensorModel.TYPE_ACCELEROMETER;
		case BARCODE_READER:
			return SensorModel.TYPE_BARCODE;
		case GRAVITY:
			return SensorModel.TYPE_GRAVITY;
		case GYROSCOPE:
			return SensorModel.TYPE_GYROSCOPE;
		case LIGHT:
			return SensorModel.TYPE_LIGHT;
		case LINEAR_ACCELERATION:
			return SensorModel.TYPE_LINEAR_ACCELERATION;
		case MAGNETIC_FIELD:
			return SensorModel.TYPE_MAGNETIC_FIELD;
		case ORIENTATION:
			return SensorModel.TYPE_ORIENTATION;
		case PRESSURE:
			return SensorModel.TYPE_PRESSURE;
		case PROXIMITY:
			return SensorModel.TYPE_PROXIMITY;
		case ROTATION:
			return SensorModel.TYPE_ROTATION_VECTOR;
		case TEMPERATURE:
			return SensorModel.TYPE_TEMPERATURE;
		default:
			throw new IllegalArgumentException("unknown sensor");
		}
	}

	public static SensorType intToSentype(int sType) {
		switch (sType) {
		case SensorModel.TYPE_ACCELEROMETER:
			return SensorType.ACCELEROMETER;
		case SensorModel.TYPE_BARCODE:
			return SensorType.BARCODE_READER;
		case SensorModel.TYPE_GRAVITY:
			return SensorType.GRAVITY;
		case SensorModel.TYPE_GYROSCOPE:
			return SensorType.GYROSCOPE;
		case SensorModel.TYPE_LIGHT:
			return SensorType.LIGHT;
		case SensorModel.TYPE_LINEAR_ACCELERATION:
			return SensorType.LINEAR_ACCELERATION;
		case SensorModel.TYPE_MAGNETIC_FIELD:
			return SensorType.MAGNETIC_FIELD;
		case SensorModel.TYPE_ORIENTATION:
			return SensorType.ORIENTATION;
		case SensorModel.TYPE_PRESSURE:
			return SensorType.PRESSURE;
		case SensorModel.TYPE_PROXIMITY:
			return SensorType.PROXIMITY;
		case SensorModel.TYPE_ROTATION_VECTOR:
			return SensorType.ROTATION;
		case SensorModel.TYPE_TEMPERATURE:
			return SensorType.TEMPERATURE;
		default:
			throw new IllegalArgumentException("unknown sensor");
		}
	}
}
