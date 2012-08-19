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
import org.openintents.tools.simulator.model.sensor.sensors.MagneticFieldModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.sensors.MagneticFieldView;

/**
 * MagneticFieldController keeps the behaviour of the MagneticField sensor
 * (listeners, etc.)
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class MagneticFieldController extends SensorController {

	public MagneticFieldController(MagneticFieldModel model,
			MagneticFieldView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		double magneticnorth;
		double magneticeast;
		double magneticvertical;
		MagneticFieldModel magModel = (MagneticFieldModel) mSensorModel;
		MagneticFieldView magView = (MagneticFieldView) mSensorView;

		if (magModel.isEnabled()) {
			magneticnorth = magView.getNorth();
			magneticeast = magView.getEast();
			magneticvertical = magView.getVertical();

			// Add random component:
			double random = magView.getRandom();
			if (random > 0) {
				magneticnorth += SensorModel.getRandom(random);
				magneticeast += SensorModel.getRandom(random);
				magneticvertical += SensorModel.getRandom(random);
			}
			magModel.setNorth(magneticnorth);
			magModel.setEast(magneticeast);
			magModel.setVertical(magneticvertical);

			// Magnetic vector in phone coordinates:
			Vector vec = new Vector(magneticeast, magneticnorth,
					-magneticvertical);
			vec.scale(0.001); // convert from nT (nano-Tesla) to �T
								// (micro-Tesla)

			// we reverse roll, pitch, and yawDegree,
			// as this is how the mobile phone sees the coordinate system.

			double rollDegree = orientation.getRoll();
			double pitchDegree = orientation.getPitch();
			double yawDegree = orientation.getYaw();
			vec.reverserollpitchyaw(rollDegree, pitchDegree, yawDegree);

			magModel.setCompass(vec);
		} else {
			magModel.resetCompas();
		}
	}

	@Override
	public String getString() {
		MagneticFieldModel magModel = (MagneticFieldModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT.format(magModel.getReadCompassX())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(magModel.getReadCompassY())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(magModel.getReadCompassZ());
	}

}
