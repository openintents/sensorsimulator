package org.openintents.hardware;

/**
 * Object used to report sensor events.
 * 
 * @author Peli
 *
 */
public class SensorEvent {

	/**
	 * No action.
	 */
	public static final int ACTION_VOID = 0;
	
	/**
	 * Indicates scrolling behavior.
	 */
	public static final int ACTION_MOVE = 1;
	
	/**
	 * Indicates shaking.
	 */
	public static final int ACTION_SHAKE = 2;
	
	int mAction;
	float mX;
	float mY;
	float mZ;
	long mEventTime;
	
	SensorEvent() {
		
	}
	
	SensorEvent(int action, float x, float y, float z, long eventTime) {
		mAction = action;
		mX = x;
		mY = y;
		mZ = z;
		mEventTime = eventTime;
	}
	
	/**
	 * Return the kind of action being performed  
	 */
	public final int getAction() {
		return mAction;
	}
	
	/**
	 * Returns the time (in ms) when this specific event was generated.
	 */
	public final long getEventTime() {
		return mEventTime;
	}
	
	/**
	 * Returns the X coordinate of this event. 
	 */
	public final float getX() {
		return mX;
	}
	
	/**
	 * Returns the Y coordinate of this event. 
	 */
	public final float getY() {
		return mY;
	}
	 
	/**
	 * Sets this event's action. 
	 * @param action
	 */
	public final void setAction(int action) {
		mAction = action;
	}
	 
	 
	/**
	 * Set this event's location. 
	 * @param x
	 * @param y
	 */
	public final void setLocation(float x, float y) {
		mX = x;
		mY = y;
	}
	 


}
