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

package org.openintents.tools.simulator.model.sensors;

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
	private OrientationModel mOrientationModel;

	/**
	 * Gravity sensor needs orientation sensor data to compute own data.
	 * 
	 * @param orientationModel
	 *            the orientation sensor model
	 */
	public GravityModel(OrientationModel orientationModel) {
		mOrientationModel = orientationModel;
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
			mNextUpdate += mUpdateDelay;
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
	public int getNumSensorValues() {
		return 3;
	}

	@Override
	public String printSensorData() {
		// number of data following + data
		return "3\n" + mGravityX + "\n" + mGravityY + "\n" + mGravityZ;

	}

	public double getGravityX() {
		return mGravityX;
	}

	public double getGravityY() {
		return mGravityY;
	}

	public double getGravityZ() {
		return mGravityZ;
	}

	@Override
	public String getSI() {
		return "m/s" + SensorModel.SQUARED;
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
		
		// inform observers
		setChanged();
		notifyObservers();
	}

	public void setGravityConstant(double gravityConstant) {
		Vector gravityVec = new Vector(0, 0, gravityConstant);
		gravityVec.reverserollpitchyaw(mOrientationModel.getRoll(),
				mOrientationModel.getPitch(), mOrientationModel.getYaw());
		setGravity(new float[] { (float) gravityVec.x, (float) gravityVec.y,
				(float) gravityVec.z });
	}
}
