/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2011 OpenIntents.org
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

/*
 * 09/Apr/08 Dale Thatcher <openintents at dalethatcher dot com>
 *           Added wii-mote data collection.
 */

package org.openintents.tools.simulator.view.sensor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.telnet.Vector;

/**
 * Displays a mobile phone in a panel and calculates sensor physics.
 * 
 * @author Peli
 * @author Josip Balic
 */
public class DeviceView extends JPanel {

	private static final long serialVersionUID = -112203026209081563L;

	public static final int MOUSE_MODE_YAW_PITCH = 1;
	public static final int MOUSE_MODE_ROLL_PITCH = 2;
	public static final int MOUSE_MODE_MOVE = 3;

	private int mMouseMode;

	/**
	 * Reference to SensorSimulator for accessing widgets.
	 */
	private SensorSimulatorModel mSensorSimulatorModel;

	/*
	 * http://code.google.com/android/reference/android/hardware/Sensors.html
	 * 
	 * With the device lying flat on a horizontal surface in front of the user,
	 * oriented so the screen is readable by the user in the normal fashion, the
	 * X axis goes from left to right, the Y axis goes from the user toward the
	 * device, and the Z axis goes upwards perpendicular to the surface.
	 */
	// Mobile size
	private final double sx = 15; // size x
	private final double sy = 40; // size y
	private final double sz = 5; // size z

	// Display size
	private final double dx = 12; // size x
	private final double dy1 = 33; // size y
	private final double dy2 = -15;

	/** Contains the grid model of the phone. */
	private double[][] phone = {
			// bottom shape
			{ sx, sy, -sz }, { -sx, sy, -sz },
			{ -sx, sy, -sz },
			{ -sx, -sy, -sz },
			{ -sx, -sy, -sz },
			{ sx, -sy, -sz },
			{ sx, -sy, -sz },
			{ sx, sy, -sz },
			// top shape
			{ sx, sy, sz }, { -sx, sy, sz }, { -sx, sy, sz }, { -sx, -sy, sz },
			{ -sx, -sy, sz },
			{ sx, -sy, sz },
			{ sx, -sy, sz },
			{ sx, sy, sz },
			// connectint top and bottom
			{ sx, sy, -sz }, { sx, sy, sz }, { -sx, sy, -sz }, { -sx, sy, sz },
			{ -sx, -sy, -sz }, { -sx, -sy, sz },
			{ sx, -sy, -sz },
			{ sx, -sy, sz },
			// display
			{ dx, dy1, sz }, { -dx, dy1, sz }, { -dx, dy1, sz },
			{ -dx, dy2, sz }, { -dx, dy2, sz }, { dx, dy2, sz },
			{ dx, dy2, sz }, { dx, dy1, sz }, };
	private JRadioButton mRollPitchButton;
	private JRadioButton mMoveButton;
	private JRadioButton mYawPitchButton;

	private Stroke mStroke;

	// File usage

	/**
	 * Constructor of MobilePanel.
	 * 
	 * @param model
	 *            , SensorSimulator that needs MobilePanel in it's frame.
	 */
	public DeviceView(SensorSimulatorModel model) {
		mSensorSimulatorModel = model;

		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		mMouseMode = MOUSE_MODE_YAW_PITCH;

		// Add mouse action selection
		// through radio buttons.
		mYawPitchButton = new JRadioButton(SensorModel.ACTION_YAW_PITCH);
		mYawPitchButton.setSelected(true);
		mRollPitchButton = new JRadioButton(SensorModel.ACTION_ROLL_PITCH);
		mMoveButton = new JRadioButton(SensorModel.ACTION_MOVE);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(mYawPitchButton);
		group.add(mRollPitchButton);
		group.add(mMoveButton);

		// radio buttons
		add(mYawPitchButton);
		add(mRollPitchButton);
		add(mMoveButton);

		// radio buttons
		layout.putConstraint(SpringLayout.NORTH, mYawPitchButton, 10,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, mRollPitchButton, 10,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, mMoveButton, 10,
				SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, mRollPitchButton, 10,
				SpringLayout.EAST, mYawPitchButton);
		layout.putConstraint(SpringLayout.WEST, mMoveButton, 10,
				SpringLayout.EAST, mRollPitchButton);

		setDoubleBuffered(true);
		mStroke = new BasicStroke(2);
	}

	/**
	 * Method that sets size of our Mobile Panel.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Global.W_DEVICE, Global.H_DEVICE);
	}

	/**
	 * Draws the phone.
	 * 
	 * @param graphics
	 */
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		OrientationModel orientation = mSensorSimulatorModel.getOrientation();
		AccelerometerModel accelerometer = mSensorSimulatorModel
				.getAccelerometer();
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setStroke(mStroke);
		// draw Line2D.Double
		double centerx = Global.DEVICE_CENTER_X;
		double centery = Global.DEVICE_CENTER_Y;
		double centerz = Global.DEVICE_CENTER_Z;
		for (int i = 0; i < phone.length; i += 2) {
			if (i == 0) {
				g2.setColor(Global.COLOR_ENABLE_BLUE);
			}
			if (i == 24) {
				g2.setColor(Global.COLOR_ENABLE_GREEN);
			}

			Vector v1 = new Vector(phone[i]);
			Vector v2 = new Vector(phone[i + 1]);
			v1.rollpitchyaw(orientation.getRoll(), orientation.getPitch(),
					orientation.getYaw());
			v2.rollpitchyaw(orientation.getRoll(), orientation.getPitch(),
					orientation.getYaw());
			g2.draw(new Line2D.Double(centerx
					+ (v1.x + accelerometer.getMoveX()) * centerz
					/ (centerz - v1.y), centery
					- (v1.z + accelerometer.getMoveZ()) * centerz
					/ (centerz - v1.y), centerx
					+ (v2.x + accelerometer.getMoveX()) * centerz
					/ (centerz - v2.y), centery
					- (v2.z + accelerometer.getMoveZ()) * centerz
					/ (centerz - v2.y)));

		}
		if (accelerometer.isShown()) {
			// Now we also draw the acceleration:
			g2.setColor(Color.GREEN);
			Vector v1 = new Vector(0, 0, 0);
			Vector v2 = new Vector(accelerometer.getAccelx(),
					accelerometer.getAccely(), accelerometer.getAccelz());
			v2.scale(20 * accelerometer.getGInverse());
			// Vector v2 = new Vector(1, 0, 0);
			v1.rollpitchyaw(orientation.getRoll(), orientation.getPitch(),
					orientation.getYaw());
			v2.rollpitchyaw(orientation.getRoll(), orientation.getPitch(),
					orientation.getYaw());
			g2.draw(new Line2D.Double(centerx
					+ (v1.x + accelerometer.getMoveX()) * centerz
					/ (centerz - v1.y), centery
					- (v1.z + accelerometer.getMoveZ()) * centerz
					/ (centerz - v1.y), centerx
					+ (v2.x + accelerometer.getMoveX()) * centerz
					/ (centerz - v2.y), centery
					- (v2.z + accelerometer.getMoveZ()) * centerz
					/ (centerz - v2.y)));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openintents.tools.sensorsimulator.IMobilePanel#doRepaint()
	 */
	public void doRepaint() {
		repaint();
	}

	public int getMouseMode() {
		return mMouseMode;
	}

	public JRadioButton getYawPitchButton() {
		return mYawPitchButton;
	}

	public JRadioButton getRollPitchButton() {
		return mRollPitchButton;
	}

	public JRadioButton getMoveButton() {
		return mMoveButton;
	}

	public void changeMouseMode(int mode) {
		mMouseMode = mode;
	}
}
