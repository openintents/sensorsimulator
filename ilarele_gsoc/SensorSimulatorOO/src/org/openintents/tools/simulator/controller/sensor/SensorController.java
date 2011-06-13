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
	private boolean isFixed;

	public SensorController(final SensorModel model, final SensorView view) {
		this.model = model;
		this.view = view;
		isFixed = false;
		final JButton enableButton = view.getEnabled();
		enableButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isFixed) {
					if (model.isEnabled()) {
						model.setEnabled(false);
						enableButton.setBackground(Global.DISABLE);
						view.setVisible(false);
					} else {
						model.setEnabled(true);
						enableButton.setBackground(Global.ENABLE);
						view.setVisible(true);
					}
				}
			}
		});
		JButton expandBtn = view.getExpandButton();
		expandBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				view.switchExpand();
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
		double rate = view.getCurrentUpdateRate();
		if (rate != 0) {
			model.setUpdateDuration((long) (1000. / rate));
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
			updateEmulatorRefresh();
		} else {
			// This sensor is currently disabled
			out.println("throw IllegalStateException");
		}
	}

	public void updateEmulatorRefresh() {
		long updateEmulatorCount = model.incUpdateEmulatorCount();
		long updateEmulatorTime = model.getEmulatorTime();

		long maxcount = model.getRefreshCount();
		if (maxcount >= 0 && updateEmulatorCount >= maxcount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - updateEmulatorTime)
					/ ((double) maxcount);

			view.setRefreshEmulatorTime(Global.TWO_DECIMAL_FORMAT.format(ms)
					+ " ms");

			updateEmulatorCount = 0;
			model.setUpdateEmulatorTime(newtime);
		}
	}

	public void fixEnabledSensors() {
		isFixed = true;
	}
	
	public void unfixEnabledSensors() {
		isFixed = false;
	}
}
