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
package org.openintents.sensorsimulator.hardware;

/**
 * Class that represents SensorEventListener of
 * android.hardware.SensorEventListener. This class represents listener and it
 * needs to be created because android's SensorEventListener doesn't recognize
 * our Sensor and SensorEvent classes.
 * 
 * @author Josip Balic
 * 
 */
public interface SensorEventListener {

	/**
	 * Method that represents onAccuracyChanged, this method is currently not
	 * used in sensor simulations.
	 * 
	 * @param sensor
	 *            , Sensor that we want to change accuracy.
	 * @param accuracy
	 *            , integer of new accuracy.
	 */
	public abstract void onAccuracyChanged(Sensor sensor, int accuracy);

	/**
	 * Method onSensorChanged is called when new SensorEvent with new input is
	 * created.
	 * 
	 * @param event
	 *            , SensorEvent that holds new input variables.
	 */
	public abstract void onSensorChanged(SensorEvent event);

}
