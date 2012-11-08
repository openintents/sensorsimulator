package org.openintents.tools.simulator.comm;

import org.openintents.tools.simulator.model.sensors.SensorType;

/**
 * Temporary interface to reflect old API.
 * 
 * @author Qui Don Ho
 * 
 */
public interface SensorDataSource {

	public String[] getSupportedSensors();

	public int getNumSensorValues(SensorType sensorType);

	public void setSensorUpdateDelay(SensorType sensorType, int updateDelay)
			throws IllegalArgumentException;

	public void unsetSensorUpdateRate(SensorType sensorType)
			throws IllegalStateException;

	public String readSensor(SensorType sensorType);
}
