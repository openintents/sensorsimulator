package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.WiiAccelerometerModel;

public class WiiAccelerometerView extends JPanel {
	private static final long serialVersionUID = 7293246659297862528L;
	// Real device bridge
	private JCheckBox mRealDeviceWiimote;
	private JTextField mRealDevicePath;
	private JLabel mRealDeviceOutputLabel;

	private WiiAccelerometerModel model;

	public WiiAccelerometerView(WiiAccelerometerModel model) {
		this.model = model;

		mRealDeviceWiimote = new JCheckBox("Use Wii-mote accelerometer");
		mRealDeviceWiimote.setSelected(model.isUsed());

		mRealDevicePath = new JTextField(20);
		mRealDevicePath.setText(model.getDevicePath());

		mRealDeviceOutputLabel = new JLabel("-", JLabel.LEFT);
	}

	public void fillPane(JPanel realSensorBridgeFieldPane) {
		realSensorBridgeFieldPane.setLayout(new GridBagLayout());
		GridBagConstraints layout = new GridBagConstraints();
		layout.fill = GridBagConstraints.HORIZONTAL;
		layout.anchor = GridBagConstraints.NORTHWEST;
		layout.gridwidth = 1;
		layout.gridy = 0;
		layout.gridx = 0;
		realSensorBridgeFieldPane.add(mRealDeviceWiimote, layout);

		layout.gridx = 0;
		layout.gridy++;
		realSensorBridgeFieldPane.add(mRealDevicePath, layout);

		layout.gridx = 0;
		layout.gridy++;
		realSensorBridgeFieldPane.add(mRealDeviceOutputLabel, layout);
	}

	public boolean isSelected() {
		return mRealDeviceWiimote.isSelected();
	}

	public void setOutputBackground(Color color) {
		mRealDeviceOutputLabel.setBackground(color);
	}

	public void setOutput(String message) {
		mRealDeviceOutputLabel.setText(message);
	}

	public String getRealDevicePath() {
		return mRealDevicePath.getText().toString();
	}
}
