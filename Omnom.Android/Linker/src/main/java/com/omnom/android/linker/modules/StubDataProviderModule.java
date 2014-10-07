package com.omnom.android.linker.modules;

import com.omnom.android.linker.activity.LoginActivity;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.api.observable.providers.StubDataProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {LoginActivity.class}, complete = false, library = true)
public class StubDataProviderModule {
	@Provides
	@Singleton
	RestaurateurObeservableApi providerLinkerApi() {
		return new StubDataProvider();
	}
}
