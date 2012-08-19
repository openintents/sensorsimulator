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

package org.openintents.tools.simulator.controller.sensor;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.model.telnet.WiiMoteData;
import org.openintents.tools.simulator.view.sensor.sensors.WiiAccelerometerView;

/**
 * WiiAccelerometerController keeps the behaviour of the WiiAccelerometer sensor
 * (listeners, etc.)
 * 
 * @author Dale Thatcher
 * 
 */
public class WiiAccelerometerController {

	// Real device bridge
	private JCheckBox mRealDeviceThinkpad;
	private JCheckBox mRealDeviceWiimote;
	private JTextField mRealDevicePath;
	private JLabel mRealDeviceOutputLabel;

	private WiiMoteData wiiMoteData = new WiiMoteData();

	public WiiAccelerometerController(WiiAccelerometerModel model,
			WiiAccelerometerView view) {
		mRealDeviceThinkpad = new JCheckBox("Use Thinkpad accelerometer");
		mRealDeviceThinkpad.setSelected(false);
		mRealDeviceThinkpad.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					mRealDeviceWiimote.setSelected(false);
			}
		});

		mRealDeviceWiimote = new JCheckBox("Use Wii-mote accelerometer");
		mRealDeviceWiimote.setSelected(false);
		mRealDeviceWiimote.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					mRealDeviceThinkpad.setSelected(false);
			}
		});

		mRealDevicePath = new JTextField(20);
		mRealDevicePath.setText("/sys/devices/platform/hdaps/position");

		mRealDeviceOutputLabel = new JLabel("-", JLabel.LEFT);
	}

	public void fillPane(JPanel realSensorBridgeFieldPane, GridBagConstraints c3) {
		c3.gridwidth = 1;
		c3.gridx = 0;
		realSensorBridgeFieldPane.add(mRealDeviceThinkpad, c3);

		c3.gridy++;
		realSensorBridgeFieldPane.add(mRealDeviceWiimote, c3);

		c3.gridx = 0;
		c3.gridy++;
		realSensorBridgeFieldPane.add(mRealDevicePath, c3);

		c3.gridx = 0;
		c3.gridy++;
		realSensorBridgeFieldPane.add(mRealDeviceOutputLabel, c3);
	}

	public boolean isSelected() {
		return mRealDeviceWiimote.isSelected();
	}

	public int getRoll() {
		return wiiMoteData.getRoll();
	}

	public int getPitch() {
		return wiiMoteData.getPitch();
	}

	public boolean readRawData() {
		boolean result = false;
		wiiMoteData.setDataFilePath(mRealDevicePath.getText());
		result = wiiMoteData.updateData();
		mRealDeviceOutputLabel.setText(wiiMoteData.getStatus());
		return result;
	}

	public Vector getWiiMoteVector() {
		return wiiMoteData.getVector();
	}

	public boolean isThinkpadUsed() {
		return mRealDeviceThinkpad.isSelected();
	}

	public boolean isWiiUsed() {
		return mRealDeviceWiimote.isSelected();
	}

	public void setBackground(Color color) {
		mRealDeviceOutputLabel.setBackground(color);
	}

	public String getRealDevicePath() {
		return mRealDevicePath.getText();
	}

	public void setOutput(String message) {
		mRealDeviceOutputLabel.setText(message);
	}
}
