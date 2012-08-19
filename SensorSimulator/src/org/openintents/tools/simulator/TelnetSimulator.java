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

package org.openintents.tools.simulator;

import org.openintents.tools.simulator.controller.TelnetSimulatorController;
import org.openintents.tools.simulator.main.SensorSimulatorMain;
import org.openintents.tools.simulator.model.telnet.TelnetSimulatorModel;
import org.openintents.tools.simulator.model.telnet.addons.GPSAddonModel;
import org.openintents.tools.simulator.view.telnet.TelnetSimulatorView;
import org.openintents.tools.simulator.view.telnet.addons.BatteryAddonView;
import org.openintents.tools.simulator.view.telnet.addons.GPSAddonView;

/**
 * TelnetSimulator is a container that keeps all the components of the
 * TelnetSimulator: the model, the view and the controller.
 * 
 * @author ilarele
 * 
 */

public class TelnetSimulator {
	private SensorSimulatorMain mMain;

	public TelnetSimulatorModel model = new TelnetSimulatorModel();
	public TelnetSimulatorView view = new TelnetSimulatorView(model);
	public TelnetSimulatorController ctrl = new TelnetSimulatorController(
			model, view);

	public TelnetSimulator(SensorSimulatorMain main) {
		mMain = main;
	}

	public void printStatus(String status) {
		mMain.printStatus(status);
	}

	public int getTelnetPort() {
		return model.getPort();
	}

	// TODO: add a view for writing messages for user in telnet tab
	// public void addMessage(String string) {
	// view.addMessage(string);
	// }

	public GPSAddonModel getGpsAddonModel() {
		return model.getGpsAddon();
	}

	public BatteryAddonView getBatteryAddonView() {
		return view.getBatteryAddon();
	}

	public GPSAddonView getGpsAddonView() {
		return view.getGpsAddon();
	}
}
