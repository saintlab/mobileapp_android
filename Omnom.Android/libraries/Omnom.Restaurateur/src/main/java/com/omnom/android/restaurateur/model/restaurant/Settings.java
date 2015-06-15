package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcelable;

import com.omnom.android.utils.generation.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Created by mvpotter on 12/30/2014.
 */
@AutoParcel
@AutoGson
public abstract class Settings implements Parcelable {
	public abstract boolean hasWaiterCall();

	public abstract boolean hasPromo();

	public abstract boolean hasMenu();

	public abstract boolean hasBarTips();
}
