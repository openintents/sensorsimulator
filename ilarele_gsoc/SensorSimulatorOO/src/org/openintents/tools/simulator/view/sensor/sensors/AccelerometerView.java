package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.AccelerometerModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

public class AccelerometerView extends SensorView {

	private static final long serialVersionUID = 4044072966491754271L;

	// computed values
	private JPanel computedLinearAcceleration;
	private JPanel computedGravity;

	// Linear Acceleration
	private JTextField mGravityConstantText;
	private JTextField mAccelerometerLimitText;
	private JTextField mPixelPerMeterText;
	private JTextField mSpringConstantText;
	private JTextField mDampingConstantText;

	// Gravity
	private JTextField mGravityXText;
	private JTextField mGravityYText;
	private JTextField mGravityZText;

	private JCheckBox mShowAcceleration;

	private WiiAccelerometerView wiiAccelerometerView;

	// Sensors Values
	private JPanel sensorGravityPane;
	private JPanel sensorLinearAccelerationPane;
	
	// Gravity
	private JLabel mSensorGravityXText;
	private JLabel mSensorGravityYText;
	private JLabel mSensorGravityZText;
	
	// Linear Acceleration
	private JLabel mSensorLinearAccelerationXText;
	private JLabel mSensorLinearAccelerationYText;
	private JLabel mSensorLinearAccelerationZText;

	private GravityView gravity;
	private LinearAccelerationView linearAcceleration;

	public AccelerometerView(AccelerometerModel model) {
		super(model);
	}

	public void setRelatedSensors(GravityView gravity,
			LinearAccelerationView linearAcceleration) {
		this.gravity = gravity;
		this.linearAcceleration = linearAcceleration;
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
		computedGravity = new JPanel(new GridBagLayout());
		computedLinearAcceleration = new JPanel(new GridBagLayout());

		// computedGravity
		computedGravity.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Computed Gravity"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Gravity constant g: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		computedGravity.add(label, c3);

		mGravityConstantText = new JTextField(5);
		mGravityConstantText.setText("9.80665");
		c3.gridx = 1;
		computedGravity.add(mGravityConstantText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		computedGravity.add(label, c3);

		label = new JLabel("Accelerometer limit: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		computedGravity.add(label, c3);

		mAccelerometerLimitText = new JTextField(5);
		mAccelerometerLimitText.setText("" + accModel.getAccelLimit());
		c3.gridx = 1;
		computedGravity.add(mAccelerometerLimitText, c3);

		label = new JLabel(" g", JLabel.LEFT);
		c3.gridx = 2;
		computedGravity.add(label, c3);
		
		// gravity x, y, z
		label = new JLabel("x: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		computedGravity.add(label, c3);
		mGravityXText = new JTextField(5);
		mGravityXText.setText("0");
		c3.gridx = 1;
		computedGravity.add(mGravityXText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		computedGravity.add(label, c3);

		label = new JLabel("y: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		computedGravity.add(label, c3);

		mGravityYText = new JTextField(5);
		mGravityYText.setText("0");
		c3.gridx = 1;
		computedGravity.add(mGravityYText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		computedGravity.add(label, c3);

		label = new JLabel("z: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		computedGravity.add(label, c3);

		mGravityZText = new JTextField(5);
		mGravityZText.setText("-9.80665");
		c3.gridx = 1;
		computedGravity.add(mGravityZText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		computedGravity.add(label, c3);
		
		// computedLinearAcceleration
		computedLinearAcceleration.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory
						.createTitledBorder("Computed Linear Acceleration"),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		label = new JLabel("Pixels per meter: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		computedLinearAcceleration.add(label, c3);

		mPixelPerMeterText = new JTextField(5);
		mPixelPerMeterText.setText("3000");
		c3.gridx = 1;
		computedLinearAcceleration.add(mPixelPerMeterText, c3);

		label = new JLabel(" p/m", JLabel.LEFT);
		c3.gridx = 2;
		computedLinearAcceleration.add(label, c3);

		label = new JLabel("Spring constant (k/m) ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		computedLinearAcceleration.add(label, c3);

		mSpringConstantText = new JTextField(5);
		mSpringConstantText.setText("500");
		c3.gridx = 1;
		computedLinearAcceleration.add(mSpringConstantText, c3);

		label = new JLabel(" p/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		computedLinearAcceleration.add(label, c3);

		label = new JLabel("Damping constant: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		computedLinearAcceleration.add(label, c3);

		mDampingConstantText = new JTextField(5);
		mDampingConstantText.setText("50");
		c3.gridx = 1;
		computedLinearAcceleration.add(mDampingConstantText, c3);

		label = new JLabel(" p/s", JLabel.LEFT);
		c3.gridx = 2;
		computedLinearAcceleration.add(label, c3);

		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy++;
		mShowAcceleration = new JCheckBox(SensorModel.SHOW_ACCELERATION);
		mShowAcceleration.setSelected(accModel.isShown());
		mShowAcceleration.setSelected(false);

		computedLinearAcceleration.add(mShowAcceleration, c3);

		c2.gridx = 0;
		c2.gridy = 0;
		resultPanel.add(computedGravity, c2);
		c2.gridy = 1;
		resultPanel.add(computedLinearAcceleration, c2);
		// Accelerometer field panel ends

		// //////////////////////////////
		// Sensor Gravity (in g = m/s^2)
		sensorGravityPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		sensorGravityPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sensor Gravity"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		label = new JLabel("x: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		sensorGravityPane.add(label, c3);

		mSensorGravityXText = new JLabel();
		mSensorGravityXText.setText("0");
		c3.gridx = 1;
		sensorGravityPane.add(mSensorGravityXText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		sensorGravityPane.add(label, c3);

		label = new JLabel("y: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		sensorGravityPane.add(label, c3);

		mSensorGravityYText = new JLabel();
		mSensorGravityYText.setText("0");
		c3.gridx = 1;
		sensorGravityPane.add(mSensorGravityYText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		sensorGravityPane.add(label, c3);

		label = new JLabel("z: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		sensorGravityPane.add(label, c3);

		mSensorGravityZText = new JLabel();
		mSensorGravityZText.setText("-9.80665");
		c3.gridx = 1;
		sensorGravityPane.add(mSensorGravityZText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		sensorGravityPane.add(label, c3);

		// Gravity field panel ends

		// Add gravity field panel to settings
		c2.gridx = 1;
		c2.gridy = 0;
		resultPanel.add(sensorGravityPane, c2);
		// //////////////////////////////
		// Sensor Linear Acceleration(in g = m/s^2)
		sensorLinearAccelerationPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		sensorLinearAccelerationPane.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory
						.createTitledBorder("Sensor Linear Acceleration"),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		label = new JLabel("x: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		sensorLinearAccelerationPane.add(label, c3);
		mSensorLinearAccelerationXText = new JLabel();
		mSensorLinearAccelerationXText .setText("0");
		c3.gridx = 1;
		sensorLinearAccelerationPane.add(mSensorLinearAccelerationXText , c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		sensorLinearAccelerationPane.add(label, c3);

		label = new JLabel("y: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		sensorLinearAccelerationPane.add(label, c3);

		mSensorLinearAccelerationYText = new JLabel();
		mSensorLinearAccelerationYText.setText("0");
		c3.gridx = 1;
		sensorLinearAccelerationPane.add(mSensorLinearAccelerationYText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		sensorLinearAccelerationPane.add(label, c3);

		label = new JLabel("z: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		sensorLinearAccelerationPane.add(label, c3);

		mSensorLinearAccelerationZText = new JLabel();
		mSensorLinearAccelerationZText.setText("-9.80665");
		c3.gridx = 1;
		sensorLinearAccelerationPane.add(mSensorLinearAccelerationZText, c3);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		c3.gridx = 2;
		sensorLinearAccelerationPane.add(label, c3);

		// Gravity field panel ends

		// Add gravity field panel to settings
		c2.gridx = 1;
		c2.gridy = 1;
		c2.gridwidth = 1;
		resultPanel.add(sensorLinearAccelerationPane, c2);

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

	public void setEnablePanel(JComponent parent, boolean enable) {
		for (Component child : parent.getComponents()) {
			child.setEnabled(enable);
		}
	}

	public JPanel getSensorGravityPane() {
		return sensorGravityPane;
	}

	public JPanel getSensorLinearAccelerationPane() {
		return sensorLinearAccelerationPane;
	}

	public JPanel getComputedLinearAcceleration() {
		return computedLinearAcceleration;
	}

	public JPanel getComputedGravity() {
		return computedGravity;
	}

	public GravityView getGravity() {
		return gravity;
	}

	public LinearAccelerationView getLinearAcceleration() {
		return linearAcceleration;
	}
}
