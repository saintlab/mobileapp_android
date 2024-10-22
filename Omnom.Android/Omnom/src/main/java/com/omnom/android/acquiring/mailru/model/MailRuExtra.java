package com.omnom.android.acquiring.mailru.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.omnom.android.acquiring.ExtraData;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class MailRuExtra extends ExtraData {
	public static final String PAYMENT_TYPE_ORDER = "order";

	public static final String PAYMENT_TYPE_WISH = "wish";

	public static final String PAYMENT_TYPE_CARD = "card";

	public static MailRuExtra create(int tip, String restaurant_id, String type) {
		return new MailRuExtra(tip, restaurant_id, type);
	}

	private int tip;

	private String restaurantId;

	private String type;

	private MailRuExtra(int tip, String restaurant_id, String type) {
		if(!PAYMENT_TYPE_ORDER.equals(type) && !PAYMENT_TYPE_WISH.equals(type) && !PAYMENT_TYPE_CARD.equals(type)) {
			throw new IllegalArgumentException("Wrong type argument : " + type);
		}
		this.tip = tip;
		this.restaurantId = restaurant_id;
		this.type = type;
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

	public String getType() {
		return type;
	}
}
