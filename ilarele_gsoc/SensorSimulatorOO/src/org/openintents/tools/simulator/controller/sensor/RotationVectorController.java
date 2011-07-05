package org.openintents.tools.simulator.controller.sensor;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.RotationVectorModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.sensors.RotationVectorView;

public class RotationVectorController extends SensorController {

	public RotationVectorController(final RotationVectorModel model, RotationVectorView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		RotationVectorModel rotationModel = (RotationVectorModel) model;
		RotationVectorView rotationView = (RotationVectorView) view;

		// RotationVector
		if (rotationModel.isEnabled()) {

			Vector rotationVec = new Vector(orientation.getPitch(), orientation.getYaw(), orientation.getRoll());
			rotationModel.setRotationVector(rotationVec);
			rotationView.setRotationVector(rotationVec);
			// Add random component:
			double random = rotationView.getRandom();
			if (random > 0) {
				rotationModel.addRotationVector(SensorModel.getRandom(random),
						SensorModel.getRandom(random),
						SensorModel.getRandom(random));
			}
		} else {
			rotationModel.setRotationVector(0, 0, 0);
		}
	}

	@Override
	public String getString() {
		RotationVectorModel rotationModel = (RotationVectorModel) model;
		return Global.TWO_DECIMAL_FORMAT.format(rotationModel.getReadRotationVectorX())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(rotationModel
						.getReadRotationVectorY())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(rotationModel
						.getReadRotationVectorZ());
	}
}
