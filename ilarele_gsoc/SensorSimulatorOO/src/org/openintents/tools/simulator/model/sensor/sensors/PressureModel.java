package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

public class PressureModel extends SensorModel {

	// pressure
	private double pressureValue;
	/** Current read-out value of pressure. */
	private double read_pressure;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long pressure_next_update;
	/** Partial read-out value of pressure. */
	private float partial_pressure;
	/** Number of summands in partial sum for pressure. */
	private int partial_pressure_n;

	public PressureModel() {
		super();
		pressureValue = 0.5;
	}

	@Override
	public String getName() {
		return SensorModel.PRESSURE;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_pressure += pressureValue;
			partial_pressure_n++;
		}

		// Update
		if (currentTime >= pressure_next_update) {
			pressure_next_update += updateDuration;
			if (pressure_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				pressure_next_update = currentTime;
			}

			if (average) {
				// form average
				read_pressure = partial_pressure / partial_pressure_n;

				// reset average
				partial_pressure = 0;
				partial_pressure_n = 0;

			} else {
				// Only take current value
				read_pressure = pressureValue;
			}
		}
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("1");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("1\n" + read_pressure);
	}

	public double getPressure() {
		return pressureValue;
	}

	@Override
	public String getSI() {
		return "";
	}

	public double getReadPressure() {
		return read_pressure;
	}

	public void setPressure(double value) {
		if (value > 1)
			pressureValue = 1;
		else if (value < 0)
			pressureValue = 0;
		else
			pressureValue = value;
	}

	public void addPressure(double value) {
		setPressure(pressureValue + value);
	}

	@Override
	public String getTypeConstant() {
		return TYPE_PRESSURE;
	}
}
