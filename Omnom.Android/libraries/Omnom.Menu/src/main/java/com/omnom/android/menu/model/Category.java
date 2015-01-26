package com.omnom.android.menu.model;

import com.omnom.android.utils.generation.AutoGson;

import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Category {
	public abstract int id();

	public abstract int parentId();

	public abstract String name();

	public abstract String description();

	public abstract int sort();

	public abstract Schedule schedule();

	public abstract List<Child> children();
}
