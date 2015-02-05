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
public abstract class Modifiers implements Parcelable {
	public static Modifiers create() {
		return new AutoParcel_Modifiers(new HashMap<String, Modifier>());
	}

	@Nullable
	public abstract Map<String, Modifier> items();
}
