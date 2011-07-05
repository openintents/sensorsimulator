package org.openintents.tools.simulator.controller.sensor;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.ProximityModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.ProximityView;

public class ProximityController extends SensorController {

	public ProximityController(ProximityModel model, ProximityView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		ProximityModel proximityModel = (ProximityModel) model;
		ProximityView proximityView = (ProximityView) view;
		// Proximity
		if (proximityModel.isEnabled()) {
			proximityModel.setProximity(proximityView.getProximity());

			// Add random component:
			double random = proximityView.getRandom();
			if (random > 0) {
				proximityModel.addProximity(SensorModel.getRandom(random));
			}
		} else {
			proximityModel.setProximity(0);
		}
	}

	@Override
	public String getString() {
		ProximityModel proximityModel = (ProximityModel) model;
		return Global.TWO_DECIMAL_FORMAT.format(proximityModel
				.getReadProximity());
	}
}
