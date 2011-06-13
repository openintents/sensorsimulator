package org.openintents.tools.simulator.controller.sensor;


import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.OrientationView;

public class OrientationController extends SensorController {

	public OrientationController(OrientationModel model, OrientationView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		OrientationModel orientModel = (OrientationModel) model;
		// TODO: refresh sliders - there are listeners => no need for update
		if (orientModel.isEnabled()) {
			// Add random component:
			double random = orientModel.getRandom();
			if (random > 0) {
				orientModel.addYaw(SensorModel.getRandom(random));
				orientModel.addPitch(SensorModel.getRandom(random));
				orientModel.addRoll(SensorModel.getRandom(random));
			}
		}
	}

	@Override
	public String getString() {
		OrientationModel orientModel = (OrientationModel) model;
		return Global.TWO_DECIMAL_FORMAT.format(orientModel.getReadYaw())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(orientModel.getReadPitch())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(orientModel.getReadRoll());
	}
}
