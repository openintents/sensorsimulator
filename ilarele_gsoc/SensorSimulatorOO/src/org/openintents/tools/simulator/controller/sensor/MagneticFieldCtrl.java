package org.openintents.tools.simulator.controller.sensor;

import hr.fer.tel.simulator.Global;

import org.openintents.tools.simulator.model.sensor.sensors.MagneticFieldModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.model.telnet.Vector;
import org.openintents.tools.simulator.view.sensor.sensors.MagneticFieldView;

public class MagneticFieldCtrl extends SensorCtrl {

	public MagneticFieldCtrl(MagneticFieldModel model, MagneticFieldView view) {
		super(model, view);
	}

	@Override
	public void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay) {
		double magneticnorth;
		double magneticeast;
		double magneticvertical;
		MagneticFieldModel magModel = (MagneticFieldModel) model;
		MagneticFieldView magView = (MagneticFieldView) view;

		if (magModel.isEnabled()) {
			magneticnorth = magView.getNorth();
			magneticeast = magView.getEast();
			magneticvertical = magView.getVertical();

			// Add random component:
			double random = magModel.getRandom();
			if (random > 0) {
				magneticnorth += SensorModel.getRandom(random);
				magneticeast += SensorModel.getRandom(random);
				magneticvertical += SensorModel.getRandom(random);
			}
			magModel.setNorth(magneticnorth);
			magModel.setEast(magneticeast);
			magModel.setVertical(magneticvertical);

			// Magnetic vector in phone coordinates:
			Vector vec = new Vector(magneticeast, magneticnorth,
					-magneticvertical);
			vec.scale(0.001); // convert from nT (nano-Tesla) to �T
								// (micro-Tesla)

			// we reverse roll, pitch, and yawDegree,
			// as this is how the mobile phone sees the coordinate system.

			double rollDegree = orientation.getRoll();
			double pitchDegree = orientation.getPitch();
			double yawDegree = orientation.getYaw();
			vec.reverserollpitchyaw(rollDegree, pitchDegree, yawDegree);

			magModel.setCompass(vec);
		} else {
			magModel.resetCompas();
		}
	}

	@Override
	public String getString() {
		MagneticFieldModel magModel = (MagneticFieldModel) model;
		return Global.TWO_DECIMAL_FORMAT.format(magModel.getReadCompassX())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(magModel.getReadCompassY())
				+ ", "
				+ Global.TWO_DECIMAL_FORMAT.format(magModel.getReadCompassZ());
	}

}
