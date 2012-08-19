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

/**
 * OrientationModel keeps the internal data model behind Orientation Sensor.
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class OrientationModel extends SensorModel {
	/** Current read-out value of orientation yaw. */
	private double mReadYaw;
	/** Current read-out value of orientation pitch. */
	private double mReadPitch;
	/** Current read-out value of orientation roll. */
	private double mReadRoll;

	/** Partial read-out value of orientation yaw. */
	private double mPartialYaw;
	/** Partial read-out value of orientation pitch. */
	private double mPartialpitch;
	/** Partial read-out value of orientation roll. */
	private double mPartialoll;
	/** Number of summands in partial sum for orientation. */
	private int mPartialN;

	/**
	 * orientation (in degree) Yaw = Rotation about the Y-Axis; Pitch = Rotation
	 * about the X-Axis; Roll = Rotation about the Z-Axis
	 */
	private int mYaw;
	private int mPitch;
	private int mRoll;

	@Override
	public String getName() {
		return SensorModel.ORIENTATION;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialYaw += mYaw;
			mPartialpitch += mPitch;
			mPartialoll += mRoll;
			mPartialN++;
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
				mReadYaw = mPartialYaw / mPartialN;
				mReadPitch = mPartialpitch / mPartialN;
				mReadRoll = mPartialoll / mPartialN;

				// reset average
				mPartialYaw = 0;
				mPartialpitch = 0;
				mPartialoll = 0;
				mPartialN = 0;

			} else {
				// Only take current value
				mReadYaw = mYaw;
				mReadPitch = mPitch;
				mReadRoll = mRoll;
			}

			// Normalize values:

			// Restrict pitch value to -90 to +90
			if (mReadPitch < -90) {
				mReadPitch = -180 - mReadPitch;
				mReadYaw += 180;
				mReadRoll += 180;
			} else if (mReadPitch > 90) {
				mReadPitch = 180 - mReadPitch;
				mReadYaw += 180;
				mReadRoll += 180;
			}

			// yaw from 0 to 360
			if (mReadYaw < 0) {
				mReadYaw = mReadYaw + 360;
			}
			if (mReadYaw >= 360) {
				mReadYaw -= 360;
			}

			// roll from -180 to + 180
			if (mReadRoll >= 180) {
				mReadRoll -= 360;
			}
		}
	}

	public double getReadPitch() {
		return mReadPitch;
	}

	public double getReadYaw() {
		return mReadYaw;
	}

	public double getReadRoll() {
		return mReadRoll;
	}

	@Override
	public void getNumSensorValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("3\n" + mReadYaw + "\n" + mReadPitch + "\n" + mReadRoll);
	}

	@Override
	public String getSI() {
		return SensorModel.DEGREES;
	}

	public int getPitch() {
		return mPitch;
	}

	public int getYaw() {
		return mYaw;
	}

	public int getRoll() {
		return mRoll;
	}

	public void setYaw(int value) {
		mYaw = value;
	}

	public void setPitch(int value) {
		mPitch = value;
	}

	public void setRoll(int value) {
		mRoll = value;
	}

	public void addYaw(double value) {
		mYaw += value;
	}

	public void addPitch(double value) {
		mPitch += value;
	}

	public void addRoll(double value) {
		mRoll += value;
	}

	@Override
	public String toString() {
		return "[y=" + mYaw + ", p=" + mPitch + ", r=" + mRoll + "]";
	}

	public void setOrientation(float[] newValue) {
		mYaw = (int) newValue[0];
		mPitch = (int) newValue[1];
		mRoll = (int) newValue[2];
	}

	@Override
	public int getType() {
		return TYPE_ORIENTATION;
	}
}
