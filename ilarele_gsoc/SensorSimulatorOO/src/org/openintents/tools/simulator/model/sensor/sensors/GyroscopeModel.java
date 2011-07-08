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
 * GyroscopeModel keeps the internal data model behind Gyroscope Sensor.
 * 
 * @author ilarele
 * 
 */
public class GyroscopeModel extends SensorModel {
	private static final double EPSILON = 0.10;

	// yaw(rotation around y-axis
	// pitch(rotation around x-axis)
	// roll(rotation around z-axis)
	private static Random mRandomGenerator = new Random();

	private double mInstantSpeedYaw;
	private double mInstantSpeedRoll;
	private double mInstantSpeedPitch;

	/** Current read-out value of gyroscope. */
	private double mReadAngleSpeedYaw;
	private double mReadAngleSpeedRoll;
	private double mReadAngleSpeedPitch;

	/** Partial read-out value of gyroscope. */
	private float mPartialAngleSpeedYaw;
	private float mPartialAngleSpeedRoll;
	private float mPartialAngleSpeedPitch;

	/** Number of summands in partial sum for gyroscope. */
	private int mPartialAngleSpeedN;

	// current yaw value
	// private double mCrtYaw;
	// private double mCrtRoll;
	// private double mCrtPitch;

	private double mOldYaw;
	private double mOldRoll;
	private double mOldPitch;

	// rotation radius in meters
	private double mRadiusYaw;
	private double mRadiusRoll;
	private double mRadiusPitch;

	public GyroscopeModel() {
		super();
		// mCrtYaw = 0;
		// mCrtPitch = 0;

		mRadiusPitch = 0.1;
		mRadiusYaw = 0.15;
		mRadiusRoll = 0.1;
	}

	@Override
	public String getName() {
		return SensorModel.GYROSCOPE;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialAngleSpeedYaw += mInstantSpeedYaw;
			mPartialAngleSpeedRoll += mInstantSpeedRoll;
			mPartialAngleSpeedPitch += mInstantSpeedPitch;
			mPartialAngleSpeedN++;
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
				mReadAngleSpeedYaw = mPartialAngleSpeedYaw
						/ mPartialAngleSpeedN;
				mReadAngleSpeedRoll = mPartialAngleSpeedRoll
						/ mPartialAngleSpeedN;
				mReadAngleSpeedPitch = mPartialAngleSpeedPitch
						/ mPartialAngleSpeedN;
				// reset average
				mPartialAngleSpeedYaw = 0;
				mPartialAngleSpeedRoll = 0;
				mPartialAngleSpeedPitch = 0;
				mPartialAngleSpeedN = 0;

			} else {
				// Only take current value
				mReadAngleSpeedYaw = mInstantSpeedYaw;
				mReadAngleSpeedRoll = mInstantSpeedRoll;
				mReadAngleSpeedPitch = mInstantSpeedPitch;
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
		out.println("3\n" + mReadAngleSpeedPitch + "\n" + mReadAngleSpeedYaw
				+ "\n" + mReadAngleSpeedRoll);

	}

	@Override
	public String getSI() {
		return "rad/s";
	}

	@Override
	public String getTypeConstant() {
		return TYPE_GYROSCOPE;
	}

	public double getReadGyroscopeYaw() {
		return mReadAngleSpeedYaw;
	}

	public double getReadGyroscopeRoll() {
		return mReadAngleSpeedRoll;
	}

	public double getReadGyroscopePitch() {
		return mReadAngleSpeedPitch;
	}

	public void reset() {
		mInstantSpeedYaw = 0;
		mInstantSpeedRoll = 0;
		mInstantSpeedPitch = 0;
	}

	public void setXYZ(Vector vec) {
		mInstantSpeedYaw = vec.x;
		mInstantSpeedRoll = vec.y;
		mInstantSpeedPitch = vec.z;
	}

	public void refreshAngularSpeed(double dt, double crtPitch, double crtYaw,
			double crtRoll) {
		// for yaw:
		// dt
		// movedAngleYaw = mCrtYaw - mOldYaw
		// rYaw = rotation radius
		// distanceYaw = movedAngleYaw.toRadians() * rYaw
		// tangentialSpeedYaw = distanceYaw / dt
		// angularSpeedYaw = tangentialSpeedYaw/ rYaw

		if (Math.abs(crtYaw - mOldYaw) > EPSILON) {
			double distanceYawDegrees = crtYaw - mOldYaw;
			double distanceYawRadians = Math.toRadians(distanceYawDegrees)
					* mRadiusYaw;
			double tangentialSpeed = distanceYawRadians / dt;
			mInstantSpeedYaw = tangentialSpeed / mRadiusYaw;
			mOldYaw += distanceYawDegrees / 20;
		} else
			mInstantSpeedYaw = 0;

		if (Math.abs(crtPitch - mOldPitch) > EPSILON) {
			double distancePitchDegrees = crtPitch - mOldPitch;
			double distancePitchRadians = Math.toRadians(distancePitchDegrees)
					* mRadiusPitch;
			double tangentialSpeed = distancePitchRadians / dt;
			mInstantSpeedPitch = tangentialSpeed / mRadiusPitch;
			mOldPitch += distancePitchDegrees / 20;
		} else
			mInstantSpeedPitch = 0;

		if (Math.abs(crtRoll - mOldRoll) > EPSILON) {
			double distanceRollDegrees = crtRoll - mOldRoll;
			double distanceRollRadians = Math.toRadians(distanceRollDegrees)
					* mRadiusRoll;
			double tangentialSpeed = distanceRollRadians / dt;
			mInstantSpeedRoll = tangentialSpeed / mRadiusRoll;
			mOldRoll += distanceRollDegrees / 20;
		} else
			mInstantSpeedRoll = 0;
	}

	public void addRandom(double random) {
		double val;
		val = mRandomGenerator.nextDouble();
		mInstantSpeedYaw += (2 * val - 1) * random;

		val = mRandomGenerator.nextDouble();
		mInstantSpeedRoll += (2 * val - 1) * random;

		val = mRandomGenerator.nextDouble();
		mInstantSpeedPitch += (2 * val - 1) * random;
	}

	public void setGyroscope(int pitch, int yaw, int roll) {
		mOldPitch = pitch;
		mOldYaw = yaw;
		mOldRoll = roll;
	}

}
