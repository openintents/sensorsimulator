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

package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.openintents.tools.simulator.controller.telnet.BatteryAddonController;
import org.openintents.tools.simulator.controller.telnet.GPSAddonController;
import org.openintents.tools.simulator.model.telnet.TelnetSimulatorModel;
import org.openintents.tools.simulator.view.telnet.TelnetSimulatorView;

/**
 * TelnetSimulatorController keeps the behaviour of the TelnetSimulator
 * (listeners, etc.)
 * 
 * TelnetSimulator simulates, using a telnet communication with the emulator,
 * the battery level and the gps position.
 * 
 * @author ilarele
 */
public class TelnetSimulatorController {
	@SuppressWarnings("unused")
	private BatteryAddonController batteryAddonCtrl;
	@SuppressWarnings("unused")
	private GPSAddonController gpsAddonCtrl;

	public TelnetSimulatorController(final TelnetSimulatorModel model,
			final TelnetSimulatorView view) {
		batteryAddonCtrl = new BatteryAddonController(model.getBatteryAddon(),
				view.getBatteryAddon());
		gpsAddonCtrl = new GPSAddonController(model.getGpsAddon(),
				view.getGpsAddon());

		JButton telnetPortButton = view.getTelnetPortButton();
		telnetPortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.connectViaTelnet(view.getTelnetPort());
			}
		});
	}

}
