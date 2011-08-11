package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.view.SensorsScenarioView;
import org.openintents.tools.simulator.view.StateViewSmall;

public class StateControllerSmall {

	private StateViewSmall mView;
	private StateModel mModel;
	private SensorsScenarioView mSensorScenarioView;
	private SensorsScenarioModel mSensorScenarioModel;

	public StateControllerSmall(SensorsScenarioModel sensorsScenarioModel,
			SensorsScenarioView sensorsScenarioView, StateModel stateModel,
			StateViewSmall stateView) {
		mModel = stateModel;
		mView = stateView;
		mSensorScenarioView = sensorsScenarioView;
		mSensorScenarioModel = sensorsScenarioModel;
		initListeners();
	}

	private void initListeners() {
		JButton deleteBtn = mView.getDeleteBtn();
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mSensorScenarioModel.remove(mModel);
				mSensorScenarioView.removeFromGui(mView);
			}
		});

		JButton editBtn = mView.getEditBtn();
		editBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// open big view
				mSensorScenarioView.showBigView(mModel, mView);
			}
		});

		JButton addBtn = mView.getAddBtn();
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StateModel newModel = new StateModel(mModel);
				int position = mSensorScenarioView.addView(mView, newModel);
				mSensorScenarioModel.add(position, newModel);
			}
		});
	}

}
