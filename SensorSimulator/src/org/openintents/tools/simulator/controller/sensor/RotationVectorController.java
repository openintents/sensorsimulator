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
import org.openintents.tools.simulator.model.sensor.sensors.RotationVectorModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.sensors.RotationVectorView;

/**
 * RotationVectorController keeps the behaviour of the Rotation Vector sensor
 * (listeners, etc.)
 * 
 * @author ilarele
 * 
 */
public class RotationVectorController extends SensorController {

	public RotationVectorController(final RotationVectorModel model,
			RotationVectorView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		RotationVectorModel rotationModel = (RotationVectorModel) mSensorModel;
		RotationVectorView rotationView = (RotationVectorView) mSensorView;

		// RotationVector
		if (rotationModel.isEnabled()) {

			Vector rotationVec = new Vector(orientation.getPitch(),
					orientation.getYaw(), orientation.getRoll());
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
		RotationVectorModel rotationModel = (RotationVectorModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT.format(rotationModel
				.getReadRotationVectorX())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(rotationModel
						.getReadRotationVectorY())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(rotationModel
						.getReadRotationVectorZ());
	}
}
