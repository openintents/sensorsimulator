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

import org.openintents.tools.simulator.model.telnet.Vector;

/**
 * GravityModel keeps the internal data model behind Gravity Sensor.
 * 
 * @author ilarele
 */
public class GravityModel extends SensorModel {
	// gravity
	private double mGravityX;
	private double mGravityY;
	private double mGravityZ;
	/** Current read-out value of gravity. */
	private double mReadGravityX;
	private double mReadGravityY;
	private double mReadGravityZ;

	/** Partial read-out value of gravity. */
	private float mPartialGravityX;
	private float mPartialGravityY;
	private float mPartialGravityZ;

	/** Number of summands in partial sum for gravity. */
	private int mPartialGravityN;

	// Gravity
	private double mGConstant;
	private double mGravityLimit;

	public GravityModel() {
		mReadGravityX = mGravityX = 0;
		mReadGravityY = mGravityY = 0;
		mReadGravityZ = mGravityZ = -9.8;

		mGConstant = 9.80665; // meter per second^2
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
		if (mAverage) {
			mPartialGravityX += mGravityX;
			mPartialGravityY += mGravityY;
			mPartialGravityZ += mGravityZ;
			mPartialGravityN++;
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
				mReadGravityX = mPartialGravityX / mPartialGravityN;
				mReadGravityY = mPartialGravityY / mPartialGravityN;
				mReadGravityZ = mPartialGravityZ / mPartialGravityN;
				// reset average
				mPartialGravityX = 0;
				mPartialGravityY = 0;
				mPartialGravityZ = 0;
				mPartialGravityN = 0;
			} else {
				// Only take current value
				mReadGravityX = mGravityX;
				mReadGravityY = mGravityY;
				mReadGravityZ = mGravityZ;
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
		out.println("3\n" + mReadGravityX + "\n" + mReadGravityY + "\n"
				+ mReadGravityZ);

	}

	public double getGravityX() {
		return mReadGravityX;
	}

	public double getGravityY() {
		return mReadGravityY;
	}

	public double getGravityZ() {
		return mReadGravityZ;
	}

	@Override
	public String getSI() {
		return "m/s" + SensorModel.SQUARED;
	}

	public void setGravity(double x, double y, double z) {
		mGravityX = x;
		mGravityY = y;
		mGravityZ = z;
	}

	public void addGravity(double addX, double addY, double addZ) {
		mGravityX += addX;
		mGravityY += addY;
		mGravityZ += addZ;
	}

	public double getReadGravityX() {
		return mReadGravityX;
	}

	public double getReadGravityY() {
		return mReadGravityY;
	}

	public double getReadGravityZ() {
		return mReadGravityZ;
	}

	public void setGravity(Vector vec) {
		mGravityX = vec.x;
		mGravityY = vec.y;
		mGravityZ = vec.z;
	}

	public double getGravityConstant() {
		return mGConstant;
	}

	public double getAccelLimit() {
		return mGravityLimit;
	}

	public void setGravity(float[] vec) {
		mGravityX = vec[0];
		mGravityY = vec[1];
		mGravityZ = vec[2];
	}

	@Override
	public int getType() {
		return TYPE_GRAVITY;
	}
}
