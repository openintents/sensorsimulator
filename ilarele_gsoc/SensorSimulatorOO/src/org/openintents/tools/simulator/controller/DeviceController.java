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

public class DeviceController {

	protected int mousedownx;
	protected int mousedowny;
	protected int mousedownyaw;
	protected int mousedownpitch;
	protected int mousedownroll;
	protected int mousedownmovex;
	protected int mousedownmovez;

	private DeviceView view;
	private SensorSimulatorModel model;

	public DeviceController(final SensorSimulatorModel model,
			final DeviceView view) {
		this.model = model;
		this.view = view;

		registerMouseModeButtons();
		registerMouseListeners();
	}

	private void registerMouseListeners() {
		view.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				OrientationModel orientation = model.getOrientation();
				AccelerometerModel accelerometer = model.getAccelerometer();
				LinearAccelerationModel linearAccelerometer = model
						.getLinearAcceleration();
				mousedownx = e.getX();
				mousedowny = e.getY();
				mousedownyaw = orientation.getYaw();
				mousedownpitch = orientation.getPitch();
				mousedownroll = orientation.getRoll();
				if (linearAccelerometer.isEnabled()) {
					mousedownmovex = linearAccelerometer.getMoveX();
					mousedownmovez = linearAccelerometer.getMoveZ();
				} else {
					mousedownmovex = accelerometer.getMoveX();
					mousedownmovez = accelerometer.getMoveZ();
				}
			}
		});

		view.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				OrientationModel orientModel = model.getOrientation();
				AccelerometerModel accelerModel = model.getAccelerometer();
				LinearAccelerationModel linearAccModel = model
						.getLinearAcceleration();
				int newpitch;
				switch (view.getMouseMode()) {
				case DeviceView.mouseYawPitch:
					// Control yawDegree
					int newyaw = mousedownyaw - (e.getX() - mousedownx);
					while (newyaw > 180)
						newyaw -= 360;
					while (newyaw < -180)
						newyaw += 360;

					orientModel.setYaw(newyaw);
					view.setYawSlider(newyaw);
					// Control pitch
					newpitch = mousedownpitch - (e.getY() - mousedowny);
					while (newpitch > 180)
						newpitch -= 360;
					while (newpitch < -180)
						newpitch += 360;
					orientModel.setPitch(newpitch);
					view.setPitchSlider(newpitch);
					break;

				case DeviceView.mouseRollPitch:
					// Control roll
					int newroll = mousedownroll + (e.getX() - mousedownx);
					while (newroll > 180)
						newroll -= 360;
					while (newroll < -180)
						newroll += 360;
					orientModel.setRoll(newroll);
					view.setRollSlider(newroll);
					// Control pitch
					newpitch = mousedownpitch - (e.getY() - mousedowny);
					while (newpitch > 180)
						newpitch -= 360;
					while (newpitch < -180)
						newpitch += 360;
					orientModel.setPitch(newpitch);
					view.setPitchSlider(newpitch);
					break;
				case DeviceView.mouseMove:
					// Control roll
					int newmovex = mousedownmovex + (e.getX() - mousedownx);
					accelerModel.setMoveX(newmovex);
					linearAccModel.setMoveX(newmovex);
					
					// Control pitch
					int newmovez = mousedownmovez - (e.getY() - mousedowny);
					accelerModel.setMoveZ(newmovez);
					linearAccModel.setMoveZ(newmovez);
					break;
				}

				view.doRepaint();
			}

			public void mouseMoved(MouseEvent evt) {
				// NOOP
			}
		});
	}

	// mouse mode buttons
	private void registerMouseModeButtons() {
		JRadioButton yawPitchButton = view.getYawPitchButton();
		JRadioButton rollPitchButton = view.getRollPitchButton();
		JRadioButton moveButton = view.getMoveButton();

		yawPitchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.changeMouseMode(DeviceView.MOUSE_MODE_YAW_PITCH);
			}
		});

		rollPitchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.changeMouseMode(DeviceView.MOUSE_MODE_ROLL_PITCH);
			}
		});

		moveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.changeMouseMode(DeviceView.MOUSE_MODE_MOVE);
			}
		});
	}

	public SensorSimulatorModel getModel() {
		return model;
	}

	public DeviceView getView() {
		return view;
	}
}
