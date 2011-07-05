package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

import org.openintents.tools.simulator.model.telnet.Vector;

public class GravityModel extends SensorModel {
	// gravity
	private double gravity_x_value;
	private double gravity_y_value;
	private double gravity_z_value;
	/** Current read-out value of gravity. */
	private double read_gravity_x;
	private double read_gravity_y;
	private double read_gravity_z;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long gravity_next_update;
	/** Partial read-out value of gravity. */
	private float partial_gravity_x;
	private float partial_gravity_y;
	private float partial_gravity_z;

	/** Number of summands in partial sum for gravity. */
	private int partial_gravity_n;

	// Gravity
	private double g;
	private double mGravityLimit;

	public GravityModel() {
		super();
		read_gravity_x = gravity_x_value = 0;
		read_gravity_y = gravity_y_value = 0;
		read_gravity_z = gravity_z_value = -9.8;

		g = 9.80665; // meter per second^2
		mGravityLimit = 10;
	}

	@Override
	public String getName() {
		return SensorModel.GRAVITY;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_gravity_x += gravity_x_value;
			partial_gravity_y += gravity_y_value;
			partial_gravity_z += gravity_z_value;
			partial_gravity_n++;
		}

		// Update
		if (currentTime >= gravity_next_update) {
			gravity_next_update += updateDuration;
			if (gravity_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				gravity_next_update = currentTime;
			}

			if (average) {
				// form average
				read_gravity_x = partial_gravity_x / partial_gravity_n;
				read_gravity_y = partial_gravity_y / partial_gravity_n;
				read_gravity_z = partial_gravity_z / partial_gravity_n;
				// reset average
				partial_gravity_x = 0;
				partial_gravity_y = 0;
				partial_gravity_z = 0;
				partial_gravity_n = 0;
			} else {
				// Only take current value
				read_gravity_x = gravity_x_value;
				read_gravity_y = gravity_y_value;
				read_gravity_z = gravity_z_value;
			}
		}
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("3\n" + read_gravity_x + "\n" + read_gravity_y + "\n"
				+ read_gravity_z);

	}

	public double getGravityX() {
		return read_gravity_x;
	}

	public double getGravityY() {
		return read_gravity_y;
	}

	public double getGravityZ() {
		return read_gravity_z;
	}

	@Override
	public String getSI() {
		return "m/s" + SensorModel.SQUARED;
	}

	public void setGravity(double x, double y, double z) {
		gravity_x_value = x;
		gravity_y_value = y;
		gravity_z_value = z;
	}

	public void addGravity(double addX, double addY, double addZ) {
		gravity_x_value += addX;
		gravity_y_value += addY;
		gravity_z_value += addZ;
	}

	@Override
	public String getTypeConstant() {
		return TYPE_GRAVITY;
	}

	public double getReadGravityX() {
		return read_gravity_x;
	}

	public double getReadGravityY() {
		return read_gravity_y;
	}

	public double getReadGravityZ() {
		return read_gravity_z;
	}

	public void setGravity(Vector vec) {
		gravity_x_value = vec.x;
		gravity_y_value = vec.y;
		gravity_z_value = vec.z;
	}

	public double getGravityConstant() {
		return g;
	}

	public double getAccelLimit() {
		return mGravityLimit;
	}
}
