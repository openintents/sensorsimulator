package org.openintents.tools.simulator;

import org.openintents.tools.simulator.controller.TelnetSimulatorController;
import org.openintents.tools.simulator.model.telnet.TelnetSimulatorModel;
import org.openintents.tools.simulator.model.telnet.addons.GPSAddonModel;
import org.openintents.tools.simulator.view.telnet.TelnetSimulatorView;
import org.openintents.tools.simulator.view.telnet.addons.BatteryAddonView;
import org.openintents.tools.simulator.view.telnet.addons.GPSAddonView;

public class TelnetSimulator {
	public TelnetSimulatorModel model = new TelnetSimulatorModel();
	public TelnetSimulatorView view = new TelnetSimulatorView(model);
	public TelnetSimulatorController ctrl = new TelnetSimulatorController(
			model, view);

	public int getTelnetPort() {
		return model.getPort();
	}

	public void addMessage(String string) {
		// view.addMessage(string);
	}

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
