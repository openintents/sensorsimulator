package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.LinearAccelerationModel;

public class LinearAccelerationView extends SensorView {
	private static final long serialVersionUID = 937382958598356357L;
	private JTextField mLinearAccTextX;
	private JTextField mLinearAccTextY;
	private JTextField mLinearAccTextZ;
	
	public LinearAccelerationView(LinearAccelerationModel model) {
		super(model);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		LinearAccelerationModel linearAccModel = (LinearAccelerationModel) model;
		
		// ////////////////////////////
		// linearAcc
		JPanel linearAccFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Linear Acceleration Value: ", JLabel.LEFT);
		linearAccFieldPane.add(label, c3);

		// x
		mLinearAccTextX = new JTextField(5);
		mLinearAccTextX.setText("" + linearAccModel.getLinearAccelerationX());
		c3.gridx = 1;
		linearAccFieldPane.add(mLinearAccTextX, c3);

		label = new JLabel(linearAccModel.getSI(), JLabel.LEFT);
		c3.gridx = 2;
		linearAccFieldPane.add(label, c3);
		
		
		// y
		c3.gridx = 0;
		c3.gridy = 1;
		mLinearAccTextY = new JTextField(5);
		mLinearAccTextY.setText("" + linearAccModel.getLinearAccelerationY());
		
		c3.gridx = 1;
		linearAccFieldPane.add(mLinearAccTextY, c3);

		label = new JLabel(linearAccModel.getSI(), JLabel.LEFT);
		c3.gridx = 2;
		linearAccFieldPane.add(label, c3);
		
		
		// z
		c3.gridx = 0;
		c3.gridy = 2;
		mLinearAccTextZ = new JTextField(5);
		mLinearAccTextZ.setText("" + linearAccModel.getLinearAccelerationX());
		
		c3.gridx = 1;
		linearAccFieldPane.add(mLinearAccTextZ, c3);

		label = new JLabel(linearAccModel.getSI(), JLabel.LEFT);
		c3.gridx = 2;
		linearAccFieldPane.add(label, c3);

		// Add linear acc panel to settings
		resultPanel.add(linearAccFieldPane);
		return resultPanel;
	}

	public double getAccX() {
		return getSafeDouble(mLinearAccTextX);
	}
	public double getAccY() {
		return getSafeDouble(mLinearAccTextY);
	}
	public double getAccZ() {
		return getSafeDouble(mLinearAccTextZ);
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
		panel1.add(new JLabel("- measures the acceleration without gravity"));
		panel1.add(new JLabel("- has values for all 3 axis"));
		panel1.add(new JLabel("- != 0 when the device is moving"));
		panel1.add(new JLabel("- accelerometer = gravity + linear acceleration"));
		
	
		panel.add(panel1);
		return panel;
	}
}
