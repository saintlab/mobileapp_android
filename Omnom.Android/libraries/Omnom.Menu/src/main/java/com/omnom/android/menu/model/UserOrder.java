package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import java.util.Map;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 29.01.2015.
 */
@AutoGson
@AutoParcel
public abstract class UserOrder implements Parcelable {
	@Nullable
	public abstract Map<String, UserOrderData> itemsTable();
}
