package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;

public class OrientationView extends SensorView {
	private static final long serialVersionUID = -7668687597011522775L;

	public OrientationView(OrientationModel model) {
		super(model);
		mEnabled.setSelected(true);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		return resultPanel;
	}

	@Override
	protected JPanel getSensorSpecificHelp() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		JPanel panel1 = new JPanel(new GridLayout(0, 1));
		panel1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Description")));
		panel1.add(new JLabel("- measures the device 'direction'"));
		panel1.add(new JLabel("- has 3 values: azimuth, pitch, roll"));
		
	
		panel.add(panel1);
		return panel;
	}
}
