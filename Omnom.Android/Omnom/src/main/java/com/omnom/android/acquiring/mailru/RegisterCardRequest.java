package com.omnom.android.acquiring.mailru;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class RegisterCardRequest {
	@Expose
	private String merch_id = "DGIS";
	@Expose
	private String vterm_id = "DGISMobileDemo";
	@Expose
	private String user_login = "5";
	@Expose
	private String user_phone = "89833087335";
	@Expose
	private String signature;
	@Expose
	private String pan = "6011000000000004";
	@Expose
	private String exp_date = "12.2015";
	@Expose
	private String cvv = "123";
	@Expose
	private String cardholder = "Omnom";

	public String getMerch_id() {
		return merch_id;
	}

	public String getVterm_id() {
		return vterm_id;
	}

	public String getUser_login() {
		return user_login;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getPan() {
		return pan;
	}

	public String getExp_date() {
		return exp_date;
	}

	public String getCvv() {
		return cvv;
	}

	public String getCardholder() {
		return cardholder;
	}
}
