package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 20.03.2015.
 */
@AutoParcel
@AutoGson
public abstract class WishResponseItem implements Parcelable {
	@Nullable
	public abstract int quantity();

	@Nullable
	public abstract int priceTotal();

	@Nullable
	public abstract int pricePerItem();

	@Nullable
	public abstract String internalId();

	@Nullable
	public abstract String title();

	@Nullable
	public abstract Boolean isModifier();

	@Nullable
	public abstract String id();
}
