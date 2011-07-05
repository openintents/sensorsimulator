package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.RotationVectorModel;
import org.openintents.tools.simulator.model.telnet.Vector;

public class RotationVectorView extends SensorView {
	private static final long serialVersionUID = 5156654905500075165L;
	private JTextField mRotationVectorTextX;
	private JTextField mRotationVectorTextY;
	private JTextField mRotationVectorTextZ;

	public RotationVectorView(RotationVectorModel model) {
		super(model);
	}

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		RotationVectorModel rotationModel = (RotationVectorModel) model;

		// ////////////////////////////
		// rotation
		JPanel rotationFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy = 0;

		// x

		JLabel label = new JLabel("θ(x): ", JLabel.LEFT);
		rotationFieldPane.add(label, c3);
		mRotationVectorTextX = new JTextField(5);
		mRotationVectorTextX.setText("" + rotationModel.getRotationVectorX());
		c3.gridx = 1;
		rotationFieldPane.add(mRotationVectorTextX, c3);

		label = new JLabel(rotationModel.getSI(), JLabel.LEFT);
		c3.gridx = 2;
		rotationFieldPane.add(label, c3);

		// y
		label = new JLabel("θ(y): ", JLabel.LEFT);
		c3.gridx = 0;
		c3.gridy = 1;
		rotationFieldPane.add(label, c3);

		mRotationVectorTextY = new JTextField(5);
		mRotationVectorTextY.setText("" + rotationModel.getRotationVectorY());
		c3.gridx = 1;
		rotationFieldPane.add(mRotationVectorTextY, c3);

		label = new JLabel(rotationModel.getSI(), JLabel.LEFT);
		c3.gridx = 2;
		rotationFieldPane.add(label, c3);

		// z
		c3.gridx = 0;
		c3.gridy = 2;
		label = new JLabel("θ(z): ", JLabel.LEFT);
		rotationFieldPane.add(label, c3);

		mRotationVectorTextZ = new JTextField(5);
		mRotationVectorTextZ.setText("" + rotationModel.getRotationVectorZ());
		c3.gridx = 1;
		rotationFieldPane.add(mRotationVectorTextZ, c3);

		label = new JLabel(rotationModel.getSI(), JLabel.LEFT);
		c3.gridx = 2;
		rotationFieldPane.add(label, c3);

		// Add rotation panel to settings
		resultPanel.add(rotationFieldPane);
		return resultPanel;
	}

	public double getRotationVectorX() {
		return getSafeDouble(mRotationVectorTextX);
	}

	public double getRotationVectorY() {
		return getSafeDouble(mRotationVectorTextY);
	}

	public double getRotationVectorZ() {
		return getSafeDouble(mRotationVectorTextZ);
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
		panel1.add(new JLabel("- measures the angle of rotation around axis(θ)"));
		panel1.add(new JLabel("- has values for all 3 axis (sin(θ/2))"));

		panel.add(panel1);
		return panel;
	}

	public void setRotationVector(Vector v) {
		mRotationVectorTextX.setText("" + v.x);
		mRotationVectorTextY.setText("" + v.y);
		mRotationVectorTextZ.setText("" + v.z);
	}
}
