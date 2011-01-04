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

package org.openintents.tools.sensorsimulator.swt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
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
 *  @author Lee Sanghoon
 */
public class SensorSimulatorSwt extends Composite implements ISensorSimulator, Listener {

	private static final String ACTION_COMMAND = "CmD";

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
	private Scale yawSlider;
	private Scale pitchSlider;
	private Scale rollSlider;

    // Text fields:
	private int socketPort;
	private Text socketText;

    //Text fields and button for Telnet socket port
	private Text telnetSocketText;

    // Field for socket related output:
	private Text ipselectionText;

    // Field for sensor simulator data output:
	private Text textAreaSensorData;

    // Settings
    // Supported sensors
	private boolean isSupportedOrientation;
	private boolean isSupportedAccelerometer;
	private boolean isSupportedTemperature;
	private boolean isSupportedMagneticField;
	private boolean isSupportedLight;
	private boolean isSupportedProximity;
	private boolean isSupportedTricorder;
	private boolean isSupportedBarcodeReader;

    // Enabled sensors
	private boolean isEnabledOrientation;
	private boolean isEnabledAccelerometer;
	private boolean isEnabledTemperature;
	private boolean isEnabledMagneticField;
	private boolean isEnabledLight;
	private boolean isEnabledProximity;
	private boolean isEnabledTricorder;
	private boolean isEnabledBarcodeReader;

	private Button mEnabledOrientation;
	private Button mEnabledAccelerometer;
	private Button mEnabledTemperature;
	private Button mEnabledMagneticField;
	private Button mEnabledLight;
	private Button mEnabledProximity;
	private Button mEnabledTricorder;
	private Button mEnabledBarcodeReader;

    // Simulation update
	private double[] mUpdateRatesAccelerometer;
	private double mDefaultUpdateRateAccelerometer;
	private double mCurrentUpdateRateAccelerometer;
	private boolean mUpdateAverageAccelerometer;
//	private Text mUpdateRatesAccelerometerText;
//	private Text mDefaultUpdateRateAccelerometerText;
	private Text mCurrentUpdateRateAccelerometerText;
    /** Whether to form an average at each update */
//	private Button mUpdateAverageAccelerometer;

	private double[] mUpdateRatesCompass;
	private double mDefaultUpdateRateCompass;
	private double mCurrentUpdateRateCompass;
	private boolean mUpdateAverageCompass;
//	private Text mUpdateRatesCompassText;
//	private Text mDefaultUpdateRateCompassText;
	private Text mCurrentUpdateRateCompassText;
    /** Whether to form an average at each update */
//	private Button mUpdateAverageCompass;

	private double[] mUpdateRatesOrientation;
	private double mDefaultUpdateRateOrientation;
	private double mCurrentUpdateRateOrientation;
	private boolean mUpdateAverageOrientation;
//	private Text mUpdateRatesOrientationText;
//	private Text mDefaultUpdateRateOrientationText;
	private Text mCurrentUpdateRateOrientationText;
    /** Whether to form an average at each update */
//	private Button mUpdateAverageOrientation;

	private double[] mUpdateRatesThermometer;
	private double mDefaultUpdateRateThermometer;
	private double mCurrentUpdateRateThermometer;
	private boolean mUpdateAverageThermometer;
//	private Text mUpdateRatesThermometerText;
//	private Text mDefaultUpdateRateThermometerText;
	private Text mCurrentUpdateRateThermometerText;
    /** Whether to form an average at each update */
//	private Button mUpdateAverageThermometer;

	private Text mUpdateText;
	private Text mRefreshCountText;
	private Label mRefreshSensorsLabel;
	private Label mRefreshEmulatorAccelerometerLabel;
	private Label mRefreshEmulatorCompassLabel;
	private Label mRefreshEmulatorOrientationLabel;
	private Label mRefreshEmulatorThermometerLabel;

    // Accelerometer
	private Text mGravityConstantText;
	private Text mAccelerometerLimitText;
	private Text mPixelPerMeterText;
	private Text mSpringConstantText;
	private Text mDampingConstantText;
	private Button mShowAcceleration;

    // Gravity
	private Text mGravityXText;
	private Text mGravityYText;
	private Text mGravityZText;

    // Magnetic field
	private Text mMagneticFieldNorthText;
	private Text mMagneticFieldEastText;
	private Text mMagneticFieldVerticalText;

    // Temperature
	private Text mTemperatureText;

    //Barcode
	private Text mBarcodeReaderText;

    // Random contribution
	private Text mRandomOrientationText;
	private Text mRandomAccelerometerText;
	private Text mRandomTemperatureText;
	private Text mRandomMagneticFieldText;
//	private Text mRandomLightText;
//	private Text mRandomProximityText;
//	private Text mRandomTricorderText;

    // Real device bridge
	private Button mRealDeviceThinkpad;
	private Button mRealDeviceWiimote;
	private Text mRealDevicePath;
	private Label mRealDeviceOutputLabel;

    //TelnetSimulations variables
	private Scale batterySlider;

    //Battery variables
	private Button batteryPresence;
	private Button batteryAC;
	private Combo batteryStatusList;
	private Combo batteryHealthList;

    //Batter file variables
	private Button batteryEmulation;
	private Button batteryNext;
//	private JFileChooser fileChooser;
	private Button openButton;

    //GPS variables
	private Text gpsLongitudeText;
	private Text gpsLatitudeText;
	private Text gpsAltitudeText;
	private Text lisName;
	private Button gpsButton;

    // Server for sending out sensor data
	private SensorServer mSensorServer;
	private int mIncomingConnections;

    //telnet server variable
	private TelnetServer mTelnetServer;

	private WiiMoteData wiiMoteData = new WiiMoteData();


	/**
	 * @param parent
	 * @param style
	 */
	public SensorSimulatorSwt(Composite parent) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(3, true));

		createLeftComposite(this);
		createCenterComposite(this);
		createRightComposite(this);

		initialize();
	}

	/**
	 *
	 */
	private void initialize() {
        /////////////////////////////////////////////////////
        // Fill the possible values

		socketText.setText("8010");
		telnetSocketText.setText("5554");

		ipselectionText.append("Write emulator command port and\n"
				+ "click on set to create connection.\n");

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
                doTimer();
            }
        });
        //timer.setInitialDelay(delay * 7); //We pause animation twice per cycle
                                          //by restarting the timer
        timer.setCoalesce(true);

        timer.start();
	}

	/**
	 * @param parent
	 */
	private Composite createLeftComposite(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		content.setLayout(new GridLayout(4, false));

		GridData gd;

		MobileComposite mobileComposite = new MobileComposite(content, this);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 250;
		gd.heightHint = 200;
		gd.horizontalSpan = 4;
		mobileComposite.setLayoutData(gd);
		mobile = mobileComposite;

		Button yawPitchButton = createButton(content, SWT.RADIO, "yaw && pitch");
		yawPitchButton.setSelection(true);
		yawPitchButton.setData(ACTION_COMMAND, yawPitch);
		yawPitchButton.addListener(SWT.Selection, this);
		mouseMode = mouseYawPitch;

		Button rollPitchButton = createButton(content, SWT.RADIO, "roll && pitch");
		rollPitchButton.setData(ACTION_COMMAND, rollPitch);
		rollPitchButton.addListener(SWT.Selection, this);

		Button moveButton = createButton(content, SWT.RADIO, move);
		moveButton.setData(ACTION_COMMAND, move);
		moveButton.addListener(SWT.Selection, this);

		createLabel(content, SWT.NONE, "");

		createLabel(content, SWT.NONE, "Socket port: ");
		socketText = createText(content, SWT.BORDER, "");
		socketText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
		    	String s = socketText.getText();
		    	try {
		    		socketPort = Integer.parseInt(s);
		    	} catch (NumberFormatException ex){
		    		socketPort = 0;
		    		addMessage("Invalid port number: " + s);
		    	}
			}
		});
		Button socketButton = createButton(content, SWT.PUSH, "Set");
		socketButton.setData(ACTION_COMMAND, setPortString);
		socketButton.addListener(SWT.Selection, this);
		createLabel(content, SWT.NONE, "");

		createLabel(content, SWT.NONE, "Telnet port: ");
		telnetSocketText = createText(content, SWT.BORDER, "");
		Button telnetSocketButton = createButton(content, SWT.PUSH, "Set");
		telnetSocketButton.setData(ACTION_COMMAND, connectViaTelnet);
		telnetSocketButton.addListener(SWT.Selection, this);
		createLabel(content, SWT.NONE, "");

		ipselectionText = createText(content, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL, "");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 4;
		ipselectionText.setLayoutData(gd);

		textAreaSensorData = createText(content, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL, "");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		gd.horizontalSpan = 4;
		textAreaSensorData.setLayoutData(gd);

		return content;
	}

	/**
	 * @param parent
	 */
	private Composite createCenterComposite(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		content.setLayout(new GridLayout(2, false));

		GridData gd;

		Label simulatorLabel = createLabel(content, SWT.BOLD, "OpenIntents Sensor Simulator");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.CENTER;
		gd.horizontalSpan = 2;
		simulatorLabel.setLayoutData(gd);

		createLabel(content, SWT.NONE, "Yaw");
		yawSlider = createScale(content, SWT.HORIZONTAL, 360, 160);	// actually, -180 ~ 180 and initial value is -20.
		yawSlider.addListener(SWT.Selection, this);
		yawSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createLabel(content, SWT.NONE, "Pitch");
		pitchSlider = createScale(content, SWT.HORIZONTAL, 360, 120);	// actually, -180 ~ 180 and initial value is -60.
		pitchSlider.addListener(SWT.Selection, this);
		pitchSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createLabel(content, SWT.NONE, "Roll");
		rollSlider = createScale(content, SWT.HORIZONTAL, 360, 180);	// actually, -180 ~ 180 and initial value is 0.
		rollSlider.addListener(SWT.Selection, this);
		rollSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createLabel(content, SWT.NONE, "");
		createTickLabels(content, -180, 180);

		ScrolledComposite sc = new ScrolledComposite(content, SWT.BORDER | SWT.V_SCROLL);
		sc.setAlwaysShowScrollBars(true);
		sc.setExpandHorizontal(true);
//		sc.setExpandVertical(true);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		sc.setLayoutData(gd);

		Composite settingsComposite = new Composite(sc, SWT.NONE);
		settingsComposite.setLayout(new GridLayout());
		sc.setContent(settingsComposite);

		Label settingsLabel = createLabel(settingsComposite, SWT.NONE, "Settings");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.CENTER;
		settingsLabel.setLayoutData(gd);

		Label seperator = createLabel(settingsComposite, SWT.SEPARATOR | SWT.HORIZONTAL, "");
		seperator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createSupportedSensorsGroup(settingsComposite);
		createEnabledSensorsGroup(settingsComposite);
		createSensorUpdateRateGroup(settingsComposite);
		createSimulationUpdateGroup(settingsComposite);
		createAccelerometerGroup(settingsComposite);
		createGravityGroup(settingsComposite);
		createMagneticFieldGroup(settingsComposite);
		createTemperatureGroup(settingsComposite);
		createBarcodeGroup(settingsComposite);
		createRandomComponentGroup(settingsComposite);
		createRealSensorBridgetGroup(settingsComposite);

		settingsComposite.setSize(settingsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//		settingsComposite.layout();

		return content;
	}

	/**
	 * @param parent
	 */
	private void createSupportedSensorsGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Supported sensors");
		group.setLayout(new GridLayout());

		Button mSupportedAccelerometer = createButton(group, SWT.CHECK, ACCELEROMETER);
		mSupportedAccelerometer.setSelection(true);
		mSupportedAccelerometer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isSupportedAccelerometer = ((Button)e.widget).getSelection();
			}
		});

		Button mSupportedMagneticField = createButton(group, SWT.CHECK, MAGNETIC_FIELD);
		mSupportedMagneticField.setSelection(true);
		mSupportedMagneticField.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isSupportedMagneticField = ((Button)e.widget).getSelection();
			}
		});

		Button mSupportedOrientation = createButton(group, SWT.CHECK, ORIENTATION);
		mSupportedOrientation.setSelection(true);
		mSupportedOrientation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isSupportedOrientation = ((Button)e.widget).getSelection();
			}
		});

		Button mSupportedTemperature = createButton(group, SWT.CHECK, TEMPERATURE);
		mSupportedTemperature.setSelection(false);
		mSupportedTemperature.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isSupportedTemperature = ((Button)e.widget).getSelection();
			}
		});

		Button mSupportedBarcodeReader = createButton(group, SWT.CHECK, BARCODE_READER);
		mSupportedBarcodeReader.setSelection(false);
		mSupportedBarcodeReader.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isSupportedBarcodeReader = ((Button)e.widget).getSelection();
			}
		});
	}

	/**
	 * @param parent
	 */
	private void createEnabledSensorsGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Enabled sensors");
		group.setLayout(new GridLayout());

		mEnabledAccelerometer = createButton(group, SWT.CHECK, ACCELEROMETER);
		mEnabledAccelerometer.setSelection(true);
		mEnabledAccelerometer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isEnabledAccelerometer = ((Button)e.widget).getSelection();
			}
		});

		mEnabledMagneticField = createButton(group, SWT.CHECK, MAGNETIC_FIELD);
		mEnabledMagneticField.setSelection(true);
		mEnabledMagneticField.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isEnabledMagneticField = ((Button)e.widget).getSelection();
			}
		});

		mEnabledOrientation = createButton(group, SWT.CHECK, ORIENTATION);
		mEnabledOrientation.setSelection(true);
		mEnabledOrientation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isEnabledOrientation = ((Button)e.widget).getSelection();
			}
		});

		mEnabledTemperature = createButton(group, SWT.CHECK, TEMPERATURE);
		mEnabledTemperature.setSelection(false);
		mEnabledTemperature.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isEnabledTemperature = ((Button)e.widget).getSelection();
			}
		});

		mEnabledBarcodeReader = createButton(group, SWT.CHECK, BARCODE_READER);
		mEnabledBarcodeReader.setSelection(false);
		mEnabledBarcodeReader.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isEnabledBarcodeReader = ((Button)e.widget).getSelection();
			}
		});
	}

	/**
	 * @param parent
	 */
	private void createSensorUpdateRateGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Sensor update rate");
		group.setLayout(new GridLayout(3, false));

		Label label;
		GridData gd;

		/*
		 * Accelerometer
		 */

		label = createLabel(group, SWT.NONE, "Accelerometer");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		createLabel(group, SWT.NONE, "Update rates: ");
		Text mUpdateRatesAccelerometerText = createText(group, SWT.BORDER, "1, 10, 50");
		mUpdateRatesAccelerometerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mUpdateRatesAccelerometer = getSafeDoubleList((Text) e.widget);
			}
		});
		createLabel(group, SWT.NONE, "1/s");


		createLabel(group, SWT.NONE, "Default rates: ");
		Text mDefaultUpdateRateAccelerometerText = createText(group, SWT.BORDER, "50");
		mDefaultUpdateRateAccelerometerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mDefaultUpdateRateAccelerometer = getSafeDouble((Text) e.widget);
			}
		});
		createLabel(group, SWT.NONE, "1/s");


		createLabel(group, SWT.NONE, "Current rates: ");
		mCurrentUpdateRateAccelerometerText = createText(group, SWT.BORDER, "50");
		mCurrentUpdateRateAccelerometerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mCurrentUpdateRateAccelerometer = getSafeDouble((Text) e.widget, 0);
			}
		});
		createLabel(group, SWT.NONE, "1/s");

		Button mUpdateAverageAccelerometerButton = createButton(group, SWT.CHECK, AVERAGE_ACCELEROMETER);
		mUpdateAverageAccelerometerButton.setSelection(true);
		mUpdateAverageAccelerometerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mUpdateAverageAccelerometer = ((Button)e.widget).getSelection();
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		mUpdateAverageAccelerometerButton.setLayoutData(gd);


		/*
		 * Compass
		 */

		label = createLabel(group, SWT.NONE, "Compass");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		createLabel(group, SWT.NONE, "Update rates: ");
		Text mUpdateRatesCompassText = createText(group, SWT.BORDER, "1, 10");
		mUpdateRatesCompassText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mUpdateRatesCompass = getSafeDoubleList((Text) e.widget);
			}
		});
		createLabel(group, SWT.NONE, "1/s");


		createLabel(group, SWT.NONE, "Default rates: ");
		Text mDefaultUpdateRateCompassText = createText(group, SWT.BORDER, "10");
		mDefaultUpdateRateCompassText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mDefaultUpdateRateCompass = getSafeDouble((Text) e.widget);
			}
		});
		createLabel(group, SWT.NONE, "1/s");


		createLabel(group, SWT.NONE, "Current rates: ");
		mCurrentUpdateRateCompassText = createText(group, SWT.BORDER, "10");
		mCurrentUpdateRateCompassText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mCurrentUpdateRateCompass = getSafeDouble((Text) e.widget, 0);
			}
		});
		createLabel(group, SWT.NONE, "1/s");

		Button mUpdateAverageCompassButton = createButton(group, SWT.CHECK, AVERAGE_MAGNETIC_FIELD);
		mUpdateAverageCompassButton.setSelection(true);
		mUpdateAverageCompassButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mUpdateAverageAccelerometer = ((Button)e.widget).getSelection();
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		mUpdateAverageCompassButton.setLayoutData(gd);


		/*
		 * Orientation
		 */

		label = createLabel(group, SWT.NONE, "Orientation");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		createLabel(group, SWT.NONE, "Update rates: ");
		Text mUpdateRatesOrientationText = createText(group, SWT.BORDER, "1, 10, 50");
		mUpdateRatesOrientationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mUpdateRatesOrientation = getSafeDoubleList((Text) e.widget);
			}
		});
		createLabel(group, SWT.NONE, "1/s");


		createLabel(group, SWT.NONE, "Default rates: ");
		Text mDefaultUpdateRateOrientationText = createText(group, SWT.BORDER, "50");
		mDefaultUpdateRateOrientationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mDefaultUpdateRateOrientation = getSafeDouble((Text) e.widget);
			}
		});
		createLabel(group, SWT.NONE, "1/s");


		createLabel(group, SWT.NONE, "Current rates: ");
		mCurrentUpdateRateOrientationText = createText(group, SWT.BORDER, "50");
		mCurrentUpdateRateOrientationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mCurrentUpdateRateOrientation = getSafeDouble((Text) e.widget, 0);
			}
		});
		createLabel(group, SWT.NONE, "1/s");

		Button mUpdateAverageOrientationButton = createButton(group, SWT.CHECK, AVERAGE_ORIENTATION);
		mUpdateAverageOrientationButton.setSelection(true);
		mUpdateAverageOrientationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mUpdateAverageAccelerometer = ((Button)e.widget).getSelection();
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		mUpdateAverageOrientationButton.setLayoutData(gd);


		/*
		 * Thermometer
		 */

		label = createLabel(group, SWT.NONE, "Thermometer");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		createLabel(group, SWT.NONE, "Update rates: ");
		Text mUpdateRatesThermometerText = createText(group, SWT.BORDER, "0.1, 1");
		mUpdateRatesThermometerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mUpdateRatesThermometer = getSafeDoubleList((Text) e.widget);
			}
		});
		createLabel(group, SWT.NONE, "1/s");


		createLabel(group, SWT.NONE, "Default rates: ");
		Text mDefaultUpdateRateThermometerText = createText(group, SWT.BORDER, "1");
		mDefaultUpdateRateThermometerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mDefaultUpdateRateThermometer = getSafeDouble((Text) e.widget);
			}
		});
		createLabel(group, SWT.NONE, "1/s");


		createLabel(group, SWT.NONE, "Current rates: ");
		mCurrentUpdateRateThermometerText = createText(group, SWT.BORDER, "1");
		mCurrentUpdateRateThermometerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mCurrentUpdateRateThermometer = getSafeDouble((Text) e.widget, 0);
			}
		});
		createLabel(group, SWT.NONE, "1/s");

		Button mUpdateAverageThermometerButton = createButton(group, SWT.CHECK, AVERAGE_TEMPERATURE);
		mUpdateAverageThermometerButton.setSelection(true);
		mUpdateAverageThermometerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mUpdateAverageAccelerometer = ((Button)e.widget).getSelection();
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		mUpdateAverageThermometerButton.setLayoutData(gd);

	}

	/**
	 * @param parent
	 */
	private void createSimulationUpdateGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Simulation update");
		group.setLayout(new GridLayout(3, false));

		GridData gd;

		createLabel(group, SWT.NONE, "Update sensors: ");
		mUpdateText = createText(group, SWT.BORDER, "10");
		createLabel(group, SWT.NONE, "ms");


		createLabel(group, SWT.NONE, "Refresh after: ");
		mRefreshCountText = createText(group, SWT.BORDER, "10");
		createLabel(group, SWT.NONE, "times");

		createLabel(group, SWT.NONE, "Sensor update: ");
		mRefreshSensorsLabel = createLabel(group, SWT.NONE, "0");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		mRefreshSensorsLabel.setLayoutData(gd);

		Label label = createLabel(group, SWT.NONE, "Emulator update: ");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		createLabel(group, SWT.NONE, " * Accelerometer: ");
		mRefreshEmulatorAccelerometerLabel = createLabel(group, SWT.NONE, "-");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		mRefreshEmulatorAccelerometerLabel.setLayoutData(gd);


		createLabel(group, SWT.NONE, " * Compass: ");
		mRefreshEmulatorCompassLabel = createLabel(group, SWT.NONE, "-");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		mRefreshEmulatorCompassLabel.setLayoutData(gd);


		createLabel(group, SWT.NONE, " * Orientation: ");
		mRefreshEmulatorOrientationLabel = createLabel(group, SWT.NONE, "-");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		mRefreshEmulatorOrientationLabel.setLayoutData(gd);


		createLabel(group, SWT.NONE, " * Thermometer: ");
		mRefreshEmulatorThermometerLabel = createLabel(group, SWT.NONE, "-");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		mRefreshEmulatorThermometerLabel.setLayoutData(gd);

	}

	/**
	 * @param parent
	 */
	private void createAccelerometerGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Accelerometer");
		group.setLayout(new GridLayout(3, false));

		GridData gd;

		createLabel(group, SWT.NONE, "Gravity constant g: ");
		mGravityConstantText = createText(group, SWT.BORDER, "9.80665");
		createLabel(group, SWT.NONE, "m/s" + SQUARED);

		createLabel(group, SWT.NONE, "Accelerometer limit: ");
		mAccelerometerLimitText = createText(group, SWT.BORDER, "10");
		createLabel(group, SWT.NONE, "g");

		createLabel(group, SWT.NONE, "Pixels per meter: ");
		mPixelPerMeterText = createText(group, SWT.BORDER, "3000");
		createLabel(group, SWT.NONE, "p/m");

		createLabel(group, SWT.NONE, "Spring constant (k/m): ");
		mSpringConstantText = createText(group, SWT.BORDER, "500");
		createLabel(group, SWT.NONE, "p/s" + SQUARED);

		createLabel(group, SWT.NONE, "Damping constant: ");
		mDampingConstantText = createText(group, SWT.BORDER, "50");
		createLabel(group, SWT.NONE, "p/s");

		mShowAcceleration = createButton(group, SWT.CHECK, SHOW_ACCELERATION);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		mShowAcceleration.setLayoutData(gd);
	}

	/**
	 * @param parent
	 */
	private void createGravityGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Gravity");
		group.setLayout(new GridLayout(3, false));

		createLabel(group, SWT.NONE, "x: ");
		mGravityXText = createText(group, SWT.BORDER, "0");
		createLabel(group, SWT.NONE, "m/s" + SQUARED);

		createLabel(group, SWT.NONE, "y: ");
		mGravityYText = createText(group, SWT.BORDER, "0");
		createLabel(group, SWT.NONE, "m/s" + SQUARED);

		createLabel(group, SWT.NONE, "z: ");
		mGravityZText = createText(group, SWT.BORDER, "0");
		createLabel(group, SWT.NONE, "m/s" + SQUARED);
	}

	/**
	 * @param parent
	 */
	private void createMagneticFieldGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Magnetic field");
		group.setLayout(new GridLayout(3, false));

		createLabel(group, SWT.NONE, "North component: ");
		mMagneticFieldNorthText = createText(group, SWT.BORDER, "22874.1");
		createLabel(group, SWT.NONE, "nT");

		createLabel(group, SWT.NONE, "East component: ");
		mMagneticFieldEastText = createText(group, SWT.BORDER, "5939.5");
		createLabel(group, SWT.NONE, "nT");

		createLabel(group, SWT.NONE, "Vertical component: ");
		mMagneticFieldVerticalText = createText(group, SWT.BORDER, "43180.5");
		createLabel(group, SWT.NONE, "nT");
	}

	/**
	 * @param parent
	 */
	private void createTemperatureGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Temperature");
		group.setLayout(new GridLayout(3, false));

		createLabel(group, SWT.NONE, "Temperature: ");
		mTemperatureText = createText(group, SWT.BORDER, "17.7");
		createLabel(group, SWT.NONE, DEGREES + "C");
	}

	/**
	 * @param parent
	 */
	private void createBarcodeGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Barcode");
		group.setLayout(new GridLayout());

		mBarcodeReaderText = createText(group, SWT.BORDER, "1234567890123");
		mBarcodeReaderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * @param parent
	 */
	private void createRandomComponentGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Random component");
		group.setLayout(new GridLayout(3, false));

		createLabel(group, SWT.NONE, "Accelerometer: ");
		mRandomAccelerometerText = createText(group, SWT.BORDER, "0");
		createLabel(group, SWT.NONE, "m/s" + SQUARED);

		createLabel(group, SWT.NONE, "Compass: ");
		mRandomMagneticFieldText = createText(group, SWT.BORDER, "0");
		createLabel(group, SWT.NONE, "nT");

		createLabel(group, SWT.NONE, "Orientation: ");
		mRandomOrientationText = createText(group, SWT.BORDER, "0");
		createLabel(group, SWT.NONE, DEGREES);

		createLabel(group, SWT.NONE, "Temperature: ");
		mRandomTemperatureText = createText(group, SWT.BORDER, "0");
		createLabel(group, SWT.NONE, DEGREES + "C");
	}

	/**
	 * @param parent
	 */
	private void createRealSensorBridgetGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Real sensor bridge");
		group.setLayout(new GridLayout());

		mRealDeviceThinkpad = createButton(group, SWT.CHECK, "Use Thinkpad accelerometer");
		mRealDeviceThinkpad.setSelection(false);
		mRealDeviceThinkpad.addListener(SWT.Selection, this);

		mRealDeviceWiimote = createButton(group, SWT.CHECK, "Use Wii-mote accelerometer");
		mRealDeviceWiimote.setSelection(false);
		mRealDeviceWiimote.addListener(SWT.Selection, this);

		mRealDevicePath = createText(group, SWT.BORDER, "/sys/devices/platform/hdaps/position");
		mRealDevicePath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		mRealDeviceOutputLabel = createLabel(group, SWT.NONE, "-");
		mRealDeviceOutputLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * @param parent
	 */
	private Composite createRightComposite(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		content.setLayout(new GridLayout(2, false));

		GridData gd;

		Label title = createLabel(content, SWT.NONE, "Telnet simulations");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.CENTER;
		gd.horizontalSpan = 2;
		title.setLayoutData(gd);

		createLabel(content, SWT.NONE, "Battery");
		batterySlider = createScale(content, SWT.HORIZONTAL, 100, 50);	// actually, 0 ~ 100 and initial value is 50.
		batterySlider.addListener(SWT.Selection, this);

		createLabel(content, SWT.NONE, "");
		createTickLabels(content, 0, 100);

		ScrolledComposite sc = new ScrolledComposite(content, SWT.BORDER | SWT.V_SCROLL);
		sc.setAlwaysShowScrollBars(true);
		sc.setExpandHorizontal(true);
//		sc.setExpandVertical(true);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		sc.setLayoutData(gd);

		Composite settingsComposite = new Composite(sc, SWT.NONE);
		settingsComposite.setLayout(new GridLayout());
		sc.setContent(settingsComposite);

		Label settingsLabel = createLabel(settingsComposite, SWT.NONE, "Telnet Settings");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.CENTER;
		settingsLabel.setLayoutData(gd);

		Label seperator = createLabel(settingsComposite, SWT.SEPARATOR | SWT.HORIZONTAL, "");
		seperator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createBatteryGroup(settingsComposite);
		createBatteryStatusGroup(settingsComposite);
		createBatteryHealthGroup(settingsComposite);
		createBatteryFileGroup(settingsComposite);
		createGpsGroup(settingsComposite);

		settingsComposite.setSize(settingsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return content;
	}

	/**
	 * @param parent
	 */
	private void createBatteryGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Battery");
		group.setLayout(new GridLayout());

		batteryPresence = createButton(group, SWT.CHECK, "Is Present");
		batteryPresence.setSelection(true);
		batteryPresence.addListener(SWT.Selection, this);

		batteryAC = createButton(group, SWT.CHECK, "AC plugged");
		batteryAC.setSelection(true);
		batteryAC.addListener(SWT.Selection, this);
	}

	/**
	 * @param parent
	 */
	private void createBatteryStatusGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Battery Status");
		group.setLayout(new GridLayout());

		String[] batteryStatus = { "unknown", "charging", "discharging", "not-charging", "full" };
		batteryStatusList = createCombo(group, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER, batteryStatus, 4);
		batteryStatusList.addListener(SWT.Selection, this);
	}

	/**
	 * @param parent
	 */
	private void createBatteryHealthGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Battery Health");
		group.setLayout(new GridLayout());

		String[] batteryHealth = { "unknown", "good", "overheat", "dead", "overvoltage", "failure" };
		batteryHealthList = createCombo(group, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER, batteryHealth, 2);
		batteryHealthList.addListener(SWT.Selection, this);
	}

	/**
	 * @param parent
	 */
	private void createBatteryFileGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "Battery File");
		group.setLayout(new GridLayout(2, true));

		GridData gd;

		openButton = createButton(group, SWT.PUSH, "Open a File");
		openButton.addListener(SWT.Selection, this);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		openButton.setLayoutData(gd);

		batteryEmulation = createButton(group, SWT.PUSH, "Emulate Battery");
		batteryEmulation.setData(ACTION_COMMAND, emulateBattery);
		batteryEmulation.addListener(SWT.Selection, this);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		batteryEmulation.setLayoutData(gd);

		batteryNext = createButton(group, SWT.PUSH, "Next time event");
		batteryNext.setData(ACTION_COMMAND, nextTimeEvent);
		batteryNext.addListener(SWT.Selection, this);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		batteryNext.setLayoutData(gd);
	}

	/**
	 * @param parent
	 */
	private void createGpsGroup(Composite parent) {
		Group group = createGroup(parent, SWT.NONE, "GPS");
		group.setLayout(new GridLayout(3, false));

		createLabel(group, SWT.NONE, "GPS Longitude: ");
		gpsLongitudeText = createText(group, SWT.BORDER, "");
		createLabel(group, SWT.NONE, "degress");

		createLabel(group, SWT.NONE, "GPS Latitude: ");
		gpsLatitudeText = createText(group, SWT.BORDER, "");
		createLabel(group, SWT.NONE, "degress");

		createLabel(group, SWT.NONE, "GPS Altitude: ");
		gpsAltitudeText = createText(group, SWT.BORDER, "");
		createLabel(group, SWT.NONE, "meters");

		createLabel(group, SWT.NONE, "LIS name: ");
		lisName = createText(group, SWT.BORDER, "");
		createLabel(group, SWT.NONE, "");

		gpsButton = createButton(group, SWT.PUSH, "Send GPS");
		gpsButton.setData(ACTION_COMMAND, sendGPS);
		gpsButton.addListener(SWT.Selection, this);
		((GridData)gpsButton.getLayoutData()).horizontalSpan = 3;
	}

	private Button createButton(Composite parent, int style, String text) {
		Button button = new Button(parent, style);
		button.setText(text);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		button.setLayoutData(gd);
		return button;
	}

	private Label createLabel(Composite parent, int style, String text) {
		Label label = new Label(parent, style);
		label.setText(text);
		label.setLayoutData(new GridData());
		return label;
	}

	private Text createText(Composite parent, int style, String text) {
		Text txt = new Text(parent, style);
		txt.setText(text);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.widthHint = 80;
		txt.setLayoutData(gd);
		return txt;
	}

	private Combo createCombo(Composite parent, int style, String[] items, int selectedIndex) {
		Combo combo = new Combo(parent, style);
		combo.setItems(items);
		combo.select(selectedIndex);
		combo.setLayoutData(new GridData());
		return combo;
	}

	private Group createGroup(Composite parent, int style, String text) {
		Group group = new Group(parent, style);
		group.setText(text);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return group;
	}

	private Scale createScale(Composite parent, int style, int max, int value) {
		Scale scale = new Scale(parent, style);
		scale.setMinimum(0);
		scale.setMaximum(max);
		scale.setSelection(value);
		scale.setLayoutData(new GridData(style == SWT.HORIZONTAL ? GridData.FILL_HORIZONTAL : GridData.FILL_VERTICAL));
		return scale;
	}

	private void createTickLabels(Composite parent, int min, int max) {
		Composite body = new Composite(parent, SWT.NONE);
		body.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		body.setLayout(new GridLayout(3, true));

		int diff = (max - min) / 2;

		Label label;

		label = createLabel(body, SWT.NONE, Integer.toString(min));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));

		label = createLabel(body, SWT.NONE, Integer.toString(min + diff));
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

		label = createLabel(body, SWT.NONE, Integer.toString(max));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
	}


	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event event) {
		Widget source = event.widget;
		String action = (String) source.getData(ACTION_COMMAND);

		if (yawPitch.equals(action)) {
			mouseMode = mouseYawPitch;
		} else if (rollPitch.equals(action)) {
			mouseMode = mouseRollPitch;
		} else if (move.equals(action)) {
			mouseMode = mouseMove;
		} else if (timerAction.equals(action)) {
			doTimer();
		} else if (setPortString.equals(action)) {
			setPort();
		} else if (connectViaTelnet.equals(action)) {
			connectViaTelnet();
		} else if (batteryHealthList.equals(source)
				&& "comboBoxChanged".equals(action)) {
			if (mTelnetServer != null) {
				mTelnetServer.changeHealth(batteryHealthList.getText());
			}
		} else if (batteryStatusList.equals(source)
				&& "comboBoxChanged".equals(action)) {
			if (mTelnetServer != null) {
				mTelnetServer.changeStatus(batteryStatusList.getText());
			}
		} else if (sendGPS.equals(action)) {
			sendGps();
		} else if (emulateBattery.equals(action)) {
			if (mTelnetServer != null) {
				mTelnetServer.slowEmulation();
			}
		} else if (nextTimeEvent.equals(action)) {
			if (mTelnetServer != null) {
				mTelnetServer.nextTimeEvent();
			}
		} else if (openButton.equals(source)) {
			FileDialog dialog = new FileDialog(getShell());
			String filepath = dialog.open();
			if (filepath != null) {
				if (mTelnetServer != null) {
					mTelnetServer.openFile(new File(filepath));
				} else {
					this.addMessage("Connect with emulator to start simulation");
				}
			}
		} else if (yawSlider.equals(source)) {
			mobile.setYawDegree(yawSlider.getSelection() - 180);
        	mobile.doRepaint();
		} else if (pitchSlider.equals(source)) {
			mobile.setPitchDegree(pitchSlider.getSelection() - 180);
        	mobile.doRepaint();
		} else if (rollSlider.equals(source)) {
			mobile.setRollDegree(rollSlider.getSelection() - 180);
        	mobile.doRepaint();
		} else if (batterySlider.equals(source)) {
        	if(mTelnetServer!=null){
        		mTelnetServer.changePower(batterySlider.getSelection());
        	}
		} else if (mRealDeviceThinkpad.equals(source) && mRealDeviceThinkpad.getSelection()) {
			mRealDeviceWiimote.setSelection(false);
		} else if (mRealDeviceWiimote.equals(source) && mRealDeviceWiimote.getSelection()) {
			mRealDeviceThinkpad.setSelection(false);
		} else if (mShowAcceleration.equals(source)) {
			mobile.doRepaint();
		} else if (batteryPresence.equals(source)) {
			if (mTelnetServer != null) {
				mTelnetServer.changePresence(batteryPresence.getSelection());
			}
		} else if (batteryAC.equals(source)) {
			if (mTelnetServer != null) {
				mTelnetServer.changePresence(batteryAC.getSelection());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		timer.stop();
		super.dispose();
	}

	private void doTimer() {
		Display display;

		try {
			display = getDisplay();
		} catch (SWTException e1) {
			// Maybe, UI disposed already.
			timer.stop();
			return;
		}

		display.asyncExec(new Runnable() {
			public void run() {
				try {
					if (mRealDeviceWiimote.getSelection()) {
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
				} catch (SWTException e) {
					// Maybe, UI disposed already.
					timer.stop();
				}
			}
		});
	}

    private void updateFromWiimote() {
    	// Read raw data
		wiiMoteData.setDataFilePath(mRealDevicePath.getText());

		boolean success = wiiMoteData.updateData();
		mRealDeviceOutputLabel.setText(wiiMoteData.getStatus());

		if (success) {
			// Update controls
			yawSlider.setSelection(180);  // Wiimote can't support yaw
			rollSlider.setSelection(180 + wiiMoteData.getRoll());
			pitchSlider.setSelection(180 + wiiMoteData.getPitch());
		}
    }

    /**
     * This method is used to show currently enabled sensor values in info pane.
     */
    private void showSensorData() {
    	DecimalFormat mf = new DecimalFormat("#0.00");

    	String data = "";
    	if (isSupportedAccelerometer) {
	    	data += ACCELEROMETER + ": ";
	    	if (isEnabledAccelerometer) {
				data += mf.format(mobile.getReadAccelerometerX()) + ", "
						+ mf.format(mobile.getReadAccelerometerY()) + ", "
						+ mf.format(mobile.getReadAccelerometerZ());
	    	} else {
				data += DISABLED;
	    	}
			data += "\n";
    	}

    	if (isSupportedMagneticField) {
    	    // Compass data
			data += MAGNETIC_FIELD + ": ";
			if (isEnabledMagneticField) {
				data += mf.format(mobile.getReadCompassX()) + ", "
						+ mf.format(mobile.getReadCompassY()) + ", "
						+ mf.format(mobile.getReadCompassZ());
			} else {
				data += DISABLED;
			}
			data += "\n";
    	}

		if (isSupportedOrientation) {
		    // Orientation data
			data += ORIENTATION + ": ";
			if (isEnabledOrientation) {
				data += mf.format(mobile.getReadYaw()) + ", "
						+ mf.format(mobile.getReadPitch()) + ", "
						+ mf.format(mobile.getReadRoll());
			} else {
				data += DISABLED;
			}
			data += "\n";
		}

		if (isSupportedTemperature) {
			data += TEMPERATURE + ": ";
			if (isEnabledTemperature) {
				data += mf.format(mobile.getReadTemperature());
			} else {
				data += DISABLED;
			}
			data += "\n";
		}

		if (isSupportedBarcodeReader) {
			data += BARCODE_READER + ": ";
			if (isEnabledBarcodeReader) {
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
	private void connectViaTelnet() {
		if (mTelnetServer == null) {
			mTelnetServer = new TelnetServer(this);
			mTelnetServer.connect();
		} else {
			addMessage("Closing telnet port " + mTelnetServer.port);
			mTelnetServer.disconnect();
			mTelnetServer = new TelnetServer(this);
			mTelnetServer.connect();
		}
	}

    /**
     * Once sendGps is called, simulator sends geo fix command to emulator via telnet.
     */
	private void sendGps() {
		if (mTelnetServer != null) {
			mTelnetServer.sendGPS();
		}
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#addMessage(java.lang.String)
	 */
	public void addMessage(final String msg) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (ipselectionText == null || ipselectionText.isDisposed())
					return;

				ipselectionText.append(msg + "\n");
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getMouseMode()
	 */
	public int getMouseMode() {
		return mouseMode;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getWiiMoteData()
	 */
	public WiiMoteData getWiiMoteData() {
		return wiiMoteData;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#newClient()
	 */
	public void newClient() {
    	mIncomingConnections++;
    	if (mIncomingConnections <= 1) {
    		getDisplay().asyncExec(new Runnable() {
				public void run() {
		    		// We have been connected for the first time.
		    		// Disable all sensors:
		    		mEnabledAccelerometer.setSelection(false);
		    		mEnabledMagneticField.setSelection(false);
		    		mEnabledOrientation.setSelection(false);
		    		mEnabledTemperature.setSelection(false);
		    		mEnabledBarcodeReader.setSelection(false);
				}
			});
    		addMessage("First incoming connection:");
    		addMessage("ALL SENSORS DISABLED!");
    	}
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getDelay()
	 */
	public int getDelay() {
		return delay;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setDelay(int)
	 */
	public void setDelay(int delay) {
		this.delay = delay;
		this.timer.setDelay(delay);
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getMobilePanel()
	 */
	public IMobilePanel getMobilePanel() {
		return mobile;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getPort()
	 */
	public int getPort() {
		// Because getPort() method called from non UI thread, can't access socketText.getText().
		// So, move original getPort() code to socketText's ModifyListener
		// and set value to global socketPort variable. In this method, access global socketPort variable.

    	return socketPort;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getTelnetPort()
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

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getYaw()
	 */
	public int getYaw() {
		return yawSlider.getSelection() - 180;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getPitch()
	 */
	public int getPitch() {
		return pitchSlider.getSelection() - 180;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getRoll()
	 */
	public int getRoll() {
		return rollSlider.getSelection() - 180;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setYaw(int)
	 */
	public void setYaw(int yaw) {
		yawSlider.setSelection(180 + yaw);
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setPitch(int)
	 */
	public void setPitch(int pitch) {
		pitchSlider.setSelection(180 + pitch);
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setRoll(int)
	 */
	public void setRoll(int roll) {
		rollSlider.setSelection(180 + roll);
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isSupportedOrientation()
	 */
	public boolean isSupportedOrientation() {
		return isSupportedOrientation;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isSupportedAccelerometer()
	 */
	public boolean isSupportedAccelerometer() {
		return isSupportedAccelerometer;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isSupportedTemperature()
	 */
	public boolean isSupportedTemperature() {
		return isSupportedTemperature;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isSupportedMagneticField()
	 */
	public boolean isSupportedMagneticField() {
		return isSupportedMagneticField;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isSupportedLight()
	 */
	public boolean isSupportedLight() {
		return isSupportedLight;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isSupportedProximity()
	 */
	public boolean isSupportedProximity() {
		return isSupportedProximity;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isSupportedTricorder()
	 */
	public boolean isSupportedTricorder() {
		return isSupportedTricorder;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isSupportedBarcodeReader()
	 */
	public boolean isSupportedBarcodeReader() {
		return isSupportedBarcodeReader;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isEnabledOrientation()
	 */
	public boolean isEnabledOrientation() {
		return isEnabledOrientation;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isEnabledAccelerometer()
	 */
	public boolean isEnabledAccelerometer() {
		return isEnabledAccelerometer;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isEnabledTemperature()
	 */
	public boolean isEnabledTemperature() {
		return isEnabledTemperature;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isEnabledMagneticField()
	 */
	public boolean isEnabledMagneticField() {
		return isEnabledMagneticField;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isEnabledLight()
	 */
	public boolean isEnabledLight() {
		return isEnabledLight;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isEnabledProximity()
	 */
	public boolean isEnabledProximity() {
		return isEnabledProximity;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isEnabledTricorder()
	 */
	public boolean isEnabledTricorder() {
		return isEnabledTricorder;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#isEnabledBarcodeReader()
	 */
	public boolean isEnabledBarcodeReader() {
		return isEnabledBarcodeReader;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setEnabledOrientation(boolean)
	 */
	public void setEnabledOrientation(boolean enable) {
		mEnabledOrientation.setSelection(enable);
		mRefreshEmulatorOrientationLabel.setText("-");
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setEnabledAccelerometer(boolean)
	 */
	public void setEnabledAccelerometer(final boolean enable) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (mEnabledAccelerometer != null && mRefreshEmulatorAccelerometerLabel != null &&
					!mEnabledAccelerometer.isDisposed() && !mRefreshEmulatorAccelerometerLabel.isDisposed()) {
					mEnabledAccelerometer.setSelection(enable);
					mRefreshEmulatorAccelerometerLabel.setText("-");
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setEnabledTemperature(boolean)
	 */
	public void setEnabledTemperature(final boolean enable) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (mEnabledTemperature != null && mRefreshEmulatorThermometerLabel != null &&
					!mEnabledTemperature.isDisposed() && !mRefreshEmulatorThermometerLabel.isDisposed()) {
					mEnabledTemperature.setSelection(enable);
					mRefreshEmulatorThermometerLabel.setText("-");
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setEnabledMagneticField(boolean)
	 */
	public void setEnabledMagneticField(final boolean enable) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (mEnabledMagneticField != null && mRefreshEmulatorCompassLabel != null &&
					!mEnabledMagneticField.isDisposed() && !mRefreshEmulatorCompassLabel.isDisposed()) {
					mEnabledMagneticField.setSelection(enable);
					mRefreshEmulatorCompassLabel.setText("-");
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setEnabledLight(boolean)
	 */
	public void setEnabledLight(final boolean enable) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (mEnabledLight != null && !mEnabledLight.isDisposed()) {
					mEnabledLight.setSelection(enable);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setEnabledProximity(boolean)
	 */
	public void setEnabledProximity(final boolean enable) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (mEnabledProximity != null && !mEnabledProximity.isDisposed()) {
					mEnabledProximity.setSelection(enable);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setEnabledTricorder(boolean)
	 */
	public void setEnabledTricorder(final boolean enable) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (mEnabledTricorder != null && !mEnabledTricorder.isDisposed()) {
					mEnabledTricorder.setSelection(enable);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#setEnabledBarcodeReader(boolean)
	 */
	public void setEnabledBarcodeReader(final boolean enable) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (mEnabledBarcodeReader != null && !mEnabledBarcodeReader.isDisposed()) {
					mEnabledBarcodeReader.setSelection(enable);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getUpdateRatesAccelerometer()
	 */
	public double[] getUpdateRatesAccelerometer() {
		return mUpdateRatesAccelerometer;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getDefaultUpdateRateAccelerometer()
	 */
	public double getDefaultUpdateRateAccelerometer() {
		return mDefaultUpdateRateAccelerometer;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getCurrentUpdateRateAccelerometer()
	 */
	public double getCurrentUpdateRateAccelerometer() {
		return mCurrentUpdateRateAccelerometer;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#updateAverageAccelerometer()
	 */
	public boolean updateAverageAccelerometer() {
		return mUpdateAverageAccelerometer;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getUpdateRatesCompass()
	 */
	public double[] getUpdateRatesCompass() {
		return mUpdateRatesCompass;
	}

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getDefaultUpdateRateCompass()
	 */
	public double getDefaultUpdateRateCompass() {
		return mDefaultUpdateRateCompass;
	}

	public double getCurrentUpdateRateCompass() {
		return mCurrentUpdateRateCompass;
	}

	public boolean updateAverageCompass() {
		return mUpdateAverageCompass;
	}

	public double[] getUpdateRatesOrientation() {
		return mUpdateRatesOrientation;
	}

	public double getDefaultUpdateRateOrientation() {
		return mDefaultUpdateRateOrientation;
	}

	public double getCurrentUpdateRateOrientation() {
		return mCurrentUpdateRateOrientation;
	}

	public boolean updateAverageOrientation() {
		return mUpdateAverageOrientation;
	}

	public double[] getUpdateRatesThermometer() {
		return mUpdateRatesThermometer;
	}

	public double getDefaultUpdateRateThermometer() {
		return mDefaultUpdateRateThermometer;
	}

	public double getCurrentUpdateRateThermometer() {
		return mCurrentUpdateRateThermometer;
	}

	public boolean updateAverageThermometer() {
		return mUpdateAverageThermometer;
	}

	public void setCurrentUpdateRateAccelerometer(final double value) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				mCurrentUpdateRateAccelerometerText.setText(Double.toString(value));
			}
		});
	}

	public void setCurrentUpdateRateCompass(final double value) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				mCurrentUpdateRateCompassText.setText(Double.toString(value));
			}
		});
	}

	public void setCurrentUpdateRateOrientation(final double value) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				mCurrentUpdateRateOrientationText.setText(Double.toString(value));
			}
		});
	}

	public void setCurrentUpdateRateThermometer(final double value) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				mCurrentUpdateRateThermometerText.setText(Double.toString(value));
			}
		});
	}

	public double getUpdateSensors() {
		return getSafeDouble(mUpdateText);
	}

	public double getRefreshAfter() {
		return getSafeDouble(mRefreshCountText);
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
		return mShowAcceleration.getSelection();
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

	/* (non-Javadoc)
	 * @see org.openintents.tools.sensorsimulator.ISensorSimulator#getBarcode()
	 */
	public String getBarcode() {
		return mBarcodeReaderText.getText();
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
		return mRealDeviceThinkpad.getSelection();
	}

	public boolean useRealDeviceWiimtoe() {
		return mRealDeviceWiimote.getSelection();
	}

	public String getRealDevicePath() {
		return mRealDevicePath.getText();
	}

	public void setRealDeviceOutput(String text) {
		mRealDeviceOutputLabel.setText(text);
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
	 * Safely retries the double value of a text field.
	 * If the value is not a valid number, 0 is returned, and the field
	 * is marked red.
	 *
	 * @param textfield Textfield from which the value should be read.
	 * @param defaultValue default value if input field is invalid.
	 * @return double value.
	 */
	public double getSafeDouble(Text textfield, double defaultValue) {
		double value = defaultValue;

		try {
			value = Double.parseDouble(textfield.getText());
			textfield.setBackground(null);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			value = defaultValue;
			textfield.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		return value;
	}


	public double getSafeDouble(Text textfield) {
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
	public double[] getSafeDoubleList(Text textfield) {
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
			textfield.setBackground(null);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			valuelist = null;
			textfield.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		return valuelist;
	}

}
