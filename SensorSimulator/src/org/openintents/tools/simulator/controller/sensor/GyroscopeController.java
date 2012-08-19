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
import org.openintents.tools.simulator.model.sensor.sensors.GyroscopeModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.GyroscopeView;

/**
 * GyroscopeController keeps the behaviour of the Gyroscope sensor (listeners,
 * etc.)
 * 
 * 
 * @author ilarele
 * 
 */
public class GyroscopeController extends SensorController {

	public GyroscopeController(GyroscopeModel model, GyroscopeView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		GyroscopeModel gyroscopeModel = (GyroscopeModel) mSensorModel;
		GyroscopeView gyroscopeView = (GyroscopeView) mSensorView;
		// Gyroscope
		if (gyroscopeModel.isEnabled()) {
			gyroscopeModel.refreshAngularSpeed(delay,
					orientation.getReadPitch(), orientation.getReadYaw(),
					orientation.getReadRoll());
			// Add random component:
			double random = gyroscopeView.getRandom();
			if (random > 0) {
				gyroscopeModel.addRandom(SensorModel.getRandom(random));
			}
		} else {
			gyroscopeModel.setGyroscope(0, 0, 0);
		}
	}

	@Override
	public String getString() {
		GyroscopeModel gyroscopeModel = (GyroscopeModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT.format(gyroscopeModel
				.getReadGyroscopePitch())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(gyroscopeModel
						.getReadGyroscopeYaw())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(gyroscopeModel
						.getReadGyroscopeRoll());
	}
}
