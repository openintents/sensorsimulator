package org.openintents.tools.simulator.model.telnet.addons;

import java.io.File;

import org.openintents.tools.simulator.model.telnet.TelnetServer;
import org.openintents.tools.simulator.view.telnet.addons.BatteryAddonView;

public class BatteryAddonModel {
	// TelnetSimulations variables
	private int batteryPercent;

	// Battery variables
	private boolean batteryPresence;
	private boolean batteryAC;
	private String batteryStatus;
	private String batteryHealth;

	private TelnetServer mTelnetServer;

	public BatteryAddonModel(final TelnetServer telnetServer) {
		this.mTelnetServer = telnetServer;
		batteryPercent = 100;
		batteryPresence = true;
		batteryAC = true;
		batteryStatus = "unknown";
		batteryHealth = "unknown";
	}

	public boolean isPresent() {
		return batteryPresence;
	}

	public boolean isBatteryAC() {
		return batteryAC;
	}

	public String getHealth() {
		return batteryHealth;
	}

	public String getStatus() {
		return batteryStatus;
	}

	public void changePower(int value) {
		// TODO Auto-generated method stub

	}

	public void changePresence(boolean b) {
		batteryPresence = b;
	}

	public void changeAC(boolean b) {
		batteryAC = b;
	}

	public void changeStatus(String status) {
		batteryStatus = status;
	}

	public void changeHealth(String health) {
		batteryHealth = health;
	}

	public void slowEmulation(BatteryAddonView view) {
		mTelnetServer.slowEmulation();
	}

	public void nextTimeEvent() {
		mTelnetServer.nextTimeEvent();
	}

	public void openFile(File file) {
		System.out.println(mTelnetServer);
		mTelnetServer.openFile(file);
	}
}
