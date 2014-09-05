package com.omnom.android.linker.modules;

import android.content.Context;
import android.text.TextUtils;

import com.omnom.android.linker.R;
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
	public interface AuthTokenProvider {
		public String getAuthToken();
		public Context getContext();
	}

	private AuthTokenProvider tokenProvider;
	private Context mContext;

	public LinkerDataProviderModule(final AuthTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
		this.mContext = tokenProvider.getContext();
	}

	@Provides
	@Singleton
	LinkerObeservableApi providerLinkerApi() {
		return LinkerDataProvider.create(
				mContext.getString(R.string.config_data_endpoint),
				mContext.getString(R.string.config_auth_endpoint),
				new RequestInterceptor() {
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
