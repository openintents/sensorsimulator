package org.openintents.tools.simulator.model.telnet.addons;

import org.openintents.tools.simulator.model.telnet.FileData;

public class ReplayAddonModel {

	private FileData replayData = new FileData();

	public ReplayAddonModel() {
	}

	public void recordData(double readYaw, double readRoll, double readPitch) {
		replayData.recordData(readYaw, readRoll, readPitch);
	}

	public boolean playData() {
		return replayData.playData();
	}

	public int getYaw() {
		return replayData.getYaw();
	}

	public int getRoll() {
		return replayData.getRoll();
	}

	public int getPitch() {
		return replayData.getPitch();
	}

	public boolean createFile() {
		return replayData.createFile();
	}

	public boolean openFile() {
		return replayData.openFile();
	}

	public boolean isPlaying() {
		return replayData.isPlaying();
	}
}
