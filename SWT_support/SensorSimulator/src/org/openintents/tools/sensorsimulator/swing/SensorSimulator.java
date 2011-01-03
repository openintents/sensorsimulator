/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2010 OpenIntents.org
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

package org.openintents.tools.sensorsimulator.swing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openintents.tools.sensorsimulator.IMobilePanel;
import org.openintents.tools.sensorsimulator.ISensorSimulator;
import org.openintents.tools.sensorsimulator.SensorServer;
import org.openintents.tools.sensorsimulator.TelnetServer;
import org.openintents.tools.sensorsimulator.WiiMoteData;

/**
 *  Class of SensorSimulator.
 *
 *  The SensorSimulator is a Java stand-alone application.
 *
 *  It simulates various sensors.
 *  An Android application can connect through
 *  TCP/IP with the settings shown to the SensorSimulator
 *  to simulate accelerometer, compass, orientation sensor,
 *  and thermometer.
 *
 *  @author Peli
 *  @author Josip Balic
 */
public class SensorSimulator extends JPanel
							implements ISensorSimulator,
								ActionListener,
								WindowListener,
								ChangeListener,
								ItemListener {

	private static final long serialVersionUID = -587503580193069930L;

	// Simulation delay:
	private int delay;
	private Timer timer;

    // for measuring updates:
	private int updateSensorCount;
	private long updateSensorTime;
	private int updateEmulatorAccelerometerCount;
	private long updateEmulatorAccelerometerTime;
	private int updateEmulatorCompassCount;
	private long updateEmulatorCompassTime;
	private int updateEmulatorOrientationCount;
	private long updateEmulatorOrientationTime;
	private int updateEmulatorThermometerCount;
	private long updateEmulatorThermometerTime;


	private int mouseMode;

    // Displays the mobile phone
	private IMobilePanel mobile;

    // Sliders:
	private JSlider yawSlider;
	private JSlider pitchSlider;
	private JSlider rollSlider;

    // Text fields:
	private JTextField socketText;

    //Text fields and button for Telnet socket port
	private JTextField telnetSocketText;

    // Field for socket related output:
	private JScrollPane areaScrollPane;
	private JTextArea ipselectionText;

    // Field for sensor simulator data output:
	private JScrollPane scrollPaneSensorData;
	private JTextArea textAreaSensorData;

    // Settings
    // Supported sensors
	private JCheckBox mSupportedOrientation;
	private JCheckBox mSupportedAccelerometer;
	private JCheckBox mSupportedTemperature;
	private JCheckBox mSupportedMagneticField;
	private JCheckBox mSupportedLight;
	private JCheckBox mSupportedProximity;
	private JCheckBox mSupportedTricorder;
	private JCheckBox mSupportedBarcodeReader;

    // Enabled sensors
	private JCheckBox mEnabledOrientation;
	private JCheckBox mEnabledAccelerometer;
	private JCheckBox mEnabledTemperature;
	private JCheckBox mEnabledMagneticField;
	private JCheckBox mEnabledLight;
	private JCheckBox mEnabledProximity;
	private JCheckBox mEnabledTricorder;
	private JCheckBox mEnabledBarcodeReader;

    // Simulation update
	private JTextField mUpdateRatesAccelerometerText;
	private JTextField mDefaultUpdateRateAccelerometerText;
	private JTextField mCurrentUpdateRateAccelerometerText;
    /** Whether to form an average at each update */
	private JCheckBox mUpdateAverageAccelerometer;

	private JTextField mUpdateRatesCompassText;
	private JTextField mDefaultUpdateRateCompassText;
	private JTextField mCurrentUpdateRateCompassText;
    /** Whether to form an average at each update */
	private JCheckBox mUpdateAverageCompass;

	private JTextField mUpdateRatesOrientationText;
	private JTextField mDefaultUpdateRateOrientationText;
	private JTextField mCurrentUpdateRateOrientationText;
    /** Whether to form an average at each update */
	private JCheckBox mUpdateAverageOrientation;

	private JTextField mUpdateRatesThermometerText;
	private JTextField mDefaultUpdateRateThermometerText;
	private JTextField mCurrentUpdateRateThermometerText;
    /** Whether to form an average at each update */
	private JCheckBox mUpdateAverageThermometer;

	private JTextField mUpdateText;
	private JTextField mRefreshCountText;
	private JLabel mRefreshSensorsLabel;
	private JLabel mRefreshEmulatorAccelerometerLabel;
	private JLabel mRefreshEmulatorCompassLabel;
	private JLabel mRefreshEmulatorOrientationLabel;
	private JLabel mRefreshEmulatorThermometerLabel;

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

    // Magnetic field
	private JTextField mMagneticFieldNorthText;
	private JTextField mMagneticFieldEastText;
	private JTextField mMagneticFieldVerticalText;

    // Temperature
	private JTextField mTemperatureText;

    //Barcode
	private JTextField mBarcodeReaderText;

    // Random contribution
	private JTextField mRandomOrientationText;
	private JTextField mRandomAccelerometerText;
	private JTextField mRandomTemperatureText;
	private JTextField mRandomMagneticFieldText;
	private JTextField mRandomLightText;
	private JTextField mRandomProximityText;
	private JTextField mRandomTricorderText;

    // Real device bridge
	private JCheckBox mRealDeviceThinkpad;
	private JCheckBox mRealDeviceWiimote;
	private JTextField mRealDevicePath;
	private JLabel mRealDeviceOutputLabel;

    //TelnetSimulations variables
	private JSlider batterySlider;

    //Battery variables
	private JCheckBox batteryPresence;
	private JCheckBox batteryAC;
	private JComboBox batteryStatusList;
	private JComboBox batteryHealthList;

    //Batter file variables
	private JButton batteryEmulation;
	private JButton batteryNext;
	private JFileChooser fileChooser;
	private JButton openButton;

    //GPS variables
	private JTextField gpsLongitudeText;
	private JTextField gpsLatitudeText;
	private JTextField gpsAltitudeText;
	private JTextField lisName;
	private JButton gpsButton;

    // Server for sending out sensor data
	private SensorServer mSensorServer;
	private int mIncomingConnections;

    //telnet server variable
	private TelnetServer mTelnetServer;

    WiiMoteData wiiMoteData = new WiiMoteData();


	public SensorSimulator() {
		// Initialize variables
		mIncomingConnections = 0;

		setLayout(new BorderLayout());

		///////////////////////////////////////////////////////////////
        // Left panel

        GridBagLayout myGridBagLayout = new GridBagLayout();
        // myGridLayout.
        GridBagConstraints c = new GridBagConstraints();
        JPanel leftPanel = new JPanel(myGridBagLayout);

        JPanel mobilePanel = new JPanel(new BorderLayout());

		// Add the mobile
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        mobile = new MobilePanel(this);
        mobilePanel.add((MobilePanel)mobile);

        leftPanel.add(mobilePanel, c);

        // Add mouse action selection
        // through radio buttons.
        JRadioButton yawPitchButton = new JRadioButton(yawPitch);
        yawPitchButton.setActionCommand(yawPitch);
        yawPitchButton.setSelected(true);
        mouseMode = mouseYawPitch;

        JRadioButton rollPitchButton = new JRadioButton(rollPitch);
        rollPitchButton.setActionCommand(rollPitch);

        JRadioButton moveButton = new JRadioButton(move);
        moveButton.setActionCommand(move);

        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(yawPitchButton);
        group.add(rollPitchButton);
        group.add(moveButton);

        //Register a listener for the radio buttons.
        yawPitchButton.addActionListener(this);
        rollPitchButton.addActionListener(this);
        moveButton.addActionListener(this);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        leftPanel.add(yawPitchButton, c);
        c.gridx++;
        leftPanel.add(rollPitchButton, c);
        c.gridx++;
        leftPanel.add(moveButton, c);

        // Add IP address properties:
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        JLabel socketLabel = new JLabel("Socket", JLabel.LEFT);
        leftPanel.add(socketLabel, c);

        c.gridx = 1;
        socketText = new JTextField(5);
        leftPanel.add(socketText, c);

        c.gridx = 2;
        JButton socketButton = new JButton("Set");
        leftPanel.add(socketButton, c);
        socketButton.setActionCommand(setPortString);
        socketButton.addActionListener(this);

        //add telnet JLabel, text field and button
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        JLabel telnetSocketLabel = new JLabel("Telnet socket port", JLabel.LEFT);
        leftPanel.add(telnetSocketLabel, c);

        c.gridx = 1;
        telnetSocketText = new JTextField(5);
        leftPanel.add(telnetSocketText, c);

        c.gridx = 2;
        JButton telnetSocketButton = new JButton("Set");
        leftPanel.add(telnetSocketButton, c);
        telnetSocketButton.setActionCommand(connectViaTelnet);
        telnetSocketButton.addActionListener(this);

        //add scrollPane for info output
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        ipselectionText = new JTextArea(3, 10);

        areaScrollPane = new JScrollPane(ipselectionText);
        areaScrollPane.setVerticalScrollBarPolicy(
        		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(250, 80));

        leftPanel.add(areaScrollPane, c);

        //add scrollPane for sensor output
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        textAreaSensorData = new JTextArea(3, 10);
        scrollPaneSensorData = new JScrollPane(textAreaSensorData);
        scrollPaneSensorData.setVerticalScrollBarPolicy(
        		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneSensorData.setPreferredSize(new Dimension(250, 80));

        leftPanel.add(scrollPaneSensorData, c);

        leftPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        ///////////////////////////////////////////////////////////////
        // Center panel
        JLabel simulatorLabel = new JLabel("OpenIntents Sensor Simulator", JLabel.CENTER);
        simulatorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font font = new Font("SansSerif", Font.PLAIN, 22);
        simulatorLabel.setFont(font);
        simulatorLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));

        //Create the label.
		JLabel yawLabel = new JLabel("Yaw", JLabel.CENTER);
        yawLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel pitchLabel = new JLabel("Pitch", JLabel.CENTER);
        pitchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rollLabel = new JLabel("Roll", JLabel.CENTER);
        rollLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

	    //Create the slider.
        yawSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, -20);
        pitchSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, -60);
	    rollSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);

	    yawSlider.addChangeListener(this);
	    pitchSlider.addChangeListener(this);
	    rollSlider.addChangeListener(this);
	    mobile.setYawDegree(yawSlider.getValue());
	    mobile.setPitchDegree(pitchSlider.getValue());
	    mobile.setRollDegree(rollSlider.getValue());

		//Turn on labels at major tick marks.

	    yawSlider.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
	    pitchSlider.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));

	    rollSlider.setMajorTickSpacing(90);
	    rollSlider.setMinorTickSpacing(10);
	    rollSlider.setPaintTicks(true);
	    rollSlider.setPaintLabels(true);
	    rollSlider.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));


        //GridBagLayout
	    myGridBagLayout = new GridBagLayout();
        // myGridLayout.
        //GridBagConstraints
	    c = new GridBagConstraints();
        JPanel centerPanel = new JPanel(myGridBagLayout);

        //Put everything together.
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        centerPanel.add(simulatorLabel, c);
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy++;
        centerPanel.add(yawLabel, c);
        c.gridx = 1;
        centerPanel.add(yawSlider, c);
        c.gridx = 0;
        c.gridy++;
        centerPanel.add(pitchLabel, c);
        c.gridx = 1;
        centerPanel.add(pitchSlider, c);
        c.gridx = 0;
        c.gridy++;
        centerPanel.add(rollLabel, c);
        c.gridx = 1;
        centerPanel.add(rollSlider, c);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Now add a scrollable panel with more controls:
        JPanel settingsPane = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();

        JScrollPane settingsScrollPane = new JScrollPane(settingsPane);
        settingsScrollPane.setVerticalScrollBarPolicy(
        		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        settingsScrollPane.setPreferredSize(new Dimension(250, 250));

        JLabel settingsLabel = new JLabel("Settings", JLabel.CENTER);
        //settingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.NORTHWEST;
        c2.gridwidth = 1;
        c2.gridx = 0;
        c2.gridy = 0;
        settingsPane.add(settingsLabel, c2);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        c2.gridy++;
        settingsPane.add(separator, c2);

        ///////////////////////////////
        // Checkbox for sensors
        JPanel supportedSensorsPane = new JPanel();
        supportedSensorsPane.setLayout(new BoxLayout(supportedSensorsPane, BoxLayout.PAGE_AXIS));

        supportedSensorsPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Supported sensors"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        mSupportedAccelerometer = new JCheckBox(ACCELEROMETER);
        mSupportedAccelerometer.setSelected(true);
        mSupportedAccelerometer.addItemListener(this);
        supportedSensorsPane.add(mSupportedAccelerometer);

        mSupportedMagneticField = new JCheckBox(MAGNETIC_FIELD);
        mSupportedMagneticField.setSelected(true);
        mSupportedMagneticField.addItemListener(this);
        supportedSensorsPane.add(mSupportedMagneticField);

        mSupportedOrientation = new JCheckBox(ORIENTATION);
        mSupportedOrientation.setSelected(true);
        mSupportedOrientation.addItemListener(this);
        supportedSensorsPane.add(mSupportedOrientation);

        mSupportedTemperature = new JCheckBox(TEMPERATURE);
        mSupportedTemperature.setSelected(false);
        mSupportedTemperature.addItemListener(this);
        supportedSensorsPane.add(mSupportedTemperature);

        mSupportedBarcodeReader = new JCheckBox(BARCODE_READER);
        mSupportedBarcodeReader.setSelected(false);
        mSupportedBarcodeReader.addItemListener(this);
        supportedSensorsPane.add(mSupportedBarcodeReader);

        c2.gridy++;
        settingsPane.add(supportedSensorsPane,c2);


        ///////////////////////////////
        // Checkbox for sensors
        JPanel enabledSensorsPane = new JPanel();
        enabledSensorsPane.setLayout(new BoxLayout(enabledSensorsPane, BoxLayout.PAGE_AXIS));

        enabledSensorsPane.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("Enabled sensors"),
        BorderFactory.createEmptyBorder(5,5,5,5)));

        mEnabledAccelerometer = new JCheckBox(ACCELEROMETER);
        mEnabledAccelerometer.setSelected(true);
        mEnabledAccelerometer.addItemListener(this);
        enabledSensorsPane.add(mEnabledAccelerometer);

        mEnabledMagneticField = new JCheckBox(MAGNETIC_FIELD);
        mEnabledMagneticField.setSelected(true);
        mEnabledMagneticField.addItemListener(this);
        enabledSensorsPane.add(mEnabledMagneticField);

        mEnabledOrientation = new JCheckBox(ORIENTATION);
        mEnabledOrientation.setSelected(true);
        mEnabledOrientation.addItemListener(this);
        enabledSensorsPane.add(mEnabledOrientation);

        mEnabledTemperature = new JCheckBox(TEMPERATURE);
        mEnabledTemperature.setSelected(false);
        mEnabledTemperature.addItemListener(this);
        enabledSensorsPane.add(mEnabledTemperature);

        mEnabledBarcodeReader = new JCheckBox(BARCODE_READER);
        mEnabledBarcodeReader.setSelected(false);
        mEnabledBarcodeReader.addItemListener(this);
        enabledSensorsPane.add(mEnabledBarcodeReader);

        c2.gridy++;
        settingsPane.add(enabledSensorsPane,c2);

        JLabel label;
        GridBagConstraints c3;

        ////////////////////////////////
        // Sensor output update frequency
        // and measure frequency
        // Also update connected sensor frequency.
        JPanel updateFieldPane = new JPanel(new GridBagLayout());
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy = 0;

        updateFieldPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Sensor update rate"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        //------------------
        label = new JLabel("Accelerometer", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        label = new JLabel("Update rates: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mUpdateRatesAccelerometerText = new JTextField(5);
        mUpdateRatesAccelerometerText.setText("1, 10, 50");
        c3.gridx = 1;
        updateFieldPane.add(mUpdateRatesAccelerometerText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        label = new JLabel("Default rate: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mDefaultUpdateRateAccelerometerText = new JTextField(5);
        mDefaultUpdateRateAccelerometerText.setText("50");
        c3.gridx = 1;
        updateFieldPane.add(mDefaultUpdateRateAccelerometerText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        label = new JLabel("Current rate: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mCurrentUpdateRateAccelerometerText = new JTextField(5);
        mCurrentUpdateRateAccelerometerText.setText("50");
        c3.gridx = 1;
        updateFieldPane.add(mCurrentUpdateRateAccelerometerText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy++;
        mUpdateAverageAccelerometer = new JCheckBox(AVERAGE_ACCELEROMETER);
        mUpdateAverageAccelerometer.setSelected(true);
        mUpdateAverageAccelerometer.addItemListener(this);
        updateFieldPane.add(mUpdateAverageAccelerometer, c3);

        c3.gridy++;
        updateFieldPane.add(new JSeparator(SwingConstants.HORIZONTAL), c3);


        //------------------
        label = new JLabel("Compass", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        label = new JLabel("Update rates: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mUpdateRatesCompassText = new JTextField(5);
        mUpdateRatesCompassText.setText("1, 10");
        c3.gridx = 1;
        updateFieldPane.add(mUpdateRatesCompassText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        label = new JLabel("Default rate: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mDefaultUpdateRateCompassText = new JTextField(5);
        mDefaultUpdateRateCompassText.setText("10");
        c3.gridx = 1;
        updateFieldPane.add(mDefaultUpdateRateCompassText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        label = new JLabel("Current rate: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mCurrentUpdateRateCompassText = new JTextField(5);
        mCurrentUpdateRateCompassText.setText("10");
        c3.gridx = 1;
        updateFieldPane.add(mCurrentUpdateRateCompassText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy++;
        mUpdateAverageCompass = new JCheckBox(AVERAGE_MAGNETIC_FIELD);
        mUpdateAverageCompass.setSelected(true);
        mUpdateAverageCompass.addItemListener(this);
        updateFieldPane.add(mUpdateAverageCompass, c3);

        c3.gridy++;
        updateFieldPane.add(new JSeparator(SwingConstants.HORIZONTAL), c3);

      //------------------
        label = new JLabel("Orientation", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        label = new JLabel("Update rates: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mUpdateRatesOrientationText = new JTextField(5);
        mUpdateRatesOrientationText.setText("1, 10, 50");
        c3.gridx = 1;
        updateFieldPane.add(mUpdateRatesOrientationText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        label = new JLabel("Default rate: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mDefaultUpdateRateOrientationText = new JTextField(5);
        mDefaultUpdateRateOrientationText.setText("50");
        c3.gridx = 1;
        updateFieldPane.add(mDefaultUpdateRateOrientationText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        label = new JLabel("Current rate: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mCurrentUpdateRateOrientationText = new JTextField(5);
        mCurrentUpdateRateOrientationText.setText("50");
        c3.gridx = 1;
        updateFieldPane.add(mCurrentUpdateRateOrientationText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy++;
        mUpdateAverageOrientation = new JCheckBox(AVERAGE_ORIENTATION);
        mUpdateAverageOrientation.setSelected(true);
        mUpdateAverageOrientation.addItemListener(this);
        updateFieldPane.add(mUpdateAverageOrientation, c3);

        c3.gridy++;
        updateFieldPane.add(new JSeparator(SwingConstants.HORIZONTAL), c3);

      //------------------
        label = new JLabel("Thermometer", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        label = new JLabel("Update rates: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mUpdateRatesThermometerText = new JTextField(5);
        mUpdateRatesThermometerText.setText("0.1, 1");
        c3.gridx = 1;
        updateFieldPane.add(mUpdateRatesThermometerText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        label = new JLabel("Default rate: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mDefaultUpdateRateThermometerText = new JTextField(5);
        mDefaultUpdateRateThermometerText.setText("1");
        c3.gridx = 1;
        updateFieldPane.add(mDefaultUpdateRateThermometerText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        label = new JLabel("Current rate: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateFieldPane.add(label, c3);

        mCurrentUpdateRateThermometerText = new JTextField(5);
        mCurrentUpdateRateThermometerText.setText("1");
        c3.gridx = 1;
        updateFieldPane.add(mCurrentUpdateRateThermometerText, c3);

        label = new JLabel("1/s", JLabel.LEFT);
        c3.gridx = 2;
        updateFieldPane.add(label, c3);

        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy++;
        mUpdateAverageThermometer = new JCheckBox(AVERAGE_TEMPERATURE);
        mUpdateAverageThermometer.setSelected(true);
        mUpdateAverageThermometer.addItemListener(this);
        updateFieldPane.add(mUpdateAverageThermometer, c3);

        // Update panel ends

        // Add update panel to settings
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        settingsPane.add(updateFieldPane, c2);

        ////////////////////////////////
        // Sensor output update frequency
        // and measure frequency
        // Also update connected sensor frequency.
        JPanel updateSimulationFieldPane = new JPanel(new GridBagLayout());
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy = 0;

        updateSimulationFieldPane.setBorder(
        		BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Simulation update"),
                BorderFactory.createEmptyBorder(5,5,5,5)));


        // ---------------------
        label = new JLabel("Update sensors: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateSimulationFieldPane.add(label, c3);

        mUpdateText = new JTextField(5);
        mUpdateText.setText("10");
        c3.gridx = 1;
        updateSimulationFieldPane.add(mUpdateText, c3);

        label = new JLabel(" ms", JLabel.LEFT);
        c3.gridx = 2;
        updateSimulationFieldPane.add(label, c3);

        label = new JLabel("Refresh after: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateSimulationFieldPane.add(label, c3);

        mRefreshCountText = new JTextField(5);
        mRefreshCountText.setText("10");
        c3.gridx = 1;
        updateSimulationFieldPane.add(mRefreshCountText, c3);

        label = new JLabel(" times", JLabel.LEFT);
        c3.gridx = 2;
        updateSimulationFieldPane.add(label, c3);

        label = new JLabel("Sensor update: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateSimulationFieldPane.add(label, c3);

        mRefreshSensorsLabel = new JLabel("0", JLabel.LEFT);
        c3.gridx = 1;
        updateSimulationFieldPane.add(mRefreshSensorsLabel, c3);

        label = new JLabel("Emulator update: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateSimulationFieldPane.add(label, c3);


        label = new JLabel(" * Accelerometer: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateSimulationFieldPane.add(label, c3);

        mRefreshEmulatorAccelerometerLabel = new JLabel("-", JLabel.LEFT);
        c3.gridx = 1;
        updateSimulationFieldPane.add(mRefreshEmulatorAccelerometerLabel, c3);


        label = new JLabel(" * Compass: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateSimulationFieldPane.add(label, c3);

        mRefreshEmulatorCompassLabel = new JLabel("-", JLabel.LEFT);
        c3.gridx = 1;
        updateSimulationFieldPane.add(mRefreshEmulatorCompassLabel, c3);


        label = new JLabel(" * Orientation: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateSimulationFieldPane.add(label, c3);

        mRefreshEmulatorOrientationLabel = new JLabel("-", JLabel.LEFT);
        c3.gridx = 1;
        updateSimulationFieldPane.add(mRefreshEmulatorOrientationLabel, c3);


        label = new JLabel(" * Thermometer: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        updateSimulationFieldPane.add(label, c3);

        mRefreshEmulatorThermometerLabel = new JLabel("-", JLabel.LEFT);
        c3.gridx = 1;
        updateSimulationFieldPane.add(mRefreshEmulatorThermometerLabel, c3);

        // Update panel ends

        // Add update panel to settings
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        settingsPane.add(updateSimulationFieldPane, c2);

        ////////////////////////////////
        // Acceleration / accelerometer settings:
        // * how much screen movement translates to
        //   real world acceleration
        // * smoothing of action(?)
        JPanel accelerometerFieldPane = new JPanel(new GridBagLayout());
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy = 0;

        accelerometerFieldPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Accelerometer"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        label = new JLabel("Gravity constant g: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        accelerometerFieldPane.add(label, c3);

        mGravityConstantText = new JTextField(5);
        mGravityConstantText.setText("9.80665");
        c3.gridx = 1;
        accelerometerFieldPane.add(mGravityConstantText, c3);

        label = new JLabel(" m/s" + SQUARED, JLabel.LEFT);
        c3.gridx = 2;
        accelerometerFieldPane.add(label, c3);


        label = new JLabel("Accelerometer limit: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        accelerometerFieldPane.add(label, c3);

        mAccelerometerLimitText = new JTextField(5);
        mAccelerometerLimitText.setText("10");
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

        label = new JLabel(" p/s" + SQUARED, JLabel.LEFT);
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
        mShowAcceleration = new JCheckBox(SHOW_ACCELERATION);
        mShowAcceleration.setSelected(false);
        mShowAcceleration.addItemListener(this);
        accelerometerFieldPane.add(mShowAcceleration, c3);

        // Accelerometer field panel ends

        // Add accelerometer field panel to settings
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        settingsPane.add(accelerometerFieldPane, c2);


        ////////////////////////////////
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
                BorderFactory.createEmptyBorder(5,5,5,5)));

        label = new JLabel("x: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        gravityFieldPane.add(label, c3);

        mGravityXText = new JTextField(5);
        mGravityXText.setText("0");
        c3.gridx = 1;
        gravityFieldPane.add(mGravityXText, c3);

        label = new JLabel(" m/s" + SQUARED, JLabel.LEFT);
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

        label = new JLabel(" m/s" + SQUARED, JLabel.LEFT);
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

        label = new JLabel(" m/s" + SQUARED, JLabel.LEFT);
        c3.gridx = 2;
        gravityFieldPane.add(label, c3);

        // Gravity field panel ends

        // Add gravity field panel to settings
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        settingsPane.add(gravityFieldPane, c2);


        ////////////////////////////////
        // Magnetic field (in nanoTesla)

        // Values can be found at
        //
        // Default values are for San Francisco.

        JPanel magneticFieldPane = new JPanel(new GridBagLayout());
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy = 0;

        magneticFieldPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Magnetic field"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        JLabel magneticFieldNorthLabel = new JLabel("North component: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        magneticFieldPane.add(magneticFieldNorthLabel, c3);

        mMagneticFieldNorthText = new JTextField(5);
        mMagneticFieldNorthText.setText("22874.1");
        c3.gridx = 1;
        magneticFieldPane.add(mMagneticFieldNorthText, c3);

        JLabel nanoTeslaLabel = new JLabel(" nT", JLabel.LEFT);
        c3.gridx = 2;
        magneticFieldPane.add(nanoTeslaLabel, c3);


        JLabel magneticFieldEastLabel = new JLabel("East component: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        magneticFieldPane.add(magneticFieldEastLabel, c3);

        mMagneticFieldEastText = new JTextField(5);
        mMagneticFieldEastText.setText("5939.5");
        c3.gridx = 1;
        magneticFieldPane.add(mMagneticFieldEastText, c3);

        nanoTeslaLabel = new JLabel(" nT", JLabel.LEFT);
        c3.gridx = 2;
        magneticFieldPane.add(nanoTeslaLabel, c3);

        JLabel magneticFieldVerticalLabel = new JLabel("Vertical component: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        magneticFieldPane.add(magneticFieldVerticalLabel, c3);

        mMagneticFieldVerticalText = new JTextField(5);
        mMagneticFieldVerticalText.setText("43180.5");
        c3.gridx = 1;
        magneticFieldPane.add(mMagneticFieldVerticalText, c3);

        label = new JLabel(" nT", JLabel.LEFT);
        c3.gridx = 2;
        magneticFieldPane.add(label, c3);

        // Magnetic field panel ends

        // Add magnetic field panel to settings
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        settingsPane.add(magneticFieldPane, c2);


        ////////////////////////////////
        // Temperature (in �C: Centigrade Celsius)
        JPanel temperatureFieldPane = new JPanel(new GridBagLayout());
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy = 0;

        temperatureFieldPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Temperature"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        label = new JLabel("Temperature: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        temperatureFieldPane.add(label, c3);

        mTemperatureText = new JTextField(5);
        mTemperatureText.setText("17.7");
        c3.gridx = 1;
        temperatureFieldPane.add(mTemperatureText, c3);

        label = new JLabel(" " + DEGREES + "C", JLabel.LEFT);
        c3.gridx = 2;
        temperatureFieldPane.add(label, c3);


        // Temperature panel ends

        // Add temperature panel to settings
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        settingsPane.add(temperatureFieldPane, c2);

        ////////////////////////////////
        // Barcode (13 numbers)
        // Panel for our barcode reader
        JPanel barcodeReaderFieldPane = new JPanel(new GridBagLayout());
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy = 0;

        barcodeReaderFieldPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Barcode"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        label = new JLabel("Barcode: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        barcodeReaderFieldPane.add(label, c3);

        mBarcodeReaderText = new JTextField(13);
        mBarcodeReaderText.setDocument(new JTextFieldLimit(13));
        mBarcodeReaderText.setText("1234567890123");
        c3.gridx = 1;
        barcodeReaderFieldPane.add(mBarcodeReaderText, c3);

        barcodeReaderFieldPane.add(label, c3);

        // Barcode reader panel ends

        // Add barcode reader panel to settings
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        settingsPane.add(barcodeReaderFieldPane, c2);

        ///////////////////////////////
        // Random contribution to sensor values

        JPanel randomFieldPane = new JPanel(new GridBagLayout());
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy = 0;

        randomFieldPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Random component"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        label = new JLabel("Accelerometer: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        randomFieldPane.add(label, c3);

        mRandomAccelerometerText = new JTextField(5);
        mRandomAccelerometerText.setText("0");
        c3.gridx = 1;
        randomFieldPane.add(mRandomAccelerometerText, c3);

        label= new JLabel(" m/s" + SQUARED, JLabel.LEFT);
        c3.gridx = 2;
        randomFieldPane.add(label, c3);


        label = new JLabel("Compass: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        randomFieldPane.add(label, c3);

        mRandomMagneticFieldText = new JTextField(5);
        mRandomMagneticFieldText.setText("0");
        c3.gridx = 1;
        randomFieldPane.add(mRandomMagneticFieldText, c3);

        label = new JLabel(" nT", JLabel.LEFT);
        c3.gridx = 2;
        randomFieldPane.add(label, c3);

        label = new JLabel("Orientation: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        randomFieldPane.add(label, c3);

        mRandomOrientationText = new JTextField(5);
        mRandomOrientationText.setText("0");
        c3.gridx = 1;
        randomFieldPane.add(mRandomOrientationText, c3);

        label = new JLabel(" " + DEGREES, JLabel.LEFT);
        c3.gridx = 2;
        randomFieldPane.add(label, c3);

        label = new JLabel("Temperature: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        randomFieldPane.add(label, c3);

        mRandomTemperatureText = new JTextField(5);
        mRandomTemperatureText.setText("0");
        c3.gridx = 1;
        randomFieldPane.add(mRandomTemperatureText, c3);

        label = new JLabel(" " + DEGREES + "C", JLabel.LEFT);
        c3.gridx = 2;
        randomFieldPane.add(label, c3);

        // Random field panel ends

        // Add random field panel to settings
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        settingsPane.add(randomFieldPane, c2);

        /////////////////////////////////////////////////////


        ///////////////////////////////
        // Real sensor bridge

        JPanel realSensorBridgeFieldPane = new JPanel(new GridBagLayout());
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridwidth = 3;
        c3.gridx = 0;
        c3.gridy = 0;

        realSensorBridgeFieldPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Real sensor bridge"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        mRealDeviceThinkpad = new JCheckBox("Use Thinkpad accelerometer");
        mRealDeviceThinkpad.setSelected(false);
        mRealDeviceThinkpad.addItemListener(this);
        c3.gridwidth = 1;
        c3.gridx = 0;
        realSensorBridgeFieldPane.add(mRealDeviceThinkpad, c3);

        mRealDeviceWiimote = new JCheckBox("Use Wii-mote accelerometer");
        mRealDeviceWiimote.setSelected(false);
        mRealDeviceWiimote.addItemListener(this);
        c3.gridy++;
        realSensorBridgeFieldPane.add(mRealDeviceWiimote, c3);

        mRealDevicePath = new JTextField(20);
        mRealDevicePath.setText("/sys/devices/platform/hdaps/position");
        c3.gridx = 0;
        c3.gridy++;
        realSensorBridgeFieldPane.add(mRealDevicePath, c3);

        mRealDeviceOutputLabel = new JLabel("-", JLabel.LEFT);
        c3.gridx = 0;
        c3.gridy++;
        realSensorBridgeFieldPane.add(mRealDeviceOutputLabel, c3);

        // Real sensor bridge ends

        // Add real sensor bridge field panel to settings
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        settingsPane.add(realSensorBridgeFieldPane, c2);


        /////////////////////////////////////////////////////

        /////////////////////////////////////////////////////
        // Add settings scroll panel to right pane.
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        centerPanel.add(settingsScrollPane, c);

        // We put both into a Split pane:
        //Create a split pane with the two scroll panes in it.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   leftPanel, centerPanel);
        splitPane.setDividerLocation(271);

        //add such splitPane to Frame
        add(splitPane, BorderLayout.CENTER);

        /////////////////////////////////////////////////////
        // Right Panel.
        //Create right Panel with BorderLayout
        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel telnetLabel = new JLabel("Telnet simulations", JLabel.CENTER);
        telnetLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font labelFont = new Font("SansSerif", Font.PLAIN, 22);
        telnetLabel.setFont(labelFont);
        telnetLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        rightPanel.add(telnetLabel, BorderLayout.NORTH);

        //Create new Panel with BorderLayout
        JPanel telnetSimulationsPanel = new JPanel(new BorderLayout());

        JPanel batteryCapacityPanel = new JPanel(new BorderLayout());

        JLabel batteryLabel = new JLabel("Battery", JLabel.CENTER);
        batteryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        batterySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        batterySlider.addChangeListener(this);

	    batterySlider.setMajorTickSpacing(10);
	    batterySlider.setMinorTickSpacing(5);
	    batterySlider.setPaintTicks(true);
	    batterySlider.setPaintLabels(true);
	    batterySlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
	    batteryCapacityPanel.add(batteryLabel, BorderLayout.LINE_START);
	    batteryCapacityPanel.add(batterySlider, BorderLayout.CENTER);

	    //add batteryCapacityPanel to telnetSimulationsPanel
	    telnetSimulationsPanel.add(batteryCapacityPanel, BorderLayout.NORTH);

	    // Now add a scrollable panel with more controls and GridBagLayout
        JPanel telnetSettingsPanel = new JPanel(new GridBagLayout());
        //define GridBagConstraints
        c2 = new GridBagConstraints();

        //Create ScrollPane for simulations through telnet connection
        JScrollPane telnetSettingsScrollPane = new JScrollPane(telnetSettingsPanel);
        telnetSettingsScrollPane.setVerticalScrollBarPolicy(
        		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        telnetSettingsScrollPane.setPreferredSize(new Dimension(300, 250));

        JLabel telnetSettingsLabel = new JLabel("Telnet Settings", JLabel.CENTER);
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.NORTHWEST;
        c2.gridwidth = 1;
        c2.gridx = 0;
        c2.gridy = 0;
        telnetSettingsPanel.add(telnetSettingsLabel, c2);

        JSeparator telnetSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        c2.gridy++;
        telnetSettingsPanel.add(telnetSeparator, c2);

        //Now add neccesary things for battery simulation
        JPanel batteryPanel = new JPanel();
        batteryPanel.setLayout(new BoxLayout(batteryPanel, BoxLayout.PAGE_AXIS));

        batteryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Battery"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        batteryPresence = new JCheckBox("Is Present");
        batteryPresence.setSelected(true);
        batteryPresence.addItemListener(this);
        batteryPanel.add(batteryPresence);

        batteryAC = new JCheckBox("AC plugged");
        batteryAC.setSelected(true);
        batteryAC.addItemListener(this);
        batteryPanel.add(batteryAC);

        c2.gridy++;
        telnetSettingsPanel.add(batteryPanel,c2);

        //Now add neccesary things for battery simulation
        JPanel batteryStatusPanel = new JPanel();
        batteryStatusPanel.setLayout(new BoxLayout(batteryStatusPanel, BoxLayout.PAGE_AXIS));

        batteryStatusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Battery Status"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        String[] batteryStatus = { "unknown", "charging", "discharging", "not-charging", "full" };
        //Create the combo box, select item at index 4.
        batteryStatusList = new JComboBox(batteryStatus);
        batteryStatusList.setSelectedIndex(4);
        batteryStatusList.addActionListener(this);
        batteryStatusPanel.add(batteryStatusList);

        c2.gridy++;
        telnetSettingsPanel.add(batteryStatusPanel,c2);

        //Now add neccesary things for battery simulation
        JPanel batteryHealthPanel = new JPanel();
        batteryHealthPanel.setLayout(new BoxLayout(batteryHealthPanel, BoxLayout.PAGE_AXIS));

        batteryHealthPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Battery Health"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        String[] batteryHealth = { "unknown", "good", "overheat", "dead", "overvoltage", "failure" };
        //Create the combo box, select item at index 4.
        batteryHealthList = new JComboBox(batteryHealth);
        batteryHealthList.setSelectedIndex(2);
        batteryHealthList.addActionListener(this);
        batteryHealthPanel.add(batteryHealthList);

        c2.gridy++;
        telnetSettingsPanel.add(batteryHealthPanel,c2);

        //Now add neccesary things for battery simulation
        JPanel batteryFilePanel = new JPanel(new BorderLayout());

        batteryFilePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Battery File"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        //create everything need for battery emulation from file
        fileChooser = new JFileChooser();
        openButton = new JButton("Open a File");
        openButton.addActionListener(this);
        batteryFilePanel.add(openButton, BorderLayout.PAGE_START);

        batteryEmulation = new JButton("Emulate Battery");
        batteryFilePanel.add(batteryEmulation, BorderLayout.WEST);
        batteryEmulation.setActionCommand(emulateBattery);
        batteryEmulation.addActionListener(this);

        batteryNext = new JButton("Next time event");
        batteryFilePanel.add(batteryNext, BorderLayout.EAST);
        batteryNext.setActionCommand(nextTimeEvent);
        batteryNext.addActionListener(this);

        c2.gridy++;
        telnetSettingsPanel.add(batteryFilePanel,c2);

        //add neccesary things for GPS emulation
        JPanel gpsPanel = new JPanel(new GridBagLayout());
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridwidth = 2;
        c3.gridx = 0;
        c3.gridy = 0;

        gpsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("GPS"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        JLabel gpsLongitudeLabel = new JLabel("GPS Longitude: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        gpsPanel.add(gpsLongitudeLabel, c3);

        gpsLongitudeText = new JTextField(10);
        c3.gridx = 1;
        gpsPanel.add(gpsLongitudeText, c3);

        JLabel gpsLongitudeUnitLabel = new JLabel("degress", JLabel.LEFT);
        c3.gridx = 2;
        gpsPanel.add(gpsLongitudeUnitLabel, c3);

        JLabel gpsLatitudeLabel = new JLabel("GPS Latitude: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        gpsPanel.add(gpsLatitudeLabel, c3);

        gpsLatitudeText = new JTextField(10);
        c3.gridx = 1;
        gpsPanel.add(gpsLatitudeText, c3);

        JLabel gpsLatitudeUnitLabel = new JLabel("degress", JLabel.LEFT);
        c3.gridx = 2;
        gpsPanel.add(gpsLatitudeUnitLabel, c3);

        JLabel gpsAltitudeLabel = new JLabel("GPS Altitude: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        gpsPanel.add(gpsAltitudeLabel, c3);

        gpsAltitudeText = new JTextField(10);
        c3.gridx = 1;
        gpsPanel.add(gpsAltitudeText, c3);

        JLabel gpsAltitudeUnitLabel = new JLabel("meters", JLabel.LEFT);
        c3.gridx = 2;
        gpsPanel.add(gpsAltitudeUnitLabel, c3);

        JLabel gpsLisNameLabel = new JLabel("LIS name: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        gpsPanel.add(gpsLisNameLabel, c3);

        lisName = new JTextField(10);
        c3.gridx = 1;
        gpsPanel.add(lisName, c3);

        gpsButton = new JButton("Send GPS");
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;

        gpsPanel.add(gpsButton, c3);
        gpsButton.setActionCommand(sendGPS);
        gpsButton.addActionListener(this);

        // Add gpsPanel to telnetSettingsPanel
        c2.gridx = 0;
        c2.gridwidth = 1;
        c2.gridy++;
        telnetSettingsPanel.add(gpsPanel, c2);

        //add telnetSettingsScrollPane to telnetSimulationsPanel
        telnetSimulationsPanel.add(telnetSettingsScrollPane, BorderLayout.CENTER);

        //add telnetSimulationsPanel to rightPanel
	    rightPanel.add(telnetSimulationsPanel, BorderLayout.CENTER);


        /////////////////////////////////////////////////////
        //add panels to GUI and use layout
        // We put previous splitPane and our new JPanel into new SplitPanel
        JSplitPane splitPanel2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   splitPane, rightPanel);
        splitPanel2.setDividerLocation(600);

        //add such splitPanel to the JFrame and place in CENTER
        add(splitPanel2, BorderLayout.CENTER);

        /////////////////////////////////////////////////////
        // Fill the possible values

        socketText.setText("8010");
        telnetSocketText.setText("5554");

        ipselectionText.append("Write emulator command port and\n" +
		"click on set to create connection.\n");

        ipselectionText.append("Possible IP addresses:\n");
        try {
	        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	        for (NetworkInterface netint : Collections.list(nets)) {
	            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	            	if (("" + inetAddress).compareTo("/127.0.0.1") != 0) {
	            		ipselectionText.append("" + inetAddress + "\n");
	            	}
		        }

	        }
        } catch (SocketException e) {
        	ipselectionText.append("Socket exception. Could not obtain IP addresses.");
        }

        // Set up the server:
        mSensorServer = new SensorServer(this);


		// Variables for timing:
		updateSensorCount = 0;
	    updateSensorTime = System.currentTimeMillis();
	    updateEmulatorAccelerometerCount = 0;
	    updateEmulatorAccelerometerTime = System.currentTimeMillis();

        //Set up a timer that calls this object's action handler.
        delay = 500;
        timer = new Timer(delay, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SensorSimulator.this.actionPerformed(new ActionEvent(evt.getSource(), evt.getID(), timerAction));
            }
        });
        //timer.setInitialDelay(delay * 7); //We pause animation twice per cycle
                                          //by restarting the timer
        timer.setCoalesce(true);

        timer.start();

	}

    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }


    //React to window events.
    public void windowIconified(WindowEvent e) {
        timer.stop();
    }
    public void windowDeiconified(WindowEvent e) {
        timer.start();
    }
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}


    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (source == yawSlider) {
        	mobile.setYawDegree(source.getValue());
        	mobile.doRepaint();
        } else if (source == pitchSlider) {
        	mobile.setPitchDegree(source.getValue());
        	mobile.doRepaint();
        } else if (source == rollSlider) {
        	mobile.setRollDegree(source.getValue());
        	mobile.doRepaint();
        }else if (source == batterySlider){
        	if(mTelnetServer!=null){
        		mTelnetServer.changePower(source.getValue());
        	}
        }
    }


    /**
     * Listener for checkbox events.
     */
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        // Don't allow the thinkpad and wiimote to be selected at the same time
        if (source == mRealDeviceThinkpad && e.getStateChange() == ItemEvent.SELECTED) {
        	mRealDeviceWiimote.setSelected(false);
        }
        else if (source == mRealDeviceWiimote && e.getStateChange() == ItemEvent.SELECTED) {
        	mRealDeviceThinkpad.setSelected(false);
        }

        if (source == mShowAcceleration) {
        	// Refresh the screen when this drawing element
        	// changes
        	mobile.doRepaint();
        }

        //add for battery
        if (source == batteryPresence && e.getStateChange() == ItemEvent.SELECTED){
        	if(mTelnetServer!=null){
            	mTelnetServer.changePresence(true);
        	}
        }else if (source == batteryPresence && e.getStateChange() == ItemEvent.DESELECTED){
        	if(mTelnetServer!=null){
            	mTelnetServer.changePresence(false);
        	}
        }

        if (source == batteryAC && e.getStateChange() == ItemEvent.SELECTED){
        	if(mTelnetServer!=null){
            	mTelnetServer.changeAC(true);
        	}
        }else if (source == batteryAC && e.getStateChange() == ItemEvent.DESELECTED){
        	if(mTelnetServer!=null){
            	mTelnetServer.changeAC(false);
        	}
        }
    }

    //Called when the Timer fires, or selection is done
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if (action.equals(yawPitch)) {
        	mouseMode = mouseYawPitch;
        } else if (action.equals(rollPitch)) {
        	mouseMode = mouseRollPitch;
        } else if (action.equals(move)) {
        	mouseMode = mouseMove;
        } else if (action.equals(timerAction)) {
        	doTimer();
        } else if (action.equals(setPortString)) {
        	setPort();
        } else if(action.equals(connectViaTelnet)){
        	connectViaTelnet();
        }else if(e.getSource().equals(batteryHealthList) && "comboBoxChanged".equals(e.getActionCommand())){
        	if(mTelnetServer!=null){
            	mTelnetServer.changeHealth(batteryHealthList.getSelectedItem());
        	}
        }else if(e.getSource().equals(batteryStatusList) && "comboBoxChanged".equals(e.getActionCommand())){
        	if(mTelnetServer!=null){
            	mTelnetServer.changeStatus(batteryStatusList.getSelectedItem());
        	}
        } else if (action.equals(sendGPS)){
        	sendGps();
        } else if (action.equals(emulateBattery)) {
        	if(mTelnetServer!=null){
            	mTelnetServer.slowEmulation();
        	}
        } else if (action.equals(nextTimeEvent)) {
        	if(mTelnetServer!=null){
            	mTelnetServer.nextTimeEvent();
        	}
        }else if(e.getSource()==openButton){
        	int returnVal = fileChooser.showOpenDialog(this);
        	if(returnVal==JFileChooser.APPROVE_OPTION){
        		File file = fileChooser.getSelectedFile();
        		if(mTelnetServer!=null){
            		mTelnetServer.openFile(file);
        		}else{
            		this.addMessage("Connect with emulator to start simulation");
        		}
        	}else{

        	}

        }
    }

    private void doTimer() {
    	if (mRealDeviceWiimote.isSelected()) {
    		updateFromWiimote();
    	}

    	// Update sensors:
    	mobile.updateSensorPhysics();

    	mobile.updateSensorReadoutValues();

    	mobile.updateUserSettings();

    	// Measure refresh
    	updateSensorRefresh();

    	// Now show updated data:
    	showSensorData();
    }

    /**
     * Updates the information about sensor updates.
     */
    public void updateSensorRefresh() {
    	updateSensorCount++;
    	long maxcount = (long) getSafeDouble(mRefreshCountText);
		if (maxcount >= 0 && updateSensorCount >= maxcount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - updateSensorTime)
						/ ((double) maxcount);

			DecimalFormat mf = new DecimalFormat("#0.0");

			mRefreshSensorsLabel.setText(mf.format(ms) + " ms");

			updateSensorCount = 0;
			updateSensorTime = newtime;
		}
    }

    /**
     * Updates information about emulator updates.
     */
    public void updateEmulatorAccelerometerRefresh() {
	    updateEmulatorAccelerometerCount++;
    	long maxcount = (long) getSafeDouble(mRefreshCountText);
    	if (maxcount >= 0 && updateEmulatorAccelerometerCount >= maxcount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - updateEmulatorAccelerometerTime)
						/ ((double) maxcount);

			DecimalFormat mf = new DecimalFormat("#0.0");

			mRefreshEmulatorAccelerometerLabel.setText(mf.format(ms) + " ms");

			updateEmulatorAccelerometerCount = 0;
			updateEmulatorAccelerometerTime = newtime;
		}
    }

    public void updateEmulatorCompassRefresh() {
	    updateEmulatorCompassCount++;
    	long maxcount = (long) getSafeDouble(mRefreshCountText);
		if (maxcount >= 0 && updateEmulatorCompassCount >= maxcount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - updateEmulatorCompassTime)
						/ ((double) maxcount);

			DecimalFormat mf = new DecimalFormat("#0.0");

			mRefreshEmulatorCompassLabel.setText(mf.format(ms) + " ms");

			updateEmulatorCompassCount = 0;
			updateEmulatorCompassTime = newtime;
		}
    }
    public void updateEmulatorOrientationRefresh() {
	    updateEmulatorOrientationCount++;
    	long maxcount = (long) getSafeDouble(mRefreshCountText);
		if (maxcount >= 0 && updateEmulatorOrientationCount >= maxcount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - updateEmulatorOrientationTime)
						/ ((double) maxcount);

			DecimalFormat mf = new DecimalFormat("#0.0");

			mRefreshEmulatorOrientationLabel.setText(mf.format(ms) + " ms");

			updateEmulatorOrientationCount = 0;
			updateEmulatorOrientationTime = newtime;
		}
    }

    public void updateEmulatorThermometerRefresh() {
	    updateEmulatorAccelerometerCount++;
    	long maxcount = (long) getSafeDouble(mRefreshCountText);
		if (maxcount >= 0 && updateEmulatorThermometerCount >= maxcount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - updateEmulatorThermometerTime)
						/ ((double) maxcount);

			DecimalFormat mf = new DecimalFormat("#0.0");

			mRefreshEmulatorThermometerLabel.setText(mf.format(ms) + " ms");

			updateEmulatorThermometerCount = 0;
			updateEmulatorThermometerTime = newtime;
		}
    }

    /**
     * This method is used to show currently enabled sensor values in info pane.
     */
    private void showSensorData() {
    	DecimalFormat mf = new DecimalFormat("#0.00");

    	String data = "";
    	if (mSupportedAccelerometer.isSelected()) {
	    	data += ACCELEROMETER + ": ";
	    	if (mEnabledAccelerometer.isSelected()) {
				data += mf.format(mobile.getReadAccelerometerX()) + ", "
						+ mf.format(mobile.getReadAccelerometerY()) + ", "
						+ mf.format(mobile.getReadAccelerometerZ());
	    	} else {
				data += DISABLED;
	    	}
			data += "\n";
    	}

    	if (mSupportedMagneticField.isSelected()) {
    	    // Compass data
			data += MAGNETIC_FIELD + ": ";
			if (mEnabledMagneticField.isSelected()) {
				data += mf.format(mobile.getReadCompassX()) + ", "
						+ mf.format(mobile.getReadCompassY()) + ", "
						+ mf.format(mobile.getReadCompassZ());
			} else {
				data += DISABLED;
			}
			data += "\n";
    	}

		if (mSupportedOrientation.isSelected()) {
		    // Orientation data
			data += ORIENTATION + ": ";
			if (mEnabledOrientation.isSelected()) {
				data += mf.format(mobile.getReadYaw()) + ", "
						+ mf.format(mobile.getReadPitch()) + ", "
						+ mf.format(mobile.getReadRoll());
			} else {
				data += DISABLED;
			}
			data += "\n";
		}

		if (mSupportedTemperature.isSelected()) {
			data += TEMPERATURE + ": ";
			if (mEnabledTemperature.isSelected()) {
				data += mf.format(mobile.getReadTemperature());
			} else {
				data += DISABLED;
			}
			data += "\n";
		}

		if (mSupportedBarcodeReader.isSelected()) {
			data += BARCODE_READER + ": ";
			if (mEnabledBarcodeReader.isSelected()) {
				data += mobile.getBarcode();
			} else {
				data += DISABLED;
			}
			data += "\n";
		}
		// Output to textArea:
		textAreaSensorData.setText(data);
    }

    /**
     * Sets the socket port for listening
     */
    private void setPort() {
    	addMessage("Closing port " + mSensorServer.port);
    	// First close all old ports:
    	mSensorServer.stop();

    	// now restart
    	mSensorServer = new SensorServer(this);
    }

    /**
     * Connect via telnet with emulator
     */
    private void connectViaTelnet(){
    	if(mTelnetServer==null){
    		mTelnetServer = new TelnetServer(this);
    		mTelnetServer.connect();
    	}else{
    		addMessage("Closing telnet port " + mTelnetServer.port);
    		mTelnetServer.disconnect();
    		mTelnetServer = new TelnetServer(this);
    		mTelnetServer.connect();
    	}
    }

    /**
     * Once sendGps is called, simulator sends geo fix command to emulator via telnet.
     */
    private void sendGps(){
    	if(mTelnetServer != null){
    	mTelnetServer.sendGPS();
    	}
    }

    /**
     * Adds new message to message box.
     * If scroll position is at end, it will scroll to new message.
     * @param msg Message.
     */
    public void addMessage(String msg) {

    	// Determine whether the scrollbar is currently at the very bottom position.
    	JScrollBar vbar = areaScrollPane.getVerticalScrollBar();
    	final int tolerance = 10; // some tolerance value
    	boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount() + tolerance) >= vbar.getMaximum());

    	// append to the JTextArea (that's wrapped in a JScrollPane named 'scrollPane'
    	ipselectionText.append(msg + "\n");

    	// now scroll if we were already at the bottom.
    	if( autoScroll ) ipselectionText.setCaretPosition( ipselectionText.getDocument().getLength() );

    }

    /**
     * Get socket port number.
     * @return String containing port number.
     */
    public int getPort() {
    	String s = socketText.getText();
    	int port = 0;
    	try {
    		port = Integer.parseInt(s);
    	} catch (NumberFormatException e){
    		addMessage("Invalid port number: " + s);
    	}
    	return port;
    }

    /**
     * Get telnet socket port number.
     * @return String containing port number.
     */
    public int getTelnetPort() {
    	String s = telnetSocketText.getText();
    	int port = 0;
    	try {
    		port = Integer.parseInt(s);
    	} catch (NumberFormatException e){
    		addMessage("Invalid port number: " + s);
    	}
    	return port;
    }

    /**
     * Get Longitude from TextField.
     * @return longitude, float longitude
     */
    public float getLongitude() {
    	String s = gpsLongitudeText.getText();
    	float longitude = 0;
    	try {
    		longitude = Float.parseFloat(s);
    	} catch (NumberFormatException e){
    	}
    	return longitude;
    }

    /**
     * Get Latitude for TextField.
     * @return latitude, float latitude
     */
    public float getLatitude() {
    	String s = gpsLatitudeText.getText();
    	float latitude = 0;
    	try {
    		latitude = Float.parseFloat(s);
    	} catch (NumberFormatException e){
    	}
    	return latitude;
    }

    /**
     * Get Altitude from TextField.
     * @return altitude, float altitude
     */
    public float getAltitude() {
    	String s = gpsAltitudeText.getText();
    	float altitude = 0;
    	try {
    		altitude = Float.parseFloat(s);
    	} catch (NumberFormatException e){
    	}
    	return altitude;
    }

    /**
     * Get LIS Name
     * @return name, String lis name
     */
    public String getLisName() {
    	String name = lisName.getText();
    	return name;
    }

    /**
     * Get barcode
     * @return barcode, String barcode
     */
    public String getBarcode() {
    	String barcode = mBarcodeReaderText.getText();
    	return barcode;
    }

    /**
     * Method used by our LIS emulator to write current Longitude and Latitude
     * to simulator's TextFields.
     * @param Longitude, Double Longitude
     * @param Latitude, Double Latitude
     */
    public void setGPS(Double Longitude, Double Latitude) {
    	gpsLongitudeText.setText(Longitude.toString());
    	gpsLatitudeText.setText(Latitude.toString());
    }

    /**
     * This method is called by SensorServerThread when
     * a new client connects.
     */
    public void newClient() {
    	mIncomingConnections++;
    	if (mIncomingConnections <= 1) {
    		// We have been connected for the first time.
    		// Disable all sensors:
    		mEnabledAccelerometer.setSelected(false);
    		mEnabledMagneticField.setSelected(false);
    		mEnabledOrientation.setSelected(false);
    		mEnabledTemperature.setSelected(false);
    		mEnabledBarcodeReader.setSelected(false);

    		addMessage("First incoming connection:");
    		addMessage("ALL SENSORS DISABLED!");
    	}
    }

    /**
	 * Safely retries the double value of a text field.
	 * If the value is not a valid number, 0 is returned, and the field
	 * is marked red.
	 *
	 * @param textfield Textfield from which the value should be read.
	 * @param defaultValue default value if input field is invalid.
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
	 * Safely retries the a list of double values of a text field.
	 * If the list contains errors, null is returned, and the field
	 * is marked red.
	 *
	 * @param textfield Textfield from which the value should be read.
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
				for (int i = 0; i<len; i++) {
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

	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        final JFrame frame = new JFrame("SensorSimulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SensorSimulator simulator = new SensorSimulator();

        //Add content to the window.
        frame.add(simulator, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }



    private void updateFromWiimote() {
    	// Read raw data
		wiiMoteData.setDataFilePath(mRealDevicePath.getText());

		boolean success = wiiMoteData.updateData();
		mRealDeviceOutputLabel.setText(wiiMoteData.getStatus());

		if (success) {
			// Update controls
			yawSlider.setValue(0);  // Wiimote can't support yaw
			rollSlider.setValue(wiiMoteData.getRoll());
			pitchSlider.setValue(wiiMoteData.getPitch());
		}
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /////////////////////////////////
    // implements ISensorSimulator
    /////////////////////////////////

	public int getMouseMode() {
		return mouseMode;
	}

	public WiiMoteData getWiiMoteData() {
		return wiiMoteData;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
		this.timer.setDelay(delay);
	}

	public IMobilePanel getMobilePanel() {
		return mobile;
	}

	public int getYaw() {
		return yawSlider.getValue();
	}
	public int getPitch() {
		return pitchSlider.getValue();
	}
	public int getRoll() {
		return rollSlider.getValue();
	}

	public void setYaw(int yaw) {
		yawSlider.setValue(yaw);
	}
	public void setPitch(int pitch) {
		pitchSlider.setValue(pitch);
	}
	public void setRoll(int roll) {
		rollSlider.setValue(roll);
	}

	public boolean isSupportedOrientation() {
		return mSupportedOrientation.isSelected();
	}

	public boolean isSupportedAccelerometer() {
		return mSupportedAccelerometer.isSelected();
	}

	public boolean isSupportedTemperature() {
		return mSupportedTemperature.isSelected();
	}

	public boolean isSupportedMagneticField() {
		return mSupportedMagneticField.isSelected();
	}

	public boolean isSupportedLight() {
		return mSupportedLight.isSelected();
	}

	public boolean isSupportedProximity() {
		return mSupportedProximity.isSelected();
	}

	public boolean isSupportedTricorder() {
		return mSupportedTricorder.isSelected();
	}

	public boolean isSupportedBarcodeReader() {
		return mSupportedBarcodeReader.isSelected();
	}

	public boolean isEnabledOrientation() {
		return mEnabledOrientation.isSelected();
	}

	public boolean isEnabledAccelerometer() {
		return mEnabledAccelerometer.isSelected();
	}

	public boolean isEnabledTemperature() {
		return mEnabledTemperature.isSelected();
	}

	public boolean isEnabledMagneticField() {
		return mEnabledMagneticField.isSelected();
	}

	public boolean isEnabledLight() {
		return mEnabledLight.isSelected();
	}

	public boolean isEnabledProximity() {
		return mEnabledProximity.isSelected();
	}

	public boolean isEnabledTricorder() {
		return mEnabledTricorder.isSelected();
	}

	public boolean isEnabledBarcodeReader() {
		return mEnabledBarcodeReader.isSelected();
	}

	public void setEnabledOrientation(boolean enable) {
		mEnabledOrientation.setSelected(enable);
		mRefreshEmulatorOrientationLabel.setText("-");
	}

	public void setEnabledAccelerometer(boolean enable) {
		mEnabledAccelerometer.setSelected(enable);
		mRefreshEmulatorAccelerometerLabel.setText("-");
	}

	public void setEnabledTemperature(boolean enable) {
		mEnabledTemperature.setSelected(enable);
		mRefreshEmulatorThermometerLabel.setText("-");
	}

	public void setEnabledMagneticField(boolean enable) {
		mEnabledMagneticField.setSelected(enable);
		mRefreshEmulatorCompassLabel.setText("-");
	}

	public void setEnabledLight(boolean enable) {
		mEnabledLight.setSelected(enable);
	}

	public void setEnabledProximity(boolean enable) {
		mEnabledProximity.setSelected(enable);
	}

	public void setEnabledTricorder(boolean enable) {
		mEnabledTricorder.setSelected(enable);
	}

	public void setEnabledBarcodeReader(boolean enable) {
		mEnabledBarcodeReader.setSelected(enable);
	}

	public double[] getUpdateRatesAccelerometer() {
		return getSafeDoubleList(mUpdateRatesAccelerometerText);
	}

	public double getDefaultUpdateRateAccelerometer() {
		return getSafeDouble(mDefaultUpdateRateAccelerometerText);
	}

	public double getCurrentUpdateRateAccelerometer() {
		return getSafeDouble(mCurrentUpdateRateAccelerometerText, 0);
	}

	public boolean updateAverageAccelerometer() {
		return mUpdateAverageAccelerometer.isSelected();
	}

	public double[] getUpdateRatesCompass() {
		return getSafeDoubleList(mUpdateRatesCompassText);
	}

	public double getDefaultUpdateRateCompass() {
		return getSafeDouble(mDefaultUpdateRateCompassText);
	}

	public double getCurrentUpdateRateCompass() {
		return getSafeDouble(mCurrentUpdateRateCompassText, 0);
	}

	public boolean updateAverageCompass() {
		return mUpdateAverageCompass.isSelected();
	}

	public double[] getUpdateRatesOrientation() {
		return getSafeDoubleList(mUpdateRatesOrientationText);
	}

	public double getDefaultUpdateRateOrientation() {
		return getSafeDouble(mDefaultUpdateRateOrientationText);
	}

	public double getCurrentUpdateRateOrientation() {
		return getSafeDouble(mCurrentUpdateRateOrientationText, 0);
	}

	public boolean updateAverageOrientation() {
		return mUpdateAverageOrientation.isSelected();
	}

	public double[] getUpdateRatesThermometer() {
		return getSafeDoubleList(mUpdateRatesThermometerText);
	}

	public double getDefaultUpdateRateThermometer() {
		return getSafeDouble(mDefaultUpdateRateThermometerText);
	}

	public double getCurrentUpdateRateThermometer() {
		return getSafeDouble(mCurrentUpdateRateThermometerText, 0);
	}

	public boolean updateAverageThermometer() {
		return mUpdateAverageThermometer.isSelected();
	}

	public void setCurrentUpdateRateAccelerometer(double value) {
		mCurrentUpdateRateAccelerometerText.setText(Double.toString(value));
	}

	public void setCurrentUpdateRateCompass(double value) {
		mCurrentUpdateRateCompassText.setText(Double.toString(value));
	}

	public void setCurrentUpdateRateOrientation(double value) {
		mCurrentUpdateRateOrientationText.setText(Double.toString(value));
	}

	public void setCurrentUpdateRateThermometer(double value) {
		mCurrentUpdateRateThermometerText.setText(Double.toString(value));
	}

	public double getUpdateSensors() {
		return getSafeDouble(mUpdateText);
	}

	public double getRefreshAfter() {
		return getSafeDouble(mRefreshCountText);
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

	public boolean isShowAcceleration() {
		return mShowAcceleration.isSelected();
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

	public double getMagneticFieldNorth() {
		return getSafeDouble(mMagneticFieldNorthText);
	}

	public double getMagneticFieldEast() {
		return getSafeDouble(mMagneticFieldEastText);
	}

	public double getMagneticFieldVertical() {
		return getSafeDouble(mMagneticFieldVerticalText);
	}

	public double getTemperature() {
		return getSafeDouble(mTemperatureText);
	}

	public double getRandomAccelerometer() {
		return getSafeDouble(mRandomAccelerometerText);
	}

	public double getRandomMagneticField() {
		return getSafeDouble(mRandomMagneticFieldText);
	}

	public double getRandomOrientation() {
		return getSafeDouble(mRandomOrientationText);
	}

	public double getRandomTemperature() {
		return getSafeDouble(mRandomTemperatureText);
	}

	public boolean useRealDeviceThinkpad() {
		return mRealDeviceThinkpad.isSelected();
	}

	public boolean useRealDeviceWiimtoe() {
		return mRealDeviceWiimote.isSelected();
	}

	public String getRealDevicePath() {
		return mRealDevicePath.getText();
	}

	public void setRealDeviceOutput(String text) {
		mRealDeviceOutputLabel.setText(text);
	}

}


