package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
		this.mModel = stateModel;
		this.mView = stateView;
		this.mSensorScenarioView = sensorsScenarioView;
		this.mSensorScenarioModel = sensorsScenarioModel;
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
				mSensorScenarioModel.add(newModel);
				mSensorScenarioView.addView(newModel);
			}
		});

		final JSpinner transitionSpinner = mView.getTransitionSpinner();
		transitionSpinner.setValue(mModel.getTime());
		transitionSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				float value = Float.parseFloat(""
						+ transitionSpinner.getValue());
				mModel.setTime(value);
			}
		});
	}

}
