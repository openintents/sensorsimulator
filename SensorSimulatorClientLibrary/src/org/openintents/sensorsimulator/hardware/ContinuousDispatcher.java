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

public class ContinuousDispatcher implements Dispatcher {

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
		mProducerSpeed = speed;
		mProducerRate = rate;
	}

	private Runnable mDispatching = new Runnable() {

		@Override
		public void run() {
			long lastTime = 0;
			long now;
			long delta;
			Handler handler = new Handler(Looper.getMainLooper());

			while (!Thread.interrupted()) {

				try {
					final SensorEvent event = mSensorEvents.take();

					Log.d(TAG, "processing [event accuracy: " + event.accuracy
							+ "]");

					// check whether it is time to dispatch
					now = System.nanoTime();
					delta = (now - lastTime) / 1000000;
					if (delta < mProducerSpeed) {
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
							}
						});
					}

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
