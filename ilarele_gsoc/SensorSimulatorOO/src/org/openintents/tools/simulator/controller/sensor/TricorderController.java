package org.openintents.tools.simulator.controller.sensor;

import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.view.sensor.sensors.SensorView;

public class TricorderController extends SensorController {

	public TricorderController(SensorModel model, SensorView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}
}
