package com.omnom.android.linker.modules;

import android.text.TextUtils;

import com.omnom.android.linker.activity.LoginActivity;
import com.omnom.android.linker.api.Protocol;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.api.observable.providers.LinkerDataProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {LoginActivity.class}, complete = false, library = true)
public class LinkerDataProviderModule {
	public static final String ENDPOINT_LAAAAB = "http://restaurateur.laaaab.com";
	public static final String ENDPOINT_STAND = "http://restaurateur.stand.saintlab.com/";
	public static final String ENDPOINT_AUTH = "http://wicket.laaaab.com";

	private AuthTokenProvider tokenProvider;

	public interface AuthTokenProvider {
		public String getAuthToken();
	}

	public LinkerDataProviderModule(final AuthTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Provides
	@Singleton
	LinkerObeservableApi providerLinkerApi() {
		return LinkerDataProvider.create(ENDPOINT_STAND, ENDPOINT_AUTH, new RequestInterceptor() {
			@Override
			public void intercept(RequestFacade request) {
				final String token = tokenProvider.getAuthToken();
				if(!TextUtils.isEmpty(token)) {
					request.addHeader(Protocol.HEADER_AUTH_TOKEN, token);
				}
			}
		});
	}
}
