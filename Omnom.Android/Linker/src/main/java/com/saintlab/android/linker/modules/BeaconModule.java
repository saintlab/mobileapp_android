package com.saintlab.android.linker.modules;

import com.omnom.android.beacon.BeaconFilter;
import com.omnom.android.beacon.BeaconFilterAlgorithm;
import com.omnom.android.beacon.BeaconFilterAlgorithmSimple;
import com.omnom.android.beacon.BeaconRssiProvider;
import com.omnom.android.beacon.BeaconRssiProviderSimple;
import com.omnom.android.utils.BaseOmnomApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xCh3Dx on 17.09.2014.
 */
@Module(injects = {BeaconFilter.class}, library = true)
public class BeaconModule {
	private BaseOmnomApplication mApp;

	public BeaconModule(BaseOmnomApplication app) {
		mApp = app;
	}

	@Provides
	@Singleton
	BeaconRssiProvider provideBeaconHelper() {
		return new BeaconRssiProviderSimple(mApp);
	}

	@Provides
	@Singleton
	BeaconFilterAlgorithm provideFilterAlgorithm() {
		return new BeaconFilterAlgorithmSimple(mApp);
	}

}
