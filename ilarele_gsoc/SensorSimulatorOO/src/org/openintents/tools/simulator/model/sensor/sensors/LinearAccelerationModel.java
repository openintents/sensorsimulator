package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;
import java.util.Random;

import org.openintents.tools.simulator.model.telnet.Vector;

public class LinearAccelerationModel extends SensorModel {
	private static Random rand = new Random();

	// linear_acceleration
	private double accelx;
	private double accely;
	private double accelz;
	
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
	private double k;
	private double gamma;
	private int movex;
	private double accx;
	private int movez;
	private double accz;
	private double ax;
	private double az;
	private double m;
	private double vx;
	private double vz;
	private double meterperpixel;


	public LinearAccelerationModel() {
		super();
		accx = 0;
		accz = 0;

		movex = 0;
		movez = 0;

		k = 500; // spring constant
		m = 1; // mass
		gamma = 50; // damping
		meterperpixel = 1 / 3000.; // meter per pixel
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
			partial_linear_acc_x += accelx;
			partial_linear_acc_y += accely;
			partial_linear_acc_z += accelz;
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
				read_linear_acc_x = accelx;
				read_linear_acc_y = accely;
				read_linear_acc_z = accelz;

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
		out.println("3\n" + read_linear_acc_x + "\n" + read_linear_acc_y + "\n"
				+ read_linear_acc_z);

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
//
//	public void setLinearAcceleration(double x, double y, double z) {
//		linear_acc_x_value = x;
//		linear_acc_y_value = y;
//		linear_acc_z_value = z;
//	}
//
//	public void addLinearAcceleration(double addX, double addY, double addZ) {
//		linear_acc_x_value += addX;
//		linear_acc_y_value += addY;
//		linear_acc_z_value += addZ;
//	}

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

	public double getPixelsPerMeter() {
		return 1.0 / meterperpixel;
	}

	public double getSpringConstant() {
		return k;
	}

	public double getDampingConstant() {
		return gamma;
	}

	public void reset() {
		accelx = 0;
		accely = 0;
		accelz = 0;
	}

	public void setXYZ(Vector vec) {
		accelx = vec.x;
		accely = vec.y;
		accelz = vec.z;
	}

	public double getAz() {
		return az;
	}

	public double getAx() {
		return ax;
	}

	public void refreshAcceleration(double kView, double gammaView, double dt) {
		k = kView;
		gamma = gammaView;

		// First calculate the force acting on the
		// sensor test particle, assuming that
		// the accelerometer is mounted by a string:
		// F = - k * x
		double Fx = kView * (movex - accx);
		double Fz = gammaView * (movez - accz);

		// a = F / m
		ax = Fx / m;
		az = Fz / m;

		vx += ax * dt;
		vz += az * dt;

		// Now this is the force that tries to adjust
		// the accelerometer back
		// integrate dx/dt = v;
		accx += vx * dt;
		accz += vz * dt;

		// We put damping here: We don't want to damp for
		// zero motion with respect to the background,
		// but with respect to the mobile phone:
		accx += gammaView * (movex - accx) * dt;
		accz += gammaView * (movez - accz) * dt;
	}

	public void addRandom(double random) {
		double val;
		val = rand.nextDouble();
		accelx += (2 * val - 1) * random;

		val = rand.nextDouble();
		accely += (2 * val - 1) * random;

		val = rand.nextDouble();
		accelz += (2 * val - 1) * random;
	}

	public void limitate(double limit) {
		if (accelx > limit)
			accelx = limit;
		if (accelx < -limit)
			accelx = -limit;
		if (accely > limit)
			accely = limit;
		if (accely < -limit)
			accely = -limit;
		if (accelz > limit)
			accelz = limit;
		if (accelz < -limit)
			accelz = -limit;
	}

	public int getMoveZ() {
		return movez;
	}

	public int getMoveX() {
		return movex;
	}

	public void setMoveZ(int newmovez) {
		movez = newmovez;
	}

	public void setMoveX(int newmovex) {
		movex = newmovex;
	}
}
