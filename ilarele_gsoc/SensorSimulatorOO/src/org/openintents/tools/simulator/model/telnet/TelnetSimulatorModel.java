package org.openintents.tools.simulator.model.telnet;

import org.openintents.tools.simulator.model.telnet.addons.BatteryAddonModel;
import org.openintents.tools.simulator.model.telnet.addons.GPSAddonModel;

public class TelnetSimulatorModel {

	// add-ons
	private BatteryAddonModel batteryAddonModel;
	private GPSAddonModel gpsAddonModel;

	// Servers
	// Telnet server variable
	private TelnetServer mTelnetServer;

	private int telnetPort;

	public TelnetSimulatorModel() {
		telnetPort = 5554;

		// Set up the servers
		mTelnetServer = new TelnetServer();

		// add-ons
		batteryAddonModel = new BatteryAddonModel(mTelnetServer);
		gpsAddonModel = new GPSAddonModel(mTelnetServer);
	}

	public BatteryAddonModel getBatteryAddon() {
		return batteryAddonModel;
	}

	public GPSAddonModel getGpsAddon() {
		return gpsAddonModel;
	}

	public void connectViaTelnet(int port) {
		telnetPort = port;
		mTelnetServer.connect(port);
	}

	public int getPort() {
		return telnetPort;
	}
}
