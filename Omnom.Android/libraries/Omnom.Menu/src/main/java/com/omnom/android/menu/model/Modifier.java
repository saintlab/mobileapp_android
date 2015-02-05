package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 05.02.2015.
 */
@AutoParcel
@AutoGson
public abstract class Modifier implements Parcelable {
	@Nullable
	public abstract String id();

	@Nullable
	public abstract String name();

	@Nullable
	public abstract String parent();

	@Nullable
	public abstract List<String> list();

	@Nullable
	public abstract String type();
}
