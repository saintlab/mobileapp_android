package com.omnom.android.entrance;

import android.os.Parcelable;

public interface EntranceData extends Parcelable {

	public static final int TYPE_BAR = 0;

	public static final int TYPE_DELIVERY = 1;

	public static final int TYPE_TABLE = 2;

	public static final int TYPE_TAKEAWAY = 3;

	public int getType();
}
