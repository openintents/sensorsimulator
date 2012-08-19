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
import org.openintents.tools.simulator.model.sensor.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.sensors.GravityView;

/**
 * GravityController keeps the behaviour of the Gravity sensor (listeners, etc.)
 * 
 * gravity + linear acceleration = acceleration (from accelerometer sensor)
 * 
 * @author ilarele
 * 
 */
public class GravityController extends SensorController {

	public GravityController(final GravityModel model, GravityView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		GravityModel gravityModel = (GravityModel) mSensorModel;
		GravityView gravityView = (GravityView) mSensorView;

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
		GravityModel gravityModel = (GravityModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT.format(gravityModel.getReadGravityX())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(gravityModel
						.getReadGravityY())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(gravityModel
						.getReadGravityZ());
	}
}
