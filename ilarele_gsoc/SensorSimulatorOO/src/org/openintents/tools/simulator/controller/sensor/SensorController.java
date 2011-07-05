package org.openintents.tools.simulator.controller.sensor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import javax.swing.JButton;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;
import org.openintents.tools.simulator.view.sensor.sensors.SensorView;

public abstract class SensorController {

	protected SensorModel model;
	protected SensorView view;
	// if the connection with the emulator has started
	private boolean isFixed;

	public SensorController(final SensorModel model, final SensorView view) {
		this.model = model;
		this.view = view;
		isFixed = false;
		JButton helpBtn = view.getHelpButton();
		helpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				view.getHelpWindow().setVisible(true);
			}
		});
	}

	public SensorModel getModel() {
		return model;
	}

	public abstract void updateSensorPhysics(OrientationModel orientation,
			WiiAccelerometerModel realDeviceBridgeAddon, int delay);

	public abstract String getString();

	public void updateUserSettings() {
		model.setAvgUpdate(view.getUpdateAvg().isSelected());
		int rate = model.getCurrentUpdateRate();
		if (rate != 0) {
			model.setUpdateDuration(rate);
		} else {
			model.setUpdateDuration(0);
		}
	}

	public SensorView getView() {
		return view;
	}

	public String showSensorData() {
		if (!model.isEnabled())
			return "";
		StringBuffer data = new StringBuffer();
		data.append(model.getName() + ": ");
		data.append(getString());
		data.append("\n");
		return data.toString();
	}

	public void readSensor(PrintWriter out) {
		if (model.isEnabled()) {
			model.printSensorData(out);
		} else {
			// This sensor is currently disabled
			out.println("throw IllegalStateException");
		}
	}

	public void updateEmulatorRefresh(long maxCount) {
		long updateEmulatorCount = model.incUpdateEmulatorCount();
		long updateEmulatorTime = model.getEmulatorTime();

		if (maxCount >= 0 && updateEmulatorCount >= maxCount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - updateEmulatorTime)
					/ ((double) maxCount);

			view.setRefreshEmulatorTime(Global.TWO_DECIMAL_FORMAT.format(ms)
					+ " ms");

			model.setUpdateEmulatorCount(0);
			model.setUpdateEmulatorTime(newtime);
		}
	}

	public void setFix(boolean value) {
		isFixed = value;
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setCurrentUpdateRate(int delay) {
		switch (delay) {
		case SensorModel.DELAY_MS_FASTEST:
		case SensorModel.DELAY_MS_GAME:
		case SensorModel.DELAY_MS_NORMAL:
		case SensorModel.DELAY_MS_UI:
			model.setCurrentUpdateDelay(delay);
			view.setCurrentUpdateRate(delay);
			break;
		default:
			break;
		}

	}

}
