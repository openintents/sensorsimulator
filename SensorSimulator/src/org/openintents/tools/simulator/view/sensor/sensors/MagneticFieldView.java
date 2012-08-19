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
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openintents.tools.simulator.model.sensor.sensors.MagneticFieldModel;

/**
 * MagneticFieldView keeps the GUI of the MagneticField sensor.
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class MagneticFieldView extends SensorView {
	private static final long serialVersionUID = -4625924984418977091L;
	// Magnetic field
	private JTextField mNorthText;
	private JTextField mEastText;
	private JTextField mVerticalText;
	private JPanel mSensorQuickPane;

	public MagneticFieldView(MagneticFieldModel model) {
		super(model);
		setSensorQuickSettingsPanel();
	}

	private void setSensorQuickSettingsPanel() {
		mSensorQuickPane = new JPanel();
	}

	public double getVertical() {
		return getSafeDouble(mVerticalText);
	}

	public double getEast() {
		return getSafeDouble(mEastText);
	}

	public double getNorth() {
		return getSafeDouble(mNorthText);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameters"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		MagneticFieldModel magModel = (MagneticFieldModel) mModel;
		// //////////////////////////////
		// Magnetic field (in nanoTesla)
		// Values can be found at
		// Default values are for San Francisco.
		JPanel magneticFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel magneticFieldNorthLabel = new JLabel("North component: ",
				SwingConstants.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		magneticFieldPane.add(magneticFieldNorthLabel, c3);

		mNorthText = new JTextField(5);
		mNorthText.setText("" + magModel.getNorth());
		c3.gridx = 1;
		magneticFieldPane.add(mNorthText, c3);

		JLabel nanoTeslaLabel = new JLabel(" nT", SwingConstants.LEFT);
		c3.gridx = 2;
		magneticFieldPane.add(nanoTeslaLabel, c3);

		JLabel magneticFieldEastLabel = new JLabel("East component: ",
				SwingConstants.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		magneticFieldPane.add(magneticFieldEastLabel, c3);

		mEastText = new JTextField(5);
		mEastText.setText("" + magModel.getEast());
		c3.gridx = 1;
		magneticFieldPane.add(mEastText, c3);

		nanoTeslaLabel = new JLabel(" nT", SwingConstants.LEFT);
		c3.gridx = 2;
		magneticFieldPane.add(nanoTeslaLabel, c3);

		JLabel magneticFieldVerticalLabel = new JLabel("Vertical component: ",
				SwingConstants.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		magneticFieldPane.add(magneticFieldVerticalLabel, c3);

		mVerticalText = new JTextField(5);
		mVerticalText.setText("" + magModel.getVertical());
		c3.gridx = 1;
		magneticFieldPane.add(mVerticalText, c3);

		JLabel label = new JLabel(" nT", SwingConstants.LEFT);
		c3.gridx = 2;
		magneticFieldPane.add(label, c3);

		// Magnetic field panel ends

		// Add magnetic field panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(magneticFieldPane, c2);
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
		panel1.add(new JLabel("- measures the magnetic field"));
		panel1.add(new JLabel("- has values for all 3 axis"));

		JPanel panel2 = new JPanel(new GridLayout(0, 1));
		panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.GRAY), BorderFactory
				.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Magnetic field values on Earth:")));
		panel2.add(new JLabel("Minimum = 30 uT"));
		panel2.add(new JLabel("Maximum = 60 uT"));

		panel.add(panel1);
		panel.add(panel2);
		return panel;
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}
}
