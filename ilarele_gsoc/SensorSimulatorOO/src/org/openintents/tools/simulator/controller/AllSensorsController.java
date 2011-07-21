package org.openintents.tools.simulator.controller;

import java.util.ArrayList;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
		final JSpinner spinner = view.getSensorsSpinner();
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				String chosenValue = (String) spinner.getModel().getValue();
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