package com.omnom.android.entrance;

import android.support.annotation.Nullable;

import com.omnom.android.utils.utils.StringUtils;

import java.util.Date;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class TakeawayEntranceData implements EntranceData {

	public static TakeawayEntranceData create() {
		return new AutoParcel_TakeawayEntranceData(new Date(), StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING);
	}

	public static TakeawayEntranceData create(final Date orderTime, final String takeawayAddress, final String takeawayAfter) {
		return new AutoParcel_TakeawayEntranceData(orderTime, takeawayAddress, takeawayAfter);
	}

	@Nullable
	public abstract Date orderTime();

	@Nullable
	public abstract String takeawayAddress();

	@Nullable
	public abstract String takeawayAfter();

}
