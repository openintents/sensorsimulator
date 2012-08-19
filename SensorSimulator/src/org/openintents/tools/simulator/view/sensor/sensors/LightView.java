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

import org.openintents.tools.simulator.model.sensor.sensors.LightModel;

/**
 * LightView keeps the GUI of the Light sensor.
 * 
 * @author ilarele
 * 
 */
public class LightView extends SensorView {
	private static final long serialVersionUID = 3945184157589120119L;
	// Light
	private JTextField mLightText;

	private JPanel mSensorQuickPane;
	private JSlider mLightSlider;

	public LightView(LightModel model) {
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
		mSensorQuickPane.add(new JLabel("Light"));

		layout.gridx = 1;
		mLightSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 2000, 400);
		mLightSlider.setPaintTicks(true);
		mLightSlider.setPaintLabels(true);
		mLightSlider.setMajorTickSpacing(500);
		mLightSlider.setMinorTickSpacing(100);
		mLightSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		mSensorQuickPane.add(mLightSlider);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameters"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		LightModel lightModel = (LightModel) mModel;
		// ////////////////////////////
		// Light (in lux)
		JPanel lightFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Light Value: ", SwingConstants.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		lightFieldPane.add(label, c3);

		mLightText = new JTextField(5);
		mLightText.setText("" + lightModel.getReadLight());
		c3.gridx = 1;
		lightFieldPane.add(mLightText, c3);

		label = new JLabel(" lux", SwingConstants.LEFT);
		c3.gridx = 2;
		lightFieldPane.add(label, c3);

		// Light panel ends

		// Add light panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(lightFieldPane, c2);
		return resultPanel;
	}

	public double getLight() {
		return getSafeDouble(mLightText);
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
		panel1.add(new JLabel("- The intensity of light"));

		panel.add(panel1);

		JPanel panel2 = new JPanel(new GridLayout(0, 1));
		panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.GRAY), BorderFactory
				.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Luminance values in universe")));

		panel2.add(new JLabel("Overcast = 10000 lux"));
		panel2.add(new JLabel("Sunrise = 400 lux"));
		panel2.add(new JLabel("Cloudy Day = 100 lux"));
		panel2.add(new JLabel("Night with Fullmoon = 0.25 lux"));
		panel2.add(new JLabel("Night without Moon = 0.001 lux"));

		panel.add(panel2);

		return panel;
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}

	public void setLight(double value) {
		mLightText.setText("" + value);
		mLightSlider.setValue((int) value);
	}

	public JSlider getLightSlider() {
		return mLightSlider;
	}
}
