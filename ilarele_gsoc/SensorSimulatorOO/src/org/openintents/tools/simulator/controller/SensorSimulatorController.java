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

package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import org.openintents.tools.simulator.controller.sensor.AccelerometerController;
import org.openintents.tools.simulator.controller.sensor.BarcodeReaderController;
import org.openintents.tools.simulator.controller.sensor.GravityController;
import org.openintents.tools.simulator.controller.sensor.GyroscopeController;
import org.openintents.tools.simulator.controller.sensor.LightController;
import org.openintents.tools.simulator.controller.sensor.LinearAccelerationController;
import org.openintents.tools.simulator.controller.sensor.MagneticFieldController;
import org.openintents.tools.simulator.controller.sensor.OrientationController;
import org.openintents.tools.simulator.controller.sensor.PressureController;
import org.openintents.tools.simulator.controller.sensor.ProximityController;
import org.openintents.tools.simulator.controller.sensor.RotationVectorController;
import org.openintents.tools.simulator.controller.sensor.SensorController;
import org.openintents.tools.simulator.controller.sensor.TemperatureController;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.DeviceView;
import org.openintents.tools.simulator.view.sensor.SensorSimulatorView;
import org.openintents.tools.simulator.view.sensor.sensors.AccelerometerView;

/**
 * SensorSimulatorController keeps the behaviour of the SensorSimulator
 * (listeners, timers, etc.)
 * 
 * SensorSimulator simulates various sensors. An Android application can connect
 * through TCP/IP with the settings shown to the SensorSimulator to simulate
 * accelerometer, compass, orientation sensor, thermometer, light, proximity,
 * pressure, gravity, linear acceleration and rotation vector.
 * 
 * 
 * @author Peli
 * @author Josip Balic
 * @author ilarele
 */

public class SensorSimulatorController implements WindowListener {
	// a list with all sensors' controllers
	private ArrayList<SensorController> mSensors;

	// mobile device (orientation sensor - visual representation) controller
	private DeviceController mDeviceController;

	private SensorSimulatorModel mSensorSimulatorModel;
	private SensorSimulatorView mSensorSimulatorView;

	// the time for a new sensors update cycle in the java application
	private Timer mUpdateTimer;

	private AllSensorsController mSensorTabController;

	public SensorSimulatorController(final SensorSimulatorModel model,
			final SensorSimulatorView view) {
		this.mSensorSimulatorModel = model;
		this.mSensorSimulatorView = view;
		mSensors = new ArrayList<SensorController>();

		DeviceView deviceView = view.getDeviceView();
		mDeviceController = new DeviceController(mSensors, deviceView);

		// sensors
		mSensors.add(new AccelerometerController(model.getAccelerometer(), view
				.getAccelerometer()));
		mSensors.add(new MagneticFieldController(model.getMagneticField(), view
				.getMagneticField()));
		mSensors.add(new OrientationController(model.getOrientation(), view
				.getOrientation(), deviceView));
		mSensors.add(new TemperatureController(model.getTemperature(), view
				.getTemperature()));
		mSensors.add(new BarcodeReaderController(model.getBarcodeReader(), view
				.getBarcodeReader()));
		mSensors.add(new LightController(model.getLight(), view.getLight()));
		mSensors.add(new ProximityController(model.getProximity(), view
				.getProximity()));
		mSensors.add(new PressureController(model.getPressure(), view
				.getPressure()));
		mSensors.add(new LinearAccelerationController(model
				.getLinearAcceleration(), view.getLinearAceleration()));
		mSensors.add(new GravityController(model.getGravity(), view
				.getGravity()));
		mSensors.add(new RotationVectorController(model.getRotationVector(),
				view.getRotationVector()));
		mSensors.add(new GyroscopeController(model.getGyroscope(), view
				.getGyroscope()));

		mSensorTabController = new AllSensorsController(
				view.getAllSensorsView(), mSensors);

		JButton sensorPortButton = view.getSensorPortButton();
		sensorPortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.restartSensorServer();
			}
		});

		// set tabs for each sensor
		JTabbedPane tabbedPanel = view.getSensorsTabbedPanel();
		for (final SensorController sensorCtrl : mSensors) {
			sensorCtrl.setTab(tabbedPanel);
		}

		mUpdateTimer = new Timer(model.getDelay(), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				doTimer();
			}
		});
		mUpdateTimer.setCoalesce(true);

		mUpdateTimer.start();
	}

	private void doTimer() {
		OrientationModel orientation = (OrientationModel) mSensors.get(
				SensorModel.POZ_ORIENTATION).getModel();
		AccelerometerModel acc = (AccelerometerModel) mSensors.get(
				SensorModel.POZ_ACCELEROMETER).getModel();
		WiiAccelerometerModel wiiAccelerometerModel = acc
				.getRealDeviceBridgeAddon();

		if (wiiAccelerometerModel.isUsed()) {
			updateFromWiimote();
		}

		int newDelay = (int) mSensorSimulatorView.getUpdateSensors();
		if (newDelay > 0) {
			setDelay(newDelay);
		}

		// Update sensors:
		for (SensorController sensorCtrl : mSensors) {
			sensorCtrl.updateSensorPhysics(orientation, wiiAccelerometerModel,
					newDelay);
		}
		for (SensorController sensorCtrl : mSensors) {
			sensorCtrl.getModel().updateSensorReadoutValues();
		}

		long currentTime = System.currentTimeMillis();
		// From time to time we get the user settings:
		if (currentTime >= mSensorSimulatorModel.getNextUpdate()) {
			// Do update
			mSensorSimulatorModel.addNextUpdate(mSensorSimulatorModel
					.getDuration());
			if (mSensorSimulatorModel.getNextUpdate() < currentTime) {
				// Skip time if we are already behind:
				mSensorSimulatorModel.setNextUpdate(System.currentTimeMillis());
			}
			for (SensorController sensorCtrl : mSensors) {
				sensorCtrl.updateUserSettings();
			}
		}

		// Measure refresh
		updateSensorRefresh();

		// Now show updated data
		StringBuffer newData = new StringBuffer();
		for (SensorController sensorCtrl : mSensors) {
			newData.append(sensorCtrl.showSensorData());
		}
		mSensorSimulatorView.setOutput(newData.toString());
	}

	private void setDelay(int newdelay) {
		mUpdateTimer.setDelay(newdelay);
		mSensorSimulatorModel.setDelay(newdelay);
	}

	private void updateFromWiimote() {
		OrientationController orientationController = (OrientationController) mSensors
				.get(SensorModel.POZ_ORIENTATION);
		AccelerometerModel accModel = (AccelerometerModel) mSensors.get(
				SensorModel.POZ_ACCELEROMETER).getModel();
		AccelerometerView accView = (AccelerometerView) mSensors.get(
				SensorModel.POZ_ACCELEROMETER).getView();

		// Read raw data
		accModel.setWiiPath(accView.getWiiPath());
		boolean success = accModel.updateFromWii();
		accView.setWiiOutput(accModel.getWiiStatus());

		if (success) {
			// Update controls
			orientationController.setYaw(0); // Wiimote can't support yaw
			orientationController.setRoll(accModel.getWiiRoll());
			orientationController.setPitch(accModel.getWiiPitch());
		}
	}

	/**
	 * Updates the information about sensors refresh time.
	 */
	public void updateSensorRefresh() {
		int updateSensorCount = mSensorSimulatorModel.incUpdateSensorCount();
		long maxcount = mSensorSimulatorView.getRefreshCount();
		if (maxcount >= 0 && updateSensorCount >= maxcount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - mSensorSimulatorModel
					.getUpdateSensorTime()) / ((double) maxcount);
			mSensorSimulatorModel.setRefreshSensors(ms);
			mSensorSimulatorView.setRefreshSensorsLabel(ms);

			mSensorSimulatorModel.setUpdateSensorCount(0);
			mSensorSimulatorModel.setUpdateSensorTime(newtime);
		}
	}

	@Override
	public void windowIconified(WindowEvent e) {
		mUpdateTimer.stop();
	}

	public void windowDeiconified(WindowEvent e) {
		mUpdateTimer.start();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {

	}

	@Override
	public void windowClosing(WindowEvent arg0) {

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

	public MagneticFieldController getMagneticField() {
		return (MagneticFieldController) mSensors
				.get(SensorModel.POZ_MAGNETIC_FIELD);
	}

	public TemperatureController getTemperature() {
		return (TemperatureController) mSensors
				.get(SensorModel.POZ_TEMPERATURE);
	}

	public BarcodeReaderController getBarcodeReader() {
		return (BarcodeReaderController) mSensors
				.get(SensorModel.POZ_BARCODE_READER);
	}

	public LightController getLight() {
		return (LightController) mSensors.get(SensorModel.POZ_LIGHT);
	}

	public ProximityController getProximity() {
		return (ProximityController) mSensors.get(SensorModel.POZ_PROXIMITY);
	}

	public AccelerometerController getAccelerometer() {
		return (AccelerometerController) mSensors
				.get(SensorModel.POZ_ACCELEROMETER);
	}

	public OrientationController getOrientation() {
		return (OrientationController) mSensors
				.get(SensorModel.POZ_ORIENTATION);
	}

	public void setFix(boolean value) {
		for (SensorController sensor : mSensors) {
			sensor.setFix(value);
		}
	}

	public PressureController getPressure() {
		return (PressureController) mSensors.get(SensorModel.POZ_PRESSURE);
	}

	public GravityController getGravity() {
		return (GravityController) mSensors.get(SensorModel.POZ_GRAVITY);
	}

	public LinearAccelerationController getLinearAcceleration() {
		return (LinearAccelerationController) mSensors
				.get(SensorModel.POZ_LINEAR_ACCELERATION);
	}

	public RotationVectorController getRotationVector() {
		return (RotationVectorController) mSensors
				.get(SensorModel.POZ_ROTATION);
	}
}
