package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import java.util.HashMap;
import java.util.Map;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Items implements Parcelable {

	public static Items create() {
		return new AutoParcel_Items(new HashMap<String, Item>());
	}

	@Nullable
	public abstract Map<String, Item> items();
}
