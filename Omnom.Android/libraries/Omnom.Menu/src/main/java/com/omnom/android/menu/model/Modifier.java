package com.omnom.android.menu.model;

import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 05.02.2015.
 */
@AutoParcel
@AutoGson
public abstract class Modifier {
	@Nullable
	public abstract String id();

	@Nullable
	public abstract String name();

	@Nullable
	public abstract String parent();
}
