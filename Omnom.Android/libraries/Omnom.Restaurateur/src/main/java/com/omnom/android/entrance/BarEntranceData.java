package com.omnom.android.entrance;

import android.support.annotation.Nullable;

import com.omnom.android.restaurateur.model.restaurant.WishResponse;
import com.omnom.android.utils.utils.StringUtils;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class BarEntranceData implements EntranceData {

	public static BarEntranceData create(final String orderNumber, final String pinCode) {
		return new AutoParcel_BarEntranceData(orderNumber, pinCode);
	}

	public static BarEntranceData create() {
		return new AutoParcel_BarEntranceData(StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING);
	}

	public static BarEntranceData create(WishResponse response) {
		return new AutoParcel_BarEntranceData(response.internalTableId(), response.code());
	}

	@Override
	public int getType() {
		return TYPE_BAR;
	}

	@Nullable
	public abstract String orderNumber();

	@Nullable
	public abstract String pinCode();

}
