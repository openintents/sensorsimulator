package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.GravityModel;

public class GravityView extends SensorView {
	private static final long serialVersionUID = -6006181483029485632L;
	private JTextField mGravityTextX;
	private JTextField mGravityTextY;
	private JTextField mGravityTextZ;

	public GravityView(GravityModel model) {
		super(model);
	}

	@Override
	public JPanel fillSensorSettingsPanel() {
		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GravityModel gravityModel = (GravityModel) model;

		// ////////////////////////////
		// gravity
		JPanel gravityFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Gravity Value: ", JLabel.LEFT);
		gravityFieldPane.add(label, c3);

		// x
		mGravityTextX = new JTextField(5);
		mGravityTextX.setText("" + gravityModel.getGravityX());
		c3.gridx = 1;
		gravityFieldPane.add(mGravityTextX, c3);

		label = new JLabel(gravityModel.getSI(), JLabel.LEFT);
		c3.gridx = 2;
		gravityFieldPane.add(label, c3);

		// y
		c3.gridx = 0;
		c3.gridy = 1;
		mGravityTextY = new JTextField(5);
		mGravityTextY.setText("" + gravityModel.getGravityY());

		c3.gridx = 1;
		gravityFieldPane.add(mGravityTextY, c3);

		label = new JLabel(gravityModel.getSI(), JLabel.LEFT);
		c3.gridx = 2;
		gravityFieldPane.add(label, c3);

		// z
		c3.gridx = 0;
		c3.gridy = 2;
		mGravityTextZ = new JTextField(5);
		mGravityTextZ.setText("" + gravityModel.getGravityZ());

		c3.gridx = 1;
		gravityFieldPane.add(mGravityTextZ, c3);

		label = new JLabel(gravityModel.getSI(), JLabel.LEFT);
		c3.gridx = 2;
		gravityFieldPane.add(label, c3);

		// Add gravity panel to settings
		resultPanel.add(gravityFieldPane);
		return resultPanel;
	}

	public double getGravityX() {
		return getSafeDouble(mGravityTextX);
	}

	public double getGravityY() {
		return getSafeDouble(mGravityTextY);
	}

	public double getGravityZ() {
		return getSafeDouble(mGravityTextZ);
	}
}
