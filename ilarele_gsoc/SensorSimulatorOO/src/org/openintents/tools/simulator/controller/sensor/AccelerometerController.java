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
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.IDeviceView;
import org.openintents.tools.simulator.view.sensor.sensors.AccelerometerView;
import org.openintents.tools.simulator.view.sensor.sensors.GravityView;

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
		GravityModel gravityModel = accModel.getGravity();
		GravityView gravityView = accView.getGravity();
		LinearAccelerationModel linearAccModel = accModel
				.getLinearAcceleration();

		JPanel computedGravityPanel = accView.getComputedGravity();
		JPanel computedLinearAccelerationPanel = accView
				.getComputedLinearAcceleration();
		JPanel sensorGravityPanel = accView.getSensorGravityPane();
		JPanel sensorLinearAccelerationPanel = accView
				.getSensorLinearAccelerationPane();

		double g = accView.getGravityConstant();
		
		// get component vectors (gravity + linear_acceleration) 
		Vector gravityVec = getGravityVector(accModel, accView, gravityModel,
				computedGravityPanel, sensorGravityPanel, orientation, g);
		Vector linearVec = getLinearAccVector(accModel, accView,
				linearAccModel, computedLinearAccelerationPanel,
				sensorLinearAccelerationPanel, orientation, delay);

		JCheckBox showAcc = accView.getShow();
		accModel.setShown(showAcc.isSelected());
		Vector resultVec = Vector.addVectors(gravityVec, linearVec);
		if (accModel.isEnabled()) {
			if (realDeviceBridgeAddon.isUsed()) {
				Vector wiiVector = realDeviceBridgeAddon.getWiiMoteVector();
				accModel.setXYZ(wiiVector);
			} else {
				accModel.setXYZ(resultVec);
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

	}

	private Vector getLinearAccVector(AccelerometerModel accModel,
			AccelerometerView accView, LinearAccelerationModel linearAccModel,
			JPanel computedLinearAccelerationPanel,
			JPanel sensorLinearAccelerationPanel, OrientationModel orientation,
			int delay) {
		Vector linearVec;
		if (linearAccModel.isEnabled()) {
			computedLinearAccelerationPanel.setVisible(false);
			sensorLinearAccelerationPanel.setVisible(true);
			updateLinearAccelerationValues(linearAccModel, accView);

			// don't apply orientation
			linearVec = new Vector(linearAccModel.getReadLinearAccelerationX(),
					linearAccModel.getReadLinearAccelerationY(),
					linearAccModel.getReadLinearAccelerationZ());

		} else {
			double meterperpixel, gamma, k;
			double dt = 0.001 * delay; // from ms to s
			double m = accModel.getMass();

			computedLinearAccelerationPanel.setVisible(true);
			sensorLinearAccelerationPanel.setVisible(false);
			meterperpixel = accView.getPixelsPerMeter();
			// compute normal
			if (meterperpixel != 0)
				meterperpixel = 1. / meterperpixel;
			else
				meterperpixel = 1. / 3000;

			k = accView.getSpringConstant();
			gamma = accView.getDampingConstant();
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
			accModel.fixRespect(gamma, accModel.getMoveX(),
					accModel.getMoveZ(), dt);

			// Now calculate this into mobile phone acceleration:
			// ! Mobile phone's acceleration is just opposite to
			// lab frame acceleration !
			linearVec = new Vector(-accModel.getAx() * meterperpixel, 0,
					-accModel.getAz() * meterperpixel);
			linearVec.reverserollpitchyaw(orientation.getRoll(),
					orientation.getPitch(), orientation.getYaw());
		}
		return linearVec;
	}

	private Vector getGravityVector(AccelerometerModel accModel,
			AccelerometerView accView, GravityModel gravityModel,
			JPanel computedGravityPanel, JPanel sensorGravityPanel,
			OrientationModel orientation, double g) {
		Vector gravityVec;
		if (gravityModel.isEnabled()) {
			computedGravityPanel.setVisible(false);
			sensorGravityPanel.setVisible(true);
			updateGravityValues(gravityModel, accView);
			// just create it, don't apply orientation (already applied in
			// Gravity sensor)
			gravityVec = new Vector(gravityModel.getReadGravityX(),
					gravityModel.getReadGravityY(),
					gravityModel.getReadGravityZ());
		} else {
			computedGravityPanel.setVisible(true);
			sensorGravityPanel.setVisible(false);

			// apply orientation
			// we reverse roll, pitch, and yawDegree,
			// as this is how the mobile phone sees the coordinate system.
			gravityVec = new Vector(0, 0, g);
			gravityVec.reverserollpitchyaw(orientation.getRoll(),
					orientation.getPitch(), orientation.getYaw());
		}
		return gravityVec;
	}

	private void updateLinearAccelerationValues(
			LinearAccelerationModel linearAccModel, AccelerometerView accView) {
		accView.setSensorLinearAccX(linearAccModel.getReadLinearAccelerationX());
		accView.setSensorLinearAccY(linearAccModel.getReadLinearAccelerationY());
		accView.setSensorLinearAccZ(linearAccModel.getReadLinearAccelerationZ());
	}

	private void updateGravityValues(GravityModel gravityModel,
			AccelerometerView accView) {
		accView.setSensorGravityX(gravityModel.getReadGravityX());
		accView.setSensorGravityY(gravityModel.getReadGravityY());
		accView.setSensorGravityZ(gravityModel.getReadGravityZ());
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

	public void setCurrentUpdateRate(int updatesPerSecond) {
		AccelerometerModel accModel = (AccelerometerModel) model;
		AccelerometerView accView = (AccelerometerView) view;
		switch (updatesPerSecond) {
		case SensorModel.DELAY_MS_FASTEST:
		case SensorModel.DELAY_MS_GAME:
		case SensorModel.DELAY_MS_NORMAL:
		case SensorModel.DELAY_MS_UI:
			accModel.setCurrentUpdateRate(updatesPerSecond);
			accView.setCurrentUpdateRate(updatesPerSecond);
			break;
		default:
			break;
		}

	}
}
