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
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.OrientationView;

/**
 * OrientationController keeps the behaviour of the Orientation sensor
 * (listeners, etc.)
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class OrientationController extends SensorController {

	public OrientationController(OrientationModel model, OrientationView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		OrientationModel orientModel = (OrientationModel) mSensorModel;
		OrientationView orientView = (OrientationView) mSensorView;
		if (orientModel.isEnabled()) {
			// Add random component:
			double random = orientView.getRandom();
			if (random > 0) {
				orientModel.addYaw(SensorModel.getRandom(random));
				orientModel.addPitch(SensorModel.getRandom(random));
				orientModel.addRoll(SensorModel.getRandom(random));
			}
		}
	}

	@Override
	public String getString() {
		OrientationModel orientModel = (OrientationModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT.format(orientModel.getReadYaw())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(orientModel.getReadPitch())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(orientModel.getReadRoll());
	}
}
