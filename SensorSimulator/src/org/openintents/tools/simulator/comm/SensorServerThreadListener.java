package org.openintents.tools.simulator.comm;

import org.openintents.tools.simulator.model.sensors.SensorType;

public interface SensorServerThreadListener {

	public String[] getSupportedSensors();

	public int getNumSensorValues(SensorType sensorType);

	public void setSensorUpdateDelay(SensorType sensorType, int updateDelay) throws IllegalArgumentException;

	public void unsetSensorUpdateRate(SensorType sensorType) throws IllegalStateException;

	public String readSensor(SensorType sensorType);
}
