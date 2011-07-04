package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

import org.openintents.tools.simulator.model.telnet.Vector;

public class LinearAccelerationModel extends SensorModel {
	// linear_acceleration
	private double linear_acc_x_value;
	private double linear_acc_y_value;
	private double linear_acc_z_value;
	/** Current read-out value of linear_acceleration. */
	private double read_linear_acc_x;
	private double read_linear_acc_y;
	private double read_linear_acc_z;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long linear_acceleration_next_update;
	/** Partial read-out value of linear_acceleration. */
	private float partial_linear_acc_x;
	private float partial_linear_acc_y;
	private float partial_linear_acc_z;

	/** Number of summands in partial sum for linear_acceleration. */
	private int partial_linear_acceleration_n;

	public LinearAccelerationModel() {
		super();
		read_linear_acc_x = linear_acc_x_value = 0.1;
		read_linear_acc_y = linear_acc_y_value = 0.1;
		read_linear_acc_z = linear_acc_z_value = 0.1;
	}

	@Override
	public String getName() {
		return SensorModel.LINEAR_ACCELERATION;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_linear_acc_x += linear_acc_x_value;
			partial_linear_acc_y += linear_acc_y_value;
			partial_linear_acc_z += linear_acc_z_value;
			partial_linear_acceleration_n++;
		}

		// Update
		if (currentTime >= linear_acceleration_next_update) {
			linear_acceleration_next_update += updateDuration;
			if (linear_acceleration_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				linear_acceleration_next_update = currentTime;
			}

			if (average) {
				// form average
				read_linear_acc_x = partial_linear_acc_x
						/ partial_linear_acceleration_n;
				read_linear_acc_y = partial_linear_acc_y
						/ partial_linear_acceleration_n;
				read_linear_acc_z = partial_linear_acc_z
						/ partial_linear_acceleration_n;
				// reset average
				partial_linear_acc_x = 0;
				partial_linear_acc_y = 0;
				partial_linear_acc_z = 0;
				partial_linear_acceleration_n = 0;

			} else {
				// Only take current value
				read_linear_acc_x = linear_acc_x_value;
				read_linear_acc_y = linear_acc_y_value;
				read_linear_acc_z = linear_acc_z_value;

			}
		}
	}

	@Override
	public String getAverageName() {
		return AVERAGE_LINEAR_ACCELERATION;
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("3\n" + read_linear_acc_x + "\n" + read_linear_acc_y + "\n"
				+ read_linear_acc_z);

	}

	public double getLinearAccelerationX() {
		return read_linear_acc_x;
	}

	public double getLinearAccelerationY() {
		return read_linear_acc_y;
	}

	public double getLinearAccelerationZ() {
		return read_linear_acc_z;
	}

	@Override
	public void setUpdateRates() {
		mDefaultUpdateRate = 50;
		mCurrentUpdateRate = 50;
	}

	@Override
	public String getSI() {
		return "m/s" + SensorModel.SQUARED;
	}

	public void setLinearAcceleration(double x, double y, double z) {
		linear_acc_x_value = x;
		linear_acc_y_value = y;
		linear_acc_z_value = z;
	}

	public void addLinearAcceleration(double addX, double addY, double addZ) {
		linear_acc_x_value += addX;
		linear_acc_y_value += addY;
		linear_acc_z_value += addZ;
	}

	@Override
	public String getTypeConstant() {
		return TYPE_LINEAR_ACCELERATION;
	}

	public double getReadLinearAccelerationX() {
		return read_linear_acc_x;
	}

	public double getReadLinearAccelerationY() {
		return read_linear_acc_y;
	}

	public double getReadLinearAccelerationZ() {
		return read_linear_acc_z;
	}

	public void setLinearAcceleration(Vector vec) {
		linear_acc_x_value = vec.x;
		linear_acc_y_value = vec.y;
		linear_acc_z_value = vec.z;
	}
}
