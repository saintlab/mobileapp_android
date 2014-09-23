package com.omnom.android.acquiring.mailru;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.omnom.android.linker.utils.StringUtils;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class MailRuExtra {
	private long tip;
	private String restaurant_id;

	public MailRuExtra(long tip, String restaurant_id) {
		this.tip = tip;
		this.restaurant_id = restaurant_id;
	}

	public String getExtra(Gson gson) {
		if(TextUtils.isEmpty(restaurant_id)) {
			return StringUtils.EMPTY_STRING;
		}
		// TODO:
		//		GsonBuilder builder = new GsonBuilder();
		//		builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		//		Gson gson = builder.create();
		return gson.toJson(this, MailRuExtra.class);
	}
}
