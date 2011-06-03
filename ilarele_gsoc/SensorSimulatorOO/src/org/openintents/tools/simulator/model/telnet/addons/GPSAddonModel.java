package org.openintents.tools.simulator.model.telnet.addons;

import org.openintents.tools.simulator.model.telnet.TelnetServer;

public class GPSAddonModel {
	// GPS variables
	private float longitude;
	private float latitude;
	private float altitude;
	private String lisName;
	private TelnetServer mTelnetServer;

	public GPSAddonModel(TelnetServer telnetServer) {
		mTelnetServer = telnetServer;
	}

	public float getLongitude() {
		return longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getAltitude() {
		return altitude;
	}

	public void sendGPS(float longit, float lat, float alt) {
		longitude = longit;
		latitude = lat;
		altitude = alt;
		mTelnetServer.sendGPS(longit, lat, alt);
	}

}
