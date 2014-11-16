package com.omnom.android.fragment.events;

/**
 * Created by Ch3D on 12.11.2014.
 */
public class SplitHideEvent {
	private String mOrderId;

	public SplitHideEvent(final String orderId) {
		mOrderId = orderId;
	}

	public String getOrderId() {
		return mOrderId;
	}
}
