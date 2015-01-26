package com.omnom.android.menu;

import android.content.Context;

import com.omnom.android.menu.api.observable.MenuDataProvider;
import com.omnom.android.menu.api.observable.MenuObservableApi;
import com.omnom.android.utils.AuthTokenProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 26.01.2015.
 */
@Module(complete = false, library = true)
public class MenuModule {

	private final AuthTokenProvider tokenProvider;

	private final int mEndpointResId;

	private final Context mContext;

	public MenuModule(final AuthTokenProvider tokenProvider, final int endpointResId) {
		this.tokenProvider = tokenProvider;
		this.mEndpointResId = endpointResId;
		this.mContext = tokenProvider.getContext();
	}

	@Provides
	@Singleton
	MenuObservableApi providerLinkerApi() {
		return MenuDataProvider.create(mContext.getString(mEndpointResId));
	}
}
