package org.openintents.tools.simulator.comm;

import com.openintents.sensorsimulator.testlibrary.Sensor;

/**
 * Temporary interface to reflect old API.
 * 
 * @author Qui Don Ho
 * 
 */
public interface SensorDataSource {

	public String[] getSupportedSensors();

	public int getNumSensorValues(Sensor.Type sensorType);

	public void setSensorUpdateDelay(Sensor.Type sensorType, int updateDelay)
			throws IllegalArgumentException;

	public void unsetSensorUpdateRate(Sensor.Type sensorType)
			throws IllegalStateException;

	public String readSensor(Sensor.Type sensorType);
}
