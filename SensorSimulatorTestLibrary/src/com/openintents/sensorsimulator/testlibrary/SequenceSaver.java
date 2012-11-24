package com.openintents.sensorsimulator.testlibrary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;

/**
 * Takes the sensor event sequence and stores it persistently in a csv file.
 * 
 * @author Qui Don Ho
 * 
 */
public class SequenceSaver implements SequenceHandler {

	private String mDirectory;
	private String mFilename;

	public SequenceSaver() {
		this(null);
	}

	public SequenceSaver(String filename) {
		mFilename = filename;
		mDirectory = System.getProperty("user.dir");
	}

	@Override
	public void handle(Queue<SensorEvent> mSensorEvents) {
		try {
			String filename = mFilename == null ? "sensoreventsequence-"
					+ getDateTime() : mFilename;
			File file = new File(mDirectory, filename);
			BufferedWriter writer;
			writer = new BufferedWriter(new FileWriter(file));

			// go through sequence and store it in a file
			for (SensorEvent sensorEventContainer : mSensorEvents) {
				writer.write(sensorEventContainer.type + ",");
				writer.write(sensorEventContainer.accuracy + ",");
				writer.write(sensorEventContainer.timestamp + ",");
				writer.write(sensorEventContainer.values.length + "");
				for (float value : sensorEventContainer.values) {
					writer.write("," + value);
				}
				writer.write("\n");
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
