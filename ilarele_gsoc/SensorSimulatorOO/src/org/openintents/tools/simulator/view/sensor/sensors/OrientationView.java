package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;

public class OrientationView extends SensorView {
	private static final long serialVersionUID = -7668687597011522775L;

	public OrientationView(OrientationModel model) {
		super(model);
		mEnabled.setSelected(true);
	}

	@Override
	public JPanel fillSensorSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		return resultPanel;
	}
}
