package com.omnom.android.menu.model;

/**
 * Created by Ch3D on 02.02.2015.
 */
public final class MenuItemState {
	public static final MenuItemState NONE = new MenuItemState(-1);

	public static final MenuItemState ADDED = new MenuItemState(0);

	public static final MenuItemState ORDERED = new MenuItemState(1);

	private final int mId;

	private MenuItemState(int id) {
		mId = id;
	}

	public int getId() {
		return mId;
	}
}
