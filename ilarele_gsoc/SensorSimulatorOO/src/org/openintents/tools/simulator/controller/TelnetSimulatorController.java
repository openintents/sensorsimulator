package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.openintents.tools.simulator.controller.telnet.BatteryAddonController;
import org.openintents.tools.simulator.controller.telnet.GPSAddonController;
import org.openintents.tools.simulator.model.telnet.TelnetSimulatorModel;
import org.openintents.tools.simulator.view.telnet.TelnetSimulatorView;

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
