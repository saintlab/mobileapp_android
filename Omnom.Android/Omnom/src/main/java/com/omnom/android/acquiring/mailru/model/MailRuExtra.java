package com.omnom.android.acquiring.mailru.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.omnom.android.acquiring.ExtraData;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class MailRuExtra extends ExtraData {
	public static MailRuExtra create(long tip, String restaurant_id) {
		return new MailRuExtra(tip, restaurant_id);
	}

	private long tip;
	private String mailRestaurantId;

	private MailRuExtra(long tip, String restaurant_id) {
		this.tip = tip;
		this.mailRestaurantId = restaurant_id;
	}

	public long getTip() {
		return tip;
	}

	public String getRestaurantId() {
		return mailRestaurantId;
	}

	public String getExtra(Gson gson) {
		if(TextUtils.isEmpty(mailRestaurantId)) {
			return StringUtils.EMPTY_STRING;
		}
		return gson.toJson(this, MailRuExtra.class);
	}
}
