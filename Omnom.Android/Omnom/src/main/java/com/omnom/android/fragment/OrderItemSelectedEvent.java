package com.omnom.android.fragment;

/**
 * Created by Ch3D on 12.11.2014.
 */
public class OrderItemSelectedEvent {
	private String mOrderId;

	private final int mPosition;

	private final boolean mSelected;

	public OrderItemSelectedEvent(final String orderId, final int position, final boolean selected) {
		mOrderId = orderId;
		mPosition = position;
		mSelected = selected;
	}

	public int getPosition() {
		return mPosition;
	}

	public boolean isSelected() {
		return mSelected;
	}

	public String getOrderId() {
		return mOrderId;
	}
}
