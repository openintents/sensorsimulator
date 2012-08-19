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
 * AccelerometerModel keeps the internal data model behind Accelerometer Sensor.
 * 
 */
public class ProximityModel extends SensorModel {

	// proximity
	public double mProximityValue;
	/** Current read-out value of proximity. */
	private double mReadProximity;

	/** Partial read-out value of proximity. */
	private float mPartialProximity;
	/** Number of summands in partial sum for proximity. */
	private int mPartialProximityN;

	// Proximity
	private float mProximityRange;
	private boolean mBinaryProximity;
	private boolean mIsProximityNear; // false => far

	public ProximityModel() {
		mProximityValue = 10;
		mProximityRange = 10;
		mBinaryProximity = true;
		mIsProximityNear = true;
	}

	@Override
	public String getName() {
		return SensorModel.PROXIMITY;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialProximity += mProximityValue;
			mPartialProximityN++;
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
				mReadProximity = mPartialProximity / mPartialProximityN;

				// reset average
				mPartialProximity = 0;
				mPartialProximityN = 0;

			} else {
				// Only take current value
				mReadProximity = mProximityValue;
			}
		}
	}

	@Override
	public void getNumSensorValues(PrintWriter out) {
		out.println("1");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("1\n" + mReadProximity);
	}

	public double getProximity() {
		return mProximityValue;
	}

	public float getProximityRange() {
		return mProximityRange;
	}

	public boolean isNear() {
		return mIsProximityNear;
	}

	public boolean isBinary() {
		return mBinaryProximity;
	}

	@Override
	public String getSI() {
		return "cm";
	}

	public void setProximity(double value) {
		mProximityValue = value;
	}

	public void addProximity(double value) {
		mProximityValue += value;
	}

	public double getReadProximity() {
		return mReadProximity;
	}

	@Override
	public int getType() {
		return TYPE_PROXIMITY;
	}
}
