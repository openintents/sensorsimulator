package org.openintents.tools.simulator.model;

/**
 * Defines an API for a specific sensor state. A state consists of a set of
 * values of all sensors.
 * 
 * @author donat3llo
 * 
 */
public interface SensorState {

	float getTemperature();

	float getLight();

	float getProximity();

	float getPressure();

	float[] getGravity();

	float[] getLinearAcceleration();

	float[] getOrientation();

	float[] getAccelerometer();

	float[] getMagneticField();

	float[] getRotationVector();

	float[] getGyroscope();

}
