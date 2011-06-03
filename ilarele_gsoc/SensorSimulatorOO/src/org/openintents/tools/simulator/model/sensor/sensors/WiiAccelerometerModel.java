package org.openintents.tools.simulator.model.sensor.sensors;

import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.model.telnet.WiiMoteData;

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
