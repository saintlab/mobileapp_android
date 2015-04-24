package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 29.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class UserOrderData implements Parcelable {

	public static UserOrderData NULL = create(0, Item.NULL);

	public static UserOrderData create(int amount, Item item) {
		return new AutoParcel_UserOrderData(amount, item);
	}

	@Nullable
	public abstract int amount();

	@Nullable
	public abstract Item item();
}