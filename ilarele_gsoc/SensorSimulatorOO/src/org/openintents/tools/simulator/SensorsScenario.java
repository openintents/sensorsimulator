package org.openintents.tools.simulator;

import org.openintents.tools.simulator.controller.SensorsScenarioController;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.view.SensorsScenarioView;

public class SensorsScenario {
	public SensorsScenarioModel model;
	public SensorsScenarioView view;
	public SensorsScenarioController controller;

	public SensorsScenario() {
		model = new SensorsScenarioModel(this);
		view = new SensorsScenarioView(model);
		controller = new SensorsScenarioController(model, view);
	}

	public void setSimulator(SensorSimulator sensorSimulator) {
		model.setSensorSimulatorModel(sensorSimulator.model);
		controller.setSensorSimulatorController(sensorSimulator.controller);

	}
}
