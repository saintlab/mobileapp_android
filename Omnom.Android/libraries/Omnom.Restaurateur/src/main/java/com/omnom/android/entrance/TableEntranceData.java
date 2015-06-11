package com.omnom.android.entrance;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class TableEntranceData implements EntranceData {

	public static TableEntranceData create() {
		return new AutoParcel_TableEntranceData();
	}

	@Override
	public int getType() {
		return TYPE_TABLE;
	}
}
