/*
 * Copyright (C) 2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.view.SensorsScenarioView;
import org.openintents.tools.simulator.view.StateViewSmall;

/**
 * Controller of a state from the scenario grid.
 * @author ilarele
 *
 */
public class StateControllerSmall {

	private StateViewSmall mView;
	private StateModel mModel;
	private SensorsScenarioView mSensorScenarioView;
	private SensorsScenarioModel mSensorScenarioModel;
	private SensorsScenarioController mSensorScenarioController;

	public StateControllerSmall(
			SensorsScenarioController sensorsScenarioController,
			StateModel stateModel, StateViewSmall stateView) {
		mModel = stateModel;
		mView = stateView;
		mSensorScenarioController = sensorsScenarioController;
		mSensorScenarioView = sensorsScenarioController.getView();
		mSensorScenarioModel = sensorsScenarioController.getModel();
		initListeners();
	}

	private void initListeners() {
		JButton deleteBtn = mView.getDeleteBtn();
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mSensorScenarioModel.remove(mModel);
				mSensorScenarioView.removeView(mView);
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
				int position = mSensorScenarioView.indexOfView(mView);
				position++;
				mSensorScenarioModel.add(position, newModel);
				mSensorScenarioModel.setCurrentPosition(position);
				StateViewSmall stateView = new StateViewSmall(newModel,
						mSensorScenarioView);
				new StateControllerSmall(mSensorScenarioController, newModel,
						stateView);
				mSensorScenarioController.setJustAdded();
				mSensorScenarioView.addView(position, stateView);
			}
		});
	}

}
