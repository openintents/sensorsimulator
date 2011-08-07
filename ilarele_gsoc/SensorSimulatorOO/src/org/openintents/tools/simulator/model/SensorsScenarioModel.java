package org.openintents.tools.simulator.model;

import java.util.ArrayList;

import org.openintents.tools.simulator.SensorsScenario;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;

public class SensorsScenarioModel {

	public static final double MAX_TIME = 1000;
	public static final double MIN_TIME = 0.5;

	private ArrayList<StateModel> mStates;
	private SensorSimulatorModel mSensorSimulator;

	public SensorsScenarioModel(SensorsScenario sensorsRecording) {
		mStates = new ArrayList<StateModel>();
	}

	public void setSensorSimulatorModel(
			SensorSimulatorModel sensorSimulatorModel) {
		this.mSensorSimulator = sensorSimulatorModel;
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

	public void add(StateModel model) {
		mStates.add(model);
	}

	public SensorSimulatorModel getSensorSimulatorModel() {
		return mSensorSimulator;
	}

	public StateModel getNextState(int index) {
		if (index < mStates.size())
			return mStates.get(index);
		return null;
	}

	public void setPrevTimeDistance(float time) {
		if (mStates.size() > 0) {
			mStates.get(mStates.size() - 1).setTime(time);
		}
	}
}
