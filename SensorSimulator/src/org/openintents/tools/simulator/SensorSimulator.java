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

package org.openintents.tools.simulator;

import org.openintents.tools.simulator.controller.SensorSimulatorController;
import org.openintents.tools.simulator.logging.Logg;
import org.openintents.tools.simulator.logging.LoggPrintable;
import org.openintents.tools.simulator.model.SensorSimulatorModel;
import org.openintents.tools.simulator.view.SensorSimulatorMain;
import org.openintents.tools.simulator.view.sensor.SensorSimulatorView;

/**
 * Class of SensorSimulator.
 * 
 * The SensorSimulator is a Java stand-alone application.
 * 
 * It simulates various sensors. An Android application can connect through
 * TCP/IP with the settings shown to the SensorSimulator to simulate
 * accelerometer, compass, orientation sensor, and thermometer.
 * 
 * SensorSimulator is a container that keeps all the components of the
 * SensorSimulator: the model, the view and the controller.
 * 
 * @author ilarele
 */
public class SensorSimulator implements LoggPrintable {

	public SensorSimulatorModel model;
	public SensorSimulatorView view;
	public SensorSimulatorController controller;

	public SensorsScenario scenario;
	private SensorSimulatorMain mMain;

	public SensorSimulator(SensorSimulatorMain main) {
		mMain = main;

		Logg.addLoggPrintable(this);

		scenario = new SensorsScenario();
		model = new SensorSimulatorModel();
		view = new SensorSimulatorView(model, scenario);
		controller = new SensorSimulatorController(model, view, scenario);
		scenario.setSimulator(this);
	}

	/**
	 * If no argument is provided, SensorSimulator is run "the old way", with
	 * Swing GUI. Else, the console-based version is launched.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			if (args[0].equals("t")) {
				SensorConsole sensorConsole = new SensorConsole(System.in,
						System.out);
				sensorConsole.start();
			} else {
				System.out.println("Usage: <apppname> t");
			}
		} else {
			SensorSimulatorMain.mainOld(args);
		}
	}

	public void printStatus(String status) {
		mMain.printStatus(status);
	}

	@Override
	public void println(String msg) {
		view.addMessage(msg);
	}
}
