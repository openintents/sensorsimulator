package org.openintents.tools.simulator.controller.sensor;

import org.openintents.tools.simulator.model.sensor.sensors.BarcodeReaderModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.BarcodeReaderView;

public class BarcodeReaderController extends SensorController {

	public BarcodeReaderController(BarcodeReaderModel model,
			BarcodeReaderView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		BarcodeReaderModel barModel = (BarcodeReaderModel) model;
		BarcodeReaderView barView = (BarcodeReaderView) view;
		// Barcode
		if (barModel.isEnabled()) {
			barModel.setBarcode(barView.getBarcode());
		}
	}

	@Override
	public String getString() {
		BarcodeReaderModel barModel = (BarcodeReaderModel) model;
		return barModel.getBarcode();
	}
}
