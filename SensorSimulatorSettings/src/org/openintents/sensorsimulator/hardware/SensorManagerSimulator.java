package org.openintents.sensorsimulator.hardware;

import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.widget.Toast;

/**
 * Replaces SensorManager with possibility to connect to SensorSimulator.
 * 
 * Note: This class does not extend SensorManager directly, because its 
 * constructor is not public.
 * 
 * @author Peli
 *
 */
public class SensorManagerSimulator {

	private static SensorManagerSimulator instance;

	/**
	 * TAG for logging.
	 */
	private static final String TAG = "SensorManagerSimulator";
	
	/**
	 * Client that communicates with the SensorSimulator application.
	 */
	private static SensorSimulatorClient mClient;
	
	private SensorManager mSensorManager = null;
	private Context mContext;
	
	/**
	 * Constructor.
	 * 
	 * If the SensorManagerSimulator is not connected, the default SensorManager is used.
	 * This is obtained through (SensorManager) getSystemService(Context.SENSOR_SERVICE),
	 * but can be overwritten using setDefaultSensorManager().
	 * 
	 * @param context Context of the activity.
	 */
	private SensorManagerSimulator(Context context, SensorManager systemsensormanager) {
		mContext = context;
		mSensorManager = systemsensormanager;
		mClient = new SensorSimulatorClient(mContext);
	}

	public static SensorManagerSimulator getSystemService(Context context, String sensorManager) {
		if (instance == null) {
			if (sensorManager.equals(Context.SENSOR_SERVICE)) {
				if (SensorManagerSimulator.isRealSensorsAvailable()) {
					instance = new SensorManagerSimulator(context, (SensorManager)context.getSystemService(sensorManager));
				}
				else {
					instance = new SensorManagerSimulator(context, null);
					Toast.makeText(
							context, "Android SensorManager disabled, 1.5 SDK emulator crashes when using it... Make sure to connect SensorSimulator", Toast.LENGTH_LONG).show();	
				
				}
				
			}
		}
		return instance;
	}
	
	/**
	 * Set the SensorManager if the SensorManagerSimulator is not connected to the
	 * SensorSimulator.
	 * 
	 * By default, it is set to (SensorManager) getSystemService(Context.SENSOR_SERVICE).
	 * 
	 * @param sensormanager
	 */
	public void setDefaultSensorManager(SensorManager sensormanager) {
		mSensorManager = sensormanager;
	}
	
	/**
	 * Returns the available sensors.
	 * 
	 * @return available sensors as bit mask.
	 */
	public int getSensors() {
		if (mClient.connected) {
			return mClient.getSensors();
		} else {
			if (mSensorManager != null) {
				return mSensorManager.getSensors();
			}
			return 0;
		}
	}

    /**
     *  Method that checks for the 1.5 SDK Emulator bug...
     *  
     * @return
     */
	private static boolean isRealSensorsAvailable() {
		if (Build.VERSION.SDK.equals("3")) {
			// We are on 1.5 SDK
			if (Build.MODEL.contains("sdk")) {
				// We are on Emulator
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Registers a listener for given sensors.
	 * 
	 * @param listener sensor listener object
	 * @param sensors a bit masks of the sensors to register to
	 * @param rate rate of events. This is only a hint to the system. events may be received 
	 *        faster or slower than the specified rate. Usually events are received faster.
	 * @return
	 */
	public boolean registerListener(SensorListener listener, int sensors, int rate) {
		if (mClient.connected) {
			return mClient.registerListener(listener, sensors, rate);
		} else {
			if (mSensorManager == null) {
				return false;
			}
			return mSensorManager.registerListener(listener, sensors, rate);
		}
	}

	/**
	 * Registers a listener for given sensors.
	 * 
	 * @param listener sensor listener object
	 * @param sensors a bit masks of the sensors to register to
	 * @return
	 */
	public boolean registerListener(SensorListener listener, int sensors) {
		if (mClient.connected) {
			return mClient.registerListener(listener, sensors);
		} else {
			if (mSensorManager == null) {
				return false;
			}
			return mSensorManager.registerListener(listener, sensors);
		}
	}

	/**
	 * Unregisters a listener for the sensors with which it is registered.
	 * 
	 * @param listener a SensorListener object
	 * @param sensors a bit masks of the sensors to unregister from
	 */
	public void unregisterListener(SensorListener listener, int sensors) {
		if (mClient.connected) {
			mClient.unregisterListener(listener, sensors);
		} else {
			if (mSensorManager == null) {
				mSensorManager.unregisterListener(listener, sensors);
			}
		}
	}

	/**
	 * Unregisters a listener for the sensors with which it is registered.
	 * 
	 * @param listener a SensorListener object
	 */
	public void unregisterListener(SensorListener listener) {
		if (mClient.connected) {
			mClient.unregisterListener(listener);
		} else {
			if (mSensorManager != null) {
				mSensorManager.unregisterListener(listener);
			}
		}
	}
	

	//  Member function extensions:
	/**
	 * Connect to the Sensor Simulator.
	 * (All the settings should have been set already.)
	 */
	public void connectSimulator() {
		mClient.connect();
	};
	
	/**
	 * Disconnect from the Sensor Simulator.
	 */
	public void disconnectSimulator() {
		mClient.disconnect();
	}
	
	/**
	 * Returns whether the Sensor Simulator is currently connected.
	 */
	public boolean isConnectedSimulator() {
		return mClient.connected;
	}

}
