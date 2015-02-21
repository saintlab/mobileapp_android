package com.omnom.android.fragment.menu;

import com.omnom.android.menu.model.Item;

/**
 * Created by Ch3D on 02.02.2015.
 */
public class OrderUpdateEvent {
	private final Item mItem;

	private final int mCount;

	private int mPosition;

	public OrderUpdateEvent(final Item item, final int count, final int position) {
		mItem = item;
		mCount = count;
		mPosition = position;
	}

	public Item getItem() {
		return mItem;
	}

	public int getCount() {
		return mCount;
	}

	public int getPosition() {
		return mPosition;
	}
}
