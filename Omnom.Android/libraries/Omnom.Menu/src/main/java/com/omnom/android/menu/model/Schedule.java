package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Schedule implements Parcelable {
	@Nullable
	public abstract boolean forever();
}
