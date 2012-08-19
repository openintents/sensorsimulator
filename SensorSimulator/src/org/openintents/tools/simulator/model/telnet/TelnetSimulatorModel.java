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

package org.openintents.tools.simulator.model.telnet;

import org.openintents.tools.simulator.model.telnet.addons.BatteryAddonModel;
import org.openintents.tools.simulator.model.telnet.addons.GPSAddonModel;

/**
 * TelnetSimulatorModel keeps the data (the model behind the GUI) of the
 * TelnetSimulator (listeners, etc.)
 * 
 * TelnetSimulator simulates, using a telnet communication with the emulator,
 * the battery level and the gps position.
 * 
 * @author ilarele
 */
public class TelnetSimulatorModel {

	// add-ons
	private BatteryAddonModel mBatteryAddonModel;
	private GPSAddonModel mGpsAddonModel;

	// Servers
	// Telnet server variable
	private TelnetServer mTelnetServer;

	private int mTelnetPort;

	public TelnetSimulatorModel() {
		mTelnetPort = 5554;

		// Set up the servers
		mTelnetServer = new TelnetServer();

		// add-ons
		mBatteryAddonModel = new BatteryAddonModel(mTelnetServer);
		mGpsAddonModel = new GPSAddonModel(mTelnetServer);
	}

	public BatteryAddonModel getBatteryAddon() {
		return mBatteryAddonModel;
	}

	public GPSAddonModel getGpsAddon() {
		return mGpsAddonModel;
	}

	public void connectViaTelnet(int port) {
		mTelnetPort = port;
		mTelnetServer.connect(port);
	}

	public int getPort() {
		return mTelnetPort;
	}
}
