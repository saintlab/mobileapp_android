package com.omnom.android.restaurateur.model.decode;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import java.util.ArrayList;

/**
 * Created by Ch3D on 23.12.2014.
 */
public class BeaconDecodeResponse extends ResponseBase {
	@Expose
	private BeaconRecord beacon;

	@Expose
	private Restaurant restaurant;

	@Expose
	private TableDataResponse table;

	@Expose
	private ArrayList<Order> orders = new ArrayList<Order>();

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(final Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public TableDataResponse getTable() {
		return table;
	}

	public void setTable(final TableDataResponse table) {
		this.table = table;
	}

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public void setOrders(final ArrayList<Order> orders) {
		this.orders = orders;
	}

	public BeaconRecord getBeacon() {
		return beacon;
	}

	public void setBeacon(final BeaconRecord beacon) {
		this.beacon = beacon;
	}
}
