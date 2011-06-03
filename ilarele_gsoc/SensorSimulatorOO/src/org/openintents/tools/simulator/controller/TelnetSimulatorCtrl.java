package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.openintents.tools.simulator.controller.telnet.BatteryAddonCtrl;
import org.openintents.tools.simulator.controller.telnet.GPSAddonCtrl;
import org.openintents.tools.simulator.model.telnet.TelnetSimulatorModel;
import org.openintents.tools.simulator.view.telnet.TelnetSimulatorView;

public class TelnetSimulatorCtrl {
	private BatteryAddonCtrl batteryAddonCtrl;
	private GPSAddonCtrl gpsAddonCtrl;

	public TelnetSimulatorCtrl(final TelnetSimulatorModel model,
			final TelnetSimulatorView view) {
		batteryAddonCtrl = new BatteryAddonCtrl(model.getBatteryAddon(),
				view.getBatteryAddon());
		gpsAddonCtrl = new GPSAddonCtrl(model.getGpsAddon(), view.getGpsAddon());

		JButton telnetPortButton = view.getTelnetPortButton();
		telnetPortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.connectViaTelnet(view.getTelnetPort());
			}
		});
	}

}
