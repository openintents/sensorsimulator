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
import java.util.Vector;

import javax.swing.JRadioButton;

import org.openintents.tools.simulator.controller.sensor.OrientationController;
import org.openintents.tools.simulator.controller.sensor.SensorController;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
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

	private DeviceView mDeviceView;
	private Vector<SensorController> mSensors;

	public DeviceController(final Vector<SensorController> sensors,
			final DeviceView deviceView) {
		mSensors = sensors;
		mDeviceView = deviceView;

		registerMouseModeButtons();
		registerMouseListeners();
	}

	private void registerMouseListeners() {
		mDeviceView.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				OrientationModel orientation = (OrientationModel) mSensors.get(
						SensorModel.POZ_ORIENTATION).getModel();
				AccelerometerModel accelerometer = (AccelerometerModel) mSensors
						.get(SensorModel.POZ_ACCELEROMETER).getModel();
				LinearAccelerationModel linearAccelerometer = (LinearAccelerationModel) mSensors
						.get(SensorModel.POZ_LINEAR_ACCELERATION).getModel();
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

		mDeviceView.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				OrientationController orientController = (OrientationController) mSensors
						.get(SensorModel.POZ_ORIENTATION);
				AccelerometerModel accelerModel = (AccelerometerModel) mSensors
						.get(SensorModel.POZ_ACCELEROMETER).getModel();
				LinearAccelerationModel linearAccModel = (LinearAccelerationModel) mSensors
						.get(SensorModel.POZ_LINEAR_ACCELERATION).getModel();
				int newpitch;
				switch (mDeviceView.getMouseMode()) {
				case DeviceView.MOUSE_MODE_YAW_PITCH:
					// Control yawDegree
					int newyaw = mMouseDownYaw - (e.getX() - mMouseDownX);
					while (newyaw > 180) {
						newyaw -= 360;
					}
					while (newyaw < -180) {
						newyaw += 360;
					}

					orientController.setYaw(newyaw);
					// Control pitch
					newpitch = mMouseDownPitch - (e.getY() - mMouseDownY);
					while (newpitch > 180) {
						newpitch -= 360;
					}
					while (newpitch < -180) {
						newpitch += 360;
					}
					orientController.setPitch(newpitch);
					break;

				case DeviceView.MOUSE_MODE_ROLL_PITCH:
					// Control roll
					int newroll = mMouseDownRoll + (e.getX() - mMouseDownX);
					while (newroll > 180) {
						newroll -= 360;
					}
					while (newroll < -180) {
						newroll += 360;
					}
					orientController.setRoll(newroll);
					// Control pitch
					newpitch = mMouseDownPitch - (e.getY() - mMouseDownY);
					while (newpitch > 180) {
						newpitch -= 360;
					}
					while (newpitch < -180) {
						newpitch += 360;
					}
					orientController.setPitch(newpitch);
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

				mDeviceView.doRepaint();
			}

			@Override
			public void mouseMoved(MouseEvent evt) {
				// NOOP
			}
		});
	}

	// mouse mode buttons
	private void registerMouseModeButtons() {
		JRadioButton yawPitchButton = mDeviceView.getYawPitchButton();
		JRadioButton rollPitchButton = mDeviceView.getRollPitchButton();
		JRadioButton moveButton = mDeviceView.getMoveButton();

		yawPitchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mDeviceView.changeMouseMode(DeviceView.MOUSE_MODE_YAW_PITCH);
			}
		});

		rollPitchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mDeviceView.changeMouseMode(DeviceView.MOUSE_MODE_ROLL_PITCH);
			}
		});

		moveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mDeviceView.changeMouseMode(DeviceView.MOUSE_MODE_MOVE);
			}
		});
	}

	public DeviceView getView() {
		return mDeviceView;
	}
}
