package org.openintents.tools.simulator.controller.sensor;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.sensors.GravityView;

public class GravityController extends SensorController {

	public GravityController(final GravityModel model, GravityView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		GravityModel gravityModel = (GravityModel) model;
		GravityView gravityView = (GravityView) view;

		// Gravity
		if (gravityModel.isEnabled()) {
			double g = gravityView.getGravityConstant();
			Vector gravityVec = new Vector(0, 0, g);
			gravityVec.reverserollpitchyaw(orientation.getRoll(),
					orientation.getPitch(), orientation.getYaw());
			gravityModel.setGravity(gravityVec);

			// Add random component:
			double random = gravityView.getRandom();
			if (random > 0) {
				gravityModel.addGravity(SensorModel.getRandom(random),
						SensorModel.getRandom(random),
						SensorModel.getRandom(random));
			}
		} else {
			gravityModel.setGravity(0, 0, 0);
		}
	}

	@Override
	public String getString() {
		GravityModel gravityModel = (GravityModel) model;
		return Global.TWO_DECIMAL_FORMAT.format(gravityModel.getReadGravityX())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(gravityModel
						.getReadGravityY())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(gravityModel
						.getReadGravityZ());
	}
}
