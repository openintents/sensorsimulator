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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.DeviceView;
import org.openintents.tools.simulator.view.sensor.sensors.AccelerometerView;

/**
 * AccelerometerController keeps the behaviour of the Accelerometer sensor
 * (listeners, etc.)
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class AccelerometerController extends SensorController {
	@SuppressWarnings("unused")
	private WiiAccelerometerController wiiAccelerometerCtrl;

	public AccelerometerController(final AccelerometerModel model,
			AccelerometerView view) {
		super(model, view);
		wiiAccelerometerCtrl = new WiiAccelerometerController(
				model.getRealDeviceBridgeAddon(),
				view.getRealDeviceBridgeAddon());
	}

	/**
	 * It is used to control "show Acceleration" in the device representation.
	 * 
	 * @param mobile
	 */
	public void setMobile(final DeviceView mobile) {
		AccelerometerView accView = (AccelerometerView) mSensorView;
		accView.getShowAcceleration().addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// Refresh the screen when this drawing element
				// changes
				mobile.doRepaint();
			}
		});
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		AccelerometerModel accModel = (AccelerometerModel) mSensorModel;
		AccelerometerView accView = (AccelerometerView) mSensorView;

		double g = accView.getGravityConstant();

		// get component vectors (gravity + linear_acceleration)
		Vector gravityVec = getGravityVector(accModel, accView, orientation, g);
		Vector linearVec = getLinearAccVector(accModel, accView, orientation,
				delay);

		JCheckBox showAcc = accView.getShow();
		accModel.setShown(showAcc.isSelected());
		Vector resultVec = Vector.addVectors(gravityVec, linearVec);
		if (accModel.isEnabled()) {
			if (realDeviceBridgeAddon.isUsed()) {
				Vector wiiVector = realDeviceBridgeAddon.getWiiMoteVector();
				accModel.setXYZ(wiiVector);
			} else {
				accModel.setXYZ(resultVec);
				// Add random component:
				double random = accView.getRandom();
				if (random > 0) {
					accModel.addRandom(random);
				}

				// Add accelerometer limit:
				double limit = g * accView.getAccelerometerLimit();
				if (limit > 0) {
					// limit on each component separately, as each is
					// a separate sensor.
					accModel.limitate(limit);
				}
			}
		} else {
			accModel.reset();
		}

	}

	private Vector getLinearAccVector(AccelerometerModel accModel,
			AccelerometerView accView, OrientationModel orientation, int delay) {
		Vector linearVec;
		double meterperpixel;
		double dt = 0.001 * delay; // from ms to s
		double k = accView.getSpringConstant();
		double gamma = accView.getDampingConstant();

		meterperpixel = accView.getPixelsPerMeter();
		// compute normal
		if (meterperpixel != 0)
			meterperpixel = 1. / meterperpixel;
		else
			meterperpixel = 1. / 3000;

		accModel.refreshAcceleration(k, gamma, dt);

		// Now calculate this into mobile phone acceleration:
		// ! Mobile phone's acceleration is just opposite to
		// lab frame acceleration !
		linearVec = new Vector(-accModel.getAx() * meterperpixel, 0,
				-accModel.getAz() * meterperpixel);
		linearVec.reverserollpitchyaw(orientation.getRoll(),
				orientation.getPitch(), orientation.getYaw());
		return linearVec;
	}

	private Vector getGravityVector(AccelerometerModel accModel,
			AccelerometerView accView, OrientationModel orientation, double g) {
		Vector gravityVec;
		// apply orientation
		// we reverse roll, pitch, and yawDegree,
		// as this is how the mobile phone sees the coordinate system.
		gravityVec = new Vector(0, 0, g);
		gravityVec.reverserollpitchyaw(orientation.getRoll(),
				orientation.getPitch(), orientation.getYaw());
		return gravityVec;
	}

	@Override
	public String getString() {
		AccelerometerModel accModel = (AccelerometerModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT.format(accModel
				.getReadAccelerometerX())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(accModel
						.getReadAccelerometerY())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(accModel
						.getReadAccelerometerZ());
	}
}
