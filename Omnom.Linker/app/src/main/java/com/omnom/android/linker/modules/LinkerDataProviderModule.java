package com.omnom.android.linker.modules;

import com.omnom.android.linker.activity.LoginActivity;
import com.omnom.android.linker.api.LinkerApi;
import com.omnom.android.linker.providers.LinkerDataProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {LoginActivity.class}, complete = false, library = true)
public class LinkerDataProviderModule {
	@Provides
	@Singleton
	LinkerApi providerLinkerApi() {
		return new LinkerDataProvider();
	}
}
