package com.omnom.android.acquiring.mailru.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.acquiring.mailru.ThreedsData;
import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class AcquiringTransactionResponse {
	private String url;
	@SerializedName("threeds_data")

	@Expose
	private ThreedsData threedsData;

	@Expose
	private String status;

	@Expose
	private AcquiringResponseError error;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ThreedsData getThreedsData() {
		return threedsData;
	}

	public void setThreedsData(ThreedsData threedsData) {
		this.threedsData = threedsData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AcquiringResponseError getError() {
		return error;
	}

	public void setError(AcquiringResponseError error) {
		this.error = error;
	}
}
