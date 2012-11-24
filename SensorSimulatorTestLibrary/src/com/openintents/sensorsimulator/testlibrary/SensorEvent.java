package com.openintents.sensorsimulator.testlibrary;

import java.util.Arrays;

/**
 * Simple Container Class to wrap sensor data and put them in the queue for the
 * network handler. Not necessary, when handler would just process the sensor
 * event, but I did not have the time to check the "sensor - id" mapping at the
 * time.
 * 
 * @author Qui Don Ho
 * 
 */
public class SensorEvent {
	public int type;
	public int accuracy;
	public long timestamp;
	public float[] values;

	public SensorEvent(int type, int accuracy, long timestamp,
			float[] values) {
		this.type = type;
		this.accuracy = accuracy;
		this.timestamp = timestamp;
		this.values = values;
	}
	
	public SensorEvent(int type, int accuracy, float[] values) {
		this.type = type;
		this.accuracy = accuracy;
		this.values = values;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type ");
		sb.append(type);
		sb.append("\n");
		sb.append("Accuracy ");
		sb.append(accuracy);
		sb.append("\n");
		sb.append("Timestamp ");
		sb.append(timestamp);
		sb.append("\n");
		sb.append("Values ");
		sb.append(Arrays.toString(values));
		sb.append("\n");
		return sb.toString();
	}
}
