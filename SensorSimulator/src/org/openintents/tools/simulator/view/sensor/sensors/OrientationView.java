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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;

/**
 * OrientationView keeps the GUI of the Orientation sensor.
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class OrientationView extends SensorView {
	private static final long serialVersionUID = -7668687597011522775L;
	private JPanel mSensorSpecificPane;
	private JPanel mSensorQuickPane;

	// Sliders:
	private JSlider mYawSlider;
	private JSlider mPitchSlider;
	private JSlider mRollSlider;

	public OrientationView(OrientationModel model,
			SensorSimulatorModel sensorSimulatorModel) {
		super(model);
		mEnabled.setSelected(true);
		setSensorQuickSettingsPanel();
	}

	private void setSensorQuickSettingsPanel() {
		mSensorQuickPane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// labels
		JLabel yawLabel = new JLabel("Yaw(y)  ", SwingConstants.CENTER);
		yawLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel pitchLabel = new JLabel("Pitch(x) ", SwingConstants.CENTER);
		pitchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel rollLabel = new JLabel("Roll(z) ", SwingConstants.CENTER);
		rollLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Create the slider.
		mYawSlider = new JSlider(SwingConstants.HORIZONTAL, -180, 180, -20);
		mPitchSlider = new JSlider(SwingConstants.HORIZONTAL, -180, 180, -60);
		mRollSlider = new JSlider(SwingConstants.HORIZONTAL, -180, 180, 0);

		// Turn on labels at major tick marks.
		mYawSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		mPitchSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		mRollSlider.setMajorTickSpacing(90);
		mRollSlider.setMinorTickSpacing(10);
		mRollSlider.setPaintTicks(true);
		mRollSlider.setPaintLabels(true);
		mRollSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		// sliders
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		mSensorQuickPane.add(yawLabel, c);
		c.gridx = 1;
		mSensorQuickPane.add(mYawSlider, c);
		c.gridx = 0;
		c.gridy++;
		mSensorQuickPane.add(pitchLabel, c);
		c.gridx = 1;
		mSensorQuickPane.add(mPitchSlider, c);
		c.gridx = 0;
		c.gridy++;
		mSensorQuickPane.add(rollLabel, c);
		c.gridx = 1;
		mSensorQuickPane.add(mRollSlider, c);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		mSensorSpecificPane = new JPanel();
		mSensorSpecificPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameters"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return mSensorSpecificPane;
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
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
		panel1.add(new JLabel("- measures the device 'direction'"));
		panel1.add(new JLabel("- has 3 values: yaw(rotation around y-axis)"));
		panel1.add(new JLabel("                pitch(rotation around x-axis)"));
		panel1.add(new JLabel("                roll(rotation around z-axis)"));

		panel.add(panel1);
		return panel;
	}

	public JSlider getYawSlider() {
		return mYawSlider;
	}

	public JSlider getPitchSlider() {
		return mPitchSlider;
	}

	public JSlider getRollSlider() {
		return mRollSlider;
	}

	public void setYawSlider(int newYaw) {
		mYawSlider.setValue(newYaw);
	}

	public void setRollSlider(int newRoll) {
		mRollSlider.setValue(newRoll);
	}

	public void setPitchSlider(int newPitch) {
		mPitchSlider.setValue(newPitch);
	}

	public int getRollSliderValue() {
		return mRollSlider.getValue();
	}

	public int getYawSliderValue() {
		return mYawSlider.getValue();
	}

	public int getPitchSliderValue() {
		return mPitchSlider.getValue();
	}

}
