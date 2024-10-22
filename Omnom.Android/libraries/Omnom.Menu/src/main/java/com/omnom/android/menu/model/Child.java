package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
@Deprecated
public abstract class Child implements Parcelable {
	@Nullable
	public abstract int id();

	@Nullable
	public abstract int parentId();

	@Nullable
	public abstract String name();

	@Nullable
	public abstract String description();

	@Nullable
	public abstract int sort();

	@Nullable
	public abstract Schedule schedule();

	@Nullable
	public abstract List<String> items();
}
