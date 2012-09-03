package org.openintents.tools.simulator;

public interface SensorServerThreadListener {

	public String[] getSupportedSensors();

	public int getNumSensorValues(String sensorName);

	public void setSensorUpdateDelay(String sensorName, int updateDelay) throws IllegalArgumentException;

	public void unsetSensorUpdateRate(String sensorName) throws IllegalStateException;

	public String readSensor(String sensorName);
}
