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
import org.openintents.tools.simulator.model.sensor.sensors.LightModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.LightView;

/**
 * LightController keeps the behaviour of the Light sensor (listeners, etc.)
 * 
 * @author ilarele
 * 
 */
public class LightController extends SensorController {

	public LightController(LightModel model, LightView view) {
		super(model, view);
		registerLightSlider(view);
	}

	private void registerLightSlider(final LightView view) {
		final JSlider lightSlider = view.getLightSlider();
		lightSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int value = lightSlider.getValue();
				setLight(value);
			}
		});
	}

	protected void setLight(double value) {
		final LightModel model = (LightModel) mSensorModel;
		final LightView view = (LightView) mSensorView;
		if (model.getReadLight() != value) {
			model.setLight(value);
			view.setLight(value);
		}
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		LightModel lightModel = (LightModel) mSensorModel;
		LightView lightView = (LightView) mSensorView;
		// Light
		if (lightModel.isEnabled()) {
			setLight(lightView.getLight());

			// Add random component:
			double random = lightView.getRandom();
			if (random > 0) {
				lightModel.addLight(SensorModel.getRandom(random));
			}
		} else {
			setLight(0);
		}
	}

	@Override
	public String getString() {
		LightModel lightModel = (LightModel) mSensorModel;
		return Global.TWO_DECIMAL_FORMAT.format(lightModel.getReadLight());
	}

}