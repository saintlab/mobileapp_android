package com.omnom.android.acquiring.mailru.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.acquiring.mailru.model.ThreedsData;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class AcquiringResponse extends AcquiringPollingResponse {
	public static final String STATUS_SUCCESS = "success";

	public static final String STATUS_FAILED = "failed";

	@Expose
	@SerializedName("acs_url")
	private String acsUrl;

	@Expose
	@SerializedName("threeds_data")
	private ThreedsData threedsData;

	@Override
	public String toString() {
		return "AcquiringResponse{" +
				"acsUrl='" + acsUrl + '\'' +
				", url='" + getUrl() + '\'' +
				", threedsData=" + threedsData +
				", error=" + getError() +
				'}';
	}

	public String getAcsUrl() {
		return acsUrl;
	}

	public void setAcsUrl(String acsUrl) {
		this.acsUrl = acsUrl;
	}

	public ThreedsData getThreedsData() {
		return threedsData;
	}

	public void setThreedsData(ThreedsData threedsData) {
		this.threedsData = threedsData;
	}
}
