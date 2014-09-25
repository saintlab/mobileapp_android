package com.omnom.android.acquiring.mailru.model;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.omnom.android.R;

import java.util.HashMap;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class MerchantData {
	@Expose
	private String merchId;

	@Expose
	private String vtermId;

	public MerchantData(Context context) {
		merchId = context.getString(R.string.acquiring_mailru_merch_id);
		vtermId = context.getString(R.string.acquiring_mailru_vterm_id);
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
