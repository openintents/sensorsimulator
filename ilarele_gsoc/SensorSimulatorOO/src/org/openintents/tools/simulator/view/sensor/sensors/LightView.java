package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
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
	public JPanel fillSensorSettingsPanel() {
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
		mLightText.setText("" + lightModel.getLight());
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

}
