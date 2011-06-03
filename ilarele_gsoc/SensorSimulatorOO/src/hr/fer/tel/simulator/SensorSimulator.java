package hr.fer.tel.simulator;

import org.openintents.tools.simulator.controller.SensorSimulatorCtrl;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.view.sensor.SensorSimulatorView;

public class SensorSimulator {
	public SensorSimulatorModel model = new SensorSimulatorModel(this);
	public SensorSimulatorView view = new SensorSimulatorView(model);
	public SensorSimulatorCtrl ctrl = new SensorSimulatorCtrl(model, view);

	public void addMessage(String string) {
		view.addMessage(string);
	}

	public void newClient() {
		// TODO Auto-generated method stub
		model.newClient();
	}
}
