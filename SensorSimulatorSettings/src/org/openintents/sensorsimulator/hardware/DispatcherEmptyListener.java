package org.openintents.sensorsimulator.hardware;

/**
 * When a <code>Dispatcher</code> has no more sensor events to dispatch, this
 * listener is called. It can then load new events or notify a GUI that all
 * events have been dispatched.
 * <p>
 * A test case which wants to test how a SensorSimulator empowered app responds
 * to a sequence of events, e.g. a shake, can use this callback to trigger the
 * test of the expected outcome.
 * 
 * @author Qui Don Ho
 * 
 */
public interface DispatcherEmptyListener {

	/**
	 * This method gets called, when the respective Dispatcher has no more
	 * sensor events to dispatch.
	 */
	public void onDispatcherEmpty();
}
