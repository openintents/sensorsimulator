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

import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.view.sensor.DeviceView;

/**
 * OrientationView keeps the GUI of the Orientation sensor.
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class OrientationView extends SensorView {
	private static final long serialVersionUID = -7668687597011522775L;
	private DeviceView mMobile;
	private JPanel mSensorSpecificPane;

	public OrientationView(OrientationModel model,
			SensorSimulatorModel sensorSimulatorModel) {
		super(model);
		mEnabled.setSelected(true);
		mMobile = new DeviceView(sensorSimulatorModel);
		mSensorSpecificPane.add(mMobile);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		mSensorSpecificPane = new JPanel();
		mSensorSpecificPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return mSensorSpecificPane;
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
		panel1.add(new JLabel("- measures the device 'direction'"));
		panel1.add(new JLabel("- has 3 values: yaw(rotation around y-axis)"));
		panel1.add(new JLabel("                pitch(rotation around x-axis)"));
		panel1.add(new JLabel("                roll(rotation around z-axis)"));

		panel.add(panel1);
		return panel;
	}

	public DeviceView getDeviceView() {
		return mMobile;
	}
}
