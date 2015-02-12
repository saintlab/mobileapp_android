package com.omnom.android.retrofit;

import retrofit.Endpoint;

/**
 * Created by mvpotter on 2/11/2015.
 */
public class DynamicEndpoint implements Endpoint {

	private String url;

	public DynamicEndpoint(String url) {
		this.url = url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		if (url == null) {
			throw new IllegalStateException("URL is not set.");
		}
		return url;
	}

	@Override
	public String getName() {
		return "default";
	}
}
