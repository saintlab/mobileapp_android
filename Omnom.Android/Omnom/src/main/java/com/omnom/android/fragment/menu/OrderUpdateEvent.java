package com.omnom.android.fragment.menu;

import com.omnom.android.menu.model.Item;

/**
 * Created by Ch3D on 02.02.2015.
 */
public class OrderUpdateEvent {
	private final Item mItem;

	private final int mCount;

	public OrderUpdateEvent(final Item item, final int count) {
		mItem = item;
		mCount = count;
	}

	public Item getItem() {
		return mItem;
	}

	public int getCount() {
		return mCount;
	}
}
