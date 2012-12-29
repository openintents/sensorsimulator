package org.openintents.sensorsimulator.hardware;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

class ContinuousDispatcher implements Dispatcher {

	protected static final String TAG = ContinuousDispatcher.class.getName();
	private Map<SensorEventListener, Integer> mListeners;
	private BlockingQueue<SensorEvent> mSensorEvents;
	private Thread mDispatchingThread;
	private int mProducerSpeed;
	private int mProducerRate;

	public ContinuousDispatcher(int speed, int rate) {
		mProducerSpeed = speed;
		mProducerRate = rate;

		mSensorEvents = new LinkedBlockingQueue<SensorEvent>();
		mListeners = new HashMap<SensorEventListener, Integer>();
	}

	@Override
	public void addListener(SensorEventListener listener, int rate) {
		mListeners.put(listener, rate);
	}

	@Override
	public void removeListener(SensorEventListener listener) {
		mListeners.remove(listener);
	}

	@Override
	public void start() {
		if (mDispatchingThread != null && mDispatchingThread.isAlive()) {
			throw new IllegalStateException("Already dispatching!!!");
		}

		mDispatchingThread = new Thread(mDispatching);
		mDispatchingThread.start();
	}

	@Override
	public void stop() {
		if (mDispatchingThread != null)
			mDispatchingThread.interrupt();
	}

	@Override
	public void putEvent(SensorEvent event) {
		try {
			mSensorEvents.put(event);
			Log.d(TAG, "queue size: " + mSensorEvents.size());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void putEvents(Collection<SensorEvent> events) {
		throw new UnsupportedOperationException(
				"ContinuousDispatcher can only take single events");
	}

	@Override
	public boolean hasStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void configProducer(int speed, int rate) {
		Log.d(TAG, "producer speed: " + speed + ", rate: " + rate);
		mProducerSpeed = speed;
		mProducerRate = rate;
	}

	private Runnable mDispatching = new Runnable() {

		private int accCounter = 0;
		private long lastAccTime = 0;
		private int measuresPerInterval = 50;

		
		@Override
		public void run() {
			long lastTime = 0;
			long now;
			long delta;
			Handler handler = new Handler(Looper.getMainLooper());

			while (!Thread.interrupted()) {

				try {
					if (mSensorEvents.isEmpty())
						System.out.println("event queue empty!");
					else 
						Log.d(TAG, "event queue size: " + mSensorEvents.size());
					final SensorEvent event = mSensorEvents.take();

					// check whether it is time to dispatch
					now = System.nanoTime();
					delta = (now - lastTime) / 1000000;
					if (delta < mProducerSpeed) {
						// Log.d(TAG, "sleeping " + (mProducerSpeed - delta));
						Thread.sleep(mProducerSpeed - delta);
						now = System.nanoTime();
					}

					// dispatch
					for (final Entry<SensorEventListener, Integer> entry : mListeners
							.entrySet()) {
						// compute if we should dispatch to this listener
						handler.post(new Runnable() {

							@Override
							public void run() {
								entry.getKey().onSensorChanged(event);
								Log.d(TAG, "Dispatching " + event.type);
							}
						});
					}
					
					// performance measuring
					if (accCounter == 0 || accCounter == measuresPerInterval) {
						String s = String.valueOf("acc dispatch: " + measuresPerInterval
								/ ((event.timestamp - lastAccTime) / 1000000000.0)
								+ "Hz");
						lastAccTime = event.timestamp;
						Log.i(TAG, s);
						accCounter = 0;
					}

					 accCounter++;

					lastTime = now;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			// clean up
			mSensorEvents.clear();
		}
	};
}
