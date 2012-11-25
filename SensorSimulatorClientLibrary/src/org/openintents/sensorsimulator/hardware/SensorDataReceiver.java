package org.openintents.sensorsimulator.hardware;

import java.util.Observable;

/**
 * SensorDataReceiver defines the methods for a client-side message broker,
 * which is responsible for accepting sensor data from a sensor data provider.
 * The data provider can be the SensorSimulator server component, or the JUnit
 * Testcases.
 * <p>
 * A class implementing SensorDataReceiver should encapsulate the act of sensor
 * data receiving and sending them to the respected SensorEventListeners.
 * 
 * @author Qui Don Ho
 */
public abstract class SensorDataReceiver extends Observable {

	/**
	 * Connect to the sensor data provider, e.g. the SensorSimulator desktop
	 * application.
	 */
	public abstract void connect();

	/**
	 * Disconnect from the sensor data provider, e.g. the SensorSimulator
	 * desktop application.
	 */
	public abstract void disconnect();

	/**
	 * Is there a connection to the sensor data provider?
	 */
	public abstract boolean isConnected();

	/**
	 * Has connect been called already?
	 */
	public abstract boolean hasStarted();

	/**
	 * Method that registers listener for specific sensor. All sensors can't be
	 * registered through this method like they can on real device, so
	 * registration of each sensor must be done.
	 * 
	 * @param listener
	 *            , SensorEventListener for the sensor we are registering
	 * @param sensor
	 *            , Sensor we are registering
	 * @param rate
	 *            , integer rate of updates
	 * @return boolean, true of false if registration was successful
	 */
	public abstract boolean registerListener(SensorEventListener listener,
			Sensor sensor, int rate);

	/**
	 * Called to unregister from specific sensor.
	 * 
	 * @param listener
	 *            , SensorEventListener of the sensor
	 * @param sensor
	 *            , Sensor we want to unregister
	 */
	public abstract void unregisterListener(SensorEventListener listener,
			Sensor sensor);

	/**
	 * Called when we want to unregister listener and all of it's sensors.
	 * 
	 * @param listener
	 *            , SensorEventListener of listener and it's sensors we want to
	 *            unregister
	 */
	public abstract void unregisterListener(SensorEventListener listener);
}
