package com.omnom.android.acquiring.mailru.response;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class AcquiringPollingResponse {
	@Expose
	private String status;

	@Expose
	private String url;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "AcquiringPollingResponse{" +
				"status='" + status + '\'' +
				", url='" + url + '\'' +
				'}';
	}
}
