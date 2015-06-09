package com.omnom.android.restaurateur.api;

import com.omnom.android.restaurateur.model.config.Config;

import retrofit.http.GET;
import retrofit.http.Headers;
import rx.Observable;

public interface ConfigDataService {
	@GET("/mobile/config")
	@Headers("X-Authentication-Token: uv5zoaRsh9uff1yiSh8Dub4oc0hum3")
	Observable<Config> getConfig();
}
