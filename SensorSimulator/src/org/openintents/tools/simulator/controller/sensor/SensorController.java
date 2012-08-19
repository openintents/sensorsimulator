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

package org.openintents.tools.simulator.controller.sensor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.gui.util.SensorButton;
import org.openintents.tools.simulator.view.sensor.sensors.SensorView;

/**
 * SensorController is the class that defines the common fields and methods for
 * sensors. It also contains abstract methods for sensor specific actions that
 * need to be implemented for each sensor, regarding their behaviour (listeners,
 * etc.)
 * 
 * @author ilarele
 * 
 */
public abstract class SensorController {

	protected SensorModel mSensorModel;
	protected SensorView mSensorView;

	// if the connection with the emulator has started
	private JPanel mSensorsButtons;
	private JButton mEnableBtn;

	public SensorController(final SensorModel model, final SensorView view) {
		mSensorModel = model;
		mSensorView = view;
		JButton helpBtn = view.getHelpButton();
		helpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				view.getHelpWindow().setVisible(true);
			}
		});
	}

	/**
	 * Updates model using view data (computes sensor internal variables). It is
	 * call on each iteration for sensor updates.
	 * 
	 * @param orientation
	 * @param realDeviceBridgeAddon
	 * @param delay
	 */
	public abstract void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay);

	/**
	 * Sensor output data (used in showSensorData() method).
	 * 
	 * @return
	 */
	protected abstract String getString();

	public void updateUserSettings() {
		mSensorModel.setAvgUpdate(mSensorView.getUpdateAvg().isSelected());
		int rate = mSensorModel.getCurrentUpdateRate();
		if (rate != 0) {
			mSensorModel.setUpdateDuration(rate);
		} else {
			mSensorModel.setUpdateDuration(0);
		}
	}

	public String showSensorData() {
		if (!mSensorModel.isEnabled())
			return "";
		StringBuffer data = new StringBuffer();
		data.append(mSensorModel.getName() + ": ");
		data.append(getString());
		data.append("\n");
		return data.toString();
	}

	public void readSensor(PrintWriter out) {
		if (mSensorModel.isEnabled()) {
			mSensorModel.printSensorData(out);
		} else {
			// This sensor is currently disabled
			out.println("throw IllegalStateException");
		}
	}

	public void updateEmulatorRefresh(long maxCount) {
		long updateEmulatorCount = mSensorModel.incUpdateEmulatorCount();
		long updateEmulatorTime = mSensorModel.getEmulatorTime();

		if (maxCount >= 0 && updateEmulatorCount >= maxCount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - updateEmulatorTime)
					/ ((double) maxCount);

			mSensorView.setRefreshEmulatorTime(Global.TWO_DECIMAL_FORMAT
					.format(ms) + " ms");

			mSensorModel.setUpdateEmulatorCount(0);
			mSensorModel.setUpdateEmulatorTime(newtime);
		}
	}

	public void setCurrentUpdateRate(int delay) {
		switch (delay) {
		case SensorModel.DELAY_MS_FASTEST:
		case SensorModel.DELAY_MS_GAME:
		case SensorModel.DELAY_MS_NORMAL:
		case SensorModel.DELAY_MS_UI:
			mSensorModel.setCurrentUpdateDelay(delay);
			mSensorView.setCurrentUpdateRate(delay);
			break;
		default:
			break;
		}
	}

	public SensorModel getModel() {
		return mSensorModel;
	}

	public SensorView getView() {
		return mSensorView;
	}

	public void setEnable(boolean enabled) {
		mSensorModel.setEnabled(enabled);
		mSensorView.setEnabled(enabled);
		SensorButton sensorButton = mSensorView.getSensorButton();
		if (enabled) {
			mSensorsButtons.add(sensorButton);
		} else {
			mSensorsButtons.remove(sensorButton);
		}
	}

	public void setTab(JPanel tabbedPanel) {
		mSensorsButtons = tabbedPanel;
		mEnableBtn = mSensorView.getEnabled();
		mEnableBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setEnable(!mSensorModel.isEnabled());
			}
		});
	}

	public String getName() {
		return mSensorModel.getName();
	}

	public int getType() {
		return mSensorModel.getType();
	}
}
