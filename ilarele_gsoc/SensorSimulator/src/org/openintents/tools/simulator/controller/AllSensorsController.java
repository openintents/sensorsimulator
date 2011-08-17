/*
 * Copyright (C) 2011 OpenIntents.org
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
package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;

import org.openintents.tools.simulator.controller.sensor.SensorController;
import org.openintents.tools.simulator.util.PhoneSensors;
import org.openintents.tools.simulator.view.sensor.AllSensorsView;

/**
 * The controller of the view that allows you to select the
 * enabled sensors.
 * @author ilarele
 *
 */
public class AllSensorsController {
	private AllSensorsView mView;
	private Vector<SensorController> mSensors;

	public AllSensorsController(final AllSensorsView view,
			Vector<SensorController> sensors) {
		mView = view;
		mSensors = sensors;
		final JComboBox comboBox = view.getSensorsComboBox();
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String chosenValue = (String) cb.getSelectedItem();
				setEnabledSensors(view.getPhoneSensors().get(chosenValue));
			}
		});
	}

	protected void setEnabledSensors(PhoneSensors phoneSensors) {
		Vector<SensorController> localSensors = mSensors;
		for (SensorController sensor : localSensors) {
			if (phoneSensors.sensors.contains(sensor.getName())) {
				sensor.setEnable(true);
			} else {
				sensor.setEnable(false);
			}
		}
	}
}