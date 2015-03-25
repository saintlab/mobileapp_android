package com.omnom.android.fragment.dinner;

import android.os.Parcelable;

import com.omnom.android.utils.generation.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 25.03.2015.
 */
@AutoParcel
@AutoGson
public abstract class AddressData implements Parcelable {

	public static AddressData create(final String name, final String address) {
		return new AutoParcel_AddressData(name, address);
	}

	public abstract String name();

	public abstract String address();
}