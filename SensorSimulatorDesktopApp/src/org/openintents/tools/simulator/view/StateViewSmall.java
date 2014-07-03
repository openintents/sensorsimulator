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
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.StateModel;

/**
 * Represents a state representation from the scenario grid.
 *  
 * @author ilarele
 *
 */
public class StateViewSmall extends JPanel {
	private static final long serialVersionUID = 2245603710509545258L;

	private StateModel mModel;
	private JButton mDeleteBtn;
	private JButton mAddBtn;
	private JButton mEditBtn;

	public StateViewSmall(StateModel stateModel,
			SensorsScenarioView scenarioView) {
		mModel = stateModel;
		fillLayout(scenarioView);
	}

	private void fillLayout(SensorsScenarioView scenarioView) {
		setLayout(new BorderLayout());

		// main panel
		JPanel mainPanel = fillMainPanel(scenarioView);
		add(mainPanel, BorderLayout.CENTER);

		// left buttons
		JPanel btnPanel = fillBtnPanel();
		add(btnPanel, BorderLayout.EAST);
	}

	private JPanel fillBtnPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		mDeleteBtn = new JButton(Global.ICON_DELETE);
		mEditBtn = new JButton(Global.ICON_EDIT);
		mAddBtn = new JButton(Global.ICON_ADD);
		panel.add(mDeleteBtn);
		panel.add(mEditBtn);
		panel.add(mAddBtn);
		return panel;
	}

	private JPanel fillMainPanel(SensorsScenarioView scenarioView) {
		JPanel panel = new JPanel(new BorderLayout());
		DevicePanel devicePanel = new DevicePanel(Global.W_DEVICE_SMALL,
				Global.H_DEVICE_SMALL, mModel, scenarioView);
		panel.add(devicePanel, BorderLayout.CENTER);
		panel.setMinimumSize(devicePanel.getPreferredSize());
		return panel;
	}

	public JButton getDeleteBtn() {
		return mDeleteBtn;
	}

	public JButton getEditBtn() {
		return mEditBtn;
	}

	public JButton getAddBtn() {
		return mAddBtn;
	}
}
