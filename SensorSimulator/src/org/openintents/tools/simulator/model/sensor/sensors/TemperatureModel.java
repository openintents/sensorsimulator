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
 * TemperatureModel keeps the internal data model behind Temperature Sensor.
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class TemperatureModel extends SensorModel {

	// thermometer
	public double temperatureValue;

	/** Current read-out value of temperature. */
	private double mReadTemperature;

	/** Partial read-out value of temperature. */
	private double mPartialTemperature;
	/** Number of summands in partial sum for temperature. */
	private int mPartialTemperatureN;

	public TemperatureModel() {
		temperatureValue = 17.7;
	}

	@Override
	public String getName() {
		return SensorModel.TEMPERATURE;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialTemperature += temperatureValue;
			mPartialTemperatureN++;
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
				mReadTemperature = mPartialTemperature / mPartialTemperatureN;

				// reset average
				mPartialTemperature = 0;
				mPartialTemperatureN = 0;

			} else {
				// Only take current value
				mReadTemperature = temperatureValue;
			}
		}
	}

	@Override
	public void getNumSensorValues(PrintWriter out) {
		out.println("1");
	}

	public double getTemperature() {
		return temperatureValue;
	}

	@Override
	public String getSI() {
		return SensorModel.DEGREES + "C";
	}

	public void setTemp(double value) {
		temperatureValue = value;
	}

	public void addTemp(double value) {
		temperatureValue += value;
	}

	public double getReadTemp() {
		return mReadTemperature;
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("1\n" + mReadTemperature);
	}

	@Override
	public int getType() {
		return TYPE_TEMPERATURE;
	}
}
