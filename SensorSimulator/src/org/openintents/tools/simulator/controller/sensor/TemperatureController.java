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
import org.openintents.tools.simulator.model.sensor.sensors.TemperatureModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.TemperatureView;

/**
 * TemperatureController keeps the behaviour of the Temperature sensor
 * (listeners, etc.)
 * 
 * @author Peli
 * @author Josip Balic
 * 
 */
public class TemperatureController extends SensorController {

	public TemperatureController(TemperatureModel model, TemperatureView view) {
		super(model, view);
		registerTemperatureSlider(view);
	}

	private void registerTemperatureSlider(final TemperatureView view) {
		final JSlider temperatureSlider = view.getTemperatureSlider();
		temperatureSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int value = temperatureSlider.getValue();
				setTemperature(value);
			}
		});
	}

	protected void setTemperature(double value) {
		final TemperatureModel model = (TemperatureModel) mSensorModel;
		final TemperatureView view = (TemperatureView) mSensorView;
		if (model.getReadTemp() != value) {
			model.setTemp(value);
			view.setTemp(value);
		}
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		TemperatureModel tempModel = (TemperatureModel) mSensorModel;
		TemperatureView tempView = (TemperatureView) mSensorView;
		if (tempModel.isEnabled()) {
			setTemperature(tempView.getTemperature());

			// Add random component:
			double random = tempView.getRandom();
			if (random > 0) {
				tempModel.addTemp(SensorModel.getRandom(random));
			}
		} else {
			setTemperature(0);
		}
	}

	@Override
	public String getString() {
		TemperatureModel tempModel = (TemperatureModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT.format(tempModel.getReadTemp());
	}
}
