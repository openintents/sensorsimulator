/**
 *
 */
package org.openintents.tools.sensorsimulator;

/**
 *
 * @author Lee Sanghoon
 */
public interface IMobilePanel {

	public void doRepaint();

	public void updateSensorPhysics();
	public void updateSensorReadoutValues();
	public void updateUserSettings();

	public double getReadAccelerometerX();
	public double getReadAccelerometerY();
	public double getReadAccelerometerZ();

	public double getReadCompassX();
	public double getReadCompassY();
	public double getReadCompassZ();

	public double getReadYaw();
	public double getReadPitch();
	public double getReadRoll();

	public double getReadTemperature();

	public String getBarcode();

	public void setYawDegree(double yawDegree);
	public void setPitchDegree(double pitchDegree);
	public void setRollDegree(double rollDegree);

}
