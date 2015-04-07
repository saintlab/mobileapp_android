package com.omnom.android.entrance;

import android.support.annotation.Nullable;

import java.util.Date;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class DeliveryEntranceData implements EntranceData {

	public static DeliveryEntranceData create(final Date orderTime, final String deliveryAddress, final Date deliveryTime) {
		return new AutoParcel_DeliveryEntranceData(orderTime, deliveryAddress, deliveryTime);
	}

	@Nullable
	public abstract Date orderTime();

	@Nullable
	public abstract String deliveryAddress();

	@Nullable
	public abstract Date deliveryTime();

}
