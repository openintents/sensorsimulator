package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

import org.openintents.tools.simulator.model.telnet.Vector;

public class MagneticFieldModel extends SensorModel {

	/** Current read-out value of compass x-component. */
	private double read_compassx;
	/** Current read-out value of compass y-component. */
	private double read_compassy;
	/** Current read-out value of compass z-component. */
	private double read_compassz;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long compass_next_update;

	/** Partial read-out value of compass x-component. */
	private double partial_compassx;
	/** Partial read-out value of compass y-component. */
	private double partial_compassy;
	/** Partial read-out value of compass z-component. */
	private double partial_compassz;
	/** Number of summands in partial sum for compass. */
	private int partial_compass_n;

	/** Internal state value of compass x-component. */
	private double compassx;
	/** Internal state value of compass y-component. */
	private double compassy;
	/** Internal state value of compass z-component. */
	private double compassz;

	// Magnetic field
	private double mNorth;
	private double mEast;
	private double mVertical;

	public MagneticFieldModel() {
		super();
		mNorth = 22874.1;
		mEast = 5939.5;
		mVertical = 43180.5;
		mEnabled = true;
	}

	@Override
	public String getName() {
		return SensorModel.MAGNETIC_FIELD;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_compassx += compassx;
			partial_compassy += compassy;
			partial_compassz += compassz;
			partial_compass_n++;
		}

		// Update
		if (currentTime >= compass_next_update) {
			compass_next_update += updateDuration;
			if (compass_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				compass_next_update = currentTime;
			}

			if (average) {
				// form average
				read_compassx = partial_compassx / partial_compass_n;
				read_compassy = partial_compassy / partial_compass_n;
				read_compassz = partial_compassz / partial_compass_n;

				// reset average
				partial_compassx = 0;
				partial_compassy = 0;
				partial_compassz = 0;
				partial_compass_n = 0;

			} else {
				// Only take current value
				read_compassx = compassx;
				read_compassy = compassy;
				read_compassz = compassz;
			}
		}
	}

	public double getVertical() {
		return mVertical;
	}

	public double getEast() {
		return mEast;
	}

	public double getNorth() {
		return mNorth;
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("3\n" + read_compassx + "\n" + read_compassy + "\n"
				+ read_compassz);
	}

	@Override
	public String getSI() {
		return "nT";
	}

	public void setVertical(double value) {
		mVertical = value;
	}

	public void setEast(double value) {
		mEast = value;
	}

	public void setNorth(double value) {
		mNorth = value;
	}

	public void setCompass(Vector vec) {
		compassx = vec.x;
		compassy = vec.y;
		compassz = vec.z;
	}

	public void resetCompas() {
		compassx = 0;
		compassy = 0;
		compassz = 0;
	}

	public double getReadCompassX() {
		return read_compassx;
	}

	public double getReadCompassY() {
		return read_compassy;
	}

	public double getReadCompassZ() {
		return read_compassz;
	}

	@Override
	public String getTypeConstant() {
		return TYPE_MAGNETIC_FIELD;
	}
}
