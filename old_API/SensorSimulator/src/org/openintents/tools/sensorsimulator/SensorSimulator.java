/* 
 * Copyright (C) 2008 OpenIntents.org
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

package org.openintents.tools.sensorsimulator;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
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

/**
 *  Main class of SensorSimulator.
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
 */
public class SensorSimulator extends JPanel
							implements ActionListener,
								WindowListener,
								ChangeListener,
								ItemListener {
	
	//static long serialVersionUID = 1234;

	// Supported sensors:
	static final String ORIENTATION = "orientation";
	static final String ACCELEROMETER = "accelerometer";
	static final String TEMPERATURE = "temperature";
	static final String MAGNETIC_FIELD = "magnetic field";
	static final String LIGHT = "light";
	static final String PROXIMITY = "proximity";
	static final String TRICORDER = "tricorder";
	
	static final String SHOW_ACCELERATION = "show acceleration";

	static final String AVERAGE_ORIENTATION = "average orientation";
	static final String AVERAGE_ACCELEROMETER = "average accelerometer";
	static final String AVERAGE_TEMPERATURE = "average temperature";
	static final String AVERAGE_MAGNETIC_FIELD = "average magnetic field";
	static final String AVERAGE_LIGHT = "average light";
	static final String AVERAGE_PROXIMITY = "average proximity";
	static final String AVERAGE_TRICORDER = "average tricorder";
	
	static final String DISABLED = "DISABLED";
	
	// Constant giving the unicode value of degrees symbol.
	final static public String DEGREES = "\u00B0";
	final static public String MICRO = "\u00b5";
	final static public String PLUSMINUS = "\u00b1";
	final static public String SQUARED = "\u00b2"; // superscript two
	
	// Simulation delay:
	int delay;
    Timer timer;
    
    // for measuring updates:
    int updateSensorCount;
    long updateSensorTime;
    int updateEmulatorAccelerometerCount;
    long updateEmulatorAccelerometerTime;
    int updateEmulatorCompassCount;
    long updateEmulatorCompassTime;
    int updateEmulatorOrientationCount;
    long updateEmulatorOrientationTime;
    int updateEmulatorThermometerCount;
    long updateEmulatorThermometerTime;
    
    
    int mouseMode;
    static int mouseYawPitch = 1;
    static int mouseRollPitch = 2;
    static int mouseMove = 3;
    
    // Displays the mobile phone
    MobilePanel mobile;
    
    // Sliders:
    JSlider yawSlider;
    JSlider pitchSlider;
    JSlider rollSlider;
    
    // Text fields:
    JTextField socketText;
    JButton socketButton;
    
    // Field for socket related output:
    JScrollPane areaScrollPane;
    JTextArea ipselectionText;
    
    // Field for sensor simulator data output:
    JScrollPane scrollPaneSensorData;
    JTextArea textAreaSensorData;
    
    // Settings
    // Supported sensors
    JCheckBox mSupportedOrientation;
    JCheckBox mSupportedAccelerometer;
    JCheckBox mSupportedTemperature;
    JCheckBox mSupportedMagneticField;
    JCheckBox mSupportedLight;
    JCheckBox mSupportedProximity;
    JCheckBox mSupportedTricorder;
    
    // Enabled sensors
    JCheckBox mEnabledOrientation;
    JCheckBox mEnabledAccelerometer;
    JCheckBox mEnabledTemperature;
    JCheckBox mEnabledMagneticField;
    JCheckBox mEnabledLight;
    JCheckBox mEnabledProximity;
    JCheckBox mEnabledTricorder;
    
    // Simulation update
    JTextField mUpdateRatesAccelerometerText;
    JTextField mDefaultUpdateRateAccelerometerText;
    JTextField mCurrentUpdateRateAccelerometerText;
    /** Whether to form an average at each update */
    JCheckBox mUpdateAverageAccelerometer;
    
    JTextField mUpdateRatesCompassText;
    JTextField mDefaultUpdateRateCompassText;
    JTextField mCurrentUpdateRateCompassText;
    /** Whether to form an average at each update */
    JCheckBox mUpdateAverageCompass;
    
    JTextField mUpdateRatesOrientationText;
    JTextField mDefaultUpdateRateOrientationText;
    JTextField mCurrentUpdateRateOrientationText;
    /** Whether to form an average at each update */
    JCheckBox mUpdateAverageOrientation;
    
    JTextField mUpdateRatesThermometerText;
    JTextField mDefaultUpdateRateThermometerText;
    JTextField mCurrentUpdateRateThermometerText;
    /** Whether to form an average at each update */
    JCheckBox mUpdateAverageThermometer;
    
    JTextField mUpdateText;
    JTextField mRefreshCountText;
    JLabel mRefreshSensorsLabel;
    JLabel mRefreshEmulatorAccelerometerLabel;
    JLabel mRefreshEmulatorCompassLabel;
    JLabel mRefreshEmulatorOrientationLabel;
    JLabel mRefreshEmulatorThermometerLabel;
    
    // Accelerometer
    JTextField mGravityConstantText;
    JTextField mAccelerometerLimitText;
    JTextField mPixelPerMeterText;
    JTextField mSpringConstantText;
    JTextField mDampingConstantText;
    JCheckBox mShowAcceleration;
    
    // Gravity
    JTextField mGravityXText;
    JTextField mGravityYText;
    JTextField mGravityZText;
    
    // Magnetic field
    JTextField mMagneticFieldNorthText;
    JTextField mMagneticFieldEastText;
    JTextField mMagneticFieldVerticalText;
    
    // Temperature
    JTextField mTemperatureText;
    
    // Random contribution
    JTextField mRandomOrientationText;
    JTextField mRandomAccelerometerText;
    JTextField mRandomTemperatureText;
    JTextField mRandomMagneticFieldText;
    JTextField mRandomLightText;
    JTextField mRandomProximityText;
    JTextField mRandomTricorderText;
    
    // Real device bridge
    JCheckBox mRealDeviceThinkpad;
    JCheckBox mRealDeviceWiimote;
    JTextField mRealDevicePath;
    JLabel mRealDeviceOutputLabel;
    
    // Action Commands:
    static String yawPitch = "yaw & pitch";
    static String rollPitch = "roll & pitch";
    static String move = "move";
    static String timerAction = "timer";
    static String setPortString = "set port";
    
    // Server for sending out sensor data
    SensorServer mSensorServer;
    int mIncomingConnections;
    
    WiiMoteData wiiMoteData = new WiiMoteData();
    
	
	public SensorSimulator() {
		// Initialize variables
		mIncomingConnections = 0;
				
		//setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setLayout(new BorderLayout());

		///////////////////////////////////////////////////////////////
        // Left pane
        //JPanel leftPane = new JPanel(new BorderLayout());
		
        GridBagLayout myGridBagLayout = new GridBagLayout();
        // myGridLayout.
        GridBagConstraints c = new GridBagConstraints();
        JPanel leftPane = new JPanel(myGridBagLayout);
        
        JPanel mobilePane = new JPanel(new BorderLayout());
        
		// Add the mobile
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        mobile = new MobilePanel(this);
        mobilePane.add(mobile);
        
        leftPane.add(mobilePane, c);
        
        // Add mouse action selection
        // through radio buttons.
        JRadioButton yawPitchButton = new JRadioButton(yawPitch);
        //yawTiltButton.setMnemonic(KeyEvent.VK_Y);
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
        leftPane.add(yawPitchButton, c);
        c.gridx++;
        leftPane.add(rollPitchButton, c);
        c.gridx++;
        leftPane.add(moveButton, c);
        
        // Add IP address properties:
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        JLabel socketLabel = new JLabel("Socket", JLabel.LEFT);
        leftPane.add(socketLabel, c);
        
        c.gridx = 1;
        socketText = new JTextField(5);
        leftPane.add(socketText, c);
        
        c.gridx = 2;
        socketButton = new JButton("Set");
        leftPane.add(socketButton, c);
        socketButton.setActionCommand(setPortString);
        socketButton.addActionListener(this);
        
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        ipselectionText = new JTextArea(3, 10);
        //Dimension d = new Dimension();
        //d.height = 50;
        //d.width = 200;
        //ipselectionText.setPreferredSize(d);
        //ipselectionText.setAutoscrolls(true);
        
        areaScrollPane = new JScrollPane(ipselectionText);
        areaScrollPane.setVerticalScrollBarPolicy(
        		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(250, 80));
        
        leftPane.add(areaScrollPane, c);
        
        
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        textAreaSensorData = new JTextArea(3, 10);
        scrollPaneSensorData = new JScrollPane(textAreaSensorData);
        scrollPaneSensorData.setVerticalScrollBarPolicy(
        		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneSensorData.setPreferredSize(new Dimension(250, 80));

        leftPane.add(scrollPaneSensorData, c);
        
        leftPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        ///////////////////////////////////////////////////////////////
        // Right pane
        JLabel simulatorLabel = new JLabel("OpenIntents Sensor Simulator", JLabel.CENTER);
        simulatorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font font = new Font("SansSerif", Font.PLAIN, 22);
        simulatorLabel.setFont(font);
        //Border border = new Border();
        //simulatorLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.blue));
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
	    mobile.yawDegree = yawSlider.getValue();
	    mobile.pitchDegree = pitchSlider.getValue();
	    mobile.rollDegree = rollSlider.getValue();    
	    mobile.yawSlider = yawSlider;
	    mobile.pitchSlider = pitchSlider;
	    mobile.rollSlider = rollSlider;

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
        JPanel rightPane = new JPanel(myGridBagLayout);
        //JPanel rightPane = new JPanel(new BorderLayout());
        //rightPane.add(splitPane);
        
        //Put everything together.
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        rightPane.add(simulatorLabel, c);
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy++;
        rightPane.add(yawLabel, c);
        c.gridx = 1;
        rightPane.add(yawSlider, c);
        c.gridx = 0;
        c.gridy++;
        rightPane.add(pitchLabel, c);
        c.gridx = 1;
        rightPane.add(pitchSlider, c);
        c.gridx = 0;
        c.gridy++;
        rightPane.add(rollLabel, c);
        c.gridx = 1;
        rightPane.add(rollSlider, c);
        rightPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

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
        // Checkbox for 3 sensors
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

        c2.gridy++;
        settingsPane.add(supportedSensorsPane,c2);


        ///////////////////////////////
        // Checkbox for 3 sensors
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
        
        /*
        label = new JLabel("Path: ", JLabel.LEFT);
        c3.gridwidth = 1;
        c3.gridx = 0;
        c3.gridy++;
        realSensorBridgeFieldPane.add(label, c3);
        */
        
        mRealDevicePath = new JTextField(20);
        mRealDevicePath.setText("/sys/devices/platform/hdaps/position");
        //mRealDeviceThinkpadPath.setText("C:\\temp\\position.txt");
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
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        rightPane.add(settingsScrollPane, c);
        
        // We put both into a Split pane:
        //Create a split pane with the two scroll panes in it.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   leftPane, rightPane);
        //splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(271);
        
        //Provide minimum sizes for the two components in the split pane
        //Dimension minimumSize = new Dimension(260, 50);
        //leftPane.setMinimumSize(minimumSize);
        //rightPane.setMinimumSize(minimumSize);

        add(splitPane, BorderLayout.CENTER);
        
        // Add left and right pane.
        //add(leftPane, BorderLayout.WEST);
        //add(rightPane, BorderLayout.EAST);
        
        
        
        // Fill the possible values
        
        socketText.setText("8010");
        
        ipselectionText.append("Possible IP addresses:\n");
        try {
	        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	        for (NetworkInterface netint : Collections.list(nets)) {
	            //out.printf("Display name: %s\n", netint.getDisplayName());
	            //ipselectionText.append("Name: " + netint.getName() + "\n");
	            //out.printf("Name: %s\n", netint.getName());
	            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	                //out.printf("InetAddress: %s\n", inetAddress);
	            	if (("" + inetAddress).compareTo("/127.0.0.1") != 0) {
	            		ipselectionText.append("" + inetAddress + "\n");
	            	}
	            	//ipselectionText.append("IP address: " + inetAddress + "\n");
		        }
	            //out.printf("\n");
	
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
        	mobile.yawDegree = source.getValue();
        	mobile.repaint();	
        } else if (source == pitchSlider) {
        	mobile.pitchDegree = source.getValue();
        	mobile.repaint();
        } else if (source == rollSlider) {
        	mobile.rollDegree = source.getValue();
        	mobile.repaint();
        }
    	
    	//if (!source.getValueIsAdjusting()) {
        //}
    }
    
    
    // Listener for checkbox events:
    // currently we don't have to do anything here...
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        /*
        if (source == mSupportedAccelerometer) {
        	
        } else if (source == mSupportedCompass) {
            
        }

        if (e.getStateChange() == ItemEvent.DESELECTED)
        
        */
        
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
        	mobile.repaint();
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
        }
    }
    
    public void doTimer() {
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
    
    public void showSensorData() {
    	DecimalFormat mf = new DecimalFormat("#0.00");
		
    	String data = "";
    	// Accelerometer data
    	if (mSupportedAccelerometer.isSelected()) {
	    	data += ACCELEROMETER + ": ";
	    	if (mEnabledAccelerometer.isSelected()) {
				data += mf.format(mobile.read_accelx) + ", " 
						+ mf.format(mobile.read_accely) + ", "
						+ mf.format(mobile.read_accelz);
	    	} else {
				data += DISABLED;
	    	}
			data += "\n";
    	}
		
    	if (mSupportedMagneticField.isSelected()) {
    	    // Compass data
			data += MAGNETIC_FIELD + ": ";
			if (mEnabledMagneticField.isSelected()) {
				data += mf.format(mobile.read_compassx) + ", " 
						+ mf.format(mobile.read_compassy) + ", "
						+ mf.format(mobile.read_compassz);
			} else {
				data += DISABLED;
			}
			data += "\n";
    	}
    	
		if (mSupportedOrientation.isSelected()) {
		    // Orientation data
			data += ORIENTATION + ": ";
			if (mEnabledOrientation.isSelected()) {
				data += mf.format(mobile.read_yaw) + ", " 
						+ mf.format(mobile.read_pitch) + ", "
						+ mf.format(mobile.read_roll);
			} else {
				data += DISABLED;
			}
			data += "\n";
		}
		
		if (mSupportedTemperature.isSelected()) {
			data += TEMPERATURE + ": ";
			if (mEnabledTemperature.isSelected()) {
				data += mf.format(mobile.read_temperature);
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
    public void setPort() {
    	addMessage("Closing port " + mSensorServer.port);
    	// First close all old ports:
    	mSensorServer.stop();
    	
    	// now restart
    	mSensorServer = new SensorServer(this);
    }
    
    /**
     * Adds new message to message box.
     * If scroll position is at end, it will scroll to new message.
     * @param msg Message.
     */
    public void addMessage(String msg) {
    	
    	// from: http://forum.java.sun.com/thread.jspa?threadID=544890&tstart=0
    	// The following code fragments demonstrate how to auto scroll a JTextArea ("area") that's wrapped in a JScrollPane ("scrollPane"). Auto scrolling is enabled whenever the vertical scrollbar is located at the very bottom of the area. This allows you to scroll back up at leisure (disabling auto scroll) and then drag the vertical scrollbar back to the bottom to re-enable auto scroll:

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
		double value;
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
		double[] valuelist;
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
        JFrame frame = new JFrame("SensorSimulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create the menu bar.  Make it have a green background.
        JMenuBar myMenuBar = new JMenuBar();
        myMenuBar.setPreferredSize(new Dimension(200, 20));

        //Create a yellow label to put in the content pane.
        JLabel yellowLabel = new JLabel();
        //yellowLabel.setOpaque(true);
        //yellowLabel.setBackground(new Color(248, 213, 131));
        yellowLabel.setPreferredSize(new Dimension(400, 180));

        //Start creating and adding components.
        JCheckBox changeButton =
                new JCheckBox("Glass pane \"visible\"");
        changeButton.setSelected(false);
        
        SensorSimulator simulator = new SensorSimulator();
        
        

        //Set the menu bar and add the label to the content pane.
        frame.setJMenuBar(myMenuBar);
        /*
        //Set up the content pane, where the "main GUI" lives.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(changeButton);
        contentPane.add(new JButton("Button 1"));
        contentPane.add(new JButton("Button 2"));
        */
        
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
}


