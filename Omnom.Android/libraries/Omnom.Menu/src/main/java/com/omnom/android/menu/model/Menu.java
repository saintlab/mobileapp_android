package com.omnom.android.menu.model;

import com.omnom.android.utils.generation.AutoGson;

import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Menu {
	public abstract String restaurantId();

	public abstract Items items();

	public abstract Modifiers modifiers();

	public abstract List<Category> categories();
}
