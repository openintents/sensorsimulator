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
import org.openintents.tools.simulator.model.sensor.sensors.PressureModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.PressureView;

/**
 * PressureController keeps the behaviour of the Pressure sensor (listeners,
 * etc.)
 * 
 * Pressure sensor can take values in [0, 1]
 * 
 * @author ilarele
 * 
 */
public class PressureController extends SensorController {

	public PressureController(PressureModel model, PressureView view) {
		super(model, view);
		registerPressureSlider(view);
	}

	private void registerPressureSlider(final PressureView view) {
		final JSlider pressureSlider = view.getPressureSlider();
		pressureSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				setPressure((double) pressureSlider.getValue() / 100);
			}
		});
	}

	protected void setPressure(double value) {
		final PressureModel model = (PressureModel) mSensorModel;
		final PressureView view = (PressureView) mSensorView;
		if (model.getReadPressure() != value) {
			model.setPressure(value);
			view.setPressure(value);
		}
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		PressureModel pressureModel = (PressureModel) mSensorModel;
		PressureView pressureView = (PressureView) mSensorView;
		// Pressure
		if (pressureModel.isEnabled()) {
			setPressure(pressureView.getPressure());

			// Add random component:
			double random = pressureView.getRandom();
			if (random > 0) {
				pressureModel.addPressure(SensorModel.getRandom(random));
			}
		} else {
			setPressure(0);
		}
	}

	@Override
	public String getString() {
		PressureModel pressureModel = (PressureModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT
				.format(pressureModel.getReadPressure());
	}

}
