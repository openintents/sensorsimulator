package org.openintents.tools.simulator.controller.sensor;

import hr.fer.tel.simulator.Global;

import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.TemperatureModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.TemperatureView;

public class TemperatureCtrl extends SensorCtrl {

	public TemperatureCtrl(TemperatureModel model, TemperatureView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		TemperatureModel tempModel = (TemperatureModel) model;
		TemperatureView tempView = (TemperatureView) view;
		if (tempModel.isEnabled()) {
			tempModel.setTemp(tempView.getTemperature());

			// Add random component:
			double random = tempModel.getRandom();
			if (random > 0) {
				tempModel.addTemp(SensorModel.getRandom(random));
			}
		} else {
			tempModel.setTemp(0);
		}
	}

	@Override
	public String getString() {
		TemperatureModel tempModel = (TemperatureModel) model;
		return Global.TWO_DECIMAL_FORMAT.format(tempModel.getReadTemp());
	}
}
