package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.view.SensorsScenarioView;
import org.openintents.tools.simulator.view.StateViewBig;

public class StateControllerBig {
	private StateModel mModel;
	private StateViewBig mView;
	private SensorsScenarioView mSensorScenarioView;
	private SensorsScenarioModel mSensorScenarioModel;

	public StateControllerBig(SensorsScenarioModel sensorsScenarioModel,
			SensorsScenarioView sensorsScenarioView, StateModel stateModel,
			StateViewBig stateView) {
		this.mModel = stateModel;
		this.mView = stateView;
		this.mSensorScenarioView = sensorsScenarioView;
		this.mSensorScenarioModel = sensorsScenarioModel;
		initListeners();
	}

	private void initListeners() {
		JButton importState = mView.getImportStateButton();
		importState.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mModel.copyState(mSensorScenarioModel.getSensorSimulatorModel());
				mView.refreshFromModel();
			}
		});
	}
}
