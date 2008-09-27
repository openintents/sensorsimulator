package org.openintents.hardware;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.openintents.R;
import org.openintents.hardware.services.ISensorService;
import org.openintents.hardware.services.ISensorServiceCallback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * Listens to sensor events by binding to a central Sensor service.
 * 
 * @author Peli
 *
 */
public class SensorEventListener {
	
	/**
	 * TAG for logging.
	 */
	private static final String TAG = "SensorListener";

	private static final int MSG_SENSOR_EVENT = 1;
    
	/** The primary interface we will be calling on the service. */
    ISensorService mService = null;
    
    Context mContext;
    
    private boolean mIsBound;
    
    OnSensorListener mSensorListener;
    
    /**
     * Stores sensor events.
     * @param context
     * @return
     */
    ConcurrentLinkedQueue<SensorEvent> mEventQueue;
    
	public SensorEventListener(Context context) {
		mIsBound = false;
		mContext = context;
		mEventQueue = new ConcurrentLinkedQueue<SensorEvent>();
	}

	/**
	 * 
	 */
	public void bindService() {
		// Establish a couple connections with the service, binding
        // by interface names.  This allows other applications to be
        // installed that replace the remote service by implementing
        // the same interface.
        mContext.bindService(new Intent(ISensorService.class.getName()),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
	}
	
	public void unbindService() {
		if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    mService.unregisterCallback(mCallback);
                } catch (DeadObjectException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                } catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            // Detach our existing connection.
            mContext.unbindService(mConnection);
            mIsBound = false;
        }
	}
	
	public boolean isBound() {
		return mIsBound;
	}

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = ISensorService.Stub.asInterface(service);
            //mCallbackText.setText("Attached.");

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                mService.registerCallback(mCallback);
            } catch (DeadObjectException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            } catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            // As part of the sample, tell the user what happened.
            Toast.makeText(mContext, R.string.sensor_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            

            // As part of the sample, tell the user what happened.
            Toast.makeText(mContext, R.string.sensor_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };


    // ----------------------------------------------------------------------
    // Code showing how to deal with callbacks.
    // ----------------------------------------------------------------------
    
    /**
     * This implementation is used to receive callbacks from the remote
     * service.
     */
    private ISensorServiceCallback mCallback = new ISensorServiceCallback.Stub() {
        /**
         * This is called by the remote service regularly to notify about
         * sensor events.  Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in the main thread like most other things -- so,
         * to update the UI, one needs to use a Handler to hop over there.
         */
        public void onISensorEvent(int action, float x, float y, float z, long eventTime) {
        	SensorEvent event = new SensorEvent(action, x, y, z, eventTime);
            
        	mEventQueue.add(event);
        	
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SENSOR_EVENT));
        }
    };
    
    /**
     * Receive messages within the UI thread and process them through a callback.
     */
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(final Message msg) {
        	Log.i(TAG, "SensorListener: handleMessage: " + msg);
            switch (msg.what) {
                case MSG_SENSOR_EVENT:
                	if (mSensorListener != null) {
                		Log.i(TAG, "SensorListener: handleMessage: Send event " + msg);
                		SensorEvent event = mEventQueue.remove();
                		
                		mSensorListener.onSensorEvent(event);
                	}
                    
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
    
	/**
	 * Register a callback to be invoked when the sensor state changes. 
	 * 
	 * @param sensorListener The callback that will run or null to unregister.
	 */
	public void setOnSensorListener(OnSensorListener sensorListener) {
		mSensorListener = sensorListener;
	}

	/** 
	 * Interface definition for a callback to be invoked 
	 * when a sensor event is dispatched.
	 * 
	 */
	public static interface OnSensorListener {
		
		/**
		 * This method is called when a sensor events occurs.
		 * @param event The sensor event. 
		 * @return True if the event was handled, false otherwise. 
		 */
		public boolean onSensorEvent(SensorEvent event);
	}
	
}
