package org.openintents.sensorsimulator.testlibrary;

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
public class GChartSequenceSaver implements SequenceHandler {

	private String mDirectory;
	private String mFilename;

	public GChartSequenceSaver() {
		this(null);
	}

	public GChartSequenceSaver(String filename) {
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

			writer.write("['Timestamp', 'x', 'y', 'z'],\n");
			// go through sequence and store it in a file
			for (SensorEvent sensorEventContainer : mSensorEvents) {
				// writer.write(sensorEventContainer.type + ",");
				// writer.write(sensorEventContainer.accuracy + ",");
				writer.write("[");
				writer.write("'" + sensorEventContainer.timestamp + "'");
				// writer.write(sensorEventContainer.values.length + "");
				for (float value : sensorEventContainer.values) {
					writer.write("," + value);
				}
				writer.write("],");
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
