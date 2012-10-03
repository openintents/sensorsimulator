package org.openintents.tools.simulator.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for measuring the sensor refreshment rate. Every sensor update, a
 * counter is increased, until it reaches a maximum value. Then, an average
 * refreshment rate is calculated by determining the time difference between the
 * last calculation and now divided by the maximum count value.
 * <br><br>
 * Observers can be added to display the refreshment rate.  
 * 
 * @author Qui Don Ho
 * 
 */
public class RefreshRateMeter {

	private int mUpdateSensorCount;
	private long mUpdateSensorTime;
	private List<RefreshRateObserver> mRefreshRateObservers;
	private long mMaxCount;

	public RefreshRateMeter(long maxCount) {
		mMaxCount = maxCount;
		
		mRefreshRateObservers = new LinkedList<RefreshRateObserver>();
	}

	/**
	 * Adds 1 to the counter. If the maximum value is reached, the average
	 * refreshment rate is calculated and the observers notified.
	 */
	public void count() {
		++mUpdateSensorCount;
		if (mMaxCount >= 0 && mUpdateSensorCount >= mMaxCount) {
			long newtime = System.currentTimeMillis();
			double ms = (double) (newtime - mUpdateSensorTime) / ((double) mMaxCount);
			for (RefreshRateObserver obs : mRefreshRateObservers) {
				obs.notifyRefreshRateChange(ms);
			}

			// reset
			mUpdateSensorCount = 0;
			mUpdateSensorTime = newtime;
		}
	}

	public void addObserver(RefreshRateObserver refreshRateObserver) {
		mRefreshRateObservers.add(refreshRateObserver);
	}
	
	public long getMaxCount() {
		return mMaxCount;
	}

	public void setMaxCount(long maxCount) {
		mMaxCount = maxCount;
	}
}
