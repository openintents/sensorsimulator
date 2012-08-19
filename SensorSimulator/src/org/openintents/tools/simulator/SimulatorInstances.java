/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2011 OpenIntents.org
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

import java.util.ArrayList;

/**
 * SensorSimulatorInstances class holds an ArrayList of all currently running
 * sensor simulator instances. This class is used to merge LIS emulator with our
 * simulators.
 * 
 * @author Josip Balic
 */
public class SimulatorInstances {

	public SensorSimulator mSimulator;

	public ArrayList<SensorSimulator> simulators;

	/**
	 * Method that adds instance of sensorSimulator to ArrayList.
	 * 
	 * @param sensorSimulator
	 *            , SensorSimulator instance we want to add to ArrayList
	 */
	public void addSimulator(SensorSimulator simulator) {
		if (simulators == null) {
			simulators = new ArrayList<SensorSimulator>();
			simulators.add(simulator);
		} else {
			simulators.add(simulator);
		}
	}

}
