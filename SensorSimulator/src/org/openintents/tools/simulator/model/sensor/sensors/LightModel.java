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
 * LightModel keeps the internal data model behind Light Sensor.
 * 
 * @author ilarele
 * 
 */
public class LightModel extends SensorModel {

	// light
	private double mLightValue;
	/** Current read-out value of light. */
	private double mReadLight;

	/** Partial read-out value of light. */
	private float mPartialLight;
	/** Number of summands in partial sum for light. */
	private int mPartialLightN;

	public LightModel() {
		mReadLight = mLightValue = 400;
	}

	@Override
	public String getName() {
		return SensorModel.LIGHT;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialLight += mLightValue;
			mPartialLightN++;
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
				mReadLight = mPartialLight / mPartialLightN;

				// reset average
				mPartialLight = 0;
				mPartialLightN = 0;

			} else {
				// Only take current value
				mReadLight = mLightValue;
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
		out.println("1\n" + mReadLight);
	}

	@Override
	public String getSI() {
		return "lux";
	}

	public void setLight(double value) {
		mLightValue = value;
	}

	public void addLight(double value) {
		mLightValue += value;
	}

	public double getReadLight() {
		return mReadLight;
	}

	@Override
	public int getType() {
		return TYPE_LIGHT;
	}
}
