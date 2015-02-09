package com.omnom.android.acquiring.mailru.response;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class AcquiringPollingResponse {
	/**
	 * Ok status for mail acquiring.
	 */
	public static final String STATUS_OK = "OK_FINISH";
	/**
	 * Error status for mail acquiring.
	 */
	public static final String STATUS_ERR = "ERR_FINISH";
	/**
	 * Result is not ready yet. Continue to check.
	 */
	public static final String STATUS_CONTINUE = "OK_CONTINUE";

	public static final String ERR_ARGUMENTS = "ERR_ARGUMENTS";

	@Expose
	private String status;

	@Expose
	private String url;

	@Expose
	private AcquiringResponseError error;

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

	public AcquiringResponseError getError() {
		return error;
	}

	public void setError(AcquiringResponseError error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "AcquiringPollingResponse{" +
				"status='" + status + '\'' +
				", url='" + url + '\'' +
				'}';
	}
}
