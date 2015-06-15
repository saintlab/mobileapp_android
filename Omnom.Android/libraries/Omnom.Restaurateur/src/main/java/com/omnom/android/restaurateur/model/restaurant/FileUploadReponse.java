package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 29.05.2015.
 */
@AutoParcel
@AutoGson
public abstract class FileUploadReponse implements Parcelable {

	@Nullable
	public abstract String resourceType();

	@Nullable
	public abstract String url();

	@Nullable
	public abstract String secureUrl();
}
