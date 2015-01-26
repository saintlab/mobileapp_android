package com.omnom.android.menu.model;

import com.omnom.android.utils.generation.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Child {
	public abstract int id();

	public abstract int parentId();

	public abstract String name();

	public abstract String description();

	public abstract int sort();

	public abstract Schedule schedule();
}
