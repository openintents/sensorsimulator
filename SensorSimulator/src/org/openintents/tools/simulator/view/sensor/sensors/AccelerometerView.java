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
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

/**
 * AccelerometerView keeps the GUI of the Accelerometer sensor.
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class AccelerometerView extends SensorView {

	private static final long serialVersionUID = 4044072966491754271L;

	// Gravity
	private JTextField mGravityConstantText;
	private JTextField mAccelerometerLimitText;

	// Linear Acceleration
	private JTextField mPixelPerMeterText;
	private JTextField mSpringConstantText;
	private JTextField mDampingConstantText;

	private JCheckBox mShowAcceleration;

	private WiiAccelerometerView wiiAccelerometerView;

	private JPanel mSensorQuickPane;

	public AccelerometerView(AccelerometerModel model) {
		super(model);
		setSensorQuickSettingsPanel();
	}

	private void setSensorQuickSettingsPanel() {
		mSensorQuickPane = new JPanel();
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		AccelerometerModel accModel = ((AccelerometerModel) mModel);
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sensor Specific Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		mShowAcceleration = new JCheckBox(SensorModel.SHOW_ACCELERATION);
		mShowAcceleration.setSelected(accModel.isShown());
		mShowAcceleration.setAlignmentX(Component.RIGHT_ALIGNMENT);
		resultPanel.add(mShowAcceleration);

		// computedGravity (m/s^2)
		JPanel computedGravity = fillComputedGravity(accModel);
		resultPanel.add(computedGravity);

		// computedLinearAcceleration (m/s^2)
		JPanel computedLinearAcceleration = fillComputedLinearAcceleration(accModel);
		resultPanel.add(computedLinearAcceleration);

		// Real sensor bridge
		JPanel realSensorBridgeFieldPane = fillRealSensorBridge(accModel);
		resultPanel.add(realSensorBridgeFieldPane);

		return resultPanel;
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}

	private JPanel fillRealSensorBridge(AccelerometerModel accModel) {
		JPanel realSensorBridgeFieldPane = new JPanel(new GridLayout(0, 1));
		realSensorBridgeFieldPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Real sensor bridge")));

		wiiAccelerometerView = new WiiAccelerometerView(
				accModel.getRealDeviceBridgeAddon());
		realSensorBridgeFieldPane.add(wiiAccelerometerView);
		return realSensorBridgeFieldPane;
	}

	private JPanel fillComputedLinearAcceleration(AccelerometerModel accModel) {
		JPanel computedLinearAcceleration = new JPanel(new GridLayout(0, 3));

		computedLinearAcceleration
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory
						.createMatteBorder(2, 0, 0, 0, Color.GRAY),
						BorderFactory.createTitledBorder(
								BorderFactory.createEmptyBorder(3, 0, 15, 0),
								"For Computing Linear Acc")));

		JLabel label = new JLabel("Pixels per meter: ", JLabel.LEFT);
		computedLinearAcceleration.add(label);

		mPixelPerMeterText = new JTextField(5);
		mPixelPerMeterText.setText("" + accModel.getPixelsPerMeter());
		computedLinearAcceleration.add(mPixelPerMeterText);

		label = new JLabel(" p/m", JLabel.LEFT);
		computedLinearAcceleration.add(label);

		label = new JLabel("Spring constant:", JLabel.LEFT);
		computedLinearAcceleration.add(label);

		mSpringConstantText = new JTextField(5);
		mSpringConstantText.setText("" + accModel.getSpringConstant());
		computedLinearAcceleration.add(mSpringConstantText);

		label = new JLabel(" p/s" + SensorModel.SQUARED, JLabel.LEFT);
		computedLinearAcceleration.add(label);

		label = new JLabel("Damping constant: ", JLabel.LEFT);
		computedLinearAcceleration.add(label);

		mDampingConstantText = new JTextField(5);
		mDampingConstantText.setText("" + accModel.getDampingConstant());
		computedLinearAcceleration.add(mDampingConstantText);

		label = new JLabel(" p/s", JLabel.LEFT);
		computedLinearAcceleration.add(label);
		return computedLinearAcceleration;
	}

	private JPanel fillComputedGravity(AccelerometerModel accModel) {
		JPanel computedGravity = new JPanel(new GridLayout(0, 3));

		computedGravity.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"For Computing Gravity")));
		JLabel label = new JLabel("Constant g: ", JLabel.LEFT);
		computedGravity.add(label);

		mGravityConstantText = new JTextField(5);
		mGravityConstantText.setText("" + accModel.getGravityConstant());
		computedGravity.add(mGravityConstantText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		computedGravity.add(label);

		label = new JLabel("Acceleration limit: ", JLabel.LEFT);
		computedGravity.add(label);

		mAccelerometerLimitText = new JTextField(5);
		mAccelerometerLimitText.setText("" + accModel.getAccelLimit());
		computedGravity.add(mAccelerometerLimitText);

		label = new JLabel(" g", JLabel.LEFT);
		computedGravity.add(label);
		return computedGravity;
	}

	public JCheckBox getShow() {
		return mShowAcceleration;
	}

	public double getGravityConstant() {
		return getSafeDouble(mGravityConstantText, 9.80665);
	}

	public double getAccelerometerLimit() {
		return getSafeDouble(mAccelerometerLimitText);
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

	public JCheckBox getShowAcceleration() {
		return mShowAcceleration;
	}

	public WiiAccelerometerView getRealDeviceBridgeAddon() {
		return wiiAccelerometerView;
	}

	public void setRealDeviceBridgeAddonOutput(String line) {
		wiiAccelerometerView.setOutput(line);
	}

	public String getWiiPath() {
		return wiiAccelerometerView.getRealDevicePath();
	}

	public void setWiiOutput(String wiiStatus) {
		wiiAccelerometerView.setOutput(wiiStatus);
	}

	public void setEnablePanel(JComponent parent, boolean enable) {
		for (Component child : parent.getComponents()) {
			child.setEnabled(enable);
		}
	}

	@Override
	protected JPanel getSensorSpecificHelp() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		JPanel panel1 = new JPanel(new GridLayout(0, 1));
		panel1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						mModel.getName())));
		panel1.add(new JLabel(
				"- measures the acceleration applied to the device"));
		panel1.add(new JLabel("- has values for all 3 axis"));
		panel1.add(new JLabel("- accelerometer = gravity + linear acceleration"));

		panel.add(panel1);
		return panel;
	}

}
