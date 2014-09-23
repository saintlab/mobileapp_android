package com.omnom.android.acquiring.mailru;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class ThreedsData {

	@SerializedName("MD")
	@Expose
	private String mD;

	@SerializedName("TermUrl")
	@Expose
	private String termUrl;

	@SerializedName("PaReq")
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
