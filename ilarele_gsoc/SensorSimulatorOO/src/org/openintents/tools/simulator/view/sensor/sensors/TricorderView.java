package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.openintents.tools.simulator.model.sensor.sensors.TricorderModel;

public class TricorderView extends SensorView {
	private static final long serialVersionUID = 6914459228248630952L;

	public TricorderView(TricorderModel model) {
		super(model);
	}

	@Override
	public JPanel fillSensorSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		return resultPanel;
	}
}
