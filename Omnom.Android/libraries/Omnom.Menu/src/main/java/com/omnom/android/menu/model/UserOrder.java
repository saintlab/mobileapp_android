package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 29.01.2015.
 */
@AutoGson
@AutoParcel
public abstract class UserOrder implements Parcelable {

	public static UserOrder create() {
		return new AutoParcel_UserOrder(new HashMap<String, UserOrderData>());
	}

	@Nullable
	public abstract Map<String, UserOrderData> itemsTable();

	public boolean contains(final Item item) {
		if(itemsTable() == null) {
			return false;
		}
		final UserOrderData data = itemsTable().get(item.id());
		return data != null && data.amount() > 0;
	}

	public BigDecimal getTotalPrice() {
		BigDecimal result = BigDecimal.ZERO;
		final Set<Map.Entry<String, UserOrderData>> entries = itemsTable().entrySet();
		for(Map.Entry<String, UserOrderData> entry : entries) {
			final UserOrderData value = entry.getValue();
			if(value != null && value.item() != null) {
				final long amount = value.item().price() * value.amount();
				result = result.add(BigDecimal.valueOf(amount));
			}
		}
		return result;
	}
}
