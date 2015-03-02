package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 09.02.2015.
 */
public class WishRequest {
	@Expose
	private int internalTableId;

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
}
