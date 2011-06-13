package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

public class ProximityModel extends SensorModel {

	// proximity
	public double proximityValue;
	/** Current read-out value of proximity. */
	private double read_proximity;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	/** Partial read-out value of proximity. */
	private float partial_proximity;
	/** Number of summands in partial sum for proximity. */
	private int partial_proximity_n;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long proximity_next_update;

	// Proximity
	private float mProximityRange;
	private boolean mBinaryProximity;
	private boolean isProximityNear; // false => far

	public ProximityModel() {
		super();
		proximityValue = 10;
		mProximityRange = 10;
		mBinaryProximity = true;
		isProximityNear = true;
	}

	@Override
	public String getName() {
		return SensorModel.PROXIMITY;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_proximity += proximityValue;
			partial_proximity_n++;
		}

		// Update
		if (currentTime >= proximity_next_update) {
			proximity_next_update += updateDuration;
			if (proximity_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				proximity_next_update = currentTime;
			}

			if (average) {
				// form average
				read_proximity = partial_proximity / partial_proximity_n;

				// reset average
				partial_proximity = 0;
				partial_proximity_n = 0;

			} else {
				// Only take current value
				read_proximity = proximityValue;
			}

		}
	}

	@Override
	public String getAverageName() {
		return AVERAGE_PROXIMITY;
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("1");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("1\n" + read_proximity);
	}

	public double getProximity() {
		return proximityValue;
	}

	public float getProximityRange() {
		return mProximityRange;
	}

	public boolean isNear() {
		return isProximityNear;
	}

	public boolean isBinary() {
		return mBinaryProximity;
	}

	@Override
	public void setUpdateRates() {
		mUpdateRates = new double[] { 1 };
		mDefaultUpdateRate = 1;
		mCurrentUpdateRate = 1;
	}

	@Override
	public String getSI() {
		return "cm";
	}

	public void setProximity(double value) {
		proximityValue = value;
	}

	public void addProximity(double value) {
		proximityValue += value;
	}
}
