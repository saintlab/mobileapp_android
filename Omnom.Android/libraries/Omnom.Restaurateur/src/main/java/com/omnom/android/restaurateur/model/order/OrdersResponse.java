package com.omnom.android.restaurateur.model.order;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;

import java.util.List;

/**
 * Created by mvpotter on 12/8/2014.
 */
public class OrdersResponse extends ResponseBase {

	@Expose
	private List<Order> orders;

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

}
