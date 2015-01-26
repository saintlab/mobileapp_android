package com.omnom.android.menu.model;

import com.omnom.android.utils.generation.AutoGson;

import java.util.ArrayList;
import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Items {

	public static Items create() {
		return new AutoParcel_Items(new ArrayList<Item>());
	}

	public abstract List<Item> items();
}
