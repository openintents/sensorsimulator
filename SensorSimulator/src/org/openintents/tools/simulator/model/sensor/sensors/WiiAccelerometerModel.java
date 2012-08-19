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

import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.model.telnet.WiiMoteData;

/**
 * SensorSimulatorModel keeps the internal data model behind SensorSimulator.
 * 
 * @author Dale Thatcher
 * 
 */
public class WiiAccelerometerModel {

	// Real device bridge
	private boolean mUseWiimote;
	private WiiMoteData wiiMoteData = new WiiMoteData();
	private String mRealDevicePath;

	public WiiAccelerometerModel() {
		mUseWiimote = false;
		mRealDevicePath = "/sys/devices/platform/hdaps/position";
	}

	public int getRoll() {
		return wiiMoteData.getRoll();
	}

	public int getPitch() {
		return wiiMoteData.getPitch();
	}

	public boolean readRawData() {
		boolean result = false;
		wiiMoteData.setDataFilePath(mRealDevicePath);
		result = wiiMoteData.updateData();
		return result;
	}

	public Vector getWiiMoteVector() {
		return wiiMoteData.getVector();
	}

	public boolean isUsed() {
		return mUseWiimote;
	}

	public String getDevicePath() {
		return mRealDevicePath;
	}

	public void setWiiPath(String path) {
		mRealDevicePath = path;
	}

	public boolean updateData() {
		return wiiMoteData.updateData();
	}

	public String getStatus() {
		return wiiMoteData.getStatus();
	}
}
