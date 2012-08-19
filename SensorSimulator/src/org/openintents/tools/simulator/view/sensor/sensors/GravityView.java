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
import javax.swing.SwingConstants;

import org.openintents.tools.simulator.model.sensor.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

/**
 * GravityView keeps the GUI of the Gravity sensor.
 * 
 * @author ilarele
 */
public class GravityView extends SensorView {
	private static final long serialVersionUID = -6006181483029485632L;

	// Gravity
	private JTextField mGravityConstantText;
	private JTextField mAccelerometerLimitText;

	private JPanel mSensorQuickPane;

	public GravityView(GravityModel model) {
		super(model);
		setSensorQuickSettingsPanel();
	}

	private void setSensorQuickSettingsPanel() {
		mSensorQuickPane = new JPanel();
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameters"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GravityModel gravityModel = (GravityModel) mModel;

		JPanel gravityFieldPane = new JPanel(new GridLayout(0, 3));
		gravityFieldPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"For Computing Gravity")));
		JLabel label = new JLabel("Constant g: ", SwingConstants.LEFT);
		gravityFieldPane.add(label);

		mGravityConstantText = new JTextField(5);
		mGravityConstantText.setText("" + gravityModel.getGravityConstant());
		gravityFieldPane.add(mGravityConstantText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, SwingConstants.LEFT);
		gravityFieldPane.add(label);

		label = new JLabel("Acceleration limit: ", SwingConstants.LEFT);
		gravityFieldPane.add(label);

		mAccelerometerLimitText = new JTextField(5);
		mAccelerometerLimitText.setText("" + gravityModel.getAccelLimit());
		gravityFieldPane.add(mAccelerometerLimitText);

		label = new JLabel(" g", SwingConstants.LEFT);
		gravityFieldPane.add(label);
		resultPanel.add(gravityFieldPane);
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
				"- the force of attraction beteen physical bodies"));
		panel1.add(new JLabel("- has values for all 3 axis"));
		panel1.add(new JLabel(
				"- equal with the acceleration if the device is not moving"));

		JPanel panel2 = new JPanel(new GridLayout(0, 1));
		panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.GRAY), BorderFactory
				.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Gravity values in various locations")));
		panel2.add(new JLabel("Earth = 9.80665 m/s^2"));
		panel2.add(new JLabel("Sun = 275.0 m/s^2"));
		panel2.add(new JLabel("Moon = 1.6f m/s^2"));

		panel.add(panel2);
		panel.add(panel2);
		return panel;
	}

	public double getGravityConstant() {
		return getSafeDouble(mGravityConstantText, 9.80665);
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}
}
