package com.omnom.android.linker.model;

import java.util.Random;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class PlaceFactory {
	public static Place create(String json) {
		// TODO: Use Gson
		return null;
	}

	public static Place create(String id, String name, String info, int rating) {
		return new Place(id, name, info, rating);
	}

	public static Place createFake(String postfix) {
		return new Place("id " + postfix, "Name " + postfix, "Info " + postfix, new Random().nextInt(5));
	}
}
