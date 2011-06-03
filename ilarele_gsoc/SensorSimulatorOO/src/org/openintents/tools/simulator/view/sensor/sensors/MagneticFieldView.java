package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.MagneticFieldModel;

public class MagneticFieldView extends SensorView {
	private static final long serialVersionUID = -4625924984418977091L;
	// Magnetic field
	private JTextField mNorthText;
	private JTextField mEastText;
	private JTextField mVerticalText;

	public MagneticFieldView(MagneticFieldModel model) {
		super(model);
	}

	public double getVertical() {
		return getSafeDouble(mVerticalText);
	}

	public double getEast() {
		return getSafeDouble(mEastText);
	}

	public double getNorth() {
		return getSafeDouble(mNorthText);
	}

	@Override
	public JPanel fillSensorSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		MagneticFieldModel magModel = (MagneticFieldModel) model;
		// //////////////////////////////
		// Magnetic field (in nanoTesla)
		// Values can be found at
		// Default values are for San Francisco.
		JPanel magneticFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel magneticFieldNorthLabel = new JLabel("North component: ",
				JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		magneticFieldPane.add(magneticFieldNorthLabel, c3);

		mNorthText = new JTextField(5);
		mNorthText.setText("" + magModel.getNorth());
		c3.gridx = 1;
		magneticFieldPane.add(mNorthText, c3);

		JLabel nanoTeslaLabel = new JLabel(" nT", JLabel.LEFT);
		c3.gridx = 2;
		magneticFieldPane.add(nanoTeslaLabel, c3);

		JLabel magneticFieldEastLabel = new JLabel("East component: ",
				JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		magneticFieldPane.add(magneticFieldEastLabel, c3);

		mEastText = new JTextField(5);
		mEastText.setText("" + magModel.getEast());
		c3.gridx = 1;
		magneticFieldPane.add(mEastText, c3);

		nanoTeslaLabel = new JLabel(" nT", JLabel.LEFT);
		c3.gridx = 2;
		magneticFieldPane.add(nanoTeslaLabel, c3);

		JLabel magneticFieldVerticalLabel = new JLabel("Vertical component: ",
				JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		magneticFieldPane.add(magneticFieldVerticalLabel, c3);

		mVerticalText = new JTextField(5);
		mVerticalText.setText("" + magModel.getVertical());
		c3.gridx = 1;
		magneticFieldPane.add(mVerticalText, c3);

		JLabel label = new JLabel(" nT", JLabel.LEFT);
		c3.gridx = 2;
		magneticFieldPane.add(label, c3);

		// Magnetic field panel ends

		// Add magnetic field panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(magneticFieldPane, c2);
		return resultPanel;
	}
}
