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
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.plaf.ColorUIResource;

public class Global {
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
	public static final ImageIcon ICON_COPY = new ImageIcon(
			"pics/icon_copy.png");
	public static final ImageIcon ICON_PLAY_PAUSE = new ImageIcon(
			"pics/icon_play_pause.png");
	public static final ImageIcon ICON_STOP = new ImageIcon(
			"pics/icon_stop.png");

	public static final ImageIcon MENU_SENSOR_SIMULATOR = new ImageIcon(
			"pics/menu_sensor_simulator.png");
	public static final ImageIcon MENU_CONSOLE = new ImageIcon(
			"pics/menu_console.png");
	public static final ImageIcon MENU_SETTINGS = new ImageIcon(
			"pics/menu_settings.png");
	public static final ImageIcon MENU_HELP = new ImageIcon(
			"pics/menu_help.png");

	public static final String ICON_SENSOR_SIMULATOR_PATH = "pics/icon_sensor_simulator.png";

	// help URLs
	public static final String HELP_ONE_SENSOR_URL = "http://developer.android.com/reference/android/hardware/SensorEvent.html";
	public static final String HELP_SENSOR_SIMULATOR_DESCRIPTION_URL = "http://openintents.org/en/node/23";
	public static final String HELP_OPENINTENTS_CONTACT_URL = "http://openintents.org/en/contact";
	public static final String HELP_OPENINTENTS_FORUM_URL = "http://openintents.org/en/forum";
	public static final int WIDTH_HELP = 550;
	public static final int HEIGHT_HELP = 550;
	public static final String FILE_CONFIG_PHONE = "configPhone.txt";
	public static final int RECORDING_PORT = 9100;

	public static final float MS_IN_SECOND = 1000;

	public static final ColorUIResource BACK = new ColorUIResource(0xf0f0f0);
	public static final ColorUIResource TEXT = new ColorUIResource(0x001B2C);
	public static final ColorUIResource BUTTON = new ColorUIResource(0x95BCC4);
	public static final ColorUIResource TAB = new ColorUIResource(0x3D7399);

	// colors
	public final static Color COLOR_ENABLE_GREEN = new Color(0x33AEA4);
	public final static Color COLOR_ENABLE_BLUE = new Color(0x2095B0)
			.brighter();
	public final static Color COLOR_DISABLE = null;
	public static final Color COLOR_BORDER = Color.BLACK;

	public static final int BORDER_HSIZE = 10;
	public static final int BORDER_VSIZE = 5;

	// width & height
	public final static int W_FRAME = 1000;

	public static final int W_BUTTONS = 100;

	public static final int H_MENU = 70;
	public static final int H_TABS = 80;
	public static final int H_BUTTONS = 40;
	public static final int H_CONTENT = 370;
	public static final int H_INFO = 120;
	public static final int H_STATUS_BAR = 20;
	public static final int H_FRAME = H_MENU + H_TABS + 2 * H_BUTTONS
			+ H_CONTENT + H_STATUS_BAR + 15;

	// upper panel split percentage
	public final static double SENSOR_SPLIT_LEFT = 0.27;
	public final static double SENSOR_SPLIT_RIGHT = 1 - SENSOR_SPLIT_LEFT;

	public static final int W_DEVICE = (int) (SENSOR_SPLIT_LEFT * W_FRAME);
	public static final int H_DEVICE = 170;

	public static final int DEVICE_CENTER_X = W_DEVICE / 2;
	public static final int DEVICE_CENTER_Y = H_DEVICE / 2;
	public static final int DEVICE_CENTER_Z = -150;

	public static final int W_DEVICE_SMALL = 100;
	public static final int H_DEVICE_SMALL = 100;

	public static final int W_DEVICE_BIG = 150;
	public static final int H_DEVICE_BIG = 150;
	public static final File IMAGE_START = new File("pics/button_start.png");
	public static final File IMAGE_STOP = new File("pics/button_stop.png");
	public static final File IMAGE_CURSOR = new File("pics/icon_cursor.png");
	public static final String CONFIG_DIR = "configDir.txt";

}
