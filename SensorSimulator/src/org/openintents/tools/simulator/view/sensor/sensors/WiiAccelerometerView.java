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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;

/**
 * WiiAccelerometerView keeps the GUI of the WiiAccelerometer sensor.
 * 
 * @author Dale Thatcher
 * 
 */
public class WiiAccelerometerView extends JPanel {
	private static final long serialVersionUID = 7293246659297862528L;
	// Real device bridge
	private JCheckBox mRealDeviceWiimote;
	private JTextField mRealDevicePath;
	private JLabel mRealDeviceOutputLabel;

	@SuppressWarnings("unused")
	private WiiAccelerometerModel model;

	public WiiAccelerometerView(WiiAccelerometerModel model) {
		this.model = model;

		mRealDeviceWiimote = new JCheckBox("Use Wii-mote accelerometer");
		mRealDeviceWiimote.setSelected(model.isUsed());

		mRealDevicePath = new JTextField(20);
		mRealDevicePath.setText(model.getDevicePath());

		mRealDeviceOutputLabel = new JLabel("-", JLabel.LEFT);

		setLayout(new GridLayout(0, 1));

		add(mRealDeviceWiimote);
		add(mRealDevicePath);
		add(mRealDeviceOutputLabel);
	}

	public boolean isSelected() {
		return mRealDeviceWiimote.isSelected();
	}

	public void setOutputBackground(Color color) {
		mRealDeviceOutputLabel.setBackground(color);
	}

	public void setOutput(String message) {
		mRealDeviceOutputLabel.setText(message);
	}

	public String getRealDevicePath() {
		return mRealDevicePath.getText().toString();
	}
}
