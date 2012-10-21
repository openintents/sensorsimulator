package org.openintents.sensorsimulator.hardware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

/**
 * Dispatches received events based on their timestamp and the listeners update
 * rate.
 * <p>
 * Do not use this class.
 * 
 * @author Qui Don Ho
 * 
 */
public class TimestampDispatcher implements Dispatcher {

	protected static final String TAG = "TimestampDispatcher";
	private List<ListenerWrapper> mListeners;
	private BlockingQueue<SensorEvent> mQueue;
	private Thread mThread;

	public TimestampDispatcher() {
		mQueue = new LinkedBlockingQueue<SensorEvent>();
		mListeners = new ArrayList<ListenerWrapper>();
	}

	@Override
	public void addListener(SensorEventListener listener, int interval) {
		Log.i(TAG, "Adding Listener, interval: " + interval);
		mListeners.add(new ListenerWrapper(listener, interval));
	}

	@Override
	public void start() {
		if (mThread != null && mThread.isAlive()) {
			throw new IllegalStateException("Already dispatching!!!");
		}

		mThread = new Thread(mDispatching);
		mThread.start();
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

	private Runnable mDispatching = new Runnable() {

		@Override
		public void run() {
			// init stuff
			long lastTimeSent = 0;
			int sentCounter = 0;
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
							// Log.i(TAG, "now: " + now);
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

						// dispatch to all listeners which are ready
						for (ListenerWrapper wrapper : mListeners) {
							if (now > wrapper.nextTime) {
								wrapper.eventListener.onSensorChanged(event);
								wrapper.updateNextTime(now);
								Log.i(TAG, "sending, last time was "
										+ (now - lastTimeSent) / 1000000f
										+ "ms ago");
								lastTimeSent = now;
								Log.i(TAG, "sent events: " + ++sentCounter
										+ ", " + mListeners.size()
										+ " Listeners");
							} else {
								Log.i(TAG, "not sending, next time is in "
										+ (wrapper.nextTime - now) / 1000000f
										+ "ms");
							}
						}

						Log.i(TAG, "events: " + ++eventCounter);

						// update for next round
						lastTimeStamp = event.timestamp;
						lastTimeDispatched = now;
					} else {
						finished = true;
					}
				}
			} catch (InterruptedException e) {
				// clean up
				mQueue.clear();
			}
		}
	};

	/**
	 * Wrapper to store sensor update rate and next time to update.
	 */
	private class ListenerWrapper {

		public SensorEventListener eventListener;
		public long interval;
		public long nextTime;

		public ListenerWrapper(SensorEventListener listener, int interval) {
			this.eventListener = listener;
			this.nextTime = 0;
			this.interval = interval * 1000000; // convert to nanoseconds
		}

		public void updateNextTime(long now) {
			// Log.i(TAG, "time to next update: " + (now + interval -
			// nextTime));
			// Log.i(TAG, "nextTime " + nextTime);
			nextTime = now + interval;
		}
	}

	@Override
	public boolean hasStarted() {
		// TODO Auto-generated method stub
		return false;
	}
}
