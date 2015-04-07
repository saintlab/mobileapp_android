package com.omnom.android.fragment.takeaway;

/**
 * Created by Ch3D on 26.03.2015.
 */
public class TakeawayTimePickedEvent {
	private int mTimeValue;

	public TakeawayTimePickedEvent(final int timeValue) {mTimeValue = timeValue;}

	/**
	 * @return pick up delay in minutes
	 */
	public int getTimeValue() {
		return mTimeValue;
	}
}
