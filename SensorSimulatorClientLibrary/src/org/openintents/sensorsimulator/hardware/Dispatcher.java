package org.openintents.sensorsimulator.hardware;

import java.util.Collection;

/**
 * A <code>Dispatcher</code> stores a queue of <code>SensorEvent</code>s and
 * dispatches them to the registered listeners.
 * <p>
 * This interface was mainly introduced to keep the possibility of evaluating
 * different implementations in terms of performance.
 * 
 * @author Qui Don Ho
 * 
 */
public interface Dispatcher {

	/**
	 * Add a new <code>SensorEventListener</code>.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addListener(SensorEventListener listener, int interval);

	/**
	 * Start dispatching (presumably in internal thread).
	 */
	public void start();

	/**
	 * Stop dispatching (and interrupt internal thread).
	 */
	public void stop();

	/**
	 * Put a single <code>SensorEvent</code> in the queue.
	 * 
	 * @param event
	 *            a collection of events to put
	 */
	void putEvent(SensorEvent event);

	/**
	 * Put a new chunk of <code>SensorEvent</code>s in the queue.
	 * 
	 * @param events
	 *            a collection of events to put
	 */
	void putEvents(Collection<SensorEvent> events);

	/**
	 * Has Dispatcher started?
	 * 
	 * @return true or false
	 */
	public boolean hasStarted();
}
