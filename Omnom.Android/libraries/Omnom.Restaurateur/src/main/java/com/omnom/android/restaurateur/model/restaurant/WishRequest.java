package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 09.02.2015.
 */
public class WishRequest {

	@Expose
	private List<WishRequestItem> items;

	@Expose
	private int time;

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

	public int getTime() {
		return time;
	}

	public void setTime(final int time) {
		this.time = time;
	}
}
