package com.omnom.android.activity.holder;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class TableEntranceData implements EntranceData {

	public static TableEntranceData create() {
		return new AutoParcel_TableEntranceData();
	}

}
