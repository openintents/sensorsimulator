package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

public class LinearAccelerationView extends SensorView {
	private static final long serialVersionUID = 937382958598356357L;

	// Linear Acceleration
	private JTextField mPixelPerMeterText;
	private JTextField mSpringConstantText;
	private JTextField mDampingConstantText;

	public LinearAccelerationView(LinearAccelerationModel model) {
		super(model);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		LinearAccelerationModel linearAccModel = (LinearAccelerationModel) model;

		JPanel linearAccFieldPane = new JPanel(new GridLayout(0, 3));

		linearAccFieldPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"For Computing Linear Acc")));

		JLabel label = new JLabel("Pixels per meter: ", JLabel.LEFT);
		linearAccFieldPane.add(label);

		mPixelPerMeterText = new JTextField(5);
		mPixelPerMeterText.setText("" + linearAccModel.getPixelsPerMeter());
		linearAccFieldPane.add(mPixelPerMeterText);

		label = new JLabel(" p/m", JLabel.LEFT);
		linearAccFieldPane.add(label);

		label = new JLabel("Spring constant:", JLabel.LEFT);
		linearAccFieldPane.add(label);

		mSpringConstantText = new JTextField(5);
		mSpringConstantText.setText("" + linearAccModel.getSpringConstant());
		linearAccFieldPane.add(mSpringConstantText);

		label = new JLabel(" p/s" + SensorModel.SQUARED, JLabel.LEFT);
		linearAccFieldPane.add(label);

		label = new JLabel("Damping constant: ", JLabel.LEFT);
		linearAccFieldPane.add(label);

		mDampingConstantText = new JTextField(5);
		mDampingConstantText.setText("" + linearAccModel.getDampingConstant());
		linearAccFieldPane.add(mDampingConstantText);

		label = new JLabel(" p/s", JLabel.LEFT);
		linearAccFieldPane.add(label);

		resultPanel.add(linearAccFieldPane);
		return resultPanel;
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
		panel1.add(new JLabel("- measures the acceleration without gravity"));
		panel1.add(new JLabel("- has values for all 3 axis"));
		panel1.add(new JLabel("- != 0 when the device is moving"));
		panel1.add(new JLabel("- accelerometer = gravity + linear acceleration"));

		panel.add(panel1);
		return panel;
	}

	public double getPixelsPerMeter() {
		return getSafeDouble(mPixelPerMeterText, 3000);
	}

	public double getSpringConstant() {
		return getSafeDouble(mSpringConstantText, 500);
	}

	public double getDampingConstant() {
		return getSafeDouble(mDampingConstantText, 50);
	}

}
