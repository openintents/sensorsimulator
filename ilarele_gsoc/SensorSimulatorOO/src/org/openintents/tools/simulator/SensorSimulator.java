package org.openintents.tools.simulator;

import org.openintents.tools.simulator.controller.SensorSimulatorController;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.view.sensor.SensorSimulatorView;

public class SensorSimulator {
	public SensorSimulatorModel model = new SensorSimulatorModel(this);
	public SensorSimulatorView view = new SensorSimulatorView(model);
	public SensorSimulatorController ctrl = new SensorSimulatorController(model, view);

	public void addMessage(String string) {
		view.addMessage(string);
	}

	public void newClient() {
		model.newClient();
	}

	public void fixEnabledSensors() {
		view.setFix(true);
		ctrl.setFix(true);
	}

	public void unfixEnabledSensors() {
		view.setFix(false);
		ctrl.setFix(false);
	}
}
