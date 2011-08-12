package org.openintents.tools.simulator.model;

import java.util.ArrayList;

import org.openintents.tools.simulator.SensorsScenario;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;

public class SensorsScenarioModel {

	private ArrayList<StateModel> mStates;
	private SensorSimulatorModel mSensorSimulator;

	public SensorsScenarioModel(SensorsScenario sensorsRecording) {
		mStates = new ArrayList<StateModel>();
	}

	public void setSensorSimulatorModel(
			SensorSimulatorModel sensorSimulatorModel) {
		mSensorSimulator = sensorSimulatorModel;
	}

	public ArrayList<StateModel> getStates() {
		return mStates;
	}

	public void emptyStates() {
		mStates.clear();
	}

	public void remove(StateModel model) {
		mStates.remove(model);
	}

	public void add(int position, StateModel model) {
		mStates.add(position, model);
	}

	public SensorSimulatorModel getSensorSimulatorModel() {
		return mSensorSimulator;
	}

	public StateModel getState(int index) {
		if (index < mStates.size())
			return mStates.get(index);
		return null;
	}

	public void add(StateModel stateModel) {
		int position = mStates.size();
		add(position, stateModel);
	}
}
