package com.omnom.android.linker.model.auth;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 04.09.2014.
 */
public class Error {
	@Expose
	private int code;

	@Expose
	private String message;

	public Error(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
