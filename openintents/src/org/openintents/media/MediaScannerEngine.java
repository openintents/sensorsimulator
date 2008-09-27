package org.openintents.media;

import java.io.File;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;

public class MediaScannerEngine {

	/** TAG for log messages. */
	static final String TAG = "MediaScannerEngine";

	//private android.media.IMediaScannerService mScanner = null;
	private MediaScannerConnection mScannerConnection = new MediaScannerConnection();
	private boolean mScannerInitializing = false;
	private boolean mScannerConnected = false;
	
	private Context mContext;

	public MediaScannerEngine(Context context) {
		mContext = context;
	}

	public class MediaScannerConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name,
				android.os.IBinder service) {
			synchronized (MediaScannerEngine.this) {
				Log.i(TAG, "Connected to MediaScanner");
				mScannerInitializing = false;
//				mScanner = IMediaScannerService.Stub.asInterface(service);
//				if (mScanner != null) {
//					mScannerConnected = true;
//					scanTarget();
//				} else {
//					// something failed, handling is implementation specific
//				}
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			synchronized (MediaScannerEngine.this) {
				Log.i(TAG, "Disconnected from MediaScanner");
				//mScanner = null;
				mScannerConnected = false;
			}
		}
	}

	private void scanTarget() {
		// should check that service is connected and
		// not null and take appropriate action
		if (mScannerConnected/* scanner ready */) {
			(new ScannerThread()).start();
		}
	}

	private class ScannerThread extends Thread {
		public void run() {
			scan();
		}
		
		/**
		 * Scans the sd card for media.
		 */
		private void scan() {
			Log.i(TAG, "Start scanning media files...");
			String sdcardpath = android.os.Environment
					.getExternalStorageDirectory().getAbsolutePath();
			File f = new File(sdcardpath);

			String filenames[] = f.list();

			for (String filename : filenames) {
				String mimeType = MediaUtils.getMimeType(filename);
//				if (mScanner != null) {
//					Log.i(TAG, "Scanning file " + filename + ", " + mimeType);
//					try {
//						mScanner.scanFile(filename, mimeType);
//					} catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
			}
			
			// When we are done, unbind

			mContext.unbindService(mScannerConnection);
			Log.i(TAG, "MediaScanner finished");
		}
	}

	/**
	 * Starts the Media scanner in a separate thread.
	 */
	public void scanInThread() {
		Intent intent = new Intent();
		intent.setClassName("com.android.providers.media",
				"com.android.providers.media.MediaScannerService");
		mScannerInitializing = true;
		mContext.bindService(intent, mScannerConnection,
				Context.BIND_AUTO_CREATE);
	}

	

}
