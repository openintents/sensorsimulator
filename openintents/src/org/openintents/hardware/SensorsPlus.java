package org.openintents.hardware;

/**
 *  SensorsPlus extends the Sensors class by useful functions.
 * 
 */
public class SensorsPlus extends Sensors {

	public SensorsPlus() {
		super();
		// TODO Auto-generated constructor stub
	}

	// General useful functions that may be used with Sensors:
	/**
	 *  Check whether a specific sensor is supported.
	 *  @param sensor Sensor to be probed.
	 *  @return Whether that sensor is supported.
	 */
	public static boolean isSupportedSensor(String sensor) {
		String[] sensors = Sensors.getSupportedSensors();
		for (String s : sensors) {
			if (s.contentEquals(sensor)) return true;
		};
		return false;
	}
	
	/**
	 * Check whether a specific sensor is enabled.
	 * This is done by trying to read out a value and catch the exception
	 * if the sensor is not enabled.
	 * WARNING: If the sensor readout influences the subsequent readout result
	 * (e.g. for a pedometer that returns the number of steps since the last 
	 *  readout), this function shall not be used.
	 * @param sensor Sensor to be probed.
	 * @return Whether that sensor is enabled.
	 */
	public static boolean isEnabledSensor(String sensor) {
		try {
			// we do this by reading out a value.
			int num = getNumSensorValues(sensor);
			float[] val = new float[num];
			readSensor(sensor, val);
		} catch (IllegalStateException e) {
			// IllegalStateException occurs if the sensor has not been enabled:
			return false;
		} catch (NullPointerException e) {
			// NullPointerException: Is it returned wrongly
			// if sensor is disabled, but data validly read????
			// TODO: File issue or ask about it.
			return false;
		}
		
		// everything went fine, so the sensor must be enabled:
		return true;
	}
	
	/**
	 * Get the default sensor update rate.
	 * 
	 * This convenience routine will remember the current
	 * sensor update rate, then unset the sensor update rate,
	 * read out the sensor update rate (which will be
	 * the default rate returned by this method), and
	 * sets the sensor update rate again to the initial
	 * value.
	 * 
	 * If no default update rate is available, 0 is returned.
	 * 
	 * Exceptions connected with no update rate available
	 * are properly caught.
	 * 
	 * Exceptions connected to sensor not supported or not 
	 * enabled are not caught.
	 * 
	 * @param sensor The sensor to be probed.
	 * @return The sensor update rate after unsetting the rate.
	 */
	public static float getDefaultSensorUpdateRate(String sensor) {
		float oldValue = getSensorUpdateRate(sensor);
		unsetSensorUpdateRate(sensor);
		float defaultValue = getSensorUpdateRate(sensor);
		setSensorUpdateRate(sensor, oldValue);	
		return defaultValue;
	}
	
	// Regarding SensorSimulator
	/**
	 * Is the Sensor Simulator connected?
	 */
	public static boolean isConnectedSimulator() {
		return Sensors.mClient.connected;
	}

}
