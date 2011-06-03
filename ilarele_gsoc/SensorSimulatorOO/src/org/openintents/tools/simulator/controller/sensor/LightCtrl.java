package org.openintents.tools.simulator.controller.sensor;

import hr.fer.tel.simulator.Global;

import org.openintents.tools.simulator.model.sensor.sensors.LightModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.view.sensor.sensors.LightView;

public class LightCtrl extends SensorCtrl {

	public LightCtrl(LightModel model, LightView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		LightModel lightModel = (LightModel) model;
		LightView lightView = (LightView) view;
		// Light
		if (lightModel.isEnabled()) {
			lightModel.setLight(lightView.getLight());

			// Add random component:
			double random = lightModel.getRandom();
			if (random > 0) {
				lightModel.addLight(SensorModel.getRandom(random));
			}
		} else {
			lightModel.setLight(0);
		}
	}

	@Override
	public String getString() {
		LightModel lightModel = (LightModel) model;
		return Global.TWO_DECIMAL_FORMAT.format(lightModel.getLight());
	}

}