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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.openintents.sensorsimulator.testlibrary.Sensor;
import org.openintents.tools.simulator.SensorsScenario;
import org.openintents.tools.simulator.comm.PollProducer;
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
import org.openintents.tools.simulator.model.RefreshRateMeter;
import org.openintents.tools.simulator.model.SensorSimulatorModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.model.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensors.BarcodeReaderModel;
import org.openintents.tools.simulator.model.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensors.GyroscopeModel;
import org.openintents.tools.simulator.model.sensors.LightModel;
import org.openintents.tools.simulator.model.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensors.MagneticFieldModel;
import org.openintents.tools.simulator.model.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensors.PressureModel;
import org.openintents.tools.simulator.model.sensors.ProximityModel;
import org.openintents.tools.simulator.model.sensors.RotationVectorModel;
import org.openintents.tools.simulator.model.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensors.TemperatureModel;
import org.openintents.tools.simulator.model.sensors.WiiAccelerometerModel;
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

	public static final int NORMAL = 0;
	public static final int RECORD = 1;
	public static final int PLAY = 2;
	public static final int PAUSE = 3;
	public static final int STOP = 4;
	public static final int TIMER_DEFAULT_DELAY = 500;
	public static final int DEFAULT_PORT = 8010;

	private int mSimulatorState = NORMAL;
	private int mToSwitchState = NORMAL;
	private Object mStateLock = new Object();

	// a list with all sensors' controllers
	private Vector<SensorController> mSensors;

	// mobile device (orientation sensor - visual representation) controller
	private DeviceController mDeviceController;

	private SensorSimulatorModel mSensorSimulatorModel;
	private SensorSimulatorView mSensorSimulatorView;

	// the time for a new sensors update cycle in the java application
	private Timer mUpdateTimer;

	private AllSensorsController mSensorTabController;
	private SensorsScenarioController mScenarioController;
	
	/**
	 * Time of next update for reading user settings from widgets. The time is
	 * compared to System.currentTimeMillis().
	 */
	private long mUserSettingsNextUpdate;
	/**
	 * Duration in milliseconds until user setting changes are read out.
	 */
	private long mUserSettingsDuration;
	private RefreshRateMeter mRefreshRateMeter;
	private PollProducer mSensorEventProducer;

	public SensorSimulatorController(final SensorSimulatorModel model,
			final SensorSimulatorView view, SensorsScenario scenario) {
		mSensorSimulatorModel = model;
		mSensorSimulatorView = view;
		mRefreshRateMeter = new RefreshRateMeter(view.getRefreshCount());
		view.addRefreshRateMeter(mRefreshRateMeter);
		mRefreshRateMeter.addObserver(view);
		view.setSensorSimulatorController(this);
		
		mSensors = new Vector<SensorController>();

		DeviceView deviceView = view.getDeviceView();
		mDeviceController = new DeviceController(mSensors, deviceView);
		mScenarioController = scenario.controller;

		// sensors
		mSensors.add(new AccelerometerController((AccelerometerModel) model
				.getSensorModel(Sensor.Type.ACCELEROMETER), view
				.getAccelerometer(), mSensorSimulatorView));
		mSensors.add(new MagneticFieldController((MagneticFieldModel) model
				.getSensorModel(Sensor.Type.MAGNETIC_FIELD), view
				.getMagneticField(), mSensorSimulatorView));
		mSensors.add(new OrientationController((OrientationModel) model
				.getSensorModel(Sensor.Type.ORIENTATION), view
				.getOrientation(), deviceView, mSensorSimulatorView));
		mSensors.add(new TemperatureController((TemperatureModel) model
				.getSensorModel(Sensor.Type.TEMPERATURE), view
				.getTemperature(), mSensorSimulatorView));
		mSensors.add(new BarcodeReaderController((BarcodeReaderModel) model
				.getSensorModel(Sensor.Type.BARCODE_READER), view
				.getBarcodeReader(), mSensorSimulatorView));
		mSensors.add(new LightController((LightModel) model
				.getSensorModel(Sensor.Type.LIGHT), view.getLight(), mSensorSimulatorView));
		mSensors.add(new ProximityController((ProximityModel) model
				.getSensorModel(Sensor.Type.PROXIMITY), view
				.getProximity(), mSensorSimulatorView));
		mSensors.add(new PressureController((PressureModel) model
				.getSensorModel(Sensor.Type.PRESSURE), view
				.getPressure(), mSensorSimulatorView));
		mSensors.add(new LinearAccelerationController(
				(LinearAccelerationModel) model
						.getSensorModel(Sensor.Type.LINEAR_ACCELERATION),
				view.getLinearAceleration(), mSensorSimulatorView));
		mSensors.add(new GravityController((GravityModel) model
				.getSensorModel(Sensor.Type.GRAVITY), view.getGravity(), mSensorSimulatorView));
		mSensors.add(new RotationVectorController((RotationVectorModel) model
				.getSensorModel(Sensor.Type.ROTATION), view
				.getRotationVector(), mSensorSimulatorView));
		mSensors.add(new GyroscopeController((GyroscopeModel) model
				.getSensorModel(Sensor.Type.GYROSCOPE), view
				.getGyroscope(), mSensorSimulatorView));

		mSensorTabController = new AllSensorsController(
				view.getAllSensorsView(), mSensors);

		JButton sensorPortButton = view.getSensorPortButton();
		sensorPortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mSensorEventProducer.connect(view.getIp());
			}
		});

		// set tabs for each sensor
		JPanel tabbedPanel = view.getSensorsButtonsPanel();
		for (final SensorController sensorCtrl : mSensors) {
			sensorCtrl.setTab(tabbedPanel);
		}
		mSensorTabController.setEnabledSensors();

		mUpdateTimer = new Timer(TIMER_DEFAULT_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				doTimer();
			}
		});
		mUpdateTimer.setCoalesce(true);

		mUpdateTimer.start();
		
		mUserSettingsDuration = 500; // Update every half second. This should be
										// enough.
		mUserSettingsNextUpdate = System.currentTimeMillis(); // First update is

		// start server
		mSensorEventProducer = PollProducer.startProducing(mSensorSimulatorModel);
	}

	private void doTimer() {
		synchronized (mStateLock) {
			switch (mSimulatorState) {
			case NORMAL: {
				OrientationModel orientation = (OrientationModel) mSensors.get(
						SensorModel.POZ_ORIENTATION).getModel();
				AccelerometerModel acc = (AccelerometerModel) mSensors.get(
						SensorModel.POZ_ACCELEROMETER).getModel();
				WiiAccelerometerModel wiiAccelerometerModel = acc
						.getRealDeviceBridgeAddon();

				if (wiiAccelerometerModel.isUsed()) {
					updateFromWiimote();
				}

				// Update sensors:
				for (SensorController sensorCtrl : mSensors) {
					sensorCtrl.updateSensorPhysics(orientation,
							wiiAccelerometerModel, mUpdateTimer.getDelay());
				}
				for (SensorController sensorCtrl : mSensors) {
					sensorCtrl.getModel().updateSensorReadoutValues();
				}

				long currentTime = System.currentTimeMillis();
				// From time to time we get the user settings:
				if (currentTime >= mUserSettingsNextUpdate) {
					// Do update
					mUserSettingsNextUpdate += mUserSettingsDuration;
					if (mUserSettingsNextUpdate < currentTime) {
						// Skip time if we are already behind:
						mUserSettingsNextUpdate=System
								.currentTimeMillis();
					}
				}
			}
				break;
			default:
				for (SensorController sensorCtrl : mSensors) {
					sensorCtrl.getModel().updateSensorReadoutValues();
				}
				mScenarioController.doTime(mSimulatorState);
				break;
			}

			// Measure sensor refreshment rate
			mRefreshRateMeter.count();

			// Now show updated data
			StringBuffer newData = new StringBuffer();
			for (SensorController sensorCtrl : mSensors) {
				newData.append(sensorCtrl.showSensorData());
			}
			mSensorSimulatorView.setOutput(newData.toString());

			synchronized (mStateLock) {
				if (mSimulatorState != mToSwitchState) {
					mSimulatorState = mToSwitchState;
				}
			}
		}
	}

	public void loadStateIntoTheModel(StateModel state) {
		loadStateIntoTheModel(state, -1, false);
	}

	public void loadStateIntoTheModel(StateModel state, int position) {
		loadStateIntoTheModel(state, position, true);
	}

	private void loadStateIntoTheModel(StateModel state, int position,
			boolean isMainState) {
		mSensorSimulatorModel.loadState(state);
		mSensorSimulatorView.invalidateDevice();
	}

	public void switchState(int newState) {
		synchronized (mStateLock) {
			// if id not resume, reset the current cursor
			if (newState == PLAY && mToSwitchState != PAUSE) {
				mScenarioController.prepareToPlay();
			}
			mToSwitchState = newState;
		}
	}

	public void setUpdateDelay(int newdelay) {
		if (newdelay > 0) {
			mUpdateTimer.setDelay(newdelay);
		}
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

	@Override
	public void windowIconified(WindowEvent e) {
		mUpdateTimer.stop();
	}

	@Override
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

	public int getState() {
		return mSimulatorState;
	}

	public float getSavingTime() {
		return mSensorSimulatorView.getSavingTime();
	}

	public float getInterpolationTime() {
		return mSensorSimulatorView.getInterpolationTime();
	}

	public void enableSensor(int sensorType) {
		switch (sensorType) {
		case SensorModel.TYPE_ACCELEROMETER:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.ACCELEROMETER).setEnabled(true);
			break;
		case SensorModel.TYPE_GRAVITY:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.GRAVITY).setEnabled(true);
			break;
		case SensorModel.TYPE_GYROSCOPE:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.GYROSCOPE).setEnabled(true);
			break;
		case SensorModel.TYPE_LIGHT:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.LIGHT).setEnabled(true);
			break;
		case SensorModel.TYPE_LINEAR_ACCELERATION:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.LINEAR_ACCELERATION).setEnabled(true);
			break;
		case SensorModel.TYPE_MAGNETIC_FIELD:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.MAGNETIC_FIELD).setEnabled(true);
			break;
		case SensorModel.TYPE_ORIENTATION:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.ORIENTATION).setEnabled(true);
			break;
		case SensorModel.TYPE_PRESSURE:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.PRESSURE).setEnabled(true);
			break;
		case SensorModel.TYPE_PROXIMITY:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.PROXIMITY).setEnabled(true);
			break;
		case SensorModel.TYPE_ROTATION_VECTOR:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.ROTATION).setEnabled(true);
			break;
		case SensorModel.TYPE_TEMPERATURE:
			mSensorSimulatorModel.getSensorModel(Sensor.Type.TEMPERATURE).setEnabled(true);
			break;
		}
	}

	public Vector<SensorController> getSensors() {
		return mSensors;
	}
	
	/**
	 * Returns the controller component of a sensor by name.
	 * 
	 * @param sensorName
	 *            name of the sensor to be returned
	 * @return controller component of the specified sensor
	 */
	public SensorController getSensorCtrlFromName(String sensorName) {
		if (sensorName.compareTo(SensorModel.ACCELEROMETER) == 0)
			return (AccelerometerController) mSensors
					.get(SensorModel.POZ_ACCELEROMETER);
		else if (sensorName.compareTo(SensorModel.MAGNETIC_FIELD) == 0)
			return (MagneticFieldController) mSensors
					.get(SensorModel.POZ_MAGNETIC_FIELD);
		else if (sensorName.compareTo(SensorModel.ORIENTATION) == 0)
			return (OrientationController) mSensors
					.get(SensorModel.POZ_ORIENTATION);
		else if (sensorName.compareTo(SensorModel.TEMPERATURE) == 0)
			return (TemperatureController) mSensors
					.get(SensorModel.POZ_TEMPERATURE);
		else if (sensorName.compareTo(SensorModel.BARCODE_READER) == 0)
			return (BarcodeReaderController) mSensors
					.get(SensorModel.POZ_BARCODE_READER);
		else if (sensorName.compareTo(SensorModel.LIGHT) == 0)
			return (LightController) mSensors.get(SensorModel.POZ_LIGHT);
		else if (sensorName.compareTo(SensorModel.PROXIMITY) == 0)
			return (ProximityController) mSensors.get(SensorModel.POZ_PROXIMITY);
		else if (sensorName.compareTo(SensorModel.PRESSURE) == 0)
			return (PressureController) mSensors.get(SensorModel.POZ_PRESSURE);
		else if (sensorName.compareTo(SensorModel.LINEAR_ACCELERATION) == 0)
			return (LinearAccelerationController) mSensors
					.get(SensorModel.POZ_LINEAR_ACCELERATION);
		else if (sensorName.compareTo(SensorModel.GRAVITY) == 0)
			return (GravityController) mSensors.get(SensorModel.POZ_GRAVITY);
		else if (sensorName.compareTo(SensorModel.ROTATION_VECTOR) == 0)
			return (RotationVectorController) mSensors
					.get(SensorModel.POZ_ROTATION);
		else if (sensorName.compareTo(SensorModel.GYROSCOPE) == 0)
			return (GyroscopeController) mSensors.get(SensorModel.POZ_GYROSCOPE);
		return null;
	}
}
