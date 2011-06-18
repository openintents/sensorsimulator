package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
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
	public JPanel fillSensorSettingsPanel() {
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


}
