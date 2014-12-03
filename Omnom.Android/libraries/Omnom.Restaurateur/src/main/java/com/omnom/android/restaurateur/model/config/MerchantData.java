package com.omnom.android.restaurateur.model.config;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class MerchantData {
	@Expose
	private String merchId;

	@Expose
	private String vtermId;

	public MerchantData(String merchId, String vtermId) {
		this.merchId = merchId;
		this.vtermId = vtermId;
	}

	public String getMerchId() {
		return merchId;
	}

	public String getVtermId() {
		return vtermId;
	}

	public void toMap(HashMap<String, String> reqiredSignatureParams) {
		reqiredSignatureParams.put("merch_id", getMerchId());
		reqiredSignatureParams.put("vterm_id", getVtermId());
	}
}
