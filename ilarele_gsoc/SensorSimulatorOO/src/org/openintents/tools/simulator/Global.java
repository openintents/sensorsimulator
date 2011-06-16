package org.openintents.tools.simulator;

import java.awt.Color;
import java.text.DecimalFormat;

public class Global {
	public final static int WIDTH = 1000;
	public final static int HEIGHT = 680;

	public static final double SENSOR_SPLIT_UP = 0.7;
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

}
