/*
 * Copyright (C) 2008 - 2011 OpenIntents.org
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

package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.GyroscopeModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

/**
 * GyroscopeView keeps the GUI of the Gyroscope sensor.
 * 
 * @author ilarele
 * 
 */
public class GyroscopeView extends SensorView {
	private static final long serialVersionUID = -13895826746028866L;

	public GyroscopeView(GyroscopeModel model) {
		super(model);
		setSensorQuickSettingsPanel();
	}

	private void setSensorQuickSettingsPanel() {
		mSensorQuickPane = new JPanel();
	}

	// gravity constant
	private JTextField mGravityText;

	private JPanel mSensorQuickPane;

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sensor Specific Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		GyroscopeModel gyroscopeModel = (GyroscopeModel) mModel;

		// gravity constant
		JPanel computedGravity = new JPanel(new GridLayout(0, 3));
		JLabel label = new JLabel("Constant g: ", JLabel.LEFT);
		computedGravity.add(label);
		mGravityText = new JTextField(5);
		mGravityText.setText("" + gyroscopeModel.getGravityConstant());
		computedGravity.add(mGravityText);
		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		computedGravity.add(label);
		resultPanel.add(computedGravity);

		return resultPanel;
	}

	@Override
	protected JPanel getSensorSpecificHelp() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JPanel panel1 = new JPanel(new GridLayout(0, 1));
		panel1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						mModel.getName())));
		panel1.add(new JLabel(
				"- describes the angular speed for each axis of a spinning disk "));

		panel.add(panel1);
		return panel;
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}
}
