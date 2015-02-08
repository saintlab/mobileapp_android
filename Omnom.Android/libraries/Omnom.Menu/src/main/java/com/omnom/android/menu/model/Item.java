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
public abstract class Item implements Parcelable {
	@Nullable
	public abstract String id();

	@Nullable
	public abstract String name();

	@Nullable
	public abstract String description();

	@Nullable
	public abstract String photo();

	@Nullable
	public abstract List<Modifier> modifiers();

	@Nullable
	public abstract long price();

	@Nullable
	public abstract Details details();

	@Nullable
	public abstract List<String> recommendations();
}
