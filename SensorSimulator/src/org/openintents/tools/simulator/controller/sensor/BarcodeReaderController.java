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

import org.openintents.tools.simulator.model.sensors.BarcodeReaderModel;
import org.openintents.tools.simulator.model.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.SensorSimulatorView;
import org.openintents.tools.simulator.view.sensor.sensors.BarcodeReaderView;

/**
 * BarcodeReader keeps the behaviour of the BarcodeReader sensor (listeners,
 * etc.)
 * 
 * @author Peli
 * 
 */
public class BarcodeReaderController extends SensorController {

	public BarcodeReaderController(BarcodeReaderModel model,
			BarcodeReaderView view, SensorSimulatorView sensorSimulatorView) {
		super(model, view, sensorSimulatorView);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		// BarcodeReaderModel barModel = (BarcodeReaderModel) mSensorModel;
		// BarcodeReaderView barView = (BarcodeReaderView) mSensorView;
		// // Barcode
		// if (barModel.isEnabled()) {
		// barModel.setBarcode(barView.getBarcode());
		// }
	}

	@Override
	public String getString() {
		BarcodeReaderModel barModel = (BarcodeReaderModel) mSensorModel;
		return barModel.getBarcode();
	}
}
