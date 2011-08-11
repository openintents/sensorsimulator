package org.openintents.sensorsimulator.record;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SensorRecordService extends Service {
	private static final String TAG = "SensorRecordService";

	// service
	private NotificationManager mNotificationManager;
	private int NOTIFICATION = R.string.sensors_recording_started;
	private final IBinder mBinder = new LocalBinder();

	// communication
	private Socket mRequestSocket;
	private ObjectOutputStream mOutStream;

	// sensors
	private ArrayList<SensorEventListener> mListenersArray;
	// private SensorManagerSimulator mSensorManager;
	private SensorManager mSensorManager;
	private Handler mHandler;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		SensorRecordService getService() {
			return SensorRecordService.this;
		}
	}

	@Override
	public void onCreate() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mListenersArray = new ArrayList<SensorEventListener>();

		showNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, R.string.sensors_recording_started,
				Toast.LENGTH_SHORT).show();

		// start socket connection
		// get ip address (from intent)
		String ipAddress = intent.getStringExtra("ip");
		try {
			mRequestSocket = new Socket(ipAddress, Global.PORT);
			Log.d(TAG, "created socket for ip address:" + ipAddress);
			mOutStream = new ObjectOutputStream(
					mRequestSocket.getOutputStream());
			mHandler = new Handler(new Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					// event captured by a listener
					// send event values to the server (sockets)
					try {
						// sensorType
						mOutStream.writeObject(msg.arg1);
						// float[] - sensor values
						mOutStream.writeObject(msg.obj);

						// Log.d(TAG, "send message:" + ((float[]) msg.obj)[0] +
						// " "
						// + ((float[]) msg.obj)[1] + " " + ((float[])
						// msg.obj)[2]);
						//
					} catch (IOException e) {
						Log.e(TAG, "Connection closed!");
						clearAll();
					}

					return true;
				}
			});
			mOutStream.flush();
		} catch (UnknownHostException e) {
			Log.e(TAG, e.getMessage());
			clearAll();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			clearAll();
		}

		// get the wanted sensors for recording (from intent)
		int[] recordingSensors = intent.getIntArrayExtra("sensors");
		for (int sensor : recordingSensors) {
			// register listeners for each of them
			registerListener(sensor);
		}

		return START_STICKY;
	}

	private void registerListener(final int sensorType) {
		Log.d(TAG, "registered:" + sensorType);
		SensorEventListener listener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				Message msg = new Message();
				msg.obj = event.values.clone();
				msg.arg1 = sensorType;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mSensorManager.registerListener(listener,
				mSensorManager.getDefaultSensor(sensorType),
				SensorManager.SENSOR_DELAY_NORMAL);

		mListenersArray.add(listener);
	}

	@Override
	public void onDestroy() {
		clearAll();
	}

	private void clearAll() {
		mNotificationManager.cancel(NOTIFICATION);
		for (SensorEventListener listener : mListenersArray) {
			mSensorManager.unregisterListener(listener);
		}
		Toast.makeText(this, R.string.sensors_recording_stopped,
				Toast.LENGTH_SHORT).show();
		try {
			if (mRequestSocket != null) {
				mRequestSocket.close();
				mOutStream.close();
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		CharSequence text = getText(R.string.sensors_recording_started);
		Notification notification = new Notification(R.drawable.icon, text,
				System.currentTimeMillis());
		PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(
				this, SensorRecordFromDeviceActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this,
				getText(R.string.sensors_record_label), text, intent);

		// Send the notification.
		mNotificationManager.notify(NOTIFICATION, notification);
	}
}
