package com.omnom.android.restaurateur.model.decode;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;

import java.util.List;

/**
 * Created by Ch3D on 25.12.2014.
 */
public class RestaurantResponse extends ResponseBase {

	@Expose
	private List<Restaurant> restaurants;

	public List<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(final List<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	public boolean hasOnlyRestaurant() {
		return restaurants != null && restaurants.size() == 1;
	}

	public boolean hasTables() {
		return RestaurantHelper.hasTables(restaurants.get(0));
	}
}
