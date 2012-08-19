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
import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.sensors.LinearAccelerationView;

/**
 * LinearAccelerationController keeps the behaviour of the LinearAcceleration
 * sensor (listeners, etc.)
 * 
 * gravity + linear acceleration = acceleration (from accelerometer sensor)
 * 
 * @author ilarele
 * 
 */
public class LinearAccelerationController extends SensorController {

	public LinearAccelerationController(final LinearAccelerationModel model,
			LinearAccelerationView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		LinearAccelerationModel linearAccelerationModel = (LinearAccelerationModel) mSensorModel;
		LinearAccelerationView linearAccelerationView = (LinearAccelerationView) mSensorView;

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

			Vector linearVec = new Vector(-linearAccelerationModel.getAx()
					* meterperpixel, 0, -linearAccelerationModel.getAz()
					* meterperpixel);
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
		LinearAccelerationModel linearAccModel = (LinearAccelerationModel) mSensorModel;
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
