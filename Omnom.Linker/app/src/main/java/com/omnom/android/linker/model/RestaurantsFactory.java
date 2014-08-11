package com.omnom.android.linker.model;

import android.location.Location;

import java.util.Random;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class RestaurantsFactory {
	public static Restaurant create(String json) {
		// TODO: Use Gson
		return null;
	}

	public static Restaurant create(String id, String name, Location location, String locationInfo, int rating) {
		return new Restaurant(id, name, location, locationInfo, rating);
	}

	public static Restaurant createFake(String postfix) {
		return new Restaurant("id " + postfix, "Name " + postfix, new Location(""), "Info " + postfix, new Random().nextInt(5));
	}
}
