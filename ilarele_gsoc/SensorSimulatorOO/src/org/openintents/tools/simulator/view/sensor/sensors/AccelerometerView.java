package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.Global;
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
	public JPanel fillSensorSpecificSettingsPanel() {
		AccelerometerModel accModel = ((AccelerometerModel) model);
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Sensor Specific Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		mShowAcceleration = new JCheckBox(SensorModel.SHOW_ACCELERATION);
		mShowAcceleration.setSelected(accModel.isShown());
		mShowAcceleration.setAlignmentX(Component.RIGHT_ALIGNMENT);
		resultPanel.add(mShowAcceleration);

		// computedGravity
		fillComputedGravity(accModel);
		resultPanel.add(computedGravity);

		// Sensor Gravity (in g = m/s^2)
		fillSensorGravity();
		resultPanel.add(sensorGravityPane);

		// computedLinearAcceleration
		fillComputedLinearAcceleration(accModel);
		resultPanel.add(computedLinearAcceleration);

		// Sensor Linear Acceleration(in g = m/s^2)
		fillSensorLinearAcceleration();
		resultPanel.add(sensorLinearAccelerationPane);

		// Real sensor bridge
		JPanel realSensorBridgeFieldPane = fillRealSensorBridge(accModel);
		resultPanel.add(realSensorBridgeFieldPane);

		return resultPanel;
	}

	private JPanel fillRealSensorBridge(AccelerometerModel accModel) {
		JPanel realSensorBridgeFieldPane = new JPanel(new GridLayout(0, 1));
		realSensorBridgeFieldPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Real sensor bridge")));

		wiiAccelerometerView = new WiiAccelerometerView(
				accModel.getRealDeviceBridgeAddon());
		realSensorBridgeFieldPane.add(wiiAccelerometerView);
		return realSensorBridgeFieldPane;
	}

	private void fillSensorLinearAcceleration() {
		sensorLinearAccelerationPane = new JPanel(new GridLayout(0, 3));

		sensorLinearAccelerationPane
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory
						.createMatteBorder(2, 0, 0, 0, Color.GRAY),
						BorderFactory.createTitledBorder(
								BorderFactory.createEmptyBorder(3, 0, 15, 0),
								"Sensor Linear Acceleration")));

		JLabel label = new JLabel("x: ", JLabel.LEFT);
		sensorLinearAccelerationPane.add(label);
		mSensorLinearAccelerationXText = new JLabel();
		mSensorLinearAccelerationXText.setText("0");
		sensorLinearAccelerationPane.add(mSensorLinearAccelerationXText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		sensorLinearAccelerationPane.add(label);

		label = new JLabel("y: ", JLabel.LEFT);
		sensorLinearAccelerationPane.add(label);

		mSensorLinearAccelerationYText = new JLabel();
		mSensorLinearAccelerationYText.setText("0");
		sensorLinearAccelerationPane.add(mSensorLinearAccelerationYText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		sensorLinearAccelerationPane.add(label);

		label = new JLabel("z: ", JLabel.LEFT);
		sensorLinearAccelerationPane.add(label);

		mSensorLinearAccelerationZText = new JLabel();
		mSensorLinearAccelerationZText.setText("-9.80665");
		sensorLinearAccelerationPane.add(mSensorLinearAccelerationZText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		sensorLinearAccelerationPane.add(label);
	}

	private void fillComputedLinearAcceleration(AccelerometerModel accModel) {
		computedLinearAcceleration = new JPanel(new GridLayout(0, 3));

		computedLinearAcceleration
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory
						.createMatteBorder(2, 0, 0, 0, Color.GRAY),
						BorderFactory.createTitledBorder(
								BorderFactory.createEmptyBorder(3, 0, 15, 0),
								"For Computing Linear Acc")));

		JLabel label = new JLabel("Pixels per meter: ", JLabel.LEFT);
		computedLinearAcceleration.add(label);

		mPixelPerMeterText = new JTextField(5);
		mPixelPerMeterText.setText("3000");
		computedLinearAcceleration.add(mPixelPerMeterText);

		label = new JLabel(" p/m", JLabel.LEFT);
		computedLinearAcceleration.add(label);

		label = new JLabel("Spring constant:", JLabel.LEFT);
		computedLinearAcceleration.add(label);

		mSpringConstantText = new JTextField(5);
		mSpringConstantText.setText("500");
		computedLinearAcceleration.add(mSpringConstantText);

		label = new JLabel(" p/s" + SensorModel.SQUARED, JLabel.LEFT);
		computedLinearAcceleration.add(label);

		label = new JLabel("Damping constant: ", JLabel.LEFT);
		computedLinearAcceleration.add(label);

		mDampingConstantText = new JTextField(5);
		mDampingConstantText.setText("50");
		computedLinearAcceleration.add(mDampingConstantText);

		label = new JLabel(" p/s", JLabel.LEFT);
		computedLinearAcceleration.add(label);
	}

	private void fillSensorGravity() {
		sensorGravityPane = new JPanel(new GridLayout(0, 3));
		sensorGravityPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"Sensor Gravity")));

		JLabel label = new JLabel("x: ", JLabel.LEFT);
		sensorGravityPane.add(label);

		mSensorGravityXText = new JLabel();
		mSensorGravityXText.setText("0");
		sensorGravityPane.add(mSensorGravityXText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		sensorGravityPane.add(label);

		label = new JLabel("y: ", JLabel.LEFT);
		sensorGravityPane.add(label);

		mSensorGravityYText = new JLabel();
		mSensorGravityYText.setText("0");
		sensorGravityPane.add(mSensorGravityYText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		sensorGravityPane.add(label);

		label = new JLabel("z: ", JLabel.LEFT);
		sensorGravityPane.add(label);

		mSensorGravityZText = new JLabel();
		mSensorGravityZText.setText("-9.80665");
		sensorGravityPane.add(mSensorGravityZText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		sensorGravityPane.add(label);
	}

	private void fillComputedGravity(AccelerometerModel accModel) {
		computedGravity = new JPanel(new GridLayout(0, 3));

		computedGravity.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0),
						"For Computing Gravity")));
		JLabel label = new JLabel("Constant g: ", JLabel.LEFT);
		computedGravity.add(label);

		mGravityConstantText = new JTextField(5);
		mGravityConstantText.setText("9.80665");
		computedGravity.add(mGravityConstantText);

		label = new JLabel(" m/s" + SensorModel.SQUARED, JLabel.LEFT);
		computedGravity.add(label);

		label = new JLabel("Acceleration limit: ", JLabel.LEFT);
		computedGravity.add(label);

		mAccelerometerLimitText = new JTextField(5);
		mAccelerometerLimitText.setText("" + accModel.getAccelLimit());
		computedGravity.add(mAccelerometerLimitText);

		label = new JLabel(" g", JLabel.LEFT);
		computedGravity.add(label);
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

	public void setSensorGravityX(double readGravityX) {
		mSensorGravityXText.setText(Global.TWO_DECIMAL_FORMAT.format(readGravityX));
	}

	public void setSensorGravityY(double readGravityY) {
		mSensorGravityYText.setText(Global.TWO_DECIMAL_FORMAT.format(readGravityY));
	}

	public void setSensorGravityZ(double readGravityZ) {
		mSensorGravityZText.setText(Global.TWO_DECIMAL_FORMAT.format(readGravityZ));
	}

	public void setSensorLinearAccX(double value) {
		mSensorLinearAccelerationXText.setText(Global.TWO_DECIMAL_FORMAT.format(value));
	}
	public void setSensorLinearAccY(double value) {
		mSensorLinearAccelerationYText.setText(Global.TWO_DECIMAL_FORMAT.format(value));
	}
	public void setSensorLinearAccZ(double value) {
		mSensorLinearAccelerationZText.setText(Global.TWO_DECIMAL_FORMAT.format(value));
	}

	public void setCurrentUpdateRate(int updatesPerSecond) {
		switch (updatesPerSecond) {
		case SensorModel.DELAY_MS_FASTEST:
			mCurrentUpdateRateText.setText(SensorModel.SENSOR_DELAY_FASTEST);
			break;
		case SensorModel.DELAY_MS_GAME:
			mCurrentUpdateRateText.setText(SensorModel.SENSOR_DELAY_GAME);
			break;
		case SensorModel.DELAY_MS_NORMAL:
			mCurrentUpdateRateText.setText(SensorModel.SENSOR_DELAY_NORMAL);
			break;
		case SensorModel.DELAY_MS_UI:
			mCurrentUpdateRateText.setText(SensorModel.SENSOR_DELAY_UI);
			break;
			default:
				mCurrentUpdateRateText.setText("Wrong update rate!");
				break;
		}
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
		panel1.add(new JLabel("- measures the acceleration applied to the device"));
		panel1.add(new JLabel("- has values for all 3 axis"));
		panel1.add(new JLabel("- accelerometer = gravity + linear acceleration"));
		
	
		panel.add(panel1);
		return panel;
	}
}
