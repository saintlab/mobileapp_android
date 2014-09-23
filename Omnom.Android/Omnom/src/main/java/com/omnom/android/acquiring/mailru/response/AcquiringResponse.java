package com.omnom.android.acquiring.mailru.response;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class AcquiringResponse {
	@Expose
	private String url;

	@Expose
	private AcquiringResponseError error;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public AcquiringResponseError getError() {
		return error;
	}

	public void setError(AcquiringResponseError error) {
		this.error = error;
	}
}
