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
import javax.swing.Timer;

import org.openintents.tools.simulator.controller.sensor.AccelerometerCtrl;
import org.openintents.tools.simulator.controller.sensor.BarcodeReaderCtrl;
import org.openintents.tools.simulator.controller.sensor.LightCtrl;
import org.openintents.tools.simulator.controller.sensor.MagneticFieldCtrl;
import org.openintents.tools.simulator.controller.sensor.OrientationCtrl;
import org.openintents.tools.simulator.controller.sensor.ProximityCtrl;
import org.openintents.tools.simulator.controller.sensor.SensorCtrl;
import org.openintents.tools.simulator.controller.sensor.TemperatureCtrl;
import org.openintents.tools.simulator.controller.telnet.ReplayAddonCtrl;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.DeviceView;
import org.openintents.tools.simulator.view.sensor.SensorSimulatorView;
import org.openintents.tools.simulator.view.sensor.sensors.AccelerometerView;

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
public class SensorSimulatorCtrl implements WindowListener {
	private ReplayAddonCtrl replayAddonCtrl;

	private ArrayList<SensorCtrl> sensors;

	private DeviceCtrl deviceCtrl;

	private SensorSimulatorModel model;
	private SensorSimulatorView view;

	private Timer timer;

	public SensorSimulatorCtrl(final SensorSimulatorModel model,
			SensorSimulatorView view) {
		this.model = model;
		this.view = view;

		// sensors
		sensors = new ArrayList<SensorCtrl>();
		sensors.add(new AccelerometerCtrl(model.getAccelerometer(), view
				.getAccelerometer()));
		sensors.add(new MagneticFieldCtrl(model.getMagneticField(), view
				.getMagneticField()));
		sensors.add(new OrientationCtrl(model.getOrientation(), view
				.getOrientation()));
		sensors.add(new TemperatureCtrl(model.getTemperature(), view
				.getTemperature()));
		sensors.add(new BarcodeReaderCtrl(model.getBarcodeReader(), view
				.getBarcodeReader()));
		sensors.add(new LightCtrl(model.getLight(), view.getLight()));
		sensors.add(new ProximityCtrl(model.getProximity(), view.getProximity()));

		// add-ons
		replayAddonCtrl = new ReplayAddonCtrl(model.getReplayAddon(),
				view.getReplayAddon(), view.getMessagePanel());

		deviceCtrl = new DeviceCtrl(model, view.getDevice());

		JButton sensorPortButton = view.getSensorPortButton();
		sensorPortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.restartSensorServer();
			}
		});

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

		updateFromFile();

		int delay = model.getDelay();

		// Update sensors:
		for (SensorCtrl sensorCtrl : sensors) {
			sensorCtrl.updateSensorPhysics(orientation, wiiAccelerometerModel,
					delay);
		}
		for (SensorCtrl sensorCtrl : sensors) {
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
			for (SensorCtrl sensorCtrl : sensors) {
				sensorCtrl.updateUserSettings();
			}
		}

		// Measure refresh
		updateSensorRefresh();

		// Now show updated data
		StringBuffer newData = new StringBuffer();
		for (SensorCtrl sensorCtrl : sensors) {
			newData.append(sensorCtrl.showSensorData());
		}
		view.setOutput(newData.toString());
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
	 * is called from within doTimer() to record/playback values
	 * recording/playback is triggered from actionListener
	 */
	private void updateFromFile() {
		DeviceView deviceView = deviceCtrl.getView();
		OrientationModel orientModel = (OrientationModel) sensors.get(
				SensorModel.POZ_ORIENTATION).getModel();
		replayAddonCtrl.recordData(orientModel.getReadYaw(),
				orientModel.getReadRoll(), orientModel.getReadPitch());

		if (replayAddonCtrl.playData()) {
			// Update sliders
			deviceView.setYawSlider(replayAddonCtrl.getYaw());
			deviceView.setRollSlider(replayAddonCtrl.getRoll());
			deviceView.setPitchSlider(replayAddonCtrl.getPitch());
		} else {
			replayAddonCtrl.setPlaybackText("Playback");
		}

	}

	/**
	 * Updates the information about sensor updates.
	 */
	public void updateSensorRefresh() {
		int updateSensorCount = model.incUpdateSensorCount();
		long maxcount = model.getRefreshCount();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public MagneticFieldCtrl getMagneticField() {
		return (MagneticFieldCtrl) sensors.get(SensorModel.POZ_MAGNETIC_FIELD);
	}

	public TemperatureCtrl getTemperature() {
		return (TemperatureCtrl) sensors.get(SensorModel.POZ_TEMPERATURE);
	}

	public BarcodeReaderCtrl getBarcodeReader() {
		return (BarcodeReaderCtrl) sensors.get(SensorModel.POZ_BARCODE_READER);
	}

	public LightCtrl getLight() {
		return (LightCtrl) sensors.get(SensorModel.POZ_LIGHT);
	}

	public ProximityCtrl getProximity() {
		return (ProximityCtrl) sensors.get(SensorModel.POZ_PROXIMITY);
	}

	public AccelerometerCtrl getAccelerometer() {
		return (AccelerometerCtrl) sensors.get(SensorModel.POZ_ACCELEROMETER);
	}

	public OrientationCtrl getOrientation() {
		return (OrientationCtrl) sensors.get(SensorModel.POZ_ORIENTATION);
	}
}
