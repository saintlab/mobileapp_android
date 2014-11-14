package com.omnom.android.restaurateur.model.table;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;

/**
 * Created by Ch3D on 14.11.2014.
 */
public class DemoTableData {
	@Expose
	private Restaurant restaurant;

	@Expose
	private TableDataResponse table;

	/**
	 * @return The restaurant
	 */
	public Restaurant getRestaurant() {
		return restaurant;
	}

	/**
	 * @param restaurant The restaurant
	 */
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	/**
	 * @return The table
	 */
	public TableDataResponse getTable() {
		return table;
	}

	/**
	 * @param table The table
	 */
	public void setTable(TableDataResponse table) {
		this.table = table;
	}
}
