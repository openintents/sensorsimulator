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

import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

/**
 * LinearAccelerationView keeps the GUI of the LinearAcceleration sensor.
 * 
 * @author ilarele
 * 
 */
public class LinearAccelerationView extends SensorView {
	private static final long serialVersionUID = 937382958598356357L;

	// Linear Acceleration
	private JTextField mPixelPerMeterText;
	private JTextField mSpringConstantText;
	private JTextField mDampingConstantText;

	private JPanel mSensorQuickPane;

	public LinearAccelerationView(LinearAccelerationModel model) {
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
		LinearAccelerationModel linearAccModel = (LinearAccelerationModel) mModel;

		JPanel linearAccFieldPane = new JPanel(new GridLayout(0, 3));

		linearAccFieldPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"For Computing Linear Acc")));

		JLabel label = new JLabel("Pixels per meter: ", SwingConstants.LEFT);
		linearAccFieldPane.add(label);

		mPixelPerMeterText = new JTextField(5);
		mPixelPerMeterText.setText("" + linearAccModel.getPixelsPerMeter());
		linearAccFieldPane.add(mPixelPerMeterText);

		label = new JLabel(" p/m", SwingConstants.LEFT);
		linearAccFieldPane.add(label);

		label = new JLabel("Spring constant:", SwingConstants.LEFT);
		linearAccFieldPane.add(label);

		mSpringConstantText = new JTextField(5);
		mSpringConstantText.setText("" + linearAccModel.getSpringConstant());
		linearAccFieldPane.add(mSpringConstantText);

		label = new JLabel(" p/s" + SensorModel.SQUARED, SwingConstants.LEFT);
		linearAccFieldPane.add(label);

		label = new JLabel("Damping constant: ", SwingConstants.LEFT);
		linearAccFieldPane.add(label);

		mDampingConstantText = new JTextField(5);
		mDampingConstantText.setText("" + linearAccModel.getDampingConstant());
		linearAccFieldPane.add(mDampingConstantText);

		label = new JLabel(" p/s", SwingConstants.LEFT);
		linearAccFieldPane.add(label);

		resultPanel.add(linearAccFieldPane);
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
		panel1.add(new JLabel("- measures the acceleration without gravity"));
		panel1.add(new JLabel("- has values for all 3 axis"));
		panel1.add(new JLabel("- != 0 when the device is moving"));
		panel1.add(new JLabel("- accelerometer = gravity + linear acceleration"));

		panel.add(panel1);
		return panel;
	}

	public double getPixelsPerMeter() {
		return getSafeDouble(mPixelPerMeterText, 3000);
	}

	public double getSpringConstant() {
		return getSafeDouble(mSpringConstantText, 500);
	}

	public double getDampingConstant() {
		return getSafeDouble(mDampingConstantText, 50);
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}
}
