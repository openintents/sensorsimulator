package org.openintents.sensorsimulator.hardware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
public class SequenceDispatcher implements Dispatcher {

	protected static final String TAG = "TimestampDispatcher";
	private List<SensorEventListener> mListeners;
	private BlockingQueue<SensorEvent> mQueue;
	private Thread mThread;
	private DispatcherEmptyListener mEmptyListener;

	public SequenceDispatcher() {
		mQueue = new LinkedBlockingQueue<SensorEvent>();
		mListeners = new ArrayList<SensorEventListener>();
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

	@Override
	public void setOnEmptyListener(DispatcherEmptyListener emptyListener) {
		mEmptyListener = emptyListener;
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

		@Override
		public void run() {
			// init stuff
			int eventCounter = 0;
			long lastTimeStamp = 0;
			long now = 0;
			long timePassedScheduled = 0;
			long lastTimeDispatched = 0;
			long timePassed = 0;
			SensorEvent event;
			boolean firstTime = true;
			boolean finished = false;

			try {
				while (!finished) {
					// take new event
					event = mQueue.poll();
					if (event != null) {

						now = System.nanoTime();

						// check if it is time for that event, starting at
						// second run
						if (!firstTime) {
							timePassedScheduled = event.timestamp
									- lastTimeStamp;
							timePassed = now - lastTimeDispatched;

							// Log time
							Log.i(TAG, "timePassed: " + timePassed / 1000000f);
							Log.i(TAG, "timePassedScheduled: "
									+ timePassedScheduled / 1000000f);

							if (timePassed < timePassedScheduled) {
								// convert to ms
								long sleepTime = (timePassedScheduled - timePassed) / 1000000;
								Thread.sleep(sleepTime);
								Log.i(TAG, "slept: " + sleepTime);
								now = System.nanoTime();
								Log.i(TAG, "new timePassed: "
										+ (now - lastTimeDispatched) / 1000000f);
							}
						} else {
							firstTime = false;
						}

						// dispatch to all listeners
						for (SensorEventListener listener : mListeners)
							listener.onSensorChanged(event);

						Log.i(TAG, "events: " + ++eventCounter);

						// update for next round
						lastTimeStamp = event.timestamp;
						lastTimeDispatched = now;
					} else {
						if (mEmptyListener != null)
							mEmptyListener.onDispatcherEmpty();
						finished = true;
					}
				}
			} catch (InterruptedException e) {
				// clean up
				mQueue.clear();
			}
		}
	};
}
