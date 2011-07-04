package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.model.sensor.sensors.TemperatureModel;

public class TemperatureView extends SensorView {
	private static final long serialVersionUID = 1000179101533155817L;

	public TemperatureView(TemperatureModel model) {
		super(model);
	}

	// Temperature
	private JTextField mTemperatureText;

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		TemperatureModel tempModel = (TemperatureModel) model;

		// //////////////////////////////
		// Temperature (in �C: Centigrade Celsius)
		JPanel temperatureFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Temperature: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		temperatureFieldPane.add(label, c3);

		mTemperatureText = new JTextField(5);
		mTemperatureText.setText("" + tempModel.getTemperature());
		c3.gridx = 1;
		temperatureFieldPane.add(mTemperatureText, c3);

		label = new JLabel(" " + SensorModel.DEGREES + "C", JLabel.LEFT);
		c3.gridx = 2;
		temperatureFieldPane.add(label, c3);

		// Temperature panel ends

		// Add temperature panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(temperatureFieldPane, c2);
		return resultPanel;
	}

	public double getTemperature() {
		return getSafeDouble(mTemperatureText);
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
		panel1.add(new JLabel("- measures the atmospheric temperature"));
		
		panel.add(panel1);
		return panel;
	}
}
