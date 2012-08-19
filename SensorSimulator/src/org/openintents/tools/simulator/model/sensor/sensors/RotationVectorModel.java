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
 * RotationVectorModel keeps the internal data model behind RotationVector
 * Sensor.
 * 
 * @author ilarele
 * 
 */
public class RotationVectorModel extends SensorModel {
	/** rotation angle in degree */
	private double mRotationXValue;
	private double mRotationYValue;
	private double mRotationZValue;

	/** Current read-out value of rotation. */
	private double mReadRotationX;
	private double mReadRotationY;
	private double mReadRotationZ;

	/** Partial read-out value of rotation. */
	private float mPartialRotationX;
	private float mPartialRotationY;
	private float mPartialRotationZ;

	/** Number of summands in partial sum for rotation. */
	private int mPartialRotationN;

	public RotationVectorModel() {
		super();
		mReadRotationX = mRotationXValue = 0;
		mReadRotationY = mRotationYValue = 0;
		mReadRotationZ = mRotationZValue = 0;
	}

	@Override
	public String getName() {
		return SensorModel.ROTATION_VECTOR;
	}

	@Override
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();
		// Form the average
		if (mAverage) {
			mPartialRotationX += mRotationXValue;
			mPartialRotationY += mRotationYValue;
			mPartialRotationZ += mRotationZValue;
			mPartialRotationN++;
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
				mReadRotationX = mPartialRotationX / mPartialRotationN;
				mReadRotationY = mPartialRotationY / mPartialRotationN;
				mReadRotationZ = mPartialRotationZ / mPartialRotationN;
				// reset average
				mPartialRotationX = 0;
				mPartialRotationY = 0;
				mPartialRotationZ = 0;
				mPartialRotationN = 0;
			} else {
				// Only take current value
				mReadRotationX = mRotationXValue;
				mReadRotationY = mRotationYValue;
				mReadRotationZ = mRotationZValue;
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
		out.println("3\n" + Math.sin(Math.toRadians(mReadRotationX / 2)) + "\n"
				+ Math.sin(Math.toRadians(mReadRotationY / 2)) + "\n"
				+ Math.sin(Math.toRadians(mReadRotationZ / 2)));

	}

	public double getRotationVectorX() {
		return Math.sin(Math.toRadians(mReadRotationX / 2));
	}

	public double getRotationVectorY() {
		return Math.sin(Math.toRadians(mReadRotationY / 2));
	}

	public double getRotationVectorZ() {
		return Math.sin(Math.toRadians(mReadRotationZ / 2));
	}

	@Override
	public String getSI() {
		return SensorModel.DEGREES;
	}

	public void setRotationVector(double x, double y, double z) {
		mRotationXValue = x;
		mRotationYValue = y;
		mRotationZValue = z;
	}

	public void addRotationVector(double addX, double addY, double addZ) {
		mRotationXValue += addX;
		mRotationYValue += addY;
		mRotationZValue += addZ;
	}

	public double getReadRotationVectorX() {
		return Math.sin(Math.toRadians(mReadRotationX / 2));
	}

	public double getReadRotationVectorY() {
		return Math.sin(Math.toRadians(mReadRotationY / 2));
	}

	public double getReadRotationVectorZ() {
		return Math.sin(Math.toRadians(mReadRotationZ / 2));
	}

	public void setRotationVector(Vector vec) {
		mRotationXValue = vec.x;
		mRotationYValue = vec.y;
		mRotationZValue = vec.z;
	}

	public void setRotationVector(float[] newValue) {
		mRotationXValue = newValue[0];
		mRotationYValue = newValue[1];
		mRotationZValue = newValue[2];
	}

	@Override
	public int getType() {
		return TYPE_ROTATION_VECTOR;
	}
}
