package com.omnom.android.acquiring.mailru.response;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class AcquiringResponseError {

	public static AcquiringResponseError create(final String code, String descr) {
		final AcquiringResponseError response = new AcquiringResponseError();
		response.setCode(code);
		response.setDescr(descr);
		return response;
	}

	@Expose
	private String code;

	@Expose
	private String descr;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	@Override
	public String toString() {
		return "code: " + code + " descr: " + descr;
	}
}
