package com.omnom.android.linker.model.restaurant;

import com.omnom.android.linker.utils.StringUtils;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class RestaurantsFactory {
	public static Restaurant create(String json) {
		// TODO: Use Gson
		return null;
	}

	public static Restaurant create(String id, String title, String authCode, String descr, Decoration decoration) {
		return new Restaurant(id, title, authCode, descr, decoration);
	}

	public static Restaurant createFake(String postfix) {
		return new Restaurant("id " + postfix, "Title " + postfix, "Auth " + postfix, "Info " + postfix,
		                      new Decoration(StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING));
	}
}
