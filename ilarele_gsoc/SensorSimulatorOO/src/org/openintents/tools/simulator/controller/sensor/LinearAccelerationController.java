package org.openintents.tools.simulator.controller.sensor;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
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
			double dt = 0.001 * delay; // from ms to s
			double k = linearAccelerationView.getSpringConstant();
			double gamma = linearAccelerationView.getDampingConstant();
			double meterperpixel = linearAccelerationView.getPixelsPerMeter();
			
			// compute normal
			if (meterperpixel != 0)
				meterperpixel = 1. / meterperpixel;
			else
				meterperpixel = 1. / 3000;

			linearAccelerationModel.refreshAcceleration(k, gamma, dt);

			Vector linearVec = new Vector(-linearAccelerationModel.getAx() * meterperpixel, 0,
					-linearAccelerationModel.getAz() * meterperpixel);
			linearVec.reverserollpitchyaw(orientation.getRoll(),
					orientation.getPitch(), orientation.getYaw());
			linearAccelerationModel.setXYZ(linearVec);			
			
			// Add random component:
			double random = linearAccelerationView.getRandom();
			if (random > 0) {
				linearAccelerationModel.addRandom(random);
			}
		} else {
			linearAccelerationModel.reset();
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
