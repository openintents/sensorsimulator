package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

public class TemperatureModel extends SensorModel {

	// thermometer
	public double temperatureValue;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long temperature_next_update;
	/** Current read-out value of temperature. */
	private double read_temperature;

	/** Partial read-out value of temperature. */
	private double partial_temperature;
	/** Number of summands in partial sum for temperature. */
	private int partial_temperature_n;

	public TemperatureModel() {
		super();
		temperatureValue = 17.7;
	}

	@Override
	public String getName() {
		return SensorModel.TEMPERATURE;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_temperature += temperatureValue;
			partial_temperature_n++;
		}

		// Update
		if (currentTime >= temperature_next_update) {
			temperature_next_update += updateDuration;
			if (temperature_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				temperature_next_update = currentTime;
			}

			if (average) {
				// form average
				read_temperature = partial_temperature / partial_temperature_n;

				// reset average
				partial_temperature = 0;
				partial_temperature_n = 0;

			} else {
				// Only take current value
				read_temperature = temperatureValue;
			}
		}
	}

	@Override
	public String getAverageName() {
		return AVERAGE_TEMPERATURE;
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("1");
	}

	public double getTemperature() {
		return temperatureValue;
	}

	@Override
	public void setUpdateRates() {
		mDefaultUpdateRate = 1;
		mCurrentUpdateRate = 1;
	}

	@Override
	public String getSI() {
		return SensorModel.DEGREES + "C";
	}

	public void setTemp(double value) {
		temperatureValue = value;
	}

	public void addTemp(double value) {
		temperatureValue += value;
	}

	public double getReadTemp() {
		return read_temperature;
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("1\n" + read_temperature);
	}
	@Override
	public String getTypeConstant() {
		return TYPE_TEMPERATURE;
	}
}
