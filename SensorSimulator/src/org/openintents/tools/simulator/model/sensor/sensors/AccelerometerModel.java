/*
 * Copyright (C) 2008 - 2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;
import java.util.Random;

import org.openintents.tools.simulator.model.telnet.Vector;

/**
 * AccelerometerModel keeps the internal data model behind Accelerometer Sensor.
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class AccelerometerModel extends SensorModel {

	private static Random mRandomGenerator = new Random();
	/**
	 * Current read-out value of accelerometer x-component.
	 * 
	 * This value is updated only at the desired updateSensorRate().
	 */
	private double mReadAccelx;
	/** Current read-out value of accelerometer y-component. */
	private double mReadAccely;
	/** Current read-out value of accelerometer z-component. */
	private double mReadAccelz;

	/**
	 * Internal state value of accelerometer x-component.
	 * 
	 * This value is updated regularly by updateSensorPhysics().
	 */
	private double mAccelX;
	/** Internal state value of accelerometer x-component. */
	private double mAccelY;
	/** Internal state value of accelerometer x-component. */
	private double mAccelZ;

	private double aX; // acceleration
	private double aZ;

	private double mAccX; // accelerometer position x on screen
	private double mAccZ; // (DONT confuse with acceleration a!)

	/**
	 * Partial read-out value of accelerometer x-component.
	 * 
	 * This partial value is used to calculate the sensor average.
	 */
	private double mPartialAccelX;
	/** Partial read-out value of accelerometer y-component. */
	private double mPartialAccelY;
	/** Partial read-out value of accelerometer z-component. */
	private double mPartialAccelZ;

	/** Number of summands in partial sum for accelerometer. */
	private int mPartialAccelN;

	/** Current position on screen. */
	private int mMoveX;
	/** Current position on screen. */
	private int mMoveZ;

	private double mVX; // velocity
	private double mVZ;

	/** Spring constant. */
	private double mSpringK;

	/**
	 * Mass of accelerometer test particle.
	 * 
	 * This is set to 1, as only the ratio k/m enters the simulation.
	 */
	private double mMass;

	private double mGamma; // damping of spring

	/** Inverse of screen pixel per meter */
	private double mMeterPerPixel;

	/**
	 * Gravity constant.
	 * 
	 * This takes the value 9.8 m/s^2.
	 * */
	private double mGConstant;

	// Accelerometer
	private double mAccelerometerLimit;
	private boolean mShowAcceleration;

	private WiiAccelerometerModel mWiiAccelerometerModel;

	public AccelerometerModel() {
		mAccX = 0;
		mAccZ = 0;

		mShowAcceleration = true;

		mMoveX = 0;
		mMoveZ = 0;

		mSpringK = 500; // spring constant
		mMass = 1; // mass
		mGamma = 50; // damping
		mMeterPerPixel = 1 / 3000.; // meter per pixel

		mGConstant = 9.80665; // meter per second^2
		mAccelerometerLimit = 10;
		mWiiAccelerometerModel = new WiiAccelerometerModel();
	}

	public void setXYZ(Vector vec) {
		mAccelX = vec.x;
		mAccelY = vec.y;
		mAccelZ = vec.z;
	}

	public void addRandom(double random) {
		double val;
		val = mRandomGenerator.nextDouble();
		mAccelX += (2 * val - 1) * random;

		val = mRandomGenerator.nextDouble();
		mAccelY += (2 * val - 1) * random;

		val = mRandomGenerator.nextDouble();
		mAccelZ += (2 * val - 1) * random;
	}

	public void limitate(double limit) {
		if (mAccelX > limit) {
			mAccelX = limit;
		}
		if (mAccelX < -limit) {
			mAccelX = -limit;
		}
		if (mAccelY > limit) {
			mAccelY = limit;
		}
		if (mAccelY < -limit) {
			mAccelY = -limit;
		}
		if (mAccelZ > limit) {
			mAccelZ = limit;
		}
		if (mAccelZ < -limit) {
			mAccelZ = -limit;
		}
	}

	public void reset() {
		mAccelX = 0;
		mAccelY = 0;
		mAccelZ = 0;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialAccelX += mAccelX;
			mPartialAccelY += mAccelY;
			mPartialAccelZ += mAccelZ;
			mPartialAccelN++;
		}

		// Update
		if (currentTime >= mNextUpdate) {
			mNextUpdate += mUpdateDuration;
			if (mNextUpdate < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				mNextUpdate = currentTime;
			}

			if (mAverage) {
				// form average
				computeAvg();

				// reset average
				resetAvg();
			} else {
				// Only take current value
				mReadAccelx = mAccelX;
				mReadAccely = mAccelY;
				mReadAccelz = mAccelZ;
			}
		}
	}

	public void resetAvg() {
		mPartialAccelX = 0;
		mPartialAccelY = 0;
		mPartialAccelZ = 0;
		mPartialAccelN = 0;
	}

	public void computeAvg() {
		mReadAccelx = mPartialAccelX / mPartialAccelN;
		mReadAccely = mPartialAccelY / mPartialAccelN;
		mReadAccelz = mPartialAccelZ / mPartialAccelN;
	}

	public double getAccelx() {
		return mAccelX;
	}

	public double getAccely() {
		return mAccelY;
	}

	public double getAccelz() {
		return mAccelZ;
	}

	public double getReadAccelerometerX() {
		return mReadAccelx;
	}

	public double getReadAccelerometerY() {
		return mReadAccely;
	}

	public double getReadAccelerometerZ() {
		return mReadAccelz;
	}

	@Override
	public String getName() {
		return SensorModel.ACCELEROMETER;
	}

	public double getGravityConstant() {
		return mGConstant;
	}

	public double getAccelerometerLimit() {
		return mAccelerometerLimit;
	}

	public double getPixelsPerMeter() {
		return 1.0 / mMeterPerPixel;
	}

	public double getSpringConstant() {
		return mSpringK;
	}

	public double getDampingConstant() {
		return mGamma;
	}

	public boolean isShown() {
		return mShowAcceleration;
	}

	@Override
	public void getNumSensorValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("3\n" + mReadAccelx + "\n" + mReadAccely + "\n"
				+ mReadAccelz);
	}

	public int getMoveX() {
		return mMoveX;
	}

	public int getMoveZ() {
		return mMoveZ;
	}

	public void setMoveX(int newmovex) {
		mMoveX = newmovex;
	}

	public void setMoveZ(int newmovez) {
		mMoveZ = newmovez;
	}

	public double getGInverse() {
		if (mGConstant != 0)
			return 1 / mGConstant;
		return 1 / 9.80665;
	}

	@Override
	public String getSI() {
		return "m/s" + SensorModel.SQUARED;
	}

	public double getMass() {
		return mMass;
	}

	public void setShown(boolean b) {
		mShowAcceleration = b;
	}

	public WiiAccelerometerModel getRealDeviceBridgeAddon() {
		return mWiiAccelerometerModel;
	}

	public void setWiiPath(String path) {
		mWiiAccelerometerModel.setWiiPath(path);
	}

	public boolean updateFromWii() {
		return mWiiAccelerometerModel.updateData();
	}

	public String getWiiStatus() {
		return mWiiAccelerometerModel.getStatus();
	}

	public int getWiiRoll() {
		return mWiiAccelerometerModel.getRoll();
	}

	public int getWiiPitch() {
		return mWiiAccelerometerModel.getPitch();
	}

	public double getAccelLimit() {
		return mAccelerometerLimit;
	}

	public void refreshAcceleration(double kView, double gammaView, double dt) {
		mSpringK = kView;
		mGamma = gammaView;

		// First calculate the force acting on the
		// sensor test particle, assuming that
		// the accelerometer is mounted by a string:
		// F = - k * x
		double Fx = kView * (mMoveX - mAccX);
		double Fz = gammaView * (mMoveZ - mAccZ);

		// a = F / m
		aX = Fx / mMass;
		aZ = Fz / mMass;

		mVX += aX * dt;
		mVZ += aZ * dt;

		// Now this is the force that tries to adjust
		// the accelerometer back
		// integrate dx/dt = v;
		mAccX += mVX * dt;
		mAccZ += mVZ * dt;

		// We put damping here: We don't want to damp for
		// zero motion with respect to the background,
		// but with respect to the mobile phone:
		mAccX += gammaView * (mMoveX - mAccX) * dt;
		mAccZ += gammaView * (mMoveZ - mAccZ) * dt;
	}

	public double getAx() {
		return aX;
	}

	public double getAz() {
		return aZ;
	}

	public void setAccelerometer(float[] newAcc) {
		mAccelX = newAcc[0];
		mAccelY = newAcc[1];
		mAccelZ = newAcc[2];

	}

	@Override
	public int getType() {
		return TYPE_ACCELEROMETER;
	}
}
