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

package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JRadioButton;

import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.view.sensor.DeviceView;

/**
 * This class controls the mobile device (visual representation for the
 * orientation sensor).
 * 
 * @author Peli
 * 
 */
public class DeviceController {

	private int mMouseDownX;
	private int mMouseDownY;
	private int mMouseDownYaw;
	private int mMouseDownPitch;
	private int mMouseDownRoll;
	private int mMouseDownMoveX;
	private int mMouseDownMoveZ;

	private DeviceView mView;
	private SensorSimulatorModel mModel;

	public DeviceController(final SensorSimulatorModel model,
			final DeviceView view) {
		this.mModel = model;
		this.mView = view;

		registerMouseModeButtons();
		registerMouseListeners();
	}

	private void registerMouseListeners() {
		mView.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				OrientationModel orientation = mModel.getOrientation();
				AccelerometerModel accelerometer = mModel.getAccelerometer();
				LinearAccelerationModel linearAccelerometer = mModel
						.getLinearAcceleration();
				mMouseDownX = e.getX();
				mMouseDownY = e.getY();
				mMouseDownYaw = orientation.getYaw();
				mMouseDownPitch = orientation.getPitch();
				mMouseDownRoll = orientation.getRoll();
				if (linearAccelerometer.isEnabled()) {
					mMouseDownMoveX = linearAccelerometer.getMoveX();
					mMouseDownMoveZ = linearAccelerometer.getMoveZ();
				} else {
					mMouseDownMoveX = accelerometer.getMoveX();
					mMouseDownMoveZ = accelerometer.getMoveZ();
				}
			}
		});

		mView.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				OrientationModel orientModel = mModel.getOrientation();
				AccelerometerModel accelerModel = mModel.getAccelerometer();
				LinearAccelerationModel linearAccModel = mModel
						.getLinearAcceleration();
				int newpitch;
				switch (mView.getMouseMode()) {
				case DeviceView.MOUSE_MODE_YAW_PITCH:
					// Control yawDegree
					int newyaw = mMouseDownYaw - (e.getX() - mMouseDownX);
					while (newyaw > 180)
						newyaw -= 360;
					while (newyaw < -180)
						newyaw += 360;

					orientModel.setYaw(newyaw);
					mView.setYawSlider(newyaw);
					// Control pitch
					newpitch = mMouseDownPitch - (e.getY() - mMouseDownY);
					while (newpitch > 180)
						newpitch -= 360;
					while (newpitch < -180)
						newpitch += 360;
					orientModel.setPitch(newpitch);
					mView.setPitchSlider(newpitch);
					break;

				case DeviceView.MOUSE_MODE_ROLL_PITCH:
					// Control roll
					int newroll = mMouseDownRoll + (e.getX() - mMouseDownX);
					while (newroll > 180)
						newroll -= 360;
					while (newroll < -180)
						newroll += 360;
					orientModel.setRoll(newroll);
					mView.setRollSlider(newroll);
					// Control pitch
					newpitch = mMouseDownPitch - (e.getY() - mMouseDownY);
					while (newpitch > 180)
						newpitch -= 360;
					while (newpitch < -180)
						newpitch += 360;
					orientModel.setPitch(newpitch);
					mView.setPitchSlider(newpitch);
					break;
				case DeviceView.MOUSE_MODE_MOVE:
					// Control roll
					int newmovex = mMouseDownMoveX + (e.getX() - mMouseDownX);
					accelerModel.setMoveX(newmovex);
					linearAccModel.setMoveX(newmovex);

					// Control pitch
					int newmovez = mMouseDownMoveZ - (e.getY() - mMouseDownY);
					accelerModel.setMoveZ(newmovez);
					linearAccModel.setMoveZ(newmovez);
					break;
				}

				mView.doRepaint();
			}

			public void mouseMoved(MouseEvent evt) {
				// NOOP
			}
		});
	}

	// mouse mode buttons
	private void registerMouseModeButtons() {
		JRadioButton yawPitchButton = mView.getYawPitchButton();
		JRadioButton rollPitchButton = mView.getRollPitchButton();
		JRadioButton moveButton = mView.getMoveButton();

		yawPitchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mView.changeMouseMode(DeviceView.MOUSE_MODE_YAW_PITCH);
			}
		});

		rollPitchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mView.changeMouseMode(DeviceView.MOUSE_MODE_ROLL_PITCH);
			}
		});

		moveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mView.changeMouseMode(DeviceView.MOUSE_MODE_MOVE);
			}
		});
	}

	public SensorSimulatorModel getModel() {
		return mModel;
	}

	public DeviceView getView() {
		return mView;
	}
}
