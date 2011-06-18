package org.openintents.tools.simulator;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;

public class Global {
	public final static int WIDTH = 1000;
	public final static int HEIGHT = 680;

	public static final double SENSOR_SPLIT_UP = 0.95;
	public static final double SENSOR_SPLIT_DOWN = 1 - SENSOR_SPLIT_UP;

	public final static double SENSOR_SPLIT_LEFT = 0.4;
	public final static double SENSOR_SPLIT_RIGHT = 1 - SENSOR_SPLIT_LEFT;

	public final static Color ENABLE = new Color(0x00BB00);
	public final static Color DISABLE = null;
	public static final Color BORDER_COLOR = Color.BLACK;
	public static final Color NOTIFY_COLOR = Color.RED;

	public static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat(
			"#0.00");
	public static final DecimalFormat ONE_DECIMAL_FORMAT = new DecimalFormat(
			"#0.0");

	public static final String ONLINE_HELP_URI = "http://www.openintents.org/en/node/23";
	
	public static final ImageIcon ICON_HELP = new ImageIcon("pics/icon_help.png");
	public static final String HELP_ONE_SENSOR_URL = "http://developer.android.com/reference/android/hardware/Sensor.html#";
}
