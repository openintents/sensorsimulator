package org.openintents.sensorsimulator.hardware;

import android.hardware.SensorManager;
import android.util.Log;

/**
 * Convenience functions to extract sensor names from a sensor bit mask,
 * and the other way round.
 * 
 * @author Peli
 *
 */
public class SensorNames {

	public static final String SENSOR_ORIENTATION = "orientation";
	public static final String SENSOR_ACCELEROMETER = "accelerometer";
	public static final String SENSOR_TEMPERATURE = "temperature";
	public static final String SENSOR_MAGNETIC_FIELD = "magnetic field";
	public static final String SENSOR_LIGHT = "light";
	public static final String SENSOR_PROXIMITY = "proximity";
	public static final String SENSOR_TRICORDER = "tricorder";
	public static final String SENSOR_ORIENTATION_RAW = "orientation raw";

	/**
	 * Largest possible bit in bit field.
	 * 
	 * Note that this is different from SensorManager.SENSOR_MAX which is only 64?!?
	 */
	public static final int SENSOR_MAX_BIT = 128;
	
	/**
	 * Convert a sensor bit into a sensor name.
	 * @param sensors
	 * @return
	 */
	public static String getSensorName(int sensorbit) {
		switch(sensorbit) {
		case SensorManager.SENSOR_ORIENTATION:
			return SensorNames.SENSOR_ORIENTATION;
		case SensorManager.SENSOR_ACCELEROMETER:
			return SensorNames.SENSOR_ACCELEROMETER;
		case SensorManager.SENSOR_TEMPERATURE:
			return SensorNames.SENSOR_TEMPERATURE;
		case SensorManager.SENSOR_MAGNETIC_FIELD:
			return SensorNames.SENSOR_MAGNETIC_FIELD;
		case SensorManager.SENSOR_LIGHT:
			return SensorNames.SENSOR_LIGHT;
		case SensorManager.SENSOR_PROXIMITY:
			return SensorNames.SENSOR_PROXIMITY;
		case SensorManager.SENSOR_TRICORDER:
			return SensorNames.SENSOR_TRICORDER;
		case SensorManager.SENSOR_ORIENTATION_RAW:
			return SensorNames.SENSOR_ORIENTATION_RAW;
		default:
			Log.d(SensorSimulatorClient.TAG, "readSensor: Unknown sensor type " + sensorbit);
			return null;
		}
	}
	
	/** 
	* Count the number of sensors.
	* @return number of sensors.
	*/
	public static int getSensorCount(int sensors) {
		int count = 0;
		for (int bit = 1; bit <= SENSOR_MAX_BIT; bit <<= 1) {
			if ((sensors & bit) != 0) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Returns a string of names from a bit mask.
	 * @return List of sensor names.
	 */
	public static String[] getSensorNames(int sensors) {
		int num = getSensorCount(sensors);
		String[] s = new String[num];
		int pos = 0;
		for (int bit = 1; bit <= SENSOR_MAX_BIT; bit <<= 1) {
			if ((sensors & bit) != 0) {
				s[pos] = getSensorName(bit);
				pos++;
			}
		}
		return s;
	}
	
	
	/**
	 * Convert a list of sensor names into the bit field specifying the sensors.
	 * @param sensornames
	 * @return
	 */
	public static int getSensorsFromNames(String[] sensornames) {
		int sensors = 0;
		
		for (int i = 0; i < sensornames.length; i++ ) {
			for (int bit = 1; bit <= SENSOR_MAX_BIT; bit <<= 1) {
				if (sensornames[i].equals(getSensorName(bit))) {
					sensors |= bit;
				}
			}
		}
		return sensors;
	}
	
	/**
	 * Returns the number of values a specific sensor returns.
	 * 
	 * @param sensor Sensor ID.
	 * @return Number of values returned.
	 */
	public static int getNumSensorValues(int sensor) {
		switch(sensor) {
		case SensorManager.SENSOR_ORIENTATION:
		case SensorManager.SENSOR_ACCELEROMETER:
		case SensorManager.SENSOR_MAGNETIC_FIELD:
		case SensorManager.SENSOR_TRICORDER:
		case SensorManager.SENSOR_ORIENTATION_RAW:
			return 3;
		case SensorManager.SENSOR_TEMPERATURE:
		case SensorManager.SENSOR_LIGHT:
		case SensorManager.SENSOR_PROXIMITY:
			return 1;
		default:
			Log.d(SensorSimulatorClient.TAG, "getNumSensorValues: Unknown sensor type " + sensor);
			return 0;
		}
	}

}
