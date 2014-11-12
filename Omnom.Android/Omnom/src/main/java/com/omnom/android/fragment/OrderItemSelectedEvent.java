package com.omnom.android.fragment;

/**
 * Created by Ch3D on 12.11.2014.
 */
public class OrderItemSelectedEvent {
	private final int mPosition;

	private final boolean mSelected;

	public OrderItemSelectedEvent(final int position, final boolean selected) {

		mPosition = position;
		mSelected = selected;
	}

	public int getPosition() {
		return mPosition;
	}

	public boolean isSelected() {
		return mSelected;
	}
}
