package org.openintents.tools.simulator.comm;

import java.util.Arrays;

/**
 * Simple Container Class to wrap sensor data and put them in the queue for the
 * network handler.
 * 
 * @author Qui Don Ho
 * 
 */
public class SensorEventContainer {
	public int type;
	public int accuracy;
	public float[] values;

	public SensorEventContainer(int type, int accuracy, float[] values) {
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
		sb.append("Values ");
		sb.append(Arrays.toString(values));
		sb.append("\n");
		return sb.toString();
	}
}
