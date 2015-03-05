package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 09.02.2015.
 */
public class WishRequest {

	public static WishRequest create(final TableDataResponse table) {
		final WishRequest request = new WishRequest();
		request.setInternalTableId(table.getInternalId());
		request.setRestaurantId(table.getRestaurantId());
		return request;
	}

	@Expose
	private int internalTableId;

	@Expose
	private String restaurantId;

	@Expose
	private List<WishRequestItem> items;

	public List<WishRequestItem> getItems() {
		return items;
	}

	public void setItems(final List<WishRequestItem> items) {
		this.items = items;
	}

	public void addItem(WishRequestItem item) {
		if(items == null) {
			items = new ArrayList<WishRequestItem>();
		}
		items.add(item);
	}

	public int getInternalTableId() {
		return internalTableId;
	}

	public void setInternalTableId(final int internalTableId) {
		this.internalTableId = internalTableId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(final String restaurantId) {
		this.restaurantId = restaurantId;
	}
}
