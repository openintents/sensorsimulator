package org.openintents.tools.simulator.model.sensor.sensors;

import java.awt.Color;
import java.io.PrintWriter;
import java.util.Random;

import javax.swing.JTextField;

public abstract class SensorModel {
	public static final int POZ_ACCELEROMETER = 0;
	public static final int POZ_MAGNETIC_FIELD = 1;
	public static final int POZ_ORIENTATION = 2;
	public static final int POZ_TEMPERATURE = 3;
	public static final int POZ_BARCODE_READER = 4;
	public static final int POZ_LIGHT = 5;
	public static final int POZ_PROXIMITY = 6;

	// Action Commands:
	public static String ACTION_YAW_PITCH = "yaw & pitch";
	public static String ACTION_ROLL_PITCH = "roll & pitch";
	public static String ACTION_MOVE = "move";

	// Supported sensors:
	public static final String ORIENTATION = "orientation";
	public static final String ACCELEROMETER = "accelerometer";
	public static final String TEMPERATURE = "temperature";
	public static final String MAGNETIC_FIELD = "magnetic field";
	public static final String LIGHT = "light";
	public static final String PROXIMITY = "proximity";
	public static final String TRICORDER = "tricorder";
	public static final String BARCODE_READER = "barcode reader";

	public static final String SHOW_ACCELERATION = "show acceleration";
	public static final String BINARY_PROXIMITY = "binary proximity";

	public static final String AVERAGE_ORIENTATION = "average orientation";
	public static final String AVERAGE_ACCELEROMETER = "average accelerometer";
	public static final String AVERAGE_TEMPERATURE = "average temperature";
	public static final String AVERAGE_MAGNETIC_FIELD = "average magnetic field";
	public static final String AVERAGE_LIGHT = "average light";
	public static final String AVERAGE_PROXIMITY = "average proximity";
	public static final String AVERAGE_TRICORDER = "average tricorder";

	public static final String DISABLED = "DISABLED";

	// Constant giving the unicode value of degrees symbol.
	final static public String DEGREES = "\u00B0";
	final static public String MICRO = "\u00b5";
	final static public String PLUSMINUS = "\u00b1";
	final static public String SQUARED = "\u00b2"; // superscript two

	private static Random rand = new Random();

	protected boolean mEnabled;

	// Simulation update
	protected double[] mUpdateRates;
	protected float mDefaultUpdateRate;
	protected double mCurrentUpdateRate;
	/** Whether to form an average at each update */
	protected boolean mUpdateAverage;

	// Random contribution
	protected float mRandom;

	protected boolean mIsUpdating = true;

	// for measuring updates:
	protected int updateEmulatorCount;
	protected long updateEmulatorTime;

	// Settings
	private long mRefreshCount;

	/**
	 * Duration (in milliseconds) between two updates. This is the inverse of
	 * the update rate.
	 */
	protected long updateDuration;
	/**
	 * Whether to form the average over the last duration when reading out
	 * sensors. Alternative is to just take the current value.
	 */
	protected boolean average;

	public SensorModel() {
		mEnabled = false;

		updateEmulatorCount = 0;
		updateEmulatorTime = System.currentTimeMillis();
		setUpdateRates();
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enable) {
		mEnabled = enable;
	}

	public double[] getUpdateRates() {
		return mUpdateRates;
	}

	public double getDefaultUpdateRate() {
		return mDefaultUpdateRate;
	}

	public double getCurrentUpdateRate() {
		return mCurrentUpdateRate;
	}

	public boolean updateAverage() {
		return mUpdateAverage;
	}

	public double getRandom() {
		return mRandom;
	}

	public abstract String getName();

	public abstract String getAverageName();

	/**
	 * get a random number in the range -random to +random
	 * 
	 * @param random
	 *            range of random number
	 * @return random number
	 */
	public static double getRandom(double random) {
		double val;
		val = rand.nextDouble();
		return (2 * val - 1) * random;
	}

	public double getSafeDouble(JTextField textfield) {
		return getSafeDouble(textfield, 0);
	}

	/**
	 * Safely retries the double value of a text field. If the value is not a
	 * valid number, 0 is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            TextField from which the value should be read.
	 * @param defaultValue
	 *            default value if input field is invalid.
	 * @return double value.
	 */
	public double getSafeDouble(JTextField textfield, double defaultValue) {
		double value = defaultValue;

		try {
			value = Double.parseDouble(textfield.getText());
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			value = defaultValue;
			textfield.setBackground(Color.RED);
		}
		return value;
	}

	/**
	 * Safely retries the a list of double values of a text field. If the list
	 * contains errors, null is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            TextField from which the value should be read.
	 * @return list double[] with values or null.
	 */
	public static double[] getSafeDoubleList(JTextField textfield) {
		double[] valuelist = null;
		try {
			String t = textfield.getText();
			// Now we have to split this into pieces
			String[] tlist = t.split(",");
			int len = tlist.length;
			if (len > 0) {
				valuelist = new double[len];
				for (int i = 0; i < len; i++) {
					valuelist[i] = Double.parseDouble(tlist[i]);
				}
			} else {
				valuelist = null;
			}
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			valuelist = null;
			textfield.setBackground(Color.RED);
		}
		return valuelist;
	}

	public abstract void updateSensorReadoutValues();

	public void enableSensor(PrintWriter out, boolean enable) {
		out.println("" + isEnabled());
		setEnabled(enable);
	}

	public void getNumSensorValues(PrintWriter out) {
		printNumValues(out);
	}

	protected abstract void printNumValues(PrintWriter out);

	public void getSensorUpdateRates(PrintWriter out) {
		double[] updatesList = getUpdateRates();
		if (updatesList == null || updatesList.length < 1) {
			out.println("0");
		} else {
			int len = updatesList.length;
			out.println("" + len);
			for (int i = 0; i < len; i++) {
				out.println("" + updatesList[i]);
			}
		}
	}

	public void getSensorUpdateRate(PrintWriter out) {
		if (isEnabled()) {
			double updatesPerSecond = getCurrentUpdateRate();
			out.println("" + updatesPerSecond);
		} else {
			// This sensor is currently disabled
			out.println("throw IllegalStateException");
		}
	}

	public void unsetSensorUpdateRate(PrintWriter out) {
		if (isEnabled()) {
			out.println("OK");
			mCurrentUpdateRate = getDefaultUpdateRate();
		} else {
			// This sensor is currently disabled
			out.println("throw IllegalStateException");
		}
	}

	public abstract void setUpdateRates();

	public abstract void printSensorData(PrintWriter out);

	public abstract String getSI();

	public boolean isUpdating() {
		return mIsUpdating;
	}

	public void setAvgUpdate(boolean b) {
		mUpdateAverage = b;
	}

	public void setUpdateDuration(long value) {
		updateDuration = value;
	}

	public long incUpdateEmulatorCount() {
		return ++updateEmulatorCount;

	}

	public long getRefreshCount() {
		return mRefreshCount;
	}

	public long getEmulatorTime() {
		return updateEmulatorTime;
	}

	public void setUpdateEmulatorTime(long newtime) {
		updateEmulatorTime = newtime;
	}
}
