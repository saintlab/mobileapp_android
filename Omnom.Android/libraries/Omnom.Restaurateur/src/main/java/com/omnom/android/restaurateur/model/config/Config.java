package com.omnom.android.restaurateur.model.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.restaurateur.model.ResponseBase;

/**
 * Created by Ch3D on 14.10.2014.
 */
public class Config extends ResponseBase {

	@Expose
	@SerializedName("mail_ru")
	private AcquiringData acquiringData;

	@Expose
	private TokensHolder tokens;

	@Expose
	private UuidsHolder uuid;

	public AcquiringData getAcquiringData() {
		return acquiringData;
	}

	public TokensHolder getTokens() {
		return tokens;
	}

	public UuidsHolder getUuid() {
		return uuid;
	}

}
