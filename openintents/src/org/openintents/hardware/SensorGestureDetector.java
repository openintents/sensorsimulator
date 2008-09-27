package org.openintents.hardware;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;

/**
 * Class to detect gestures through sensors.
 * 
 * @author Peli
 *
 */
public class SensorGestureDetector {

    private final Handler mHandler;
    private final OnSensorGestureListener mListener;

    private class SensorGestureHandler extends Handler
    {

        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
            
            default:
                throw new RuntimeException((new StringBuilder()).append("Unknown message ").append(msg).toString());
            }
        }

        final SensorGestureDetector mGestureDetector;

        SensorGestureHandler()
        {
            super();
        	mGestureDetector = SensorGestureDetector.this;
        }

        SensorGestureHandler(Handler handler)
        {
            super(handler.getLooper());
        	mGestureDetector = SensorGestureDetector.this;
        }
    }

    /**
     * A convenience class to extend when you only want to listen 
     * for a subset of all the gestures. 
     * This implements all methods in the 
     * SensorGestureDetector.OnSensorGestureListener 
     * but does nothing and return false for all applicable methods.
     *
     */
    public static class SimpleOnSensorGestureListener
        implements OnSensorGestureListener
    {

        public SimpleOnSensorGestureListener()
        {
        }
        
        public boolean onShake()
        {
            return false;
        }

    }
    
    /**
     * The listener that is used to notify when gestures occur. 
     * If you want to listen for all the different gestures 
     * then implement this interface. 
     * If you only want to listen for a subset it might be 
     * easier to extend 
     * SensorGestureDetector.SimpleOnSensorGestureListener.
     *
     */
    public static interface OnSensorGestureListener
    {

    	public abstract boolean onShake();
    }
    

    /**
     * Creates a GestureDetector with the supplied listener. 
     * 
     * This variant of the constructor should be used 
     * from a non-UI thread (as it allows specifying the Handler).
     * 
     * @param listener the listener invoked for all the callbacks, this must not be null.
     * @param handler the handler to use, this must not be null.
     */
    public SensorGestureDetector(OnSensorGestureListener listener, Handler handler)
    {
        mHandler = new SensorGestureHandler(handler);
        mListener = listener;
        init();
    }

    /**
     * Creates a GestureDetector with the supplied listener. 
     * 
     * You may only use this constructor from a UI thread 
     * (this is the usual situation).
     * 
     * @param listener the listener invoked for all the callbacks, 
     * 			this must not be null.
     */
    public SensorGestureDetector(OnSensorGestureListener listener)
    {
        mHandler = new SensorGestureHandler();
        mListener = listener;
        init();
    }

    private void init()
    {
        if(mListener == null)
        {
            throw new NullPointerException("OnGestureListener must not be null");
        } else
        {
            return;
        }
    }

    
    void onSensorChanged(int sensor, float[] values) {
    	
    }
    

	public void onAccuracyChanged(int sensor, int accuracy) {
		
	}
	
	

    /** 
     * Shake detection threshold.
     */
    public float accelerometer_shake_threshold = 1.2f;
    
    //////////////////////////////////////////////////////
    // Internal constants.
    
    /**
     * Internal conversion factor for faster processing.
     */
    static final int ACCELEROMETER_FLOAT_TO_INT = 1024;
    
    //////////////////////////////////////////////////////
    // Internal variables.
    
    int mACCELEROMETER_SHAKE_THRESHOLD_SQUARE;
	
    int action = SensorEvent.ACTION_VOID;
    float x = 0;
    float y = 0;
    float z = 0;
    long eventTime = 0;
    
	private void testAccelerometerShake() {
    	// Read out sensor values
        int num = Sensors.getNumSensorValues(Sensors.SENSOR_ACCELEROMETER);
		float[] val = new float[num];
		Sensors.readSensor(Sensors.SENSOR_ACCELEROMETER, val);
		
		int ax = (int)(ACCELEROMETER_FLOAT_TO_INT * val[0]);
		int ay = (int)(ACCELEROMETER_FLOAT_TO_INT * val[1]);
		int az = (int)(ACCELEROMETER_FLOAT_TO_INT * val[2]);
		
		int len2 = ax * ax + ay * ay + az * az;
		
		if (len2 > mACCELEROMETER_SHAKE_THRESHOLD_SQUARE) {
			// Shaking
			action = SensorEvent.ACTION_SHAKE;
			x = val[0];
			y = val[1];
			z = val[2];
			eventTime = SystemClock.uptimeMillis();
			
			mListener.onShake();
		}
    }
}
