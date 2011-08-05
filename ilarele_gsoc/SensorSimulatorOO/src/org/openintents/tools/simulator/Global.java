/*
 * Copyright (C) 2008 - 2011 OpenIntents.org
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

package org.openintents.tools.simulator;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;

public class Global {
	// main window size
	public final static int WIDTH = 1000;
	public final static int HEIGHT = 600;

	// main window split percentage
	public static final double SENSOR_SPLIT_UP = 0.42;
	public static final double SENSOR_SPLIT_DOWN = 1 - SENSOR_SPLIT_UP;

	// upper panel split percentage
	public final static double SENSOR_SPLIT_LEFT = 0.4;
	public final static double SENSOR_SPLIT_RIGHT = 1 - SENSOR_SPLIT_LEFT;

	// colors
	public final static Color COLOR_ENABLE = new Color(0x00BB00);
	public final static Color COLOR_DISABLE = null;
	public static final Color COLOR_BORDER = Color.BLACK;
	public static final Color COLOR_NOTIFY = Color.RED;

	// decimal formats
	public static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat(
			"#0.00");
	public static final DecimalFormat ONE_DECIMAL_FORMAT = new DecimalFormat(
			"#0.0");

	// icons
	public static final ImageIcon ICON_HELP = new ImageIcon(
			"pics/icon_help.png");
	public static final ImageIcon ICON_EDIT = new ImageIcon(
			"pics/icon_edit.png");
	public static final ImageIcon ICON_ADD = new ImageIcon("pics/icon_add.png");
	public static final ImageIcon ICON_DELETE = new ImageIcon(
			"pics/icon_delete.png");

	// help URLs
	public static final String HELP_ONE_SENSOR_URL = "http://developer.android.com/reference/android/hardware/SensorEvent.html";
	public static final String HELP_SENSOR_SIMULATOR_DESCRIPTION_URL = "http://openintents.org/en/node/23";
	public static final String HELP_OPENINTENTS_CONTACT_URL = "http://openintents.org/en/contact";
	public static final String HELP_OPENINTENTS_FORUM_URL = "http://openintents.org/en/forum";
	public static final int WIDTH_HELP = 550;
	public static final int HEIGHT_HELP = 550;
	public static final String FILE_CONFIG_PHONE = "configPhone.txt";
	public static final int RECORDING_PORT = 9100;

	public static final int DEVICE_WIDTH = (int) (SENSOR_SPLIT_LEFT * WIDTH);
	public static final int DEVICE_HEIGHT = 160;

	public static final double DEVICE_CENTER_X = 125;
	public static final double DEVICE_CENTER_Y = 100;
	public static final double DEVICE_CENTER_Z = -150;

	public static final double SCENARIO_SPLIT_LEFT = 0.55;

	public static final int DEVICE_WIDTH_SMALL = 100;
	public static final int DEVICE_HEIGHT_SMALL = 100;

	public static final int DEVICE_WIDTH_BIG = 100;
	public static final int DEVICE_HEIGHT_BIG = 100;
	public static final float INTERPOLATION_DISTANCE = 0.5f;

}
