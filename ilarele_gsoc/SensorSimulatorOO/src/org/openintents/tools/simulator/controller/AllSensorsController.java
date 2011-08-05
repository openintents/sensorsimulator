package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;

import org.openintents.tools.simulator.controller.sensor.SensorController;
import org.openintents.tools.simulator.view.sensor.AllSensorsView;
import org.openintents.tools.simulator.view.sensor.PhoneSensors;

public class AllSensorsController {
	private AllSensorsView mView;
	private ArrayList<SensorController> mSensors;

	public AllSensorsController(final AllSensorsView view,
			ArrayList<SensorController> sensors) {
		this.mView = view;
		this.mSensors = sensors;
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
		ArrayList<SensorController> localSensors = mSensors;
		for (SensorController sensor : localSensors) {
			if (phoneSensors.sensors.contains(sensor.getName())) {
				sensor.setEnable(true);
			} else {
				sensor.setEnable(false);
			}
		}
	}
}