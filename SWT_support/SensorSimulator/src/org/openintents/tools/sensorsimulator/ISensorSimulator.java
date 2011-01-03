/**
 *
 */
package org.openintents.tools.sensorsimulator;

/**
 *
 * @author Lee Sanghoon
 */
public interface ISensorSimulator {

	// Supported sensors:
	static final String ORIENTATION = "orientation";
	static final String ACCELEROMETER = "accelerometer";
	static final String TEMPERATURE = "temperature";
	static final String MAGNETIC_FIELD = "magnetic field";
	static final String LIGHT = "light";
	static final String PROXIMITY = "proximity";
	static final String TRICORDER = "tricorder";
	static final String BARCODE_READER = "barcode reader";

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

    // Action Commands:
    static String yawPitch = "yaw & pitch";
    static String rollPitch = "roll & pitch";
    static String move = "move";
    static String timerAction = "timer";
    static String setPortString = "set port";
    //action for telnet connection and send gps
    static String connectViaTelnet = "connectViaTelnet";
    static String sendGPS = "send gps";

    static String emulateBattery = "emulate battery";
    static String nextTimeEvent = "next Time Event";

	static int mouseYawPitch = 1;
	static int mouseRollPitch = 2;
	static int mouseMove = 3;


	/**
	 * @param string
	 */
	public void addMessage(String string);

	public int getMouseMode();

	public WiiMoteData getWiiMoteData();

	public void newClient();

	public int getDelay();
	/**
	 * mSensorSimulator.delay = (int) newdelay;
	 * mSensorSimulator.timer.setDelay(mSensorSimulator.delay);
	 *
	 * @param delay
	 */
	public void setDelay(int delay);


	/*
	 *
	 */
	public IMobilePanel getMobilePanel();

	/*
	 *
	 */
	public int getPort();
	public int getTelnetPort();

	/*
	 * yaw & pitch & roll slide
	 */
	public int getYaw();
	public int getPitch();
	public int getRoll();

	public void setYaw(int yaw);
	public void setPitch(int pitch);
	public void setRoll(int roll);

	/*
	 * Support Sensors
	 */
    public boolean isSupportedOrientation();
    public boolean isSupportedAccelerometer();
    public boolean isSupportedTemperature();
    public boolean isSupportedMagneticField();
    public boolean isSupportedLight();
    public boolean isSupportedProximity();
    public boolean isSupportedTricorder();
    public boolean isSupportedBarcodeReader();

    /*
     * Enabled Sensors
     */
    public boolean isEnabledOrientation();
    public boolean isEnabledAccelerometer();
    public boolean isEnabledTemperature();
    public boolean isEnabledMagneticField();
    public boolean isEnabledLight();
    public boolean isEnabledProximity();
    public boolean isEnabledTricorder();
    public boolean isEnabledBarcodeReader();

    /**
     * mSensorSimulator.setEnabledAccelerometer(enable);
     * mSensorSimulator.mRefreshEmulatorAccelerometerLabel.setText("-");
     *
     * @param enable
     * @return
     */
    public void setEnabledOrientation(boolean enable);
    public void setEnabledAccelerometer(boolean enable);
    public void setEnabledTemperature(boolean enable);
    public void setEnabledMagneticField(boolean enable);
    public void setEnabledLight(boolean enable);
    public void setEnabledProximity(boolean enable);
    public void setEnabledTricorder(boolean enable);
    public void setEnabledBarcodeReader(boolean enable);

    /*
     * Sensor Update Rate
     */
    public double[] getUpdateRatesAccelerometer();
    public double getDefaultUpdateRateAccelerometer();
    public double getCurrentUpdateRateAccelerometer();
    public boolean updateAverageAccelerometer();
    public double[] getUpdateRatesCompass();
    public double getDefaultUpdateRateCompass();
    public double getCurrentUpdateRateCompass();
    public boolean updateAverageCompass();
    public double[] getUpdateRatesOrientation();
    public double getDefaultUpdateRateOrientation();
    public double getCurrentUpdateRateOrientation();
    public boolean updateAverageOrientation();
    public double[] getUpdateRatesThermometer();
    public double getDefaultUpdateRateThermometer();
    public double getCurrentUpdateRateThermometer();
    public boolean updateAverageThermometer();

    public void setCurrentUpdateRateAccelerometer(double value);
    public void setCurrentUpdateRateCompass(double value);
    public void setCurrentUpdateRateOrientation(double value);
    public void setCurrentUpdateRateThermometer(double value);

    /*
     * Simulation Update
     */
    public double getUpdateSensors();
    public double getRefreshAfter();

    public void updateSensorRefresh();
	public void updateEmulatorAccelerometerRefresh();
	public void updateEmulatorCompassRefresh();
	public void updateEmulatorOrientationRefresh();
	public void updateEmulatorThermometerRefresh();

    /*
     * Accelerometer
     */
	public double getGravityConstant();
	public double getAccelerometerLimit();
	public double getPixelsPerMeter();
	public double getSpringConstant();
	public double getDampingConstant();

    public boolean isShowAcceleration();

    /*
     * Gravity
     */
	public double getGravityX();
	public double getGravityY();
	public double getGravityZ();

    /*
     * Magnetic Field
     */
	public double getMagneticFieldNorth();
	public double getMagneticFieldEast();
	public double getMagneticFieldVertical();

    /*
     * Temperature
     */
    public double getTemperature();

    /*
     * Barcode
     */
    public String getBarcode();

    /*
     * Random Component
     */
    public double getRandomAccelerometer();
    public double getRandomMagneticField();
    public double getRandomOrientation();
    public double getRandomTemperature();

    /*
     * Real Sensor Bridge
     */
	public boolean useRealDeviceThinkpad();
	public boolean useRealDeviceWiimtoe();
	public String getRealDevicePath();
	public void setRealDeviceOutput(String text);


	/*
	 * GPS
	 */
	public float getLongitude();
	public float getLatitude();
	public float getAltitude();
	public String getLisName();




//  /**
//   * @param textField It can be JTextField (for Swing) or Text (for SWT)
//   * @param defaultValue
//   * @return
//   */
//  public double getSafeDouble(Object textField, double defaultValue);
//
//  /**
//   * @param textField It can be JTextField (for Swing) or Text (for SWT)
//   * @return
//   */
//  public double getSafeDouble(Object textField);
//
//  /**
//   * @param textField It can be JTextField (for Swing) or Text (for SWT)
//   * @return
//   */
//  public double[] getSafeDoubleList(Object textField);

}
