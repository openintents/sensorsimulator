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
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openintents.tools.simulator.model.sensor.sensors.ProximityModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

/**
 * ProximityView keeps the GUI of the Proximity sensor.
 * 
 * @author ilarele
 * 
 */
public class ProximityView extends SensorView {
	private static final long serialVersionUID = -13895826746028866L;

	public ProximityView(ProximityModel model) {
		super(model);
		setSensorQuickSettingsPanel();
	}

	private void setSensorQuickSettingsPanel() {
		mSensorQuickPane = new JPanel();
	}

	// Proximity
	private JTextField mProximityText;
	private JTextField mProximityRangeText;
	private JCheckBox mBinaryProximity;
	private JRadioButton mProximityNear;
	private JRadioButton mProximityFar;
	private ButtonGroup mProximityButtonGroup;
	private JPanel mSensorQuickPane;

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameters"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		ProximityModel proxModel = (ProximityModel) mModel;
		/*
		 * Settings for the proximity in centimetres: Value FAR corresponds to
		 * the maximum value of the proximity. Value NEAR corresponds to any
		 * value less than FAR.
		 */
		JPanel proximityFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Proximity: ", SwingConstants.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		proximityFieldPane.add(label, c3);

		mProximityText = new JTextField(5);
		mProximityText.setText("" + proxModel.getProximity());
		c3.gridx = 1;
		mProximityText.setEnabled(false);
		proximityFieldPane.add(mProximityText, c3);

		label = new JLabel(" cm", SwingConstants.LEFT);
		c3.gridx = 2;
		proximityFieldPane.add(label, c3);

		label = new JLabel("Maximum range: ", SwingConstants.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		proximityFieldPane.add(label, c3);

		mProximityRangeText = new JTextField(5);
		mProximityRangeText.setText("" + proxModel.getProximityRange());

		/*
		 * On key press, update the proximity text to reflect the value of the
		 * maximum range if the FAR option is selected, otherwise set the
		 * proximity to any random number less than the maximum range.
		 */
		mProximityRangeText.getDocument().addDocumentListener(
				new DocumentListener() {

					@Override
					public void changedUpdate(DocumentEvent arg0) {
						updateProximityText();
					}

					@Override
					public void insertUpdate(DocumentEvent arg0) {
						updateProximityText();
					}

					@Override
					public void removeUpdate(DocumentEvent arg0) {
						updateProximityText();
					}

					public void updateProximityText() {
						if (mProximityFar.isSelected()) {
							mProximityText.setText(mProximityRangeText
									.getText());
						} else {
							Random r = new Random();
							int currentMaximumRange = Integer
									.parseInt(mProximityRangeText.getText());
							int reduction = r.nextInt(currentMaximumRange);
							int randomNearProximity = currentMaximumRange
									- reduction;
							mProximityText.setText(Integer
									.toString(randomNearProximity));
						}
					}

				});

		c3.gridx = 1;
		proximityFieldPane.add(mProximityRangeText, c3);

		label = new JLabel(" cm", SwingConstants.LEFT);
		c3.gridx = 2;
		proximityFieldPane.add(label, c3);

		mBinaryProximity = new JCheckBox(SensorModel.BINARY_PROXIMITY);
		mBinaryProximity.setSelected(proxModel.isBinary());
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		proximityFieldPane.add(mBinaryProximity, c3);

		mProximityNear = new JRadioButton("NEAR", proxModel.isNear());
		mProximityFar = new JRadioButton("FAR", !proxModel.isNear());
		mProximityButtonGroup = new ButtonGroup();
		mProximityButtonGroup.add(mProximityFar);
		mProximityButtonGroup.add(mProximityNear);

		mProximityNear.setActionCommand("NEAR");
		mProximityFar.setActionCommand("FAR");

		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;

		proximityFieldPane.add(mProximityNear, c3);
		c3.gridx++;
		proximityFieldPane.add(mProximityFar, c3);

		// Proximity panel ends

		// Add proximity panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(proximityFieldPane, c2);
		return resultPanel;
	}

	public double getProximity() {
		return getSafeDouble(mProximityText);
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
				"- measures the distance from the sensor, in centimeters"));
		panel1.add(new JLabel(
				"- depending on device, may have only far or near values"));

		panel.add(panel1);
		return panel;
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}
}
