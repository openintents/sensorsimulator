package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

public class OrientationModel extends SensorModel {
	/** Current read-out value of orientation yaw. */
	private double read_yaw;
	/** Current read-out value of orientation pitch. */
	private double read_pitch;
	/** Current read-out value of orientation roll. */
	private double read_roll;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long orientation_next_update;

	/** Partial read-out value of orientation yaw. */
	private double partial_yaw;
	/** Partial read-out value of orientation pitch. */
	private double partial_pitch;
	/** Partial read-out value of orientation roll. */
	private double partial_roll;
	/** Number of summands in partial sum for orientation. */
	private int partial_orientation_n;

	// orientation (in degree)
	// Yaw = Rotation about the Y-Axis
	// Pitch = Rotation about the X-Axis
	// Roll = Rotation about the Z-Axis
	private int yaw;
	private int pitch;
	private int roll;

	public OrientationModel() {
		super();
		mEnabled = true;
	}

	@Override
	public String getName() {
		return SensorModel.ORIENTATION;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_yaw += yaw;
			partial_pitch += pitch;
			partial_roll += roll;
			partial_orientation_n++;
		}

		// Update
		if (currentTime >= orientation_next_update) {
			orientation_next_update += updateDuration;
			if (orientation_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				orientation_next_update = currentTime;
			}

			if (average) {
				// form average
				read_yaw = partial_yaw / partial_orientation_n;
				read_pitch = partial_pitch / partial_orientation_n;
				read_roll = partial_roll / partial_orientation_n;

				// reset average
				partial_yaw = 0;
				partial_pitch = 0;
				partial_roll = 0;
				partial_orientation_n = 0;

			} else {
				// Only take current value
				read_yaw = yaw;
				read_pitch = pitch;
				read_roll = roll;
			}

			// Normalize values:

			// Restrict pitch value to -90 to +90
			if (read_pitch < -90) {
				read_pitch = -180 - read_pitch;
				read_yaw += 180;
				read_roll += 180;
			} else if (read_pitch > 90) {
				read_pitch = 180 - read_pitch;
				read_yaw += 180;
				read_roll += 180;
			}

			// yaw from 0 to 360
			if (read_yaw < 0) {
				read_yaw = read_yaw + 360;
			}
			if (read_yaw >= 360) {
				read_yaw -= 360;
			}

			// roll from -180 to + 180
			if (read_roll >= 180) {
				read_roll -= 360;
			}
		}
	}

	public double getReadPitch() {
		return read_pitch;
	}

	public double getReadYaw() {
		return read_yaw;
	}

	public double getReadRoll() {
		return read_roll;
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("3\n" + read_yaw + "\n" + read_pitch + "\n" + read_roll);
	}

	@Override
	public String getSI() {
		return SensorModel.DEGREES;
	}

	public int getPitch() {
		return pitch;
	}

	public int getYaw() {
		return yaw;
	}

	public int getRoll() {
		return roll;
	}

	public void setYaw(int value) {
		yaw = value;
	}

	public void setPitch(int value) {
		pitch = value;
	}

	public void setRoll(int value) {
		roll = value;
	}

	public void addYaw(double value) {
		yaw += value;
	}

	public void addPitch(double value) {
		pitch += value;
	}

	public void addRoll(double value) {
		roll += value;
	}

	@Override
	public String toString() {
		return "[y=" + yaw + ", r=" + roll + ", p=" + pitch + "]";
	}

	@Override
	public String getTypeConstant() {
		return TYPE_ORIENTATION;
	}
}
