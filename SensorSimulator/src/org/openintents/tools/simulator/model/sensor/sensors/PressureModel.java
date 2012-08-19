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
 * PressureModel keeps the internal data model behind Pressure Sensor.
 * 
 * @author ilarele
 * 
 */
public class PressureModel extends SensorModel {

	// pressure
	private double mPressureValue;
	/** Current read-out value of pressure. */
	private double mReadPressure;

	/** Partial read-out value of pressure. */
	private float mPartialPressure;
	/** Number of summands in partial sum for pressure. */
	private int mPartialPressureN;

	public PressureModel() {
		mPressureValue = 0.5;
	}

	@Override
	public String getName() {
		return SensorModel.PRESSURE;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialPressure += mPressureValue;
			mPartialPressureN++;
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
				mReadPressure = mPartialPressure / mPartialPressureN;

				// reset average
				mPartialPressure = 0;
				mPartialPressureN = 0;

			} else {
				// Only take current value
				mReadPressure = mPressureValue;
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
		out.println("1\n" + mReadPressure);
	}

	public double getPressure() {
		return mPressureValue;
	}

	@Override
	public String getSI() {
		return "";
	}

	public double getReadPressure() {
		return mReadPressure;
	}

	public void setPressure(double value) {
		if (value > 1) {
			mPressureValue = 1;
		} else if (value < 0) {
			mPressureValue = 0;
		} else {
			mPressureValue = value;
		}
	}

	public void addPressure(double value) {
		setPressure(mPressureValue + value);
	}

	@Override
	public int getType() {
		return TYPE_PRESSURE;
	}
}
