package com.omnom.android.menu.model;

import com.omnom.android.utils.generation.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Item {
	public abstract String id();

	public abstract String name();

	public abstract String description();

	public abstract String photo();

	public abstract long price();
}
