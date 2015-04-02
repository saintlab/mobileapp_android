package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	public List<UserOrderData> getSelectedItems() {
		ArrayList<UserOrderData> result = new ArrayList<UserOrderData>();
		final Set<Map.Entry<String, UserOrderData>> entries = itemsTable().entrySet();
		for(Map.Entry<String, UserOrderData> entry : entries) {
			final UserOrderData value = entry.getValue();
			if(value != null && value.amount() > 0) {
				result.add(value);
			}
		}
		return result;
	}

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
				final double amount = value.item().price() * value.amount();
				result = result.add(BigDecimal.valueOf(amount));
			}
		}
		return result;
	}

	public void addItem(final Item item, int count) {
		if(item == Item.NULL || item == null) {
			// skip
		}
		itemsTable().put(item.id(), UserOrderData.create(count, item));
	}

	public void updateData(final UserOrder resultOrder) {
		itemsTable().clear();
		for(UserOrderData orderData : resultOrder.getSelectedItems()) {
			addItem(orderData.item(), orderData.amount());
		}
	}
}
