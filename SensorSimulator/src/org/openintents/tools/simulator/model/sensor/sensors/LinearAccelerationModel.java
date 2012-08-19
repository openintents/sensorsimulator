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
 * @author ilarele
 * 
 */
public class LinearAccelerationModel extends SensorModel {
	private static Random mRandomGenerator = new Random();

	// linear_acceleration
	private double mAccelX;
	private double mAccelY;
	private double mAccelZ;

	/** Current read-out value of linear_acceleration. */
	private double mReadLinearAccX;
	private double mReadLinearAccY;
	private double mReadLinearAccZ;

	/** Partial read-out value of linear_acceleration. */
	private float mPartialLinearAccX;
	private float mPartialLinearAccY;
	private float mPartialLinearAccZ;

	/** Number of summands in partial sum for linear_acceleration. */
	private int mPartialLinearAccN;

	private double mSpringK;
	private double mGamma;

	private int mMoveX;
	private int mMoveZ;

	private double mAccX; // position x on screen
	private double mAccZ; // (DONT confuse with acceleration a!)

	private double mAX;
	private double mAZ;
	private double mMass;
	private double mVX;
	private double mVZ;
	private double mMeterPerPixel;

	public LinearAccelerationModel() {
		mAccX = 0;
		mAccZ = 0;

		mMoveX = 0;
		mMoveZ = 0;

		mSpringK = 500; // spring constant
		mMass = 1; // mass
		mGamma = 50; // damping
		mMeterPerPixel = 1 / 3000.; // meter per pixel
	}

	@Override
	public String getName() {
		return SensorModel.LINEAR_ACCELERATION;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialLinearAccX += mAccelX;
			mPartialLinearAccY += mAccelY;
			mPartialLinearAccZ += mAccelZ;
			mPartialLinearAccN++;
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
				mReadLinearAccX = mPartialLinearAccX / mPartialLinearAccN;
				mReadLinearAccY = mPartialLinearAccY / mPartialLinearAccN;
				mReadLinearAccZ = mPartialLinearAccZ / mPartialLinearAccN;
				// reset average
				mPartialLinearAccX = 0;
				mPartialLinearAccY = 0;
				mPartialLinearAccZ = 0;
				mPartialLinearAccN = 0;

			} else {
				// Only take current value
				mReadLinearAccX = mAccelX;
				mReadLinearAccY = mAccelY;
				mReadLinearAccZ = mAccelZ;

			}
		}
	}

	@Override
	public void getNumSensorValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("3\n" + mReadLinearAccX + "\n" + mReadLinearAccY + "\n"
				+ mReadLinearAccZ);

	}

	@Override
	public String getSI() {
		return "m/s" + SensorModel.SQUARED;
	}

	public double getReadLinearAccelerationX() {
		return mReadLinearAccX;
	}

	public double getReadLinearAccelerationY() {
		return mReadLinearAccY;
	}

	public double getReadLinearAccelerationZ() {
		return mReadLinearAccZ;
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

	public void reset() {
		mAccelX = 0;
		mAccelY = 0;
		mAccelZ = 0;
	}

	public void setXYZ(Vector vec) {
		mAccelX = vec.x;
		mAccelY = vec.y;
		mAccelZ = vec.z;
	}

	public double getAz() {
		return mAZ;
	}

	public double getAx() {
		return mAX;
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
		mAX = Fx / mMass;
		mAZ = Fz / mMass;

		mVX += mAX * dt;
		mVZ += mAZ * dt;

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

	public int getMoveZ() {
		return mMoveZ;
	}

	public int getMoveX() {
		return mMoveX;
	}

	public void setMoveZ(int newmovez) {
		mMoveZ = newmovez;
	}

	public void setMoveX(int newmovex) {
		mMoveX = newmovex;
	}

	public void setLinearAcceleration(float[] newValue) {
		mAccelX = newValue[0];
		mAccelY = newValue[1];
		mAccelZ = newValue[2];
	}

	@Override
	public int getType() {
		return TYPE_LINEAR_ACCELERATION;
	}
}
