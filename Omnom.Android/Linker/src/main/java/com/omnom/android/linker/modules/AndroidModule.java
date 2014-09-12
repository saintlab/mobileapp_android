package com.omnom.android.linker.modules;

import android.app.SearchManager;
import android.location.LocationManager;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

import com.omnom.android.linker.LinkerApplication;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.POWER_SERVICE;
import static android.content.Context.SEARCH_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

@Module(library = true)
public class AndroidModule {
	private final LinkerApplication application;

	public AndroidModule(LinkerApplication app) {
		application = app;
	}

	@Provides
	@Singleton
	LinkerApplication provideApplicationContext() {
		return application;
	}

	@Provides
	@Singleton
	Bus provideEventBus() {
		return new Bus(ThreadEnforcer.ANY);
	}

	@Provides
	@Singleton
	LocationManager provideLocationManager() {
		return (LocationManager) application.getSystemService(LOCATION_SERVICE);
	}

	@Provides
	@Singleton
	PowerManager providePowerManager() {
		return (PowerManager) application.getSystemService(POWER_SERVICE);
	}

	@Provides
	@Singleton
	SearchManager provideSearchManager() {
		return (SearchManager) application.getSystemService(SEARCH_SERVICE);
	}

	@Provides
	@Singleton
	TelephonyManager provideTelephonyManager() {
		return (TelephonyManager) application.getSystemService(TELEPHONY_SERVICE);
	}
}
