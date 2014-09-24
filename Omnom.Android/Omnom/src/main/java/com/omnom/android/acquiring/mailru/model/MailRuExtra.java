package com.omnom.android.acquiring.mailru.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.omnom.android.linker.utils.StringUtils;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class MailRuExtra {
	public static MailRuExtra create(long tip, String restaurant_id) {
		return new MailRuExtra(tip, restaurant_id);
	}

	private long tip;
	private String restaurantId;

	private MailRuExtra(long tip, String restaurant_id) {
		this.tip = tip;
		this.restaurantId = restaurant_id;
	}

	public long getTip() {
		return tip;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getExtra(Gson gson) {
		if(TextUtils.isEmpty(restaurantId)) {
			return StringUtils.EMPTY_STRING;
		}
		return gson.toJson(this, MailRuExtra.class);
	}
}
