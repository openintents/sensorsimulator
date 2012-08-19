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

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.DeviceView;
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

	public OrientationController(OrientationModel model, OrientationView view,
			DeviceView deviceView) {
		super(model, view);
		registerSliders(deviceView);
	}

	private void registerSliders(final DeviceView deviceView) {
		final OrientationModel orientModel = (OrientationModel) mSensorModel;
		final OrientationView orientView = (OrientationView) mSensorView;
		final JSlider yawSlider = orientView.getYawSlider();
		final JSlider rollSlider = orientView.getRollSlider();
		final JSlider pitchSlider = orientView.getPitchSlider();

		ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (source == yawSlider) {
					orientModel.setYaw(source.getValue());
				} else if (source == pitchSlider) {
					orientModel.setPitch(source.getValue());
				} else if (source == rollSlider) {
					orientModel.setRoll(source.getValue());
				}
				deviceView.doRepaint();
			}
		};
		yawSlider.addChangeListener(changeListener);
		pitchSlider.addChangeListener(changeListener);
		rollSlider.addChangeListener(changeListener);

		orientModel.setYaw(yawSlider.getValue());
		orientModel.setPitch(pitchSlider.getValue());
		orientModel.setRoll(rollSlider.getValue());
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

	public void setPitch(int newPitch) {
		OrientationModel orientModel = (OrientationModel) mSensorModel;
		OrientationView orientView = (OrientationView) mSensorView;
		orientModel.setPitch(newPitch);
		orientView.setPitchSlider(newPitch);
	}

	public void setYaw(int value) {
		OrientationModel orientModel = (OrientationModel) mSensorModel;
		OrientationView orientView = (OrientationView) mSensorView;
		orientModel.setYaw(value);
		orientView.setYawSlider(value);
	}

	public void setRoll(int value) {
		OrientationModel orientModel = (OrientationModel) mSensorModel;
		OrientationView orientView = (OrientationView) mSensorView;
		orientModel.setRoll(value);
		orientView.setRollSlider(value);
	}
}
