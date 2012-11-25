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

package org.openintents.tools.simulator.view.telnet.addons;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import org.openintents.tools.simulator.model.telnet.addons.BatteryAddonModel;

/**
 * BatteryAddonView keeps the GUI for Battery Addon
 * 
 * Battery add-on sets emulator battery state (via telnet communication).
 * 
 * @author Peli
 */
public class BatteryAddonView extends JPanel {
	private static final long serialVersionUID = -5834092002279084857L;

	// TelnetSimulations variables
	private JSlider mBatterySlider;

	// Battery variables
	private JCheckBox mBatteryPresence;
	private JCheckBox mBatteryAC;
	private JComboBox mBatteryStatusList;
	private JComboBox mBatteryHealthList;

	private final String[] mBatteryStatus = { "unknown", "charging",
			"discharging", "not-charging", "full" };
	private final String[] mBatteryHealth = { "unknown", "good", "overheat",
			"dead", "overvoltage", "failure" };

	// Batter file variables
	private JButton mBatteryEmulation;
	private JButton mBatteryNext;
	private JFileChooser mFileChooser;
	private JButton mOpenButton;

	public BatteryAddonView(BatteryAddonModel model) {
		mBatterySlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 50);
		mBatterySlider.setMajorTickSpacing(10);
		mBatterySlider.setMinorTickSpacing(5);
		mBatterySlider.setPaintTicks(true);
		mBatterySlider.setPaintLabels(true);
		mBatterySlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		mBatteryPresence = new JCheckBox("Is Present");
		mBatteryPresence.setSelected(true);

		mBatteryAC = new JCheckBox("AC plugged");
		mBatteryAC.setSelected(true);

		mBatteryStatusList = new JComboBox(mBatteryStatus);

		mBatteryHealthList = new JComboBox(mBatteryHealth);

		mBatteryNext = new JButton("Next time event");

		mFileChooser = new JFileChooser();
		mOpenButton = new JButton("Open a File");

		mBatteryEmulation = new JButton("Emulate Battery");
	}

	public JSlider getBatterySlider() {
		return mBatterySlider;
	}

	public JCheckBox getBatteryPresence() {
		return mBatteryPresence;
	}

	public JCheckBox getBatteryAC() {
		return mBatteryAC;
	}

	public JComboBox getBatteryStatusList() {
		return mBatteryStatusList;
	}

	public JComboBox getBatteryHealthList() {
		return mBatteryHealthList;
	}

	public void fillFileEmulationPane(JPanel batteryFilePanel) {
		batteryFilePanel.add(mOpenButton, BorderLayout.PAGE_START);
		batteryFilePanel.add(mBatteryEmulation, BorderLayout.WEST);
		batteryFilePanel.add(mBatteryNext, BorderLayout.EAST);
	}

	public JButton getBatteryEmulation() {
		return mBatteryEmulation;
	}

	public JButton getBatteryNext() {
		return mBatteryNext;
	}

	public JFileChooser getFileChooser() {
		return mFileChooser;
	}

	public JButton getOpenFileButton() {
		return mOpenButton;
	}
}
