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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;

/**
 * This class simulates SensorEvent of android.hardware.sensorevent. Once sensor
 * values are changed, SensorEvent object is created and it contains values,
 * type of sensor it's linked to and time when values are changed and send to
 * the sensor's listener.
 * 
 * @author Josip Balic
 * 
 */
public class SensorEvent extends Object {

	public int accuracy;
	public Sensor sensor;
	public String time;
	public float[] values = null;
	public int type;
	@SuppressWarnings("unused")
	private Context mContext;

	// BARCODE
	public String barcode;

	public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
	Calendar calendar = new GregorianCalendar();

	/**
	 * Constructor for SensorEvent. Every SensorEvent contains context of the
	 * application, values of the sensor, integer that represents sensor and
	 * time when sensor values have changed.
	 * 
	 * @param context
	 *            , Context of application it belongs to.
	 * @param values2
	 *            , float[] values that this SensorEvent contains.
	 * @param type2
	 *            , integer number of sensor that this SensorEvent is linked to.
	 */
	protected SensorEvent(Context context, float[] values2, int type2) {
		super();
		mContext = context;
		values = values2;
		type = type2;
		SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_FORMAT);
		time = dataFormat.format(calendar.getTime());
	}

	/**
	 * Since barcode sensor implementation works with String attribute and not
	 * float, new type of SensorEvent is created only for barcode reader. It
	 * contains String attribute instead of float values.
	 * 
	 * @param context
	 *            , Context of application it belongs to.
	 * @param barcode2
	 *            , String attribute that represents barcode.
	 * @param sensorType
	 *            , integer of sensor this event is linked to.
	 */
	public SensorEvent(Context context, String barcode2, int sensorType) {
		mContext = context;
		barcode = barcode2;
		type = sensorType;
	}

}
