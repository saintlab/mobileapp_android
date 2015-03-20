package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;

import java.util.List;

/**
 * Created by Ch3D on 05.03.2015.
 */
public class WishResponse extends ResponseBase {

	@Expose
	private String restaurantId;

	@Expose
	private String code;

	@Expose
	private String internalTableId;

	@Expose
	private boolean isReady;

	@Expose
	private String id;

	// TODO: items in stop-list
	private List<WishResponseItem> items;

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(final String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getInternalTableId() {
		return internalTableId;
	}

	public void setInternalTableId(final String internalTableId) {
		this.internalTableId = internalTableId;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(final boolean isReady) {
		this.isReady = isReady;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public List<WishResponseItem> getItems() {
		return items;
	}

	public void setItems(final List<WishResponseItem> items) {
		this.items = items;
	}
}
