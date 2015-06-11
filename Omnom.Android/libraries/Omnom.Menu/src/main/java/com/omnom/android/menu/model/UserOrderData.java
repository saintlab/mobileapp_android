package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import java.util.Collections;
import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 29.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class UserOrderData implements Parcelable {

	public static UserOrderData NULL = create(0, Item.NULL, Collections.EMPTY_LIST);

	public static UserOrderData create(int amount, Item item, final List<String> selectedModifiersIds) {
		return new AutoParcel_UserOrderData(amount, selectedModifiersIds, item);
	}

	@Nullable
	public abstract int amount();

	/**
	 * @return ids of modifiers selected by a user for this item
	 */
	@Nullable
	public abstract List<String> modifiers();

	@Nullable
	public abstract Item item();
}
