package org.openintents.sensorsimulator.testlibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Can load a sequence from a file for sending it to an app.
 * 
 * @author Qui Don Ho
 * 
 */
public class SequenceLoader {

	public LinkedList<SensorEvent> loadGesture(String fileName) {
		BufferedReader reader = null;
		LinkedList<SensorEvent> result = new LinkedList<SensorEvent>();
		String s;
		try {
			reader = new BufferedReader(new InputStreamReader(this.getClass()
					.getClassLoader()
					.getResourceAsStream(fileName)));
			while ((s = reader.readLine()) != null) {
				String[] elements = s.split(",");
				int type = Integer.parseInt(elements[0]);
				int accuracy = Integer.parseInt(elements[1]);
				long timestamp = Long.parseLong(elements[2]);
				int length = Integer.parseInt(elements[3]);
				float[] values = new float[length];
				for (int i = 0; i < length; i++) {
					values[i] = Float.parseFloat(elements[4 + i]);
				}

				result.push(new SensorEvent(type, accuracy, timestamp,
						values));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// reverse, because file was written from top to bottom
		Collections.reverse(result);

		return result;
	}

	public LinkedList<SensorEvent> loadFile(String fileName) {
		BufferedReader reader = null;
		LinkedList<SensorEvent> result = new LinkedList<SensorEvent>();
		String s;
		try {
			reader = new BufferedReader(new FileReader(new File(
					System.getProperty("user.dir"),
					fileName)));
			while ((s = reader.readLine()) != null) {
				String[] elements = s.split(",");
				int type = Integer.parseInt(elements[0]);
				int accuracy = Integer.parseInt(elements[1]);
				long timestamp = Long.parseLong(elements[2]);
				int length = Integer.parseInt(elements[3]);
				float[] values = new float[length];
				for (int i = 0; i < length; i++) {
					values[i] = Float.parseFloat(elements[4 + i]);
				}

				result.push(new SensorEvent(type, accuracy, timestamp,
						values));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// reverse, because file was written from top to bottom
		Collections.reverse(result);

		return result;
	}
}
