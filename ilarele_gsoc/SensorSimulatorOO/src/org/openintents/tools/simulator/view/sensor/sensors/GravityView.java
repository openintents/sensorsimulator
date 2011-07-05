package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.GravityModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

public class GravityView extends SensorView {
	private static final long serialVersionUID = -6006181483029485632L;
	
	// Gravity
	private JTextField mGravityConstantText;
	private JTextField mAccelerometerLimitText;
	
	
	public GravityView(GravityModel model) {
		super(model);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GravityModel gravityModel = (GravityModel) model;
		
		JPanel gravityFieldPane = new JPanel(new GridLayout(0, 3));
		gravityFieldPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"For Computing Gravity")));
		JLabel label = new JLabel("Constant g: ", JLabel.LEFT);
		gravityFieldPane.add(label);

		mGravityConstantText = new JTextField(5);
		mGravityConstantText.setText("" + gravityModel.getGravityConstant());
		gravityFieldPane.add(mGravityConstantText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		gravityFieldPane.add(label);

		label = new JLabel("Acceleration limit: ", JLabel.LEFT);
		gravityFieldPane.add(label);

		mAccelerometerLimitText = new JTextField(5);
		mAccelerometerLimitText.setText("" + gravityModel.getAccelLimit());
		gravityFieldPane.add(mAccelerometerLimitText);

		label = new JLabel(" g", JLabel.LEFT);
		gravityFieldPane.add(label);
		return gravityFieldPane;
	}


	@Override
	protected JPanel getSensorSpecificHelp() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		JPanel panel1 = new JPanel(new GridLayout(0, 1));
		panel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.GRAY), BorderFactory
				.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Description")));
		panel1.add(new JLabel(
				"- the force of attraction beteen physical bodies"));
		panel1.add(new JLabel("- has values for all 3 axis"));
		panel1.add(new JLabel(
				"- equal with the acceleration if the device is not moving"));

		JPanel panel2 = new JPanel(new GridLayout(0, 1));
		panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.GRAY), BorderFactory
				.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Gravity values in various locations")));
		panel2.add(new JLabel("Earth = 9.80665 m/s^2"));
		panel2.add(new JLabel("Sun = 275.0 m/s^2"));
		panel2.add(new JLabel("Moon = 1.6f m/s^2"));

		panel.add(panel2);
		panel.add(panel2);
		return panel;
	}

	public double getGravityConstant() {
		return getSafeDouble(mGravityConstantText, 9.80665);
	}
}
