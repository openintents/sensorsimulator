package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

public class LightModel extends SensorModel {

	// light
	private double lightValue;
	/** Current read-out value of light. */
	private double read_light;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long light_next_update;
	/** Partial read-out value of light. */
	private float partial_light;
	/** Number of summands in partial sum for light. */
	private int partial_light_n;

	public LightModel() {
		super();
		lightValue = 400;
	}

	@Override
	public String getName() {
		return SensorModel.LIGHT;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_light += lightValue;
			partial_light_n++;
		}

		// Update
		if (currentTime >= light_next_update) {
			light_next_update += updateDuration;
			if (light_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				light_next_update = currentTime;
			}

			if (average) {
				// form average
				read_light = partial_light / partial_light_n;

				// reset average
				partial_light = 0;
				partial_light_n = 0;

			} else {
				// Only take current value
				read_light = lightValue;
			}
		}
	}

	@Override
	public String getAverageName() {
		return AVERAGE_LIGHT;
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("1");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("1\n" + read_light);
	}

	public double getLight() {
		return lightValue;
	}

	@Override
	public void setUpdateRates() {
		mUpdateRates = new double[] { 1 };
		mDefaultUpdateRate = 1;
		mCurrentUpdateRate = 1;
	}

	@Override
	public String getSI() {
		return "lux";
	}

	public void setLight(double value) {
		lightValue = value;
	}

	public void addLight(double value) {
		lightValue += value;
	}
}
