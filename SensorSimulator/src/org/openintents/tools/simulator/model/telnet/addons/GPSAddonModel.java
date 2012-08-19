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

package org.openintents.tools.simulator.model.telnet.addons;

import org.openintents.tools.simulator.model.telnet.TelnetServer;

/**
 * GPSAddonModel keeps the internal data model behind GPS Add-on.
 * 
 * Gps add-on sets emulator gps position (via telnet communication).
 * 
 * @author Peli
 * 
 */
public class GPSAddonModel {
	// GPS variables
	private float longitude;
	private float latitude;
	private float altitude;
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
