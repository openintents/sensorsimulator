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

package org.openintents.tools.simulator.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.StateModel;

/**
 * Contains extended view of a state from the scenario. 
 * User can change sensors values by importing the current state
 * (similar to the default device). 
 * 
 * 
 * @author ilarele
 * 
 */
public class StateViewBig extends JPanel {
	private static final long serialVersionUID = 2245403710809545258L;
	private JButton mImportState;
	private DevicePanel mDevicePanel;
	private JTextArea mSensorsValues;
	private StateModel mModel;
	private StateViewSmall mSmallView;

	public StateViewBig(StateModel stateModel, StateViewSmall smallView,
			SensorsScenarioView sensorView) {
		mModel = stateModel;
		mSmallView = smallView;
		initLayout(stateModel, sensorView);
	}

	private void initLayout(StateModel stateModel,
			SensorsScenarioView sensorView) {
		setLayout(new FlowLayout());

		JPanel leftPanel = new JPanel(new BorderLayout());
		// device view
		mDevicePanel = new DevicePanel(Global.W_DEVICE_BIG,
				Global.H_DEVICE_BIG, stateModel, sensorView);
		leftPanel.add(mDevicePanel, BorderLayout.CENTER);
		// import current state button
		mImportState = new JButton("Copy from the Simulator", Global.ICON_COPY);
		leftPanel.add(mImportState, BorderLayout.SOUTH);
		add(leftPanel, BorderLayout.CENTER);

		// sensors values
		String values = stateModel.getSensorsValues();
		mSensorsValues = new JTextArea();
		JScrollPane scrollPaneSensorData = new JScrollPane(mSensorsValues);
		scrollPaneSensorData
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		mSensorsValues.setText(values);
		add(mSensorsValues, BorderLayout.EAST);

	}

	public JButton getImportStateButton() {
		return mImportState;
	}

	public void refreshFromModel() {
		mDevicePanel.repaint();
		mSensorsValues.setText(mModel.getSensorsValues());
		mSmallView.repaint();
	}
}
