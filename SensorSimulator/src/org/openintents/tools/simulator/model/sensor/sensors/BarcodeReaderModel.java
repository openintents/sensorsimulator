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

package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;

/**
 * BarcodeReaderModel keeps the internal data model behind BarcodeReader Sensor.
 * 
 * @author Peli
 * 
 */
public class BarcodeReaderModel extends SensorModel {

	/** Current read-out value of barcode. */
	private String barcodeValue;

	public BarcodeReaderModel() {
		barcodeValue = "1234567890123";
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
	public void getNumSensorValues(PrintWriter out) {
		out.println("1");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// number of data following + data
		if (barcodeValue.length() == 13) {
			out.println("1\n" + barcodeValue);
		} else {
			out.println("1\n1");
		}
	}

	public String getBarcode() {
		return barcodeValue;
	}

	@Override
	public String getSI() {
		return "";
	}

	public void setBarcode(String value) {
		barcodeValue = value;
	}

	@Override
	public int getType() {
		return TYPE_BARCODE;
	}
}
