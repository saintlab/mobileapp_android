package com.omnom.android.acquiring.mailru.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.omnom.android.acquiring.ExtraData;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class MailRuExtra extends ExtraData {
	public static MailRuExtra create(double tip, String restaurant_id) {
		return new MailRuExtra(tip, restaurant_id);
	}

	private double tip;
	private String restaurantId;

	private MailRuExtra(double tip, String restaurant_id) {
		this.tip = tip;
		this.restaurantId = restaurant_id;
	}

	public double getTip() {
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
