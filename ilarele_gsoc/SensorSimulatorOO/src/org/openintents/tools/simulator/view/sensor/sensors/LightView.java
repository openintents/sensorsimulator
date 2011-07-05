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

import org.openintents.tools.simulator.model.sensor.sensors.LightModel;

public class LightView extends SensorView {
	private static final long serialVersionUID = 3945184157589120119L;

	public LightView(LightModel model) {
		super(model);
	}

	// Light
	private JTextField mLightText;

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		LightModel lightModel = (LightModel) model;
		// ////////////////////////////
		// Light (in lux)
		JPanel lightFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Light Value: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		lightFieldPane.add(label, c3);

		mLightText = new JTextField(5);
		mLightText.setText("" + lightModel.getReadLight());
		c3.gridx = 1;
		lightFieldPane.add(mLightText, c3);

		label = new JLabel(" lux", JLabel.LEFT);
		c3.gridx = 2;
		lightFieldPane.add(label, c3);

		// Light panel ends

		// Add light panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(lightFieldPane, c2);
		return resultPanel;
	}

	public double getLight() {
		return getSafeDouble(mLightText);
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
		panel1.add(new JLabel("- The intensity of light"));

		panel.add(panel1);

		JPanel panel2 = new JPanel(new GridLayout(0, 1));
		panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(2, 0, 0, 0, Color.GRAY), BorderFactory
				.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Luminance values in universe")));
		panel2.add(new JLabel("Sunrise = 400 lux"));
		panel2.add(new JLabel("Cloudy Day = 100 lux"));
		panel2.add(new JLabel("Night with Fullmoon = 0.25 lux"));
		panel2.add(new JLabel("Night without Moon = 0.001 lux"));

		panel.add(panel2);

		return panel;
	}

}
