package com.omnom.android.restaurateur.model.restaurant;

import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 08.04.2015.
 */
@AutoGson
@AutoParcel
public abstract class WishForbiddenResponse {
	@Nullable
	public abstract List<WishRequestItem> forbidden();

	@Override
	public String toString() {
		return "WishForbiddenResponse{" + forbidden() +"}";
	}
}
