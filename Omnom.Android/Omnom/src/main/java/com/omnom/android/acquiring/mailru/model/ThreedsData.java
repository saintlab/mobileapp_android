package com.omnom.android.acquiring.mailru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class ThreedsData {

	public static final String MD = "MD";

	public static final String PA_REQ = "PaReq";

	public static final String TERM_URL = "TermUrl";

	@SerializedName(MD)
	@Expose
	private String mD;

	@SerializedName(TERM_URL)
	@Expose
	private String termUrl;

	@SerializedName(PA_REQ)
	@Expose
	private String paReq;

	public String getMD() {
		return mD;
	}

	public void setMD(String mD) {
		this.mD = mD;
	}

	public String getTermUrl() {
		return termUrl;
	}

	public void setTermUrl(String termUrl) {
		this.termUrl = termUrl;
	}

	public String getPaReq() {
		return paReq;
	}

	public void setPaReq(String paReq) {
		this.paReq = paReq;
	}

}
