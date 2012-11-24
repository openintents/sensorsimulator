package org.openintents.tools.simulator.comm;

import java.util.Map;

import com.openintents.sensorsimulator.testlibrary.Sensor;

public interface SensorEventProducer {

	/**
	 * Take sensor rate registrations and return the delays.
	 * 
	 * @param sensorRates
	 *            Map: SensorType - Rate (FASTEST, GAME...)
	 * @return Map: SensorType(int) - update speed(ms)
	 */
	public abstract Map<Integer, Integer> registerSensors(
			Map<Sensor.Type, Integer> sensorRates);

}