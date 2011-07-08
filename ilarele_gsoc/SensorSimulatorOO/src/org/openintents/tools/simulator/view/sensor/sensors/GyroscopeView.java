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
	}

	// gravity constant
	private JTextField mGravityText;

	// force in the string
	private JTextField mStringForceText;

	// radius of the wheel spindle
	private JTextField mWheelRadiusText;

	// inertia of gyro wheel
	private JTextField mWheelInertiaText;

	// lateral inertia of gyro
	private JTextField mWheelLateraInertiaText;

	// Wheel Friction Coefficient
	private JTextField mWheelFrictionText;

	// pedestal friction coefficient
	private JTextField mPedestFrictionText;

	// the mass of gyro
	private JTextField mGyroMassText;

	// pedestal from gyro gravity center
	private JTextField mCenterPedestalText;

	// length of the pull string
	private JTextField mPullStringText;

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		GyroscopeModel gyroscopeModel = (GyroscopeModel) model;
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sensor Specific Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// gravity constant
		JPanel computedGravity = new JPanel(new GridLayout(0, 3));
		JLabel label = new JLabel("Constant g: ", JLabel.LEFT);
		computedGravity.add(label);
		mGravityText = new JTextField(5);
//		mGravityText.setText("" + gyroscopeModel.getGravityConstant());
		computedGravity.add(mGravityText);
		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		computedGravity.add(label);
		resultPanel.add(computedGravity);

		// force in the string
		JPanel stringForce = new JPanel(new GridLayout(0, 3));
		label = new JLabel("String Force: ", JLabel.LEFT);
		stringForce.add(label);
		mStringForceText = new JTextField(5);
//		mStringForceText.setText("" + gyroscopeModel.getStringForce());
		stringForce.add(mStringForceText);
		label = new JLabel(" N", JLabel.LEFT);
		stringForce.add(label);
		resultPanel.add(stringForce);

		// radius of the wheel spindle
		JPanel wheelRadius = new JPanel(new GridLayout(0, 3));
		label = new JLabel("Wheel Radius: ", JLabel.LEFT);
		wheelRadius.add(label);
		mWheelRadiusText = new JTextField(5);
//		mWheelRadiusText.setText("" + gyroscopeModel.getWheelRadius());
		wheelRadius.add(mWheelRadiusText);
		label = new JLabel(" m", JLabel.LEFT);
		wheelRadius.add(label);
		resultPanel.add(wheelRadius);

		// inertia of gyro wheel
		JPanel wheelInertia = new JPanel(new GridLayout(0, 3));
		label = new JLabel("Wheel Inertia: ", JLabel.LEFT);
		wheelInertia.add(label);
		mWheelInertiaText = new JTextField(5);
//		mWheelInertiaText.setText("" + gyroscopeModel.getWheelInertia());
		wheelInertia.add(mWheelInertiaText);
		label = new JLabel(" Kg * m" + SensorModel.SQUARED, JLabel.LEFT);
		wheelInertia.add(label);
		resultPanel.add(wheelInertia);

		// lateral inertia of gyro
		JPanel lateralInertia = new JPanel(new GridLayout(0, 3));
		label = new JLabel("Lateral Intertia: ", JLabel.LEFT);
		lateralInertia.add(label);
		mWheelLateraInertiaText = new JTextField(5);
//		mWheelLateraInertiaText
//				.setText("" + gyroscopeModel.getLateralInertia());
		lateralInertia.add(mWheelLateraInertiaText);
		label = new JLabel(" Kg * m" + SensorModel.SQUARED, JLabel.LEFT);
		lateralInertia.add(label);
		resultPanel.add(lateralInertia);

		// Wheel Friction Coefficient
		JPanel wheelFrictionCoef = new JPanel(new GridLayout(0, 3));
		label = new JLabel("Wheel Friction Coef: ", JLabel.LEFT);
		wheelFrictionCoef.add(label);
		mWheelFrictionText = new JTextField(5);
//		mWheelFrictionText.setText("" + gyroscopeModel.getWheelFriction());
		wheelFrictionCoef.add(mWheelFrictionText);
		resultPanel.add(wheelFrictionCoef);

		// pedestal friction coefficient
		JPanel pedestalFrictionCoef = new JPanel(new GridLayout(0, 3));
		label = new JLabel("Pedestal Friction Coef.: ", JLabel.LEFT);
		pedestalFrictionCoef.add(label);
		mPedestFrictionText = new JTextField(5);
//		mPedestFrictionText.setText("" + gyroscopeModel.getPedestalFriction());
		pedestalFrictionCoef.add(mPedestFrictionText);
		resultPanel.add(pedestalFrictionCoef);

		// the mass of gyro
		JPanel gyroMass = new JPanel(new GridLayout(0, 3));
		label = new JLabel("Gyro Mass: ", JLabel.LEFT);
		gyroMass.add(label);
		mGyroMassText = new JTextField(5);
//		mGyroMassText.setText("" + gyroscopeModel.getGyroMass());
		gyroMass.add(mGyroMassText);
		label = new JLabel(" Kg", JLabel.LEFT);
		gyroMass.add(label);
		resultPanel.add(gyroMass);

		// pedestal from gyro gravity center
		JPanel centerPedestal = new JPanel(new GridLayout(0, 3));
		label = new JLabel("Pedestal from g Center: ", JLabel.LEFT);
		centerPedestal.add(label);
		mCenterPedestalText = new JTextField(5);
//		mCenterPedestalText.setText("" + gyroscopeModel.getCenterPedestal());
		centerPedestal.add(mCenterPedestalText);
		resultPanel.add(centerPedestal);

		// length of the pull string
		JPanel pullString = new JPanel(new GridLayout(0, 3));
		label = new JLabel("Pedestal from g Center: ", JLabel.LEFT);
		pullString.add(label);
		mPullStringText = new JTextField(5);
//		mPullStringText.setText("" + gyroscopeModel.getPullString());
		pullString.add(mPullStringText);
		label = new JLabel(" m", JLabel.LEFT);
		pullString.add(label);
		resultPanel.add(pullString);

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
						model.getName())));
		panel1.add(new JLabel(
				"- describes the angular speed for each axis of a spinning disk "));

		panel.add(panel1);
		return panel;
	}

}
