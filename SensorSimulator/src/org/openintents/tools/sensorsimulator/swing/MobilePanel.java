/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2010 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * 09/Apr/08 Dale Thatcher <openintents at dalethatcher dot com>
 *           Added wii-mote data collection.
 */

package org.openintents.tools.sensorsimulator.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JSlider;

import org.openintents.tools.sensorsimulator.IMobilePanel;
import org.openintents.tools.sensorsimulator.ISensorSimulator;
import org.openintents.tools.sensorsimulator.Vector;

/**
 * Displays a mobile phone in a panel and calculates sensor physics.
 *
 * @author Peli
 * @author Josip Balic
 */
public class MobilePanel extends JPanel implements IMobilePanel {

	private static final long serialVersionUID = -112203026209081563L;

	/**
	 * Reference to SensorSimulator for accessing widgets.
	 */
	private ISensorSimulator mSensorSimulator;

	/**
	 * Current read-out value of accelerometer x-component.
	 *
	 * This value is updated only at the desired
	 * updateSensorRate().
	 */
	private double read_accelx;
	/** Current read-out value of accelerometer y-component. */
	private double read_accely;
	/** Current read-out value of accelerometer z-component. */
	private double read_accelz;

	/** Current read-out value of compass x-component. */
	private double read_compassx;
	/** Current read-out value of compass y-component. */
	private double read_compassy;
	/** Current read-out value of compass z-component. */
	private double read_compassz;

	/** Current read-out value of orientation yaw. */
	private double read_yaw;
	/** Current read-out value of orientation pitch. */
	private double read_pitch;
	/** Current read-out value of orientation roll. */
	private double read_roll;

	/** Current read-out value of temperature. */
	private double read_temperature;

	/**Current read-out value of barcode. */
	private String barcode_reader;

	/** Duration (in milliseconds) between two updates.
	 * This is the inverse of the update rate.
	 */
	private long accel_update_duration;

	/** Time of next update required.
	 * The time is compared to System.currentTimeMillis().
	 */
	private long accel_next_update;

	/** Duration (in milliseconds) between two updates.
	 * This is the inverse of the update rate.
	 */
	private long compass_update_duration;

	/** Time of next update required.
	 * The time is compared to System.currentTimeMillis().
	 */
	private long compass_next_update;

	/** Duration (in milliseconds) between two updates.
	 * This is the inverse of the update rate.
	 */
	private long orientation_update_duration;

	/** Time of next update required.
	 * The time is compared to System.currentTimeMillis().
	 */
	private long orientation_next_update;

	/** Duration (in milliseconds) between two updates.
	 * This is the inverse of the update rate.
	 */
	private long temperature_update_duration;

	/** Time of next update required.
	 * The time is compared to System.currentTimeMillis().
	 */
	private long temperature_next_update;

	/** Duration in milliseconds until user setting
	 * changes are read out.
	 */
	private long user_settings_duration;

	/** Time of next update for reading user settings from widgets.
	 * The time is compared to System.currentTimeMillis().
	 */
	private long user_settings_next_update;

	/**
	 * Partial read-out value of accelerometer x-component.
	 *
	 * This partial value is used to calculate the
	 * sensor average.
	 */
	private double partial_accelx;
	/** Partial read-out value of accelerometer y-component. */
	private double partial_accely;
	/** Partial read-out value of accelerometer z-component. */
	private double partial_accelz;
	/** Number of summands in partial sum for accelerometer. */
	private int partial_accel_n;
	/** Whether to form the average over the last duration when reading out sensors.
	 * Alternative is to just take the current value.
	 */
	private boolean average_accel;

	/** Partial read-out value of compass x-component. */
	private double partial_compassx;
	/** Partial read-out value of compass y-component. */
	private double partial_compassy;
	/** Partial read-out value of compass z-component. */
	private double partial_compassz;
	/** Number of summands in partial sum for compass. */
	private int partial_compass_n;
	/** Whether to form the average over the last duration when reading out sensors.
	 * Alternative is to just take the current value.
	 */
	private boolean average_compass;

	/** Partial read-out value of orientation yaw. */
	private double partial_yaw;
	/** Partial read-out value of orientation pitch. */
	private double partial_pitch;
	/** Partial read-out value of orientation roll. */
	private double partial_roll;
	/** Number of summands in partial sum for orientation. */
	private int partial_orientation_n;
	/** Whether to form the average over the last duration when reading out sensors.
	 * Alternative is to just take the current value.
	 */
	private boolean average_orientation;

	/** Partial read-out value of temperature. */
	private double partial_temperature;
	/** Number of summands in partial sum for temperature. */
	private int partial_temperature_n;
	/** Whether to form the average over the last duration when reading out sensors.
	 * Alternative is to just take the current value.
	 */
	private boolean average_temperature;


	/** Internal state value of accelerometer x-component.
	 *
	 * This value is updated regularly by updateSensorPhysics().
	 */
	private double accelx;
	/** Internal state value of accelerometer x-component. */
	private double accely;
	/** Internal state value of accelerometer x-component. */
	private double accelz;

	/** Internal state value of compass x-component. */
	private double compassx;
	/** Internal state value of compass y-component. */
	private double compassy;
	/** Internal state value of compass z-component. */
	private double compassz;

	// orientation (in degree)
	private double yaw;
	private double pitch;
	private double roll;

	// thermometer
	private double temperature;

	//barcode
	private String barcode;

	// orientation sensor raw data (in degree)
	private double yawDegree;
	private double pitchDegree;
	private double rollDegree;

	/** Current position on screen. */
	private int movex;
	/** Current position on screen. */
	private int movez;

	private int oldx;
	private int oldz;
	private double vx; // velocity
	private double vz;
	private double oldvx;
	private double oldvz;
	private double ax; // acceleration
	private double az;

	private double Fx; // force
	private double Fz; // force
	private double accx; // accelerometer position x on screen
	private double accz; // (DONT confuse with acceleration a!)

	/** Spring constant. */
	private double k; // spring constant

	/** Mass of accelerometer test particle.
	 *
	 *  This is set to 1, as only the ratio
	 *  k/m enters the simulation. */
	private double m; // mass of accelerometer test particle

	/** Damping constant. */
	private double gamma; // damping of spring

	private double dt; // time step size

	/** Inverse of screen pixel per meter */
	private double meterperpixel;

	/** Gravity constant.
	 *
	 * This takes the value 9.8 m/s^2.
	 * */
	private double g;

	/** 1/g (g-inverse) */
	private double ginverse;

	private int mousedownx;
	private int mousedowny;
	private int mousedownyaw;
	private int mousedownpitch;
	private int mousedownroll;
	private int mousedownmovex;
	private int mousedownmovez;

	private Random r;


	/*
	 * http://code.google.com/android/reference/android/hardware/Sensors.html
	 *
	 * With the device lying flat on a horizontal surface in front of the user,
	 * oriented so the screen is readable by the user in the normal fashion,
	 * the X axis goes from left to right, the Y axis goes from the user
	 * toward the device, and the Z axis goes upwards perpendicular to the
	 * surface.
	 */
	// Mobile size
	private final double sx = 15; // size x
	private final double sy = 40; // size y
	private final double sz = 5; // size z

	// Display size
	private final double dx = 12; // size x
	private final double dy1 = 33; // size y
	private final double dy2 = -15;

	/** Contains the grid model of the phone. */
	private double[][] phone = {
			// bottom shape
			{ sx, sy, -sz}, {-sx, sy, -sz},
			{-sx, sy, -sz}, {-sx,-sy, -sz},
			{-sx,-sy, -sz}, { sx,-sy, -sz},
			{ sx,-sy, -sz}, { sx, sy, -sz},
			// top shape
			{ sx, sy, sz}, {-sx, sy, sz},
			{-sx, sy, sz}, {-sx,-sy, sz},
			{-sx,-sy, sz}, { sx,-sy, sz},
			{ sx,-sy, sz}, { sx, sy, sz},
			// connectint top and bottom
			{ sx, sy, -sz}, { sx, sy, sz},
			{-sx, sy, -sz}, {-sx, sy, sz},
			{-sx,-sy, -sz}, {-sx,-sy, sz},
			{ sx,-sy, -sz}, { sx,-sy, sz},
			// display
			{ dx, dy1, sz}, {-dx, dy1, sz},
			{-dx, dy1, sz}, {-dx, dy2, sz},
			{-dx, dy2, sz}, { dx, dy2, sz},
			{ dx, dy2, sz}, { dx, dy1, sz},
		};

	/**
	 * Constructor of MobilePanel.
	 *
	 * @param newSensorSimulator, SensorSimulator that needs MobilePanel in it's frame.
	 */
	public MobilePanel(ISensorSimulator newSensorSimulator) {
		mSensorSimulator = newSensorSimulator;

		// setBorder(BorderFactory.createLineBorder(Color.black));

		yawDegree = 0;
		pitchDegree = 0;
		rollDegree = 0;

		movex = 0;
		movez = 0;
		oldx = 0;
		oldz = 0;
		oldvx = 0;
		oldvz = 0;

		Fx = 0;
		Fz = 0;
		accx = 0;
		accz = 0;
		k = 500; // spring constant
		m = 1; // mass
		gamma = 50; // damping

		dt = 0.1;
		meterperpixel = 1/3000.; // meter per pixel
		g = 9.80665; // meter per second^2
		ginverse = 1 / g;

		user_settings_duration = 500;  // Update every half second. This should be enough.
		user_settings_next_update = System.currentTimeMillis(); // First update is now.


		r = new Random();

		//this.setDoubleBuffered(true);

	    addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            //moveSquare(e.getX(),e.getY());
	        	mousedownx = e.getX();
	        	mousedowny = e.getY();
	        	mousedownyaw = mSensorSimulator.getYaw();
	        	mousedownpitch = mSensorSimulator.getPitch();
	        	mousedownroll = mSensorSimulator.getRoll();
	        	mousedownmovex = movex;
	        	mousedownmovez = movez;

	        }
	    });

	    addMouseMotionListener(new MouseMotionListener() {
	        public void mouseDragged(MouseEvent e) {
	            //moveSquare(e.getX(),e.getY());
	        	if (mSensorSimulator.getMouseMode()
	        			== ISensorSimulator.mouseYawPitch) {
		        	// Control yawDegree
		        	int newyaw = mousedownyaw - (e.getX() - mousedownx);
		        	while (newyaw > 180) newyaw -= 360;
		        	while (newyaw < -180) newyaw += 360;
		        	mSensorSimulator.setYaw(newyaw);
		        	yawDegree = newyaw;

		        	// Control pitch
		        	int newpitch = mousedownpitch - (e.getY() - mousedowny);
		        	while (newpitch > 180) newpitch -= 360;
		        	while (newpitch < -180) newpitch += 360;
		        	mSensorSimulator.setPitch(newpitch);
		        	pitchDegree = newpitch;
	        	} else if (mSensorSimulator.getMouseMode()
	        			== ISensorSimulator.mouseRollPitch) {
		        	// Control roll
		        	int newroll = mousedownroll + (e.getX() - mousedownx);
		        	while (newroll > 180) newroll -= 360;
		        	while (newroll < -180) newroll += 360;
		        	mSensorSimulator.setRoll(newroll);
		        	rollDegree = newroll;

		        	// Control pitch
		        	int newpitch = mousedownpitch - (e.getY() - mousedowny);
		        	while (newpitch > 180) newpitch -= 360;
		        	while (newpitch < -180) newpitch += 360;
		        	mSensorSimulator.setPitch(newpitch);
		        	pitchDegree = newpitch;
	        	} else if (mSensorSimulator.getMouseMode()
	        			== ISensorSimulator.mouseMove) {
		        	// Control roll
		        	int newmovex = mousedownmovex + (e.getX() - mousedownx);
		        	movex = newmovex;

		        	// Control pitch
		        	int newmovez = mousedownmovez - (e.getY() - mousedowny);
		        	movez = newmovez;
	        	}

	        	repaint();
	        }

            public void mouseMoved(MouseEvent evt) {
                // NOOP
            }
	    });
	}

	/**
	 * Updates physical model of all sensors by minimum time-step.
	 *
	 * This internal update provides the close-to-continuum
	 * description of the sensors.
	 * It does not yet provide the values that
	 * are read out by the Sensors class (which are
	 * obtained by further time-selection or averaging).
	 */
	public void updateSensorPhysics() {
		Vector vec;
		double random;

		// Update the timer if necessary:
		double newdelay;
		newdelay = mSensorSimulator.getUpdateSensors();
		if (newdelay > 0) {
			mSensorSimulator.setDelay((int) newdelay);
		}

		dt = 0.001 * mSensorSimulator.getDelay(); // from ms to s
		g = mSensorSimulator.getGravityConstant();
		if (g != 0) {
			ginverse = 1 / g;
		}
		meterperpixel = 1 / mSensorSimulator.getPixelsPerMeter();
		k = mSensorSimulator.getSpringConstant();
		gamma = mSensorSimulator.getDampingConstant();

	/*
		// Calculate velocity induced by mouse:
		double f = meterperpixel / g;
		vx = f * ((double) (movex - oldx)) / dt;
		vz = f * ((double) (movez - oldz)) / dt;

		// Calculate acceleration induced by mouse:
		ax = (vx - oldvx) / dt;
		az = (vz - oldvz) / dt;
*/
		// New physical model of acceleration:
		// Have accelerometer be steered by string.
		// We will treat this 2D only, as the rest is a linear
		// transformation, and we assume all three accelerometer
		// directions behave the same.

		// F = m * a
		// F = - k * x

		// First calculate the force acting on the
		// sensor test particle, assuming that
		// the accelerometer is mounted by a string:
		// F = - k * x
		Fx = + k * (movex - accx);
		Fz = + k * (movez - accz);

		// a = F / m
		ax = Fx / m;
		az = Fz / m;

		// Calculate velocity by integrating
		// the current acceleration.
		// Take into account damping
		// by damping constant gamma.
		// integrate dv/dt = a - v*gamma
		//vx += (ax - vx * gamma) * dt;
		//vz += (az - vz * gamma) * dt;

		vx += (ax) * dt;
		vz += (az) * dt;

		// Now this is the force that tries to adjust
		// the accelerometer back
		// integrate dx/dt = v;
		accx += vx * dt;
		accz += vz * dt;

		// We put damping here: We don't want to damp for
		// zero motion with respect to the background,
		// but with respect to the mobile phone:
		accx += gamma * (movex - accx) * dt;
		accz += gamma * (movez - accz) * dt;

		/*
		// Old values:
		oldx = movex;
		oldz = movez;
		oldvx = vx;
		oldvz = vz;
		*/

		// Calculate acceleration by gravity:
		double gravityax;
		double gravityay;
		double gravityaz;

		gravityax = mSensorSimulator.getGravityX();
		gravityay = mSensorSimulator.getGravityY();
		gravityaz = mSensorSimulator.getGravityZ();


		////
		// Now calculate this into mobile phone acceleration:
		// ! Mobile phone's acceleration is just opposite to
		// lab frame acceleration !
		vec = new Vector(-ax * meterperpixel + gravityax, gravityay, -az * meterperpixel + gravityaz);

		// we reverse roll, pitch, and yawDegree,
		// as this is how the mobile phone sees the coordinate system.
		vec.reverserollpitchyaw(rollDegree, pitchDegree, yawDegree);

		if (mSensorSimulator.isEnabledAccelerometer()) {
			if (mSensorSimulator.useRealDeviceWiimtoe()) {
				accelx = mSensorSimulator.getWiiMoteData().getX() * g;
				accely = mSensorSimulator.getWiiMoteData().getY() * g;
				accelz = mSensorSimulator.getWiiMoteData().getZ() * g;
			}
			else {
				accelx = vec.x;
				accely = vec.y;
				accelz = vec.z;

				if (mSensorSimulator.useRealDeviceThinkpad()) {
					// We will use data directly from sensor instead:

					// Read data from file
					String line = "";
					try {
					  //FileReader always assumes default encoding is OK!
					  BufferedReader input =  new BufferedReader(
							  new FileReader(mSensorSimulator.getRealDevicePath()));
					  try {
						  line = input.readLine();
					  } finally {
					    input.close();
					    //mSensorSimulator.mRealDeviceThinkpadOutputLabel.setBackground(Color.WHITE);
					  }
					}
					catch (IOException ex){
					  ex.printStackTrace();
					  //mSensorSimulator.mRealDeviceThinkpadOutputLabel.setBackground(Color.RED);
					  line = "Error reading file!";
					}

					// Show the line content:
					mSensorSimulator.setRealDeviceOutput(line);

					// Assign values

					// Create z-component (optional)

				}

				// Add random component:
				random = mSensorSimulator.getRandomAccelerometer();
				if (random > 0) {
					accelx += getRandom(random);
					accely += getRandom(random);
					accelz += getRandom(random);
				}

				// Add accelerometer limit:
				double limit = g * mSensorSimulator.getAccelerometerLimit();
				if (limit > 0) {
					// limit on each component separately, as each is
					// a separate sensor.
					if (accelx > limit) accelx = limit;
					if (accelx < -limit) accelx = -limit;
					if (accely > limit) accely = limit;
					if (accely < -limit) accely = -limit;
					if (accelz > limit) accelz = limit;
					if (accelz < -limit) accelz = -limit;
				}
			}
		} else {
			accelx = 0;
			accely = 0;
			accelz = 0;
		}

		// Calculate magnetic field:
		// Calculate acceleration by gravity:
		double magneticnorth;
		double magneticeast;
		double magneticvertical;

		if (mSensorSimulator.isEnabledMagneticField()) {
			magneticnorth = mSensorSimulator.getMagneticFieldNorth();
			magneticeast = mSensorSimulator.getMagneticFieldEast();
			magneticvertical = mSensorSimulator.getMagneticFieldVertical();

			// Add random component:
			random = mSensorSimulator.getRandomMagneticField();
			if (random > 0) {
				magneticnorth += getRandom(random);
				magneticeast += getRandom(random);
				magneticvertical += getRandom(random);
			}

			// Magnetic vector in phone coordinates:
			vec = new Vector(magneticeast, magneticnorth, -magneticvertical);
			vec.scale(0.001); // convert from nT (nano-Tesla) to �T (micro-Tesla)

			// we reverse roll, pitch, and yawDegree,
			// as this is how the mobile phone sees the coordinate system.
			vec.reverserollpitchyaw(rollDegree, pitchDegree, yawDegree);

			compassx = vec.x;
			compassy = vec.y;
			compassz = vec.z;
		} else {
			compassx = 0;
			compassy = 0;
			compassz = 0;
		}

		// Orientation is currently not affected:
		if (mSensorSimulator.isEnabledOrientation()) {
			//yaw = Math.toRadians(yawDegree);
			//pitch = Math.toRadians(pitchDegree);
			//roll = Math.toRadians(rollDegree);
			// Since OpenGL uses degree as input,
			// and it seems also more user-friendly,
			// let us stick to degree.
			// (it seems, professional sensors also use
			//  degree output.)
			yaw = yawDegree;
			pitch = pitchDegree;
			roll = rollDegree;

			// Add random component:
			random = mSensorSimulator.getRandomOrientation();
			if (random > 0) {
				yaw += getRandom(random);
				pitch += getRandom(random);
				roll += getRandom(random);
			}
		} else {
			yaw = 0;
			pitch = 0;
			roll = 0;
		}

		// Thermometer
		if (mSensorSimulator.isEnabledTemperature()) {
			temperature = mSensorSimulator.getTemperature();

			// Add random component:
			random = mSensorSimulator.getRandomTemperature();
			if (random > 0) {
				temperature += getRandom(random);
			}
		} else {
			temperature = 0;
		}

		// Barcode
		if (mSensorSimulator.isEnabledBarcodeReader()) {
			barcode = mSensorSimulator.getBarcode();
		}

		if (mSensorSimulator.isShowAcceleration()) {
			// We only have to repaint if we show the acceleration,
			// otherwise the phone does not change as long as there is
			// no user interaction.
			repaint();
		};

}

	/**
	 * Updates sensor values in time intervals as specified by updateSensorRate().
	 */
	public void updateSensorReadoutValues() {
		long currentTime = System.currentTimeMillis();

		// From time to time we

		if (average_accel) {
			// Form the average
			partial_accelx += accelx;
			partial_accely += accely;
			partial_accelz += accelz;
			partial_accel_n++;
		}

		if (average_compass) {
			// Form the average
			partial_compassx += compassx;
			partial_compassy += compassy;
			partial_compassz += compassz;
			partial_compass_n++;
		}

		if (average_orientation) {
			// Form the average
			partial_yaw += yaw;
			partial_pitch += pitch;
			partial_roll += roll;
			partial_orientation_n++;
		}

		if (average_temperature) {
			// Form the average
			partial_temperature += temperature;
			partial_temperature_n++;
		}

		if (currentTime >= accel_next_update) {
			// Update
			accel_next_update += accel_update_duration;
			if (accel_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				accel_next_update = currentTime;
			}

			if (average_accel) {
				// form average
				read_accelx = partial_accelx / partial_accel_n;
				read_accely = partial_accely / partial_accel_n;
				read_accelz = partial_accelz / partial_accel_n;

				// reset average
				partial_accelx = 0;
				partial_accely = 0;
				partial_accelz = 0;
				partial_accel_n = 0;

			} else {
				// Only take current value
				read_accelx = accelx;
				read_accely = accely;
				read_accelz = accelz;
			}

		}

		if (currentTime >= compass_next_update) {
			// Update
			compass_next_update += compass_update_duration;
			if (compass_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				compass_next_update = currentTime;
			}

			if (average_compass) {
				// form average
				read_compassx = partial_compassx / partial_compass_n;
				read_compassy = partial_compassy / partial_compass_n;
				read_compassz = partial_compassz / partial_compass_n;

				// reset average
				partial_compassx = 0;
				partial_compassy = 0;
				partial_compassz = 0;
				partial_compass_n = 0;

			} else {
				// Only take current value
				read_compassx = compassx;
				read_compassy = compassy;
				read_compassz = compassz;
			}

		}

		if (currentTime >= orientation_next_update) {
			// Update
			orientation_next_update += orientation_update_duration;
			if (orientation_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				orientation_next_update = currentTime;
			}

			if (average_orientation) {
				// form average
				read_yaw = partial_yaw / partial_orientation_n;
				read_pitch = partial_pitch / partial_orientation_n;
				read_roll = partial_roll / partial_orientation_n;

				// reset average
				partial_yaw = 0;
				partial_pitch = 0;
				partial_roll = 0;
				partial_orientation_n = 0;

			} else {
				// Only take current value
				read_yaw = yaw;
				read_pitch = pitch;
				read_roll = roll;
			}

			// Normalize values:

			// Restrict pitch value to -90 to +90
			if (read_pitch < -90) {
				read_pitch = -180 - read_pitch;
				read_yaw += 180;
				read_roll += 180;
			} else if (read_pitch > 90) {
				read_pitch = 180 - read_pitch;
				read_yaw += 180;
				read_roll += 180;

			}

			// yaw from 0 to 360
			if (read_yaw < 0) {
				read_yaw = read_yaw + 360;
			}
			if (read_yaw >= 360) {
				read_yaw -= 360;
			}

			// roll from -180 to + 180
			if (read_roll >= 180) {
				read_roll -= 360;
			}

		}

		if (currentTime >= temperature_next_update) {
			// Update
			temperature_next_update += temperature_update_duration;
			if (temperature_next_update < currentTime) {
				// Don't lag too much behind.
				// If we are too slow, then we are too slow.
				temperature_next_update = currentTime;
			}

			if (average_temperature) {
				// form average
				read_temperature = partial_temperature / partial_temperature_n;

				// reset average
				partial_temperature = 0;
				partial_temperature_n = 0;

			} else {
				// Only take current value
				read_temperature = temperature;
			}

		}
	}

	/**
	 * Updates user settings by reading out the widgets.
	 * This is done after some duration
	 * set by user_settings_duration.
	 */
	public void updateUserSettings() {
		long currentTime = System.currentTimeMillis();
		double rate;

		// From time to time we get the user settings:
		if (currentTime >= user_settings_next_update) {
			// Do update
			user_settings_next_update += user_settings_duration;
			if (user_settings_next_update < currentTime) {
				// Skip time if we are already behind:
				user_settings_next_update = System.currentTimeMillis();
			}

			average_accel = mSensorSimulator.updateAverageAccelerometer();
			rate = mSensorSimulator.getCurrentUpdateRateAccelerometer();
			if (rate != 0) {
				accel_update_duration = (long) (1000. / rate);
			} else {
				accel_update_duration = 0;
			}

			average_compass = mSensorSimulator.updateAverageCompass();
			rate = mSensorSimulator.getCurrentUpdateRateCompass();
			if (rate != 0) {
				compass_update_duration = (long) (1000. / rate);
			} else {
				compass_update_duration = 0;
			}

			average_orientation = mSensorSimulator.updateAverageOrientation();
			rate = mSensorSimulator.getCurrentUpdateRateOrientation();
			if (rate != 0) {
				orientation_update_duration = (long) (1000. / rate);
			} else {
				orientation_update_duration = 0;
			}

			average_temperature = mSensorSimulator.updateAverageThermometer();
			rate = mSensorSimulator.getCurrentUpdateRateThermometer();
			if (rate != 0) {
				temperature_update_duration = (long) (1000. / rate);
			} else {
				temperature_update_duration = 0;
			}
		}
	}

	/**
	 * get a random number in the range -random to +random
	 *
	 * @param random range of random number
	 * @return random number
	 */
	private double getRandom(double random) {
		double val;
		val = r.nextDouble();
		return (2*val - 1) * random;
	}

	/**
	 * Method that sets size of our Mobile Panel.
	 */
    public Dimension getPreferredSize() {
        return new Dimension(250,200);
    }

    /**
     * Draws the phone.
     * @param graphics
     */
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        // g.drawString("This is my custom Panel!",(int)yawDegree,(int)pitch);

        Graphics2D g2 = (Graphics2D) graphics;
        // draw Line2D.Double

        double centerx = 100;
        double centery = 100;
        double centerz = -150;
        for (int i=0; i<phone.length; i+=2) {
        	if (i==0) g2.setColor(Color.RED);
        	if (i==24) g2.setColor(Color.BLUE);

        	Vector v1 = new Vector(phone[i]);
        	Vector v2 = new Vector(phone[i+1]);
        	v1.rollpitchyaw(rollDegree, pitchDegree, yawDegree);
        	v2.rollpitchyaw(rollDegree, pitchDegree, yawDegree);
            g2.draw(new Line2D.Double(
            		centerx + (v1.x + movex) * centerz / (centerz - v1.y),
            		centery - (v1.z + movez) * centerz / (centerz - v1.y),
            		centerx + (v2.x + movex) * centerz / (centerz - v2.y),
            		centery - (v2.z + movez) * centerz / (centerz - v2.y)));
        }

        if (mSensorSimulator.isShowAcceleration()) {
	        // Now we also draw the acceleration:
	        g2.setColor(Color.GREEN);
	    	Vector v1 = new Vector(0,0,0);
	    	Vector v2 = new Vector(accelx, accely, accelz);
	    	v2.scale(20 * ginverse);
	        //Vector v2 = new Vector(1, 0, 0);
	    	v1.rollpitchyaw(rollDegree, pitchDegree, yawDegree);
	    	v2.rollpitchyaw(rollDegree, pitchDegree, yawDegree);
	    	g2.draw(new Line2D.Double(
	        		centerx + (v1.x + movex) * centerz / (centerz - v1.y),
	        		centery - (v1.z + movez) * centerz / (centerz - v1.y),
	        		centerx + (v2.x + movex) * centerz / (centerz - v2.y),
	        		centery - (v2.z + movez) * centerz / (centerz - v2.y)));

        }
    }

    /* (non-Javadoc)
     * @see org.openintents.tools.sensorsimulator.IMobilePanel#doRepaint()
     */
    public void doRepaint() {
    	repaint();
    }

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadAccelerometerX()
	 */
	public double getReadAccelerometerX() {
		return read_accelx;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadAccelerometerY()
	 */
	public double getReadAccelerometerY() {
		return read_accely;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadAccelerometerZ()
	 */
	public double getReadAccelerometerZ() {
		return read_accelz;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadCompassX()
	 */
	public double getReadCompassX() {
		return read_compassx;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadCompassY()
	 */
	public double getReadCompassY() {
		return read_compassy;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadCompassZ()
	 */
	public double getReadCompassZ() {
		return read_compassz;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadYaw()
	 */
	public double getReadYaw() {
		return read_yaw;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadPitch()
	 */
	public double getReadPitch() {
		return read_pitch;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadRoll()
	 */
	public double getReadRoll() {
		return read_roll;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getReadTemperature()
	 */
	public double getReadTemperature() {
		return read_temperature;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#getBarcode()
	 */
	public String getBarcode() {
		return barcode;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#setYawDegree(double)
	 */
	public void setYawDegree(double yawDegree) {
		this.yawDegree = yawDegree;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#setPitchDegree(double)
	 */
	public void setPitchDegree(double pitchDegree) {
		this.pitchDegree = pitchDegree;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#setRollDegree(double)
	 */
	public void setRollDegree(double rollDegree) {
		this.rollDegree = rollDegree;
	}

}
