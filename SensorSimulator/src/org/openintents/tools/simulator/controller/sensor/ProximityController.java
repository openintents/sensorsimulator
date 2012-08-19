/*
 * Copyright (C) 2008 - 2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.tools.simulator.controller.sensor;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.ProximityModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.ProximityView;

/**
 * ProximityController keeps the behaviour of the Proximity sensor (listeners,
 * etc.)
 * 
 * @author Emaad Ahmed Manzoor
 * @author ilarele
 * 
 */
public class ProximityController extends SensorController {

	public ProximityController(ProximityModel model, ProximityView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		ProximityModel proximityModel = (ProximityModel) mSensorModel;
		ProximityView proximityView = (ProximityView) mSensorView;
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
		ProximityModel proximityModel = (ProximityModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT.format(proximityModel
				.getReadProximity());
	}
}
