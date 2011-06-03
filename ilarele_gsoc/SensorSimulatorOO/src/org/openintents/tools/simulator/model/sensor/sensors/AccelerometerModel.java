package org.openintents.tools.simulator.model.sensor.sensors;

import hr.fer.tel.simulator.Global;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;

import org.openintents.tools.simulator.model.telnet.Vector;

public class AccelerometerModel extends SensorModel {
	private static Random rand = new Random();
	/**
	 * Current read-out value of accelerometer x-component.
	 * 
	 * This value is updated only at the desired updateSensorRate().
	 */
	private double read_accelx;
	/** Current read-out value of accelerometer y-component. */
	private double read_accely;
	/** Current read-out value of accelerometer z-component. */
	private double read_accelz;

	/**
	 * Internal state value of accelerometer x-component.
	 * 
	 * This value is updated regularly by updateSensorPhysics().
	 */
	private double accelx;
	/** Internal state value of accelerometer x-component. */
	private double accely;
	/** Internal state value of accelerometer x-component. */
	private double accelz;

	private double ax; // acceleration
	private double az;

	private double accx; // accelerometer position x on screen
	private double accz; // (DONT confuse with acceleration a!)

	/**
	 * Partial read-out value of accelerometer x-component.
	 * 
	 * This partial value is used to calculate the sensor average.
	 */
	private double partial_accelx;
	/** Partial read-out value of accelerometer y-component. */
	private double partial_accely;
	/** Partial read-out value of accelerometer z-component. */
	private double partial_accelz;

	/** Number of summands in partial sum for accelerometer. */
	private int partial_accel_n;

	/**
	 * Time of next update required. The time is compared to
	 * System.currentTimeMillis().
	 */
	private long accel_next_update;

	/** Current position on screen. */
	private int movex;
	/** Current position on screen. */
	private int movez;

	private double vx; // velocity
	private double vz;

	/** Spring constant. */
	private double k; // spring constant

	/**
	 * Mass of accelerometer test particle.
	 * 
	 * This is set to 1, as only the ratio k/m enters the simulation.
	 */
	private double m; // mass of accelerometer test particle

	private double gamma; // damping of spring

	/** Inverse of screen pixel per meter */
	private double meterperpixel;

	/**
	 * Gravity constant.
	 * 
	 * This takes the value 9.8 m/s^2.
	 * */
	private double g;

	// Accelerometer
	private double mAccelerometerLimit;
	private boolean mShowAcceleration;

	// Gravity
	private double gX;
	private double gY;
	private double gZ;

	private WiiAccelerometerModel wiiAccelerometerModel;

	public AccelerometerModel(int posX, int posY) {
		super();
		accx = posX;
		accz = posY;
		// mEnabled = true;

		mShowAcceleration = true;

		movex = 0;
		movez = 0;

		k = 500; // spring constant
		m = 1; // mass
		gamma = 50; // damping
		meterperpixel = 1 / 3000.; // meter per pixel

		g = 9.80665; // meter per second^2
		mAccelerometerLimit = 10;
		mEnabled = true;
		wiiAccelerometerModel = new WiiAccelerometerModel();
	}

	public void setXYZ(Vector vec) {
		accelx = vec.x;
		accely = vec.y;
		accelz = vec.z;
	}

	public double getXPos() {
		return accx;
	}

	public double getZPos() {
		return accz;
	}

	public void setA(double newAccX, double newAccZ) {
		ax = newAccX;
		az = newAccZ;
	}

	public double getAx() {
		return ax;
	}

	public double getAz() {
		return az;
	}

	public void adjustPos(double addPosX, double addPosZ) {
		accx += addPosX;
		accz += addPosZ;
	}

	public void fixRespect(double gamma, int movex, int movez, double dt) {
		accx += gamma * (movex - accx) * dt;
		accz += gamma * (movez - accz) * dt;
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

	public void reset() {
		accelx = 0;
		accely = 0;
		accelz = 0;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (average) {
			partial_accelx += accelx;
			partial_accely += accely;
			partial_accelz += accelz;
			partial_accel_n++;
		}

		// Update
		if (currentTime >= accel_next_update) {
			accel_next_update += updateDuration;
			if (accel_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				accel_next_update = currentTime;
			}

			if (average) {
				// form average
				computeAvg();

				// reset average
				resetAvg();
			} else {
				// Only take current value
				read_accelx = accelx;
				read_accely = accely;
				read_accelz = accelz;
			}
		}
	}

	public void resetAvg() {
		partial_accelx = 0;
		partial_accely = 0;
		partial_accelz = 0;
		partial_accel_n = 0;
	}

	public void computeAvg() {
		read_accelx = partial_accelx / partial_accel_n;
		read_accely = partial_accely / partial_accel_n;
		read_accelz = partial_accelz / partial_accel_n;
	}

	public double getAccelx() {
		return accelx;
	}

	public double getAccely() {
		return accely;
	}

	public double getAccelz() {
		return accelz;
	}

	public double getReadAccelerometerX() {
		return read_accelx;
	}

	public double getReadAccelerometerY() {
		return read_accely;
	}

	public double getReadAccelerometerZ() {
		return read_accelz;
	}

	@Override
	public String getName() {
		return SensorModel.ACCELEROMETER;
	}

	@Override
	public String getAverageName() {
		return AVERAGE_ACCELEROMETER;
	}

	public double getGravityConstant() {
		return g;
	}

	public double getAccelerometerLimit() {
		return mAccelerometerLimit;
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

	public double getGravityX() {
		return gX;
	}

	public double getGravityY() {
		return gY;
	}

	public double getGravityZ() {
		return gZ;
	}

	public boolean isShown() {
		return mShowAcceleration;
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("3\n" + read_accelx + "\n" + read_accely + "\n"
				+ read_accelz);
	}

	public int getMoveX() {
		return movex;
	}

	public int getMoveZ() {
		return movez;
	}

	public void setMoveX(int newmovex) {
		movex = newmovex;
	}

	public void setMoveZ(int newmovez) {
		movez = newmovez;
	}

	public double getGInverse() {
		if (g != 0)
			return 1 / g;
		return 1 / 9.80665;
	}

	@Override
	public void setUpdateRates() {
		mUpdateRates = new double[] { 1, 10, 50 };
		mDefaultUpdateRate = 50;
		mCurrentUpdateRate = 50;
	}

	@Override
	public String getSI() {
		return "m/s" + SensorModel.SQUARED;
	}

	public double getMass() {
		return m;
	}

	public void addVX(double value) {
		vx += value;
	}

	public void addVZ(double value) {
		vz += value;
	}

	public double getVX() {
		return vx;
	}

	public double getVZ() {
		return vz;
	}

	public void setShown(boolean b) {
		mShowAcceleration = b;
	}

	public WiiAccelerometerModel getRealDeviceBridgeAddon() {
		return wiiAccelerometerModel;
	}

	public void setWiiPath(String path) {
		wiiAccelerometerModel.setWiiPath(path);
	}

	public boolean updateFromWii() {
		return wiiAccelerometerModel.updateData();
	}

	public String getWiiStatus() {
		return wiiAccelerometerModel.getStatus();
	}

	public int getWiiRoll() {
		return wiiAccelerometerModel.getRoll();
	}

	public int getWiiPitch() {
		return wiiAccelerometerModel.getPitch();
	}

	public double getAccelLimit() {
		return mAccelerometerLimit;
	}

	public double getAccZ() {
		return accz;
	}

	public double getAccX() {
		return accx;
	}
}
