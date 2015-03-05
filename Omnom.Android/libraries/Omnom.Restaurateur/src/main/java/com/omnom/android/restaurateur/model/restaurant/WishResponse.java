package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.bill.BillResponse;

import java.util.List;

/**
 * Created by Ch3D on 05.03.2015.
 */
public class WishResponse extends BillResponse {

	@Expose
	// TODO: items in stop-list
	private List<String> items;

	public List<String> getItems() {
		return items;
	}

	public void setItems(final List<String> items) {
		this.items = items;
	}
}
