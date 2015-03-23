package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 05.03.2015.
 */
@AutoParcel
@AutoGson
public abstract class WishResponse implements Parcelable {

	@Nullable
	public abstract String restaurantId();

	@Nullable
	public abstract String code();

	@Nullable
	public abstract String internalTableId();

	@Nullable
	public abstract boolean isReady();

	@Nullable
	public abstract String id();

	@Nullable
	public abstract List<WishResponseItem> items();

}
