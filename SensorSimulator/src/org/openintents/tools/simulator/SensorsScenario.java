/*
 * Copyright (C) 2011 OpenIntents.org
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

package org.openintents.tools.simulator;

import org.openintents.tools.simulator.controller.SensorsScenarioController;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.view.SensorsScenarioView;

/**
 * A scenario means a list of device states, each state being characterized by
 * its sensors values.
 * 
 * SensorScenario class implements the capability of creating/editing/saving/loading 
 * and recording/playing a scenario. 
 * 		When creating/editing, current simulator state (sensors values) can be used.
 * 		For saving/loading it is used an XML file.
 * 		Recording is made from a real device.
 * 		When playing, the main states values are interpolated for obtaining smooth
 * 			results. Time between main states and between interpolated states can be set
 * 			by the user, in order to provide an accurate playback.
 * 
 * Supported sensors: accelerometer, linear acceleration,
 * gravity, orientation, magnetic field, temperature, proximity, pressure,
 * light, gyroscope, rotation vector.
 * 
 * SensorScenario class also follows model-view-controller design.
 * 
 * @author ilarele
 */
public class SensorsScenario {
	public SensorsScenarioModel model;
	public SensorsScenarioView view;
	public SensorsScenarioController controller;

	public SensorsScenario() {
		model = new SensorsScenarioModel(this);
		view = new SensorsScenarioView(model);
		controller = new SensorsScenarioController(model, view);
	}

	public void setSimulator(SensorSimulator sensorSimulator) {
		model.setSensorSimulatorModel(sensorSimulator.model);
		controller.setSensorSimulatorController(sensorSimulator.controller);
	}
}
