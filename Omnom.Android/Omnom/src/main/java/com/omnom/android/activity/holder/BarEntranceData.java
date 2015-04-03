package com.omnom.android.activity.holder;

import android.support.annotation.Nullable;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class BarEntranceData implements EntranceData {

	public static BarEntranceData create(final String orderNumber, final String pinCode) {
		return new AutoParcel_BarEntranceData(orderNumber, pinCode);
	}

	@Nullable
	public abstract String orderNumber();

	@Nullable
	public abstract String pinCode();

}
