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

import java.io.File;

import org.openintents.tools.simulator.model.telnet.TelnetServer;
import org.openintents.tools.simulator.view.telnet.addons.BatteryAddonView;

/**
 * BatteryAddonModel keeps the internal data model behind Battery Add-on.
 * 
 * Battery add-on sets emulator battery state (via telnet communication).
 * 
 * @author Peli
 * 
 */
public class BatteryAddonModel {
	// TelnetSimulations variables

	private TelnetServer mTelnetServer;

	public BatteryAddonModel(final TelnetServer telnetServer) {
		mTelnetServer = telnetServer;
	}

	public void changePower(int value) {
		mTelnetServer.changePower(value);
	}

	public void changePresence(boolean presence) {
		mTelnetServer.changePresence(presence);
	}

	public void changeAC(boolean ac) {
		mTelnetServer.changeAC(ac);
	}

	public void changeStatus(String status) {
		mTelnetServer.changeStatus(status);
	}

	public void changeHealth(String health) {
		mTelnetServer.changeHealth(health);
	}

	public void slowEmulation(BatteryAddonView view) {
		mTelnetServer.slowEmulation();
	}

	public void nextTimeEvent() {
		mTelnetServer.nextTimeEvent();
	}

	public void openFile(File file) {
		mTelnetServer.openFile(file);
	}
}
