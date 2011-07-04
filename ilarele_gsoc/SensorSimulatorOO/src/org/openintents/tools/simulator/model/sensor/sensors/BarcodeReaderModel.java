package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

public class BarcodeReaderModel extends SensorModel {

	// barcode
	private String barcodeValue;

	/** Current read-out value of barcode. */
	// private String barcode_reader;

	public BarcodeReaderModel() {
		super();
		barcodeValue = "1234567890123";
		mIsUpdating = false;
	}

	@Override
	public String getName() {
		return SensorModel.BARCODE_READER;
	}

	@Override
	public String toString() {
		return barcodeValue;
	}

	@Override
	public void updateSensorReadoutValues() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAverageName() {
		return null;
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("1");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		if (barcodeValue.length() == 13)
			out.println("1\n" + barcodeValue);
		else
			out.println("1\n1");
	}

	public String getBarcode() {
		return barcodeValue;
	}

	public void setUpdateRates() {
	}

	@Override
	public String getSI() {
		return "";
	}

	public void setBarcode(String value) {
		barcodeValue = value;
	}
	
	@Override
	public String getTypeConstant() {
		return "BARCODE";
	}
}
