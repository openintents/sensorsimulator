package org.openintents.tools.simulator.controller.sensor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.IDeviceView;
import org.openintents.tools.simulator.view.sensor.sensors.AccelerometerView;

public class AccelerometerController extends SensorController {
	@SuppressWarnings("unused")
	private WiiAccelerometerController wiiAccelerometerCtrl;

	public AccelerometerController(final AccelerometerModel model,
			AccelerometerView view) {
		super(model, view);
		wiiAccelerometerCtrl = new WiiAccelerometerController(
				model.getRealDeviceBridgeAddon(),
				view.getRealDeviceBridgeAddon());
	}

	public void setMobile(final IDeviceView mobile) {
		AccelerometerView accView = (AccelerometerView) view;
		accView.getShowAcceleration().addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// Refresh the screen when this drawing element
				// changes
				mobile.doRepaint();
			}
		});
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		AccelerometerModel accModel = (AccelerometerModel) model;
		AccelerometerView accView = (AccelerometerView) view;

		double dt = 0.001 * delay; // from ms to s
		double g = accView.getGravityConstant();
		double meterperpixel = accView.getPixelsPerMeter();
		if (meterperpixel != 0)
			meterperpixel = 1. / meterperpixel;
		else
			meterperpixel = 1. / 3000;

		double k = accView.getSpringConstant();
		double gamma = accView.getDampingConstant();
		double m = accModel.getMass();

		JCheckBox showAcc = accView.getShow();
		accModel.setShown(showAcc.isSelected());
		// First calculate the force acting on the
		// sensor test particle, assuming that
		// the accelerometer is mounted by a string:
		// F = - k * x
		double Fx = k * (accModel.getMoveX() - accModel.getAccX());
		double Fz = k * (accModel.getMoveZ() - accModel.getAccZ());

		// a = F / m
		accModel.setA(Fx / m, Fz / m);

		accModel.addVX(accModel.getAx() * dt);
		accModel.addVZ(accModel.getAz() * dt);
		// Now this is the force that tries to adjust
		// the accelerometer back
		// integrate dx/dt = v;
		accModel.adjustPos(accModel.getVX() * dt, accModel.getVZ() * dt);

		// We put damping here: We don't want to damp for
		// zero motion with respect to the background,
		// but with respect to the mobile phone:
		accModel.fixRespect(gamma, accModel.getMoveX(), accModel.getMoveZ(), dt);

		// Calculate acceleration by gravity:
		double gravityax = accView.getGravityX();
		double gravityay = accView.getGravityY();
		double gravityaz = accView.getGravityZ();

		// Now calculate this into mobile phone acceleration:
		// ! Mobile phone's acceleration is just opposite to
		// lab frame acceleration !
		Vector vec = new Vector(-accModel.getAx() * meterperpixel + gravityax,
				gravityay, -accModel.getAz() * meterperpixel + gravityaz);
		// we reverse roll, pitch, and yawDegree,
		// as this is how the mobile phone sees the coordinate system.
		vec.reverserollpitchyaw(orientation.getRoll(), orientation.getPitch(),
				orientation.getYaw());

		if (accModel.isEnabled()) {
			if (realDeviceBridgeAddon.isUsed()) {
				Vector wiiVector = realDeviceBridgeAddon.getWiiMoteVector();
				accModel.setXYZ(wiiVector);
			} else {
				accModel.setXYZ(vec);
				// Add random component:
				double random = accView.getRandom();
				if (random > 0) {
					accModel.addRandom(random);
				}

				// Add accelerometer limit:
				double limit = g * accView.getAccelerometerLimit();
				if (limit > 0) {
					// limit on each component separately, as each is
					// a separate sensor.
					accModel.limitate(limit);
				}
			}
		} else {
			accModel.reset();
		}

		GravityModel gravityModel = accModel.getGravity();
		LinearAccelerationModel linearAccModel = accModel
				.getLinearAcceleration();
		JPanel computedGravityPanel = accView.getComputedGravity();
		JPanel computedLinearAccelerationPanel = accView.getComputedLinearAcceleration();
		JPanel sensorGravityPanel = accView.getSensorGravityPane();
		JPanel sensorLinearAccelerationPanel = accView.getSensorLinearAccelerationPane();
		
		
		if (gravityModel.isEnabled()) {
			accView.setEnablePanel(computedGravityPanel, false);
			sensorGravityPanel.setVisible(true);
		} else {
			accView.setEnablePanel(computedGravityPanel, true);
			sensorGravityPanel.setVisible(false);
		}

		if (linearAccModel.isEnabled()) {
			accView.setEnablePanel(computedLinearAccelerationPanel, false);
			sensorLinearAccelerationPanel.setVisible(true);
		} else {
			accView.setEnablePanel(computedLinearAccelerationPanel, true);
			sensorLinearAccelerationPanel.setVisible(false);
		}
	}

	@Override
	public String getString() {
		AccelerometerModel accModel = (AccelerometerModel) model;
		return Global.TWO_DECIMAL_FORMAT.format(accModel
				.getReadAccelerometerX())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(accModel
						.getReadAccelerometerY())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(accModel
						.getReadAccelerometerZ());
	}
}
