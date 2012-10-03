package org.openintents.sensorsimulator.hardware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;

/**
 * Dispatches received events based on their timestamp, ignoring the sensor rate
 * specified by the listener.
 * 
 * @author Qui Don Ho
 * 
 */
public class TimestampDispatcher implements Dispatcher {

	private List<ListenerWrapper> mListeners;
	private BlockingQueue<SensorEvent> mQueue;
	private Thread mThread;

	public TimestampDispatcher() {
		// create a blocking queue for sensor events
		mQueue = new LinkedBlockingQueue<SensorEvent>();
		mListeners = new ArrayList<ListenerWrapper>();
	}

	@Override
	public void addListener(SensorEventListener listener, int interval) {
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
			long lastTimeStamp = 0;
			long now = 0;
			long timePassedScheduled = 0;
			long lastTimeDispatched = 0;
			long timePassed = 0;
			SensorEvent event;
			boolean firstTime = true;

			try {
				while (true) {
					// take new event
					event = mQueue.take();

					now = System.nanoTime();

					// check if it is time for that event, starting at
					// second run
					if (!firstTime) {
						timePassedScheduled = event.timestamp - lastTimeStamp;
						timePassed = now - lastTimeDispatched;
						if (timePassed < timePassedScheduled) {
							// convert to ms
							Thread.sleep((timePassedScheduled - timePassed) / 1000000);
							now = System.nanoTime();
						}
					} else {
						firstTime = false;
					}

					// dispatch to all listeners which are ready
					for (ListenerWrapper wrapper : mListeners) {
						if (now > wrapper.nextTime) {
							wrapper.eventListener.onSensorChanged(event);
							wrapper.updateNextTime(now);
						}
					}

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
			nextTime = now + interval;
		}
	}
}
