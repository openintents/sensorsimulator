package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.GridLayout;

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

	@SuppressWarnings("unused")
	private WiiAccelerometerModel model;

	public WiiAccelerometerView(WiiAccelerometerModel model) {
		this.model = model;

		mRealDeviceWiimote = new JCheckBox("Use Wii-mote accelerometer");
		mRealDeviceWiimote.setSelected(model.isUsed());

		mRealDevicePath = new JTextField(20);
		mRealDevicePath.setText(model.getDevicePath());

		mRealDeviceOutputLabel = new JLabel("-", JLabel.LEFT);

		setLayout(new GridLayout(0, 1));

		add(mRealDeviceWiimote);
		add(mRealDevicePath);
		add(mRealDeviceOutputLabel);
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
