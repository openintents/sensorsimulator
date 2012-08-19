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

package org.openintents.tools.simulator.controller.telnet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openintents.tools.simulator.model.telnet.addons.BatteryAddonModel;
import org.openintents.tools.simulator.view.telnet.addons.BatteryAddonView;

/**
 * BatteryAddonController keeps the behaviour of the battery add-on (listeners,
 * etc.)
 * 
 * Battery add-on sets emulator battery state (via telnet communication).
 * 
 * @author Peli
 * 
 */
public class BatteryAddonController {

	public BatteryAddonController(final BatteryAddonModel model,
			final BatteryAddonView view) {
		JSlider batterySlider = view.getBatterySlider();

		batterySlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				model.changePower(source.getValue());
			}
		});

		JCheckBox batteryPresence = view.getBatteryPresence();
		batteryPresence.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange()) {
				case ItemEvent.SELECTED:
					model.changePresence(true);
					break;
				case ItemEvent.DESELECTED:
					model.changePresence(false);
					break;
				}
			}
		});

		JCheckBox batteryAC = view.getBatteryAC();
		batteryAC.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange()) {
				case ItemEvent.SELECTED:
					model.changeAC(true);
					break;
				case ItemEvent.DESELECTED:
					model.changeAC(false);
					break;
				}
			}
		});

		final JComboBox batteryStatusList = view.getBatteryStatusList();
		batteryStatusList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("comboBoxChanged".equals(e.getActionCommand())) {
					model.changeStatus((String) batteryStatusList
							.getSelectedItem());
				}
			}
		});

		final JComboBox batteryHealthList = view.getBatteryHealthList();
		batteryHealthList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("comboBoxChanged".equals(e.getActionCommand())) {
					model.changeHealth((String) batteryHealthList
							.getSelectedItem());
				}
			}
		});

		JButton batteryNext = view.getBatteryNext();
		batteryNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.nextTimeEvent();
			}
		});

		final JFileChooser fileChooser = view.getFileChooser();
		JButton openButton = view.getOpenFileButton();
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(view);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					model.openFile(file);
				}
			}
		});

		JButton batteryEmulation = view.getBatteryEmulation();
		batteryEmulation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.slowEmulation(view);
			}
		});
	}
}
