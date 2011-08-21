/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * 09/Apr/08 Dale Thatcher <openintents at dalethatcher dot com>
 *           Added wii-mote data collection.
 */

package org.openintents.tools.simulator.view.sensor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.SensorsScenario;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.view.gui.util.SensorButton;
import org.openintents.tools.simulator.view.sensor.sensors.AccelerometerView;
import org.openintents.tools.simulator.view.sensor.sensors.BarcodeReaderView;
import org.openintents.tools.simulator.view.sensor.sensors.GravityView;
import org.openintents.tools.simulator.view.sensor.sensors.GyroscopeView;
import org.openintents.tools.simulator.view.sensor.sensors.LightView;
import org.openintents.tools.simulator.view.sensor.sensors.LinearAccelerationView;
import org.openintents.tools.simulator.view.sensor.sensors.MagneticFieldView;
import org.openintents.tools.simulator.view.sensor.sensors.OrientationView;
import org.openintents.tools.simulator.view.sensor.sensors.PressureView;
import org.openintents.tools.simulator.view.sensor.sensors.ProximityView;
import org.openintents.tools.simulator.view.sensor.sensors.RotationVectorView;
import org.openintents.tools.simulator.view.sensor.sensors.SensorView;
import org.openintents.tools.simulator.view.sensor.sensors.TemperatureView;

/**
 * 
 * SensorSimulatorView keeps the GUI of the SensorSimulator.
 * 
 * SensorSimulator simulates various sensors. An Android application can connect
 * through TCP/IP with the settings shown to the SensorSimulator to simulate
 * accelerometer, compass, orientation sensor, and thermometer.
 * 
 * @author Peli
 * @author Josip Balic
 * @author ilarele
 */
public class SensorSimulatorView extends JPanel {
	private static final long serialVersionUID = -587503580193069930L;

	// port for sensor simulation
	private JTextField mSensorPortText;
	private JButton mSensorPortButton;

	// Field for socket related output:
	// private JScrollPane areaScrollPane;
	private JTextPane mMessageTextArea;

	// Field for sensor simulator data output:
	private JTextArea mTextAreaSensorData;

	// Settings
	private JTextField mUpdateText;
	private JTextField mRefreshCountText;
	private JLabel mRefreshSensorsLabel;

	private final SensorSimulatorModel mModel;
	private final ArrayList<SensorView> mSensors;

	private JTabbedPane mSensorsTabbedPane;

	private AllSensorsView mAllSensorsTab;

	private final DeviceView mMobile;

	private JPanel mSettingsPanel;

	protected SensorButton mLastPressedSensor;

	private JPanel mSensorsButtonsPanel;

	private JTextField mSaveTime;
	private JTextField mPlaybackTime;

	public SensorSimulatorView(SensorSimulatorModel model) {
		mModel = model;
		setLayout(new BorderLayout());

		// sensors
		mSensors = new ArrayList<SensorView>();
		mSensors.add(new AccelerometerView(model.getAccelerometer()));
		mSensors.add(new MagneticFieldView(model.getMagneticField()));
		mSensors.add(new OrientationView(model.getOrientation(), model));
		mSensors.add(new TemperatureView(model.getTemperature()));
		mSensors.add(new BarcodeReaderView(model.getBarcodeReader()));
		mSensors.add(new LightView(model.getLight()));
		mSensors.add(new ProximityView(model.getProximity()));
		mSensors.add(new PressureView(model.getPressure()));
		mSensors.add(new LinearAccelerationView(model.getLinearAcceleration()));
		mSensors.add(new GravityView(model.getGravity()));
		mSensors.add(new RotationVectorView(model.getRotationVector()));
		mSensors.add(new GyroscopeView(model.getGyroscope()));

		mMobile = new DeviceView(model);

		// up/down & split
		mSettingsPanel = fillSettingsPanel();

		// left panel
		JPanel leftPane = fillLeftPanel();
		JScrollPane leftPaneScroll = new JScrollPane(leftPane);
		leftPaneScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		leftPaneScroll.setPreferredSize(new Dimension(
				(int) (Global.W_FRAME * Global.SENSOR_SPLIT_LEFT),
				Global.H_TABS + 2 * Global.H_BUTTONS + Global.H_CONTENT));

		// right panel
		mSensorsTabbedPane = fillRightPanel();
		JScrollPane tabbedScroll = new JScrollPane(mSensorsTabbedPane);
		tabbedScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		tabbedScroll.setPreferredSize(new Dimension(750, 2 * Global.H_BUTTONS
				+ Global.H_CONTENT));

		JSplitPane splitUpPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPaneScroll, tabbedScroll);
		splitUpPane.setResizeWeight(Global.SENSOR_SPLIT_LEFT);
		splitUpPane.setOneTouchExpandable(true);

		add(splitUpPane);
	}

	//
	// /**
	// * Fills the panel from the bottom split.
	// *
	// * @return
	// */
	// private JPanel fillDownPanel() {
	// SpringLayout layout = new SpringLayout();
	// JPanel downPanel = new JPanel(layout);
	//
	// // Sensor output update/measure frequency
	// JPanel updateSimulationPanel = fillSettingsPanel();
	// downPanel.add(updateSimulationPanel);
	// Dimension minSize = updateSimulationPanel.getPreferredSize();
	// downPanel.setPreferredSize(new Dimension(minSize.width,
	// minSize.height + 195));
	// return downPanel;
	// }

	private JScrollPane fillSensorOutputArea() {
		mTextAreaSensorData = new JTextArea();
		JScrollPane scrollPaneSensorData = new JScrollPane(mTextAreaSensorData);
		scrollPaneSensorData
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneSensorData.setPreferredSize(new Dimension(
				(int) (Global.W_FRAME * Global.SENSOR_SPLIT_LEFT),
				(int) (1.6 * Global.H_INFO)));
		return scrollPaneSensorData;
	}

	/**
	 * Fills info panel with possible IPs.
	 * 
	 * @return
	 */
	private JScrollPane fillInfoOutput() {
		JTextPane messageTextArea = new JTextPane();
		mMessageTextArea = messageTextArea;
		messageTextArea.setContentType("text/html");
		messageTextArea.setEditable(false);

		StringBuffer infoText = new StringBuffer();
		infoText.append("Write emulator command port and click on "
				+ "set to create connection. Possible IP addresses:");
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface
					.getNetworkInterfaces();
			infoText.append("<p style='color:E96B14'>10.0.2.2<br\\>");
			for (NetworkInterface netint : Collections.list(nets)) {
				Enumeration<InetAddress> inetAddresses = netint
						.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					String address = inetAddress.toString();
					if (address.compareTo("/127.0.0.1") != 0
							&& !address.contains(":")) {
						infoText.append(address.substring(1) + "<br\\>");
					}
				}
			}
			infoText.append("</p>");
		} catch (SocketException e) {
			infoText.append("Socket exception. Could not obtain IP addresses.");
		}

		messageTextArea.setText(infoText.toString());
		JScrollPane areaScrollPane = new JScrollPane(messageTextArea);
		areaScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setPreferredSize(new Dimension((int) (Global.W_FRAME
				* Global.SENSOR_SPLIT_LEFT - 50), Global.H_INFO));
		return areaScrollPane;
	}

	private JPanel fillSettingsPanel() {
		JPanel settingsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints layout = new GridBagConstraints();
		layout.fill = GridBagConstraints.HORIZONTAL;
		layout.anchor = GridBagConstraints.NORTHWEST;

		// Add IP address properties:
		Font fontNotify = new Font("SansSerif", Font.BOLD, 12);
		JLabel socketLabel = new JLabel("Socket ", SwingConstants.LEFT);
		socketLabel.setFont(fontNotify);
		layout.gridx = 0;
		layout.gridy = 0;
		layout.gridwidth = 2;
		settingsPanel.add(socketLabel);

		layout.gridx = 1;
		layout.gridy = 0;
		layout.gridwidth = 2;
		mSensorPortText = new JTextField(5);
		mSensorPortText.setText("" + mModel.getSimulationPort());
		settingsPanel.add(mSensorPortText);

		mSensorPortButton = new JButton("Change");
		mSensorPortButton.setFont(fontNotify);
		layout.gridx = 2;
		layout.gridy = 0;
		layout.gridwidth = 1;
		settingsPanel.add(mSensorPortButton);

		// Update sensors
		layout.gridwidth = 1;
		layout.gridy = 1;
		layout.gridx = 0;
		JLabel label = new JLabel("Update sensors: ", SwingConstants.LEFT);
		settingsPanel.add(label, layout);

		mUpdateText = new JTextField(5);
		mUpdateText.setText("" + mModel.getUpdateSensors());
		layout.gridx++;
		settingsPanel.add(mUpdateText, layout);

		label = new JLabel(" ms", SwingConstants.LEFT);
		layout.gridx++;
		settingsPanel.add(label, layout);

		// Refresh after
		layout.gridy++;
		label = new JLabel("Refresh after: ", SwingConstants.LEFT);
		layout.gridx = 0;
		settingsPanel.add(label, layout);

		mRefreshCountText = new JTextField(5);
		mRefreshCountText.setText("10");
		layout.gridx++;
		settingsPanel.add(mRefreshCountText, layout);

		label = new JLabel(" times", SwingConstants.LEFT);
		layout.gridx++;
		settingsPanel.add(label, layout);

		layout.gridy++;
		layout.gridx = 0;
		layout.gridwidth = 3;
		settingsPanel.add(new JSeparator(), layout);

		// info about save/recording time
		label = new JLabel("Distance in time between two consecutive states",
				SwingConstants.LEFT);
		layout.gridy++;
		settingsPanel.add(label, layout);
		label = new JLabel("(for an accurate state transition description)",
				SwingConstants.LEFT);
		layout.gridy++;
		settingsPanel.add(label, layout);

		// Save
		layout.gridwidth = 1;
		layout.gridy++;
		label = new JLabel("Save: ", SwingConstants.LEFT);
		layout.gridx = 0;
		settingsPanel.add(label, layout);

		mSaveTime = new JTextField(5);
		mSaveTime.setText("0.5");
		layout.gridx++;
		settingsPanel.add(mSaveTime, layout);

		label = new JLabel(" seconds", SwingConstants.LEFT);
		layout.gridx++;
		settingsPanel.add(label, layout);

		layout.gridy++;
		layout.gridx = 0;
		layout.gridwidth = 3;
		settingsPanel.add(new JSeparator(), layout);

		// info about interpolation time - on playing
		label = new JLabel("Distance in time between playback states",
				SwingConstants.LEFT);
		layout.gridy++;
		settingsPanel.add(label, layout);
		label = new JLabel("(used in interpolation, for an accurate playback)",
				SwingConstants.LEFT);
		layout.gridy++;
		settingsPanel.add(label, layout);

		// Play
		layout.gridwidth = 1;
		layout.gridy++;
		label = new JLabel("Play: ", SwingConstants.LEFT);
		layout.gridx = 0;
		settingsPanel.add(label, layout);

		mPlaybackTime = new JTextField(5);
		mPlaybackTime.setText("0.1");
		layout.gridx++;
		settingsPanel.add(mPlaybackTime, layout);

		label = new JLabel(" seconds", SwingConstants.LEFT);
		layout.gridx++;
		settingsPanel.add(label, layout);
		return settingsPanel;
	}

	private JTabbedPane fillRightPanel() {
		JTabbedPane rightPanel = new JTabbedPane();
		SensorsScenario scenario = mModel.getScenario();

		// add enable sensors panel
		mAllSensorsTab = new AllSensorsView(mSensors);
		rightPanel.addTab("Sensors", mAllSensorsTab);

		// add scenario panel
		JScrollPane scenarioScroll = new JScrollPane(scenario.view);
		scenarioScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scenarioScroll.setPreferredSize(new Dimension(
				(int) (Global.W_FRAME * Global.SENSOR_SPLIT_RIGHT), 2
						* Global.H_BUTTONS + Global.H_CONTENT));
		rightPanel.addTab("Scenario Simulator", scenarioScroll);

		// add quick settings panel
		JPanel quickSettings = fillQuickSettingsPanel();
		JScrollPane quickSettingsScroll = new JScrollPane(quickSettings);
		quickSettingsScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		quickSettingsScroll.setPreferredSize(new Dimension(
				(int) (Global.W_FRAME * Global.SENSOR_SPLIT_RIGHT), 2
						* Global.H_BUTTONS + Global.H_CONTENT));
		rightPanel.addTab("Quick Settings", quickSettingsScroll);

		// add sensor specific panel
		JPanel sensorSpecific = fillSpecificPerSensorPanel();
		rightPanel.addTab("Sensors Parameters", sensorSpecific);

		return rightPanel;
	}

	private JPanel fillSpecificPerSensorPanel() {
		final JPanel sensorSpecific = new JPanel(new BorderLayout());

		// sensors bar
		mSensorsButtonsPanel = new JPanel();
		mSensorsButtonsPanel.setLayout(new BoxLayout(mSensorsButtonsPanel,
				BoxLayout.X_AXIS));
		JScrollPane sensorsBarScroll = new JScrollPane(mSensorsButtonsPanel);
		sensorsBarScroll.setPreferredSize(new Dimension(
				(int) (Global.W_FRAME * Global.SENSOR_SPLIT_RIGHT),
				Global.H_BUTTONS * 2));
		sensorSpecific.add(sensorsBarScroll, BorderLayout.NORTH);

		// content panel
		final JPanel contentPanel = new JPanel(new BorderLayout());
		sensorSpecific.add(contentPanel, BorderLayout.CENTER);

		for (final SensorView sensor : mSensors) {
			SensorModel sensorModel = sensor.getModel();
			final SensorButton sensorButton = new SensorButton(
					sensorModel.getName(), sensor);
			sensor.setButton(sensorButton);

			sensorButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					contentPanel.removeAll();
					contentPanel.add(sensor);
					if (mLastPressedSensor != null) {
						mLastPressedSensor.setSelectedSensor(false);
						mLastPressedSensor.repaint();
					}
					sensorButton.setSelectedSensor(true);
					mLastPressedSensor = sensorButton;
					sensorButton.repaint();
					contentPanel.validate();
					contentPanel.repaint();
				}
			});
			if (sensorModel.isEnabled()) {
				mSensorsButtonsPanel.add(sensorButton);
				if (mLastPressedSensor == null) {
					mLastPressedSensor = sensorButton;
				}
			}
		}

		return sensorSpecific;
	}

	private JPanel fillLeftPanel() {
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		// mobile
		mMobile.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(mMobile);

		leftPanel.add(new JSeparator());

		// Sensor update
		JPanel sensorUpdate = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Sensor update: ", SwingConstants.LEFT);
		sensorUpdate.add(label, BorderLayout.WEST);

		mRefreshSensorsLabel = new JLabel("0", SwingConstants.LEFT);
		sensorUpdate.add(mRefreshSensorsLabel, BorderLayout.EAST);
		sensorUpdate.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(sensorUpdate);

		leftPanel.add(new JSeparator());

		// sensors output
		JScrollPane sensorOutputPane = fillSensorOutputArea();
		sensorOutputPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(sensorOutputPane);

		leftPanel.add(new JSeparator());

		// ip info
		JScrollPane ipInfoPanel = fillInfoOutput();
		ipInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(ipInfoPanel);

		return leftPanel;
	}

	private JPanel fillQuickSettingsPanel() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		for (SensorView sensorView : mSensors) {
			sensorView.setQuickSettingsPanel(result);
			if (sensorView.getModel().isEnabled()) {
				result.add(sensorView.getQuickSettingsPanel());
			}
		}
		return result;
	}

	/**
	 * Sets the socket port for listening
	 */
	public void setPort() {
		addMessage("Closing port " + mModel.getSimulationPort());
		mModel.restartSensorServer();
	}

	/**
	 * Adds new message to message box. If scroll position is at end, it will
	 * scroll to new message.
	 * 
	 * @param msg
	 *            Message.
	 */
	public void addMessage(String msg) {
		String oldText = mMessageTextArea.getText();
		mMessageTextArea.setText(oldText + msg + "\n");
	}

	/**
	 * Get socket port number.
	 * 
	 * @return String containing port number.
	 */
	public int getPort() {
		String s = mSensorPortText.getText();
		int port = 0;
		try {
			port = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			addMessage("Invalid port number: " + s);
		}
		return port;
	}

	/**
	 * This method is called by SensorServerThread when a new client connects.
	 */
	public void newClient() {
		addMessage("First incoming connection:");
		addMessage("ALL SENSORS DISABLED!");
	}

	/**
	 * Safely retries the double value of a text field. If the value is not a
	 * valid number, 0 is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            Textfield from which the value should be read.
	 * @param defaultValue
	 *            default value if input field is invalid.
	 * @return double value.
	 */
	public double getSafeDouble(JTextField textfield, double defaultValue) {
		double value = defaultValue;

		try {
			value = Double.parseDouble(textfield.getText());
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			value = defaultValue;
			textfield.setBackground(Color.RED);
		}
		return value;
	}

	public double getSafeDouble(JTextField textfield) {
		return getSafeDouble(textfield, 0);
	}

	/**
	 * Safely retries the float value of a text field. If the value is not a
	 * valid number, 0 is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            TextField from which the value should be read.
	 * @param defaultValue
	 *            default value if input field is invalid.
	 * @return float value.
	 */
	public float getSafeFloat(JTextField textfield, float defaultValue) {
		float value = defaultValue;
		try {
			value = Float.parseFloat(textfield.getText());
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			value = defaultValue;
			textfield.setBackground(Color.RED);
		}
		return value;
	}

	public float getSafeFloat(JTextField textfield) {
		return getSafeFloat(textfield, 0);
	}

	/**
	 * Safely retries the a list of double values of a text field. If the list
	 * contains errors, null is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            TextField from which the value should be read.
	 * @return list double[] with values or null.
	 */
	public double[] getSafeDoubleList(JTextField textfield) {
		double[] valuelist = null;

		try {
			String t = textfield.getText();
			// Now we have to split this into pieces
			String[] tlist = t.split(",");
			int len = tlist.length;
			if (len > 0) {
				valuelist = new double[len];
				for (int i = 0; i < len; i++) {
					valuelist[i] = Double.parseDouble(tlist[i]);
				}
			} else {
				valuelist = null;
			}
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			valuelist = null;
			textfield.setBackground(Color.RED);
		}
		return valuelist;
	}

	public double getUpdateSensors() {
		return getSafeDouble(mUpdateText);
	}

	public double getRefreshAfter() {
		return getSafeDouble(mRefreshCountText);
	}

	public BarcodeReaderView getBarcodeReader() {
		return (BarcodeReaderView) mSensors.get(SensorModel.POZ_BARCODE_READER);
	}

	public AccelerometerView getAccelerometer() {
		return (AccelerometerView) mSensors.get(SensorModel.POZ_ACCELEROMETER);
	}

	public LightView getLight() {
		return (LightView) mSensors.get(SensorModel.POZ_LIGHT);
	}

	public OrientationView getOrientation() {
		return (OrientationView) mSensors.get(SensorModel.POZ_ORIENTATION);
	}

	public ProximityView getProximity() {
		return (ProximityView) mSensors.get(SensorModel.POZ_PROXIMITY);
	}

	public TemperatureView getTemperature() {
		return (TemperatureView) mSensors.get(SensorModel.POZ_TEMPERATURE);
	}

	public MagneticFieldView getMagneticField() {
		return (MagneticFieldView) mSensors.get(SensorModel.POZ_MAGNETIC_FIELD);
	}

	public JButton getSensorPortButton() {
		return mSensorPortButton;
	}

	public long getRefreshCount() {
		return (long) getSafeDouble(mRefreshCountText);
	}

	public void setRefreshSensorsLabel(double ms) {
		mRefreshSensorsLabel.setText(Global.TWO_DECIMAL_FORMAT.format(ms)
				+ " ms");
	}

	public void setOutput(String data) {
		if (!data.equals(mTextAreaSensorData.getText())) {
			mTextAreaSensorData.setText(data);
		}
	}

	public JTextPane getMessagePanel() {
		return mMessageTextArea;
	}

	public JPanel getSensorsButtonsPanel() {
		return mSensorsButtonsPanel;
	}

	public PressureView getPressure() {
		return (PressureView) mSensors.get(SensorModel.POZ_PRESSURE);
	}

	public LinearAccelerationView getLinearAceleration() {
		return (LinearAccelerationView) mSensors
				.get(SensorModel.POZ_LINEAR_ACCELERATION);
	}

	public GravityView getGravity() {
		return (GravityView) mSensors.get(SensorModel.POZ_GRAVITY);
	}

	public RotationVectorView getRotationVector() {
		return (RotationVectorView) mSensors.get(SensorModel.POZ_ROTATION);
	}

	public GyroscopeView getGyroscope() {
		return (GyroscopeView) mSensors.get(SensorModel.POZ_GYROSCOPE);
	}

	public DeviceView getDeviceView() {
		return mMobile;
	}

	public AllSensorsView getAllSensorsView() {
		return mAllSensorsTab;
	}

	public void invalidateDevice() {
		mMobile.doRepaint();
	}

	public JPanel getSettingsPanel() {
		return mSettingsPanel;
	}

	public float getSavingTime() {
		return getSafeFloat(mSaveTime);
	}

	public float getInterpolationTime() {
		return getSafeFloat(mPlaybackTime);
	}

}
