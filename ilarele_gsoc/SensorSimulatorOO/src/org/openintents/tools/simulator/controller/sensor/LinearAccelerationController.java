package org.openintents.tools.simulator.controller.sensor;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.LinearAccelerationView;

public class LinearAccelerationController extends SensorController {

	public LinearAccelerationController(final LinearAccelerationModel model,
			LinearAccelerationView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		LinearAccelerationModel linearAccelerationModel = (LinearAccelerationModel) model;
		LinearAccelerationView linearAccelerationView = (LinearAccelerationView) view;

		// LinearAcceleration
		if (linearAccelerationModel.isEnabled()) {
			linearAccelerationModel.setLinearAcceleration(
					linearAccelerationView.getAccX(),
					linearAccelerationView.getAccY(),
					linearAccelerationView.getAccZ());

			// Add random component:
			double random = linearAccelerationView.getRandom();
			if (random > 0) {
				linearAccelerationModel.addLinearAcceleration(
						SensorModel.getRandom(random),
						SensorModel.getRandom(random),
						SensorModel.getRandom(random));
			}
		} else {
			linearAccelerationModel.setLinearAcceleration(0, 0, 0);
		}
	}

	@Override
	public String getString() {
		LinearAccelerationModel linearAccModel = (LinearAccelerationModel) model;
		return Global.TWO_DECIMAL_FORMAT.format(linearAccModel
				.getReadLinearAccelerationX())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(linearAccModel
						.getReadLinearAccelerationY())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(linearAccModel
						.getReadLinearAccelerationZ());
	}
}
