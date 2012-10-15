package org.openintents.sensorsimulator.hardware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Dispatches a sequence of events in the same time interval it was recorded
 * (based on their timestamps). Should be used if you want to test how an app
 * reacts to a prerecorded event sequence (e.g. shake):
 * <ul>
 * <li>record a sequence using the SensorRecordFromDevice app
 * <li>take that sequence and put it into this dispatcher
 * <li>the dispatcher will try to dispatch events exactly in the same time
 * interval as it was recorded
 * <li>it will ignore the listeners update rate
 * <li>it will then notify the DispatcherEmptyListener if there is one
 * </ul>
 * 
 * @author Qui Don Ho
 * 
 */
public class SequenceDispatcher extends Observable implements Dispatcher {

	protected static final String TAG = "TimestampDispatcher";
	private List<SensorEventListener> mListeners;
	private BlockingQueue<SensorEvent> mQueue;
	private Thread mThread;
	private Handler mUiThreadHandler;

	public SequenceDispatcher() {
		mQueue = new LinkedBlockingQueue<SensorEvent>();
		mListeners = new ArrayList<SensorEventListener>();
		mUiThreadHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	public void addListener(SensorEventListener listener, int interval) {
		Log.i(TAG, "Adding Listener, interval: " + interval);
		mListeners.add(listener);
	}

	@Override
	public void start() {
		if (mThread != null && mThread.isAlive()) {
			throw new IllegalStateException("Already dispatching!!!");
		}

		mThread = new Thread(mDispatching);
	}

	@Override
	public void stop() {
		if (mThread != null)
			mThread.interrupt();
	}

	@Override
	public void putEvent(SensorEvent event) {
		try {
			mQueue.put(event);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void putEvents(Collection<SensorEvent> events) {
		mQueue.addAll(events);
	}

	/**
	 * Call this method when you have put a sequence of events into the
	 * dispatcher and want it to start dispatching.
	 */
	public void play() {
		mThread.start();
	}

	/**
	 * Should dispatch events as close as possible to the original time
	 * intervals when it was recorded.
	 */
	private Runnable mDispatching = new Runnable() {

		private SensorEvent event;

		@Override
		public void run() {
			// init stuff
			long lastTimeStamp = 0;
			long now = 0;
			long timePassedScheduled = 0;
			long lastTimeDispatched = 0;
			long timePassed = 0;
			boolean firstTime = true;

			try {
				int size = mQueue.size();

				// nothing to do
				if (size == 0) {
					// TODO add attribute which indicates that
					// app has onSensorChanged has returned
					setChanged();
					notifyObservers();
					return;
				}

				for (int i = 0; i < size; i++) {
					// take new event
					event = mQueue.poll();
					now = System.nanoTime();

					// check if it is time for that event (and just dispatch it
					// on first run)
					if (!firstTime) {
						timePassedScheduled = event.timestamp - lastTimeStamp;
						timePassed = now - lastTimeDispatched;

						if (timePassed < timePassedScheduled) {
							// convert to ms
							long sleepTime = (timePassedScheduled - timePassed) / 1000000;
							Thread.sleep(sleepTime);
							now = System.nanoTime();
						}
					} else {
						firstTime = false;
					}

					// dispatch to all listeners on ui thread
					final boolean lastTime = i == size - 1;
					for (final SensorEventListener listener : mListeners)
						mUiThreadHandler.post(new Runnable() {

							@Override
							public void run() {
								listener.onSensorChanged(event);
								if (lastTime) {
									// TODO add attribute which indicates that
									// app has onSensorChanged has returned
									setChanged();
									notifyObservers();
								}
							}
						});

					// update for next round
					lastTimeStamp = event.timestamp;
					lastTimeDispatched = now;
				}
			} catch (InterruptedException e) {
				// clean up
				mQueue.clear();
			}
		}
	};
}
