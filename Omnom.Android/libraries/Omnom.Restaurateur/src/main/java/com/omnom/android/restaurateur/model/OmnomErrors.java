package com.omnom.android.restaurateur.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 01.10.2014.
 */
public class OmnomErrors {
	@Expose
	private String authentication;
	@Expose
	private String common;

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public String getCommon() {
		return common;
	}

	public void setCommon(String common) {
		this.common = common;
	}

}
