package org.openintents.tools.simulator.controller.sensor;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.PressureModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.PressureView;

public class PressureController extends SensorController {

	public PressureController(PressureModel model, PressureView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		PressureModel pressureModel = (PressureModel) model;
		PressureView pressureView = (PressureView) view;
		// Pressure
		if (pressureModel.isEnabled()) {
			pressureModel.setPressure(pressureView.getPressure());

			// Add random component:
			double random = pressureView.getRandom();
			if (random > 0) {
				pressureModel.addPressure(SensorModel.getRandom(random));
			}
		} else {
			pressureModel.setPressure(0);
		}
	}

	@Override
	public String getString() {
		PressureModel pressureModel = (PressureModel) model;
		return Global.TWO_DECIMAL_FORMAT
				.format(pressureModel.getReadPressure());
	}

}
