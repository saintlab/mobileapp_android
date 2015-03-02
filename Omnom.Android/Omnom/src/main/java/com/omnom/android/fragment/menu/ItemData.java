package com.omnom.android.fragment.menu;

import com.omnom.android.menu.model.Item;

import java.util.Collections;
import java.util.List;

import static com.omnom.android.adapter.MultiLevelRecyclerAdapter.Data;

/**
 * Created by Ch3D on 20.02.2015.
 */
public class ItemData implements Data {

	public static final int TYPE_RECOMMENDATION_TOP = 0;

	public static final int TYPE_NORMAL = 1;

	public static final int TYPE_RECOMMENDATION_BOTTOM = 2;

	public static int getType(final int i, final int size) {
		if(i == 0) {
			return TYPE_RECOMMENDATION_TOP;
		}
		if(i == size - 1) {
			return TYPE_RECOMMENDATION_BOTTOM;
		}
		return TYPE_NORMAL;
	}

	private Data mParent;

	private Item mItem;

	private int mType;

	private boolean mIsGroup;

	public ItemData(Data parent, final Item item) {
		this(parent, item, TYPE_NORMAL);
	}

	public ItemData(Data parent, final Item item, int type) {
		mParent = parent;
		mItem = item;
		mType = type;
	}

	@Override
	public List<? extends Data> getChildren() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean isGroup() {
		return mIsGroup;
	}

	@Override
	public Data getParent() {
		return mParent;
	}

	@Override
	public Data getRoot() {
		if(mParent != null) {
			return mParent.getRoot();
		}
		return null;
	}

	@Override
	public void setIsGroup(final boolean value) {
		mIsGroup = value;
	}

	@Override
	public void add(final Data anchor, final Data newItem, final int indexIncrement) {
		// Do nothing
	}

	@Override
	public void remove(final Data item) {
		// Do nothing
	}

	@Override
	public int getLevel() {
		return 0;
	}

	public String getName() {
		return mItem.name();
	}

	public Item getItem() {
		return mItem;
	}

	public int getType() {
		return mType;
	}
}
