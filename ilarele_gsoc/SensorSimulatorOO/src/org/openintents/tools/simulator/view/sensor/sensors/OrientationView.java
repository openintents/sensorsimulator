package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.OrientationModel;
import org.openintents.tools.simulator.view.sensor.DeviceView;

public class OrientationView extends SensorView {
	private static final long serialVersionUID = -7668687597011522775L;
	private DeviceView mobile;
	private JPanel sensorSpecificPane;

	public OrientationView(OrientationModel model, SensorSimulatorModel sensorSimulatorModel) {
		super(model);
		mEnabled.setSelected(true);
		mobile = new DeviceView(sensorSimulatorModel);
		sensorSpecificPane.add(mobile);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		sensorSpecificPane = new JPanel();
		sensorSpecificPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return sensorSpecificPane;
	}

	@Override
	protected JPanel getSensorSpecificHelp() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JPanel panel1 = new JPanel(new GridLayout(0, 1));
		panel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.GRAY), BorderFactory
				.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						model.getName())));
		panel1.add(new JLabel("- measures the device 'direction'"));
		panel1.add(new JLabel("- has 3 values: yaw(rotation around y-axis)"));
		panel1.add(new JLabel("                pitch(rotation around x-axis)"));
		panel1.add(new JLabel("                roll(rotation around z-axis)"));

		panel.add(panel1);
		return panel;
	}

	public DeviceView getDeviceView() {
		return mobile;
	}
}
