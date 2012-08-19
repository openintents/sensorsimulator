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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openintents.tools.simulator.model.sensor.sensors.PressureModel;

/**
 * PressureView keeps the GUI of the Pressure sensor.
 * 
 * @author ilarele
 * 
 */
public class PressureView extends SensorView {
	private static final long serialVersionUID = -13895826746028866L;

	private JSlider mPressureSlider;

	public PressureView(PressureModel model) {
		super(model);
		setSensorQuickSettingsPanel();
	}

	private void setSensorQuickSettingsPanel() {
		mSensorQuickPane = new JPanel(new GridBagLayout());
		GridBagConstraints layout = new GridBagConstraints();
		layout.fill = GridBagConstraints.HORIZONTAL;
		layout.anchor = GridBagConstraints.NORTHWEST;
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy = 0;
		mSensorQuickPane.add(new JLabel("Pressure"));

		layout.gridx = 1;
		mPressureSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
		mPressureSlider.setPaintTicks(true);
		mPressureSlider.setPaintLabels(true);
		mPressureSlider.setMajorTickSpacing(50);
		mPressureSlider.setMinorTickSpacing(10);
		mPressureSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		mSensorQuickPane.add(mPressureSlider);
	}

	// Pressure
	private JTextField mPressureText;
	private JPanel mSensorQuickPane;

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameters"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		PressureModel pressureModel = (PressureModel) mModel;
		// ////////////////////////////
		// Pressure
		JPanel pressureFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Pressure Value: ", SwingConstants.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		pressureFieldPane.add(label, c3);

		mPressureText = new JTextField(5);
		mPressureText.setText("" + pressureModel.getPressure());
		c3.gridx = 1;
		pressureFieldPane.add(mPressureText, c3);

		label = new JLabel(mModel.getSI(), SwingConstants.LEFT);
		c3.gridx = 2;
		pressureFieldPane.add(label, c3);

		// Pressure panel ends

		// Add pressure panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(pressureFieldPane, c2);
		return resultPanel;
	}

	public double getPressure() {
		return getSafeDouble(mPressureText);
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
		panel1.add(new JLabel("- measures the air pressure"));
		panel1.add(new JLabel("- used to predict the weather"));

		JPanel panel2 = new JPanel(new GridLayout(0, 1));
		panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.GRAY), BorderFactory
				.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Pressure values:")));
		panel2.add(new JLabel("Normal Atmospheric Pressure = 1013.25 hPa"));

		panel.add(panel1);
		panel.add(panel2);
		return panel;
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}

	public void setPressure(double value) {
		mPressureText.setText("" + value);
		mPressureSlider.setValue((int) (value * 100));
	}

	public JSlider getPressureSlider() {
		return mPressureSlider;
	}
}
