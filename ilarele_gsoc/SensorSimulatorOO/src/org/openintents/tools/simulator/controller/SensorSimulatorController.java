/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2010 OpenIntents.org
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

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.controller.sensor.AccelerometerController;
import org.openintents.tools.simulator.controller.sensor.BarcodeReaderController;
import org.openintents.tools.simulator.controller.sensor.LightController;
import org.openintents.tools.simulator.controller.sensor.MagneticFieldController;
import org.openintents.tools.simulator.controller.sensor.OrientationController;
import org.openintents.tools.simulator.controller.sensor.ProximityController;
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
import org.openintents.tools.simulator.view.sensor.sensors.SensorView;

/**
 * Class of SensorSimulator.
 * 
 * The SensorSimulator is a Java stand-alone application.
 * 
 * It simulates various sensors. An Android application can connect through
 * TCP/IP with the settings shown to the SensorSimulator to simulate
 * accelerometer, compass, orientation sensor, and thermometer.
 * 
 * @author Peli
 * @author Josip Balic
 */
public class SensorSimulatorController implements WindowListener {
	private ArrayList<SensorController> sensors;

	private DeviceController deviceCtrl;

	private SensorSimulatorModel model;
	private SensorSimulatorView view;

	private Timer timer;

	public SensorSimulatorController(final SensorSimulatorModel model,
			final SensorSimulatorView view) {
		this.model = model;
		this.view = view;

		// sensors
		sensors = new ArrayList<SensorController>();
		sensors.add(new AccelerometerController(model.getAccelerometer(), view
				.getAccelerometer()));
		sensors.add(new MagneticFieldController(model.getMagneticField(), view
				.getMagneticField()));
		sensors.add(new OrientationController(model.getOrientation(), view
				.getOrientation()));
		sensors.add(new TemperatureController(model.getTemperature(), view
				.getTemperature()));
		sensors.add(new BarcodeReaderController(model.getBarcodeReader(), view
				.getBarcodeReader()));
		sensors.add(new LightController(model.getLight(), view.getLight()));
		sensors.add(new ProximityController(model.getProximity(), view
				.getProximity()));

		// add-ons

		deviceCtrl = new DeviceController(model, view.getDevice());

		JButton sensorPortButton = view.getSensorPortButton();
		sensorPortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.restartSensorServer();
			}
		});

		for (final SensorController sensorCtrl : sensors) {
			final SensorView sensorView = sensorCtrl.getView();
			final SensorModel sensorModel = sensorCtrl.getModel();

			final JButton enableBtn = sensorView.getEnabled();
			enableBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JTabbedPane tabbedPanel = view.getSensorsTabbedPanel();
					if (!sensorCtrl.isFixed()) {
						if (sensorModel.isEnabled()) {
							sensorModel.setEnabled(false);
							enableBtn.setBackground(Global.DISABLE);
							tabbedPanel.remove(sensorView);
						} else {
							sensorModel.setEnabled(true);
							enableBtn.setBackground(Global.ENABLE);
							tabbedPanel.add(sensorModel.getName(), sensorView);
						}
					}
				}
			});
		}

		timer = new Timer(model.getDelay(), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				doTimer();
			}
		});
		timer.setCoalesce(true);

		timer.start();
	}

	private void doTimer() {
		OrientationModel orientation = (OrientationModel) sensors.get(
				SensorModel.POZ_ORIENTATION).getModel();
		AccelerometerModel acc = (AccelerometerModel) sensors.get(
				SensorModel.POZ_ACCELEROMETER).getModel();
		WiiAccelerometerModel wiiAccelerometerModel = acc
				.getRealDeviceBridgeAddon();

		if (wiiAccelerometerModel.isUsed()) {
			updateFromWiimote();
		}

		int newDelay = (int) view.getUpdateSensors();
		if (newDelay > 0) {
			setDelay(newDelay);
		}

		// Update sensors:
		for (SensorController sensorCtrl : sensors) {
			sensorCtrl.updateSensorPhysics(orientation, wiiAccelerometerModel,
					newDelay);
		}
		for (SensorController sensorCtrl : sensors) {
			sensorCtrl.getModel().updateSensorReadoutValues();
		}

		long currentTime = System.currentTimeMillis();
		// From time to time we get the user settings:
		if (currentTime >= model.getNextUpdate()) {
			// Do update
			model.addNextUpdate(model.getDuration());
			if (model.getNextUpdate() < currentTime) {
				// Skip time if we are already behind:
				model.setNextUpdate(System.currentTimeMillis());
			}
			for (SensorController sensorCtrl : sensors) {
				sensorCtrl.updateUserSettings();
			}
		}

		// Measure refresh
		updateSensorRefresh();

		// Now show updated data
		StringBuffer newData = new StringBuffer();
		for (SensorController sensorCtrl : sensors) {
			newData.append(sensorCtrl.showSensorData());
		}
		view.setOutput(newData.toString());
	}

	private void setDelay(int newdelay) {
		timer.setDelay(newdelay);
		model.setDelay(newdelay);
	}

	private void updateFromWiimote() {
		DeviceView deviceView = deviceCtrl.getView();
		AccelerometerModel accModel = (AccelerometerModel) sensors.get(
				SensorModel.POZ_ACCELEROMETER).getModel();
		AccelerometerView accView = (AccelerometerView) sensors.get(
				SensorModel.POZ_ACCELEROMETER).getView();

		// Read raw data
		accModel.setWiiPath(accView.getWiiPath());
		boolean success = accModel.updateFromWii();
		accView.setWiiOutput(accModel.getWiiStatus());

		if (success) {
			// Update controls
			deviceView.setYawSlider(0); // Wiimote can't support yaw
			deviceView.setRollSlider(accModel.getWiiRoll());
			deviceView.setPitchSlider(accModel.getWiiPitch());
		}
	}

	/**
	 * Updates the information about sensor updates.
	 */
	public void updateSensorRefresh() {
		int updateSensorCount = model.incUpdateSensorCount();
		long maxcount = view.getRefreshCount();
		if (maxcount >= 0 && updateSensorCount >= maxcount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - model.getUpdateSensorTime())
					/ ((double) maxcount);
			model.setRefreshSensors(ms);
			view.setRefreshSensorsLabel(ms);

			model.setUpdateSensorCount(0);
			model.setUpdateSensorTime(newtime);
		}
	}

	@Override
	public void windowIconified(WindowEvent e) {
		timer.stop();
	}

	public void windowDeiconified(WindowEvent e) {
		timer.start();
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
		return (MagneticFieldController) sensors
				.get(SensorModel.POZ_MAGNETIC_FIELD);
	}

	public TemperatureController getTemperature() {
		return (TemperatureController) sensors.get(SensorModel.POZ_TEMPERATURE);
	}

	public BarcodeReaderController getBarcodeReader() {
		return (BarcodeReaderController) sensors
				.get(SensorModel.POZ_BARCODE_READER);
	}

	public LightController getLight() {
		return (LightController) sensors.get(SensorModel.POZ_LIGHT);
	}

	public ProximityController getProximity() {
		return (ProximityController) sensors.get(SensorModel.POZ_PROXIMITY);
	}

	public AccelerometerController getAccelerometer() {
		return (AccelerometerController) sensors
				.get(SensorModel.POZ_ACCELEROMETER);
	}

	public OrientationController getOrientation() {
		return (OrientationController) sensors.get(SensorModel.POZ_ORIENTATION);
	}

	public void setFix(boolean value) {
		for (SensorController sensor : sensors) {
			sensor.setFix(value);
		}
	}
}
