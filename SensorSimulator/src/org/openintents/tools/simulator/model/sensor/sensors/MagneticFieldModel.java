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
 * MagneticFieldModel keeps the internal data model behind MagneticField Sensor.
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class MagneticFieldModel extends SensorModel {

	/** Current read-out value of compass x-component. */
	private double mReadCompassX;
	/** Current read-out value of compass y-component. */
	private double mReadCompassY;
	/** Current read-out value of compass z-component. */
	private double mReadCompassZ;

	/** Partial read-out value of compass x-component. */
	private double mPartialCompassX;
	/** Partial read-out value of compass y-component. */
	private double mPartialCompassY;
	/** Partial read-out value of compass z-component. */
	private double mPartialCompassZ;
	/** Number of summands in partial sum for compass. */
	private int mPartialCompassN;

	/** Internal state value of compass x-component. */
	private double mCompassX;
	/** Internal state value of compass y-component. */
	private double mCompassY;
	/** Internal state value of compass z-component. */
	private double mCompassZ;

	// Magnetic field
	private double mNorth;
	private double mEast;
	private double mVertical;

	public MagneticFieldModel() {
		mNorth = 22874.1;
		mEast = 5939.5;
		mVertical = 43180.5;
	}

	@Override
	public String getName() {
		return SensorModel.MAGNETIC_FIELD;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialCompassX += mCompassX;
			mPartialCompassY += mCompassY;
			mPartialCompassZ += mCompassZ;
			mPartialCompassN++;
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
				mReadCompassX = mPartialCompassX / mPartialCompassN;
				mReadCompassY = mPartialCompassY / mPartialCompassN;
				mReadCompassZ = mPartialCompassZ / mPartialCompassN;

				// reset average
				mPartialCompassX = 0;
				mPartialCompassY = 0;
				mPartialCompassZ = 0;
				mPartialCompassN = 0;

			} else {
				// Only take current value
				mReadCompassX = mCompassX;
				mReadCompassY = mCompassY;
				mReadCompassZ = mCompassZ;
			}
		}
	}

	public double getVertical() {
		return mVertical;
	}

	public double getEast() {
		return mEast;
	}

	public double getNorth() {
		return mNorth;
	}

	@Override
	public void getNumSensorValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		out.println("3\n" + mReadCompassX + "\n" + mReadCompassY + "\n"
				+ mReadCompassZ);
	}

	@Override
	public String getSI() {
		return "nT";
	}

	public void setVertical(double value) {
		mVertical = value;
	}

	public void setEast(double value) {
		mEast = value;
	}

	public void setNorth(double value) {
		mNorth = value;
	}

	public void setCompass(Vector vec) {
		mCompassX = vec.x;
		mCompassY = vec.y;
		mCompassZ = vec.z;
	}

	public void resetCompas() {
		mCompassX = 0;
		mCompassY = 0;
		mCompassZ = 0;
	}

	public double getReadCompassX() {
		return mReadCompassX;
	}

	public double getReadCompassY() {
		return mReadCompassY;
	}

	public double getReadCompassZ() {
		return mReadCompassZ;
	}

	public void setMagneticField(float[] newValue) {
		mCompassX = newValue[0];
		mCompassY = newValue[1];
		mCompassZ = newValue[2];
	}

	@Override
	public int getType() {
		return TYPE_MAGNETIC_FIELD;
	}
}
