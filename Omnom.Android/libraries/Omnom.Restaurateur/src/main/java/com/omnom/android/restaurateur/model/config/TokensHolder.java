package com.omnom.android.restaurateur.model.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mvpotter on 12/3/2014.
 */
public class TokensHolder {

	@Expose
	@SerializedName("CardIOAppToken")
	private String cardIOAppToken;

	@Expose
	@SerializedName("MixpanelToken")
	private String mixpanelToken;

	@Expose
	@SerializedName("MixpanelTokenDebug")
	private String mixpanelTokenDebug;

	@Expose
	@SerializedName("MixpanelTokenAndroid")
	private String mixpanelTokenAndroid;

	public String getCardIOAppToken() {
		return cardIOAppToken;
	}

	public String getMixpanelToken() {
		return mixpanelToken;
	}

	public String getMixpanelTokenDebug() {
		return mixpanelTokenDebug;
	}

	public String getMixpanelTokenAndroid() {
		return mixpanelTokenAndroid;
	}

}
