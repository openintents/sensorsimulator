package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.PressureModel;

public class PressureView extends SensorView {
	private static final long serialVersionUID = -13895826746028866L;

	public PressureView(PressureModel model) {
		super(model);
	}

	// Pressure
	private JTextField mPressureText;

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		PressureModel pressureModel = (PressureModel) model;
		// ////////////////////////////
		// Pressure (in lux)
		JPanel pressureFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Pressure Value: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		pressureFieldPane.add(label, c3);

		mPressureText = new JTextField(5);
		mPressureText.setText("" + pressureModel.getPressure());
		c3.gridx = 1;
		pressureFieldPane.add(mPressureText, c3);

		label = new JLabel(" lux", JLabel.LEFT);
		c3.gridx = 2;
		pressureFieldPane.add(label, c3);

		// Pressure panel ends

		// Add pressure panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(pressureFieldPane, c2);
		return resultPanel;
	}

	public double getPressure() {
		return getSafeDouble(mPressureText);
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
		panel1.add(new JLabel("- measures the air pressure"));
		panel1.add(new JLabel("- used to predict the weather"));

		JPanel panel2 = new JPanel(new GridLayout(0, 1));
		panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.GRAY), BorderFactory
				.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Pressure values:")));
		panel2.add(new JLabel("Normal Atmospheric Pressure = 1013.25 hPa"));

		panel.add(panel1);
		panel.add(panel2);
		return panel;
	}

}
