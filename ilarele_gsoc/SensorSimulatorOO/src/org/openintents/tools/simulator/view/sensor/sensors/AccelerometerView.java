package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

public class AccelerometerView extends SensorView {

	private static final long serialVersionUID = 4044072966491754271L;
	// Accelerometer
	private JTextField mGravityConstantText;
	private JTextField mAccelerometerLimitText;
	private JTextField mPixelPerMeterText;
	private JTextField mSpringConstantText;
	private JTextField mDampingConstantText;
	private JCheckBox mShowAcceleration;

	// Gravity
	private JTextField mGravityXText;
	private JTextField mGravityYText;
	private JTextField mGravityZText;

	private WiiAccelerometerView wiiAccelerometerView;

	public AccelerometerView(AccelerometerModel model) {
		super(model);
	}

	@Override
	public JPanel fillSensorSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();

		AccelerometerModel accModel = ((AccelerometerModel) model);
		JPanel accelerometerFieldPane = new JPanel(new GridBagLayout());
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Gravity constant g: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		accelerometerFieldPane.add(label, c3);

		mGravityConstantText = new JTextField(5);
		mGravityConstantText.setText("9.80665");
		c3.gridx = 1;
		accelerometerFieldPane.add(mGravityConstantText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		accelerometerFieldPane.add(label, c3);

		label = new JLabel("Accelerometer limit: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		accelerometerFieldPane.add(label, c3);

		mAccelerometerLimitText = new JTextField(5);
		mAccelerometerLimitText.setText("" + accModel.getAccelLimit());
		c3.gridx = 1;
		accelerometerFieldPane.add(mAccelerometerLimitText, c3);

		label = new JLabel(" g", JLabel.LEFT);
		c3.gridx = 2;
		accelerometerFieldPane.add(label, c3);

		label = new JLabel("Pixels per meter: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		accelerometerFieldPane.add(label, c3);

		mPixelPerMeterText = new JTextField(5);
		mPixelPerMeterText.setText("3000");
		c3.gridx = 1;
		accelerometerFieldPane.add(mPixelPerMeterText, c3);

		label = new JLabel(" p/m", JLabel.LEFT);
		c3.gridx = 2;
		accelerometerFieldPane.add(label, c3);

		label = new JLabel("Spring constant (k/m) ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		accelerometerFieldPane.add(label, c3);

		mSpringConstantText = new JTextField(5);
		mSpringConstantText.setText("500");
		c3.gridx = 1;
		accelerometerFieldPane.add(mSpringConstantText, c3);

		label = new JLabel(" p/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		accelerometerFieldPane.add(label, c3);

		label = new JLabel("Damping constant: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		accelerometerFieldPane.add(label, c3);

		mDampingConstantText = new JTextField(5);
		mDampingConstantText.setText("50");
		c3.gridx = 1;
		accelerometerFieldPane.add(mDampingConstantText, c3);

		label = new JLabel(" p/s", JLabel.LEFT);
		c3.gridx = 2;
		accelerometerFieldPane.add(label, c3);

		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy++;
		mShowAcceleration = new JCheckBox(SensorModel.SHOW_ACCELERATION);
		mShowAcceleration.setSelected(accModel.isShown());
		mShowAcceleration.setSelected(false);

		accelerometerFieldPane.add(mShowAcceleration, c3);

		// Accelerometer field panel ends

		// Add accelerometer field panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(accelerometerFieldPane, c2);

		// //////////////////////////////
		// Gravity (in g = m/s^2)
		JPanel gravityFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		gravityFieldPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Gravity"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		label = new JLabel("x: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		gravityFieldPane.add(label, c3);

		mGravityXText = new JTextField(5);
		mGravityXText.setText("0");
		c3.gridx = 1;
		gravityFieldPane.add(mGravityXText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		gravityFieldPane.add(label, c3);

		label = new JLabel("y: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		gravityFieldPane.add(label, c3);

		mGravityYText = new JTextField(5);
		mGravityYText.setText("0");
		c3.gridx = 1;
		gravityFieldPane.add(mGravityYText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		gravityFieldPane.add(label, c3);

		label = new JLabel("z: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		gravityFieldPane.add(label, c3);

		mGravityZText = new JTextField(5);
		mGravityZText.setText("-9.80665");
		c3.gridx = 1;
		gravityFieldPane.add(mGravityZText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		gravityFieldPane.add(label, c3);

		// Gravity field panel ends

		// Add gravity field panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(gravityFieldPane, c2);

		// /////////////////////////////
		// Real sensor bridge
		JPanel realSensorBridgeFieldPane = new JPanel(new GridBagLayout());
		realSensorBridgeFieldPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Real sensor bridge"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		wiiAccelerometerView = new WiiAccelerometerView(
				accModel.getRealDeviceBridgeAddon());
		wiiAccelerometerView.fillPane(realSensorBridgeFieldPane);
		realSensorBridgeFieldPane.add(wiiAccelerometerView);

		// // Add real sensor bridge field panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(realSensorBridgeFieldPane, c2);
		return resultPanel;
	}

	public JCheckBox getShow() {
		return mShowAcceleration;
	}

	public double getGravityConstant() {
		return getSafeDouble(mGravityConstantText, 9.80665);
	}

	public double getAccelerometerLimit() {
		return getSafeDouble(mAccelerometerLimitText);
	}

	public double getPixelsPerMeter() {
		return getSafeDouble(mPixelPerMeterText, 3000);
	}

	public double getSpringConstant() {
		return getSafeDouble(mSpringConstantText, 500);
	}

	public double getDampingConstant() {
		return getSafeDouble(mDampingConstantText, 50);
	}

	public double getGravityX() {
		return getSafeDouble(mGravityXText);
	}

	public double getGravityY() {
		return getSafeDouble(mGravityYText);
	}

	public double getGravityZ() {
		return getSafeDouble(mGravityZText);
	}

	public JCheckBox getShowAcceleration() {
		return mShowAcceleration;
	}

	public WiiAccelerometerView getRealDeviceBridgeAddon() {
		return wiiAccelerometerView;
	}

	public void setRealDeviceBridgeAddonOutput(String line) {
		wiiAccelerometerView.setOutput(line);
	}

	public String getWiiPath() {
		return wiiAccelerometerView.getRealDevicePath();
	}

	public void setWiiOutput(String wiiStatus) {
		wiiAccelerometerView.setOutput(wiiStatus);
	}
}
