package org.openintents.tools.simulator.model.sensor.sensors;

import java.io.PrintWriter;
import java.text.DecimalFormat;

public class TricorderModel extends SensorModel {
	@Override
	public String getName() {
		return SensorModel.TRICORDER;
	}

	@Override
	public void updateSensorReadoutValues() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getAverageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printNumValues(PrintWriter out) {
		out.println("3");
	}

	@Override
	public void printSensorData(PrintWriter out) {
		// TODO
	}

	@Override
	public void setUpdateRates() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSI() {
		return "";
	}
}
