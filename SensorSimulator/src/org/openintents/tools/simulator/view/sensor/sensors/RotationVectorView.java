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

import org.openintents.tools.simulator.model.sensor.sensors.RotationVectorModel;
import org.openintents.tools.simulator.model.telnet.Vector;

/**
 * RotationVectorView keeps the GUI of the RotationVector sensor.
 * 
 * @author ilarele
 * 
 */
public class RotationVectorView extends SensorView {
	private static final long serialVersionUID = 5156654905500075165L;
	private JTextField mRotationVectorTextX;
	private JTextField mRotationVectorTextY;
	private JTextField mRotationVectorTextZ;
	private JPanel mSensorQuickPane;

	public RotationVectorView(RotationVectorModel model) {
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
		GridBagConstraints c3 = new GridBagConstraints();
		RotationVectorModel rotationModel = (RotationVectorModel) mModel;

		// ////////////////////////////
		// rotation
		JPanel rotationFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy = 0;

		// x

		JLabel label = new JLabel("Θ(x): ", SwingConstants.LEFT);
		rotationFieldPane.add(label, c3);
		mRotationVectorTextX = new JTextField(5);
		mRotationVectorTextX.setText("" + rotationModel.getRotationVectorX());
		c3.gridx = 1;
		rotationFieldPane.add(mRotationVectorTextX, c3);

		label = new JLabel(rotationModel.getSI(), SwingConstants.LEFT);
		c3.gridx = 2;
		rotationFieldPane.add(label, c3);

		// y
		label = new JLabel("Θ(y): ", SwingConstants.LEFT);
		c3.gridx = 0;
		c3.gridy = 1;
		rotationFieldPane.add(label, c3);

		mRotationVectorTextY = new JTextField(5);
		mRotationVectorTextY.setText("" + rotationModel.getRotationVectorY());
		c3.gridx = 1;
		rotationFieldPane.add(mRotationVectorTextY, c3);

		label = new JLabel(rotationModel.getSI(), SwingConstants.LEFT);
		c3.gridx = 2;
		rotationFieldPane.add(label, c3);

		// z
		c3.gridx = 0;
		c3.gridy = 2;
		label = new JLabel("Θ(z): ", SwingConstants.LEFT);
		rotationFieldPane.add(label, c3);

		mRotationVectorTextZ = new JTextField(5);
		mRotationVectorTextZ.setText("" + rotationModel.getRotationVectorZ());
		c3.gridx = 1;
		rotationFieldPane.add(mRotationVectorTextZ, c3);

		label = new JLabel(rotationModel.getSI(), SwingConstants.LEFT);
		c3.gridx = 2;
		rotationFieldPane.add(label, c3);

		// Add rotation panel to settings
		resultPanel.add(rotationFieldPane);
		return resultPanel;
	}

	public double getRotationVectorX() {
		return getSafeDouble(mRotationVectorTextX);
	}

	public double getRotationVectorY() {
		return getSafeDouble(mRotationVectorTextY);
	}

	public double getRotationVectorZ() {
		return getSafeDouble(mRotationVectorTextZ);
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
		panel1.add(new JLabel("- measures the angle of rotation around axis(Θ)"));
		panel1.add(new JLabel("- has values for all 3 axis (sin(Θ/2))"));

		panel.add(panel1);
		return panel;
	}

	public void setRotationVector(Vector v) {
		mRotationVectorTextX.setText("" + v.x);
		mRotationVectorTextY.setText("" + v.y);
		mRotationVectorTextZ.setText("" + v.z);
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}
}
