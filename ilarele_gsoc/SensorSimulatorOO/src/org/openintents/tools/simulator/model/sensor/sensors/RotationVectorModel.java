package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

import org.openintents.tools.simulator.model.telnet.Vector;

public class RotationVectorModel extends SensorModel {
	// rotation angle in degree
	private double rotation_x_value;
	private double rotation_y_value;
	private double rotation_z_value;

	/** Current read-out value of rotation. */
	private double read_rotation_x;
	private double read_rotation_y;
	private double read_rotation_z;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long rotation_next_update;
	/** Partial read-out value of rotation. */
	private float partial_rotation_x;
	private float partial_rotation_y;
	private float partial_rotation_z;

	/** Number of summands in partial sum for rotation. */
	private int partial_rotation_n;

	public RotationVectorModel() {
		super();
		read_rotation_x = rotation_x_value = 0;
		read_rotation_y = rotation_y_value = 0;
		read_rotation_z = rotation_z_value = 0;
	}

	@Override
	public String getName() {
		return SensorModel.ROTATION_VECTOR;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_rotation_x += rotation_x_value;
			partial_rotation_y += rotation_y_value;
			partial_rotation_z += rotation_z_value;
			partial_rotation_n++;
		}

		// Update
		if (currentTime >= rotation_next_update) {
			rotation_next_update += updateDuration;
			if (rotation_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				rotation_next_update = currentTime;
			}

			if (average) {
				// form average
				read_rotation_x = partial_rotation_x / partial_rotation_n;
				read_rotation_y = partial_rotation_y / partial_rotation_n;
				read_rotation_z = partial_rotation_z / partial_rotation_n;
				// reset average
				partial_rotation_x = 0;
				partial_rotation_y = 0;
				partial_rotation_z = 0;
				partial_rotation_n = 0;
			} else {
				// Only take current value
				read_rotation_x = rotation_x_value;
				read_rotation_y = rotation_y_value;
				read_rotation_z = rotation_z_value;
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
		out.println("3\n" + Math.sin(Math.toRadians(read_rotation_x / 2))
				+ "\n" + Math.sin(Math.toRadians(read_rotation_y / 2)) + "\n"
				+ Math.sin(Math.toRadians(read_rotation_z / 2)));

	}

	public double getRotationVectorX() {
		return Math.sin(Math.toRadians(read_rotation_x / 2));
	}

	public double getRotationVectorY() {
		return Math.sin(Math.toRadians(read_rotation_y / 2));
	}

	public double getRotationVectorZ() {
		return Math.sin(Math.toRadians(read_rotation_z / 2));
	}

	@Override
	public void setUpdateRates() {
		mDefaultUpdateRate = 50;
		mCurrentUpdateRate = 50;
	}

	@Override
	public String getSI() {
		return SensorModel.DEGREES;
	}

	public void setRotationVector(double x, double y, double z) {
		rotation_x_value = x;
		rotation_y_value = y;
		rotation_z_value = z;
	}

	public void addRotationVector(double addX, double addY, double addZ) {
		rotation_x_value += addX;
		rotation_y_value += addY;
		rotation_z_value += addZ;
	}

	@Override
	public String getTypeConstant() {
		return TYPE_GRAVITY;
	}

	public double getReadRotationVectorX() {
		return Math.sin(Math.toRadians(read_rotation_x / 2));
	}

	public double getReadRotationVectorY() {
		return Math.sin(Math.toRadians(read_rotation_y / 2));
	}

	public double getReadRotationVectorZ() {
		return Math.sin(Math.toRadians(read_rotation_z / 2));
	}

	public void setRotationVector(Vector vec) {
		rotation_x_value = vec.x;
		rotation_y_value = vec.y;
		rotation_z_value = vec.z;
	}
}
