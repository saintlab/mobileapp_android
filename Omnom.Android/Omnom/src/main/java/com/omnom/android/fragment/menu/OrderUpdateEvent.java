package com.omnom.android.fragment.menu;

import com.omnom.android.menu.model.Item;

import java.util.List;

/**
 * Created by Ch3D on 02.02.2015.
 */
public class OrderUpdateEvent {
	private final Item mItem;

	private final int mCount;

	private final List<String> mSelectedModifiersIds;

	private int mPosition;

	public OrderUpdateEvent(final Item item, final int count, final int position, final List<String> selectedModifiersIds) {
		mItem = item;
		mCount = count;
		mPosition = position;
		mSelectedModifiersIds = selectedModifiersIds;
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

	public List<String> getSelectedModifiersIds() {
		return mSelectedModifiersIds;
	}
}
