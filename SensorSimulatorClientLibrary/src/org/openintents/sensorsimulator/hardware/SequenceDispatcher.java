package org.openintents.sensorsimulator.hardware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Looper;

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

	protected static final String TAG = "SequenceDispatcher";
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
	public void addListener(SensorEventListener listener, int rate) {
		if (!mListeners.contains(listener))
			mListeners.add(listener);
	}

	@Override
	public void removeListener(SensorEventListener listener) {
		if (mListeners.contains(listener))
			mListeners.remove(listener);
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
	public synchronized void play() {
		this.notify();
	}

	/**
	 * Should dispatch events as close as possible to the original time
	 * intervals when it was recorded.
	 * <p>
	 * Waits, until <code>play()</code> is called, dispatches the sequence in
	 * the queue, notifies all observers and waits again.
	 */
	private Runnable mDispatching = new Runnable() {

		private SensorEvent event;

		@Override
		public void run() {

			try {
				while (true) {

					// wait until play method is called
					synchronized (SequenceDispatcher.this) {
						SequenceDispatcher.this.wait();
					}

					// init stuff
					long lastTimeStamp = 0;
					long now = 0;
					long timePassedScheduled = 0;
					long lastTimeDispatched = 0;
					long timePassed = 0;
					boolean firstTime = true;

					int size = mQueue.size();

					// nothing to do
					if (size == 0) {
						setChanged();
						notifyObservers();
						continue;
					}

					// actual dispatching
					for (int i = 0; i < size; i++) {
						// take new event
						event = mQueue.poll();
						now = System.nanoTime();

						// check if it is time for that event (and just dispatch
						// it on first run)
						if (!firstTime) {
							timePassedScheduled = event.timestamp
									- lastTimeStamp;
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
						for (final SensorEventListener listener : mListeners)
							mUiThreadHandler.post(new Runnable() {

								@Override
								public void run() {
									listener.onSensorChanged(event);
								}
							});

						// update for next round
						lastTimeStamp = event.timestamp;
						lastTimeDispatched = now;
					}

					// Tell observers we finished dispatching. Must be on UI
					// thread to ensure this is done _after_ the last event was
					// dispatched.
					mUiThreadHandler.post(new Runnable() {

						@Override
						public void run() {
							setChanged();
							notifyObservers();
						}
					});
				}
			} catch (InterruptedException e) {
				// clean up
				mQueue.clear();
			}

			mThread = null;
		}
	};

	@Override
	public boolean hasStarted() {
		return mThread != null && mThread.isAlive();
	}
}
