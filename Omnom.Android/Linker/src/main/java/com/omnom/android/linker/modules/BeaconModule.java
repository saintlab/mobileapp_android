package com.omnom.android.linker.modules;

import android.content.Context;

import com.omnom.android.linker.beacon.BeaconFilter;
import com.omnom.android.linker.beacon.BeaconFilterAlgorithm;
import com.omnom.android.linker.beacon.BeaconFilterAlgorithmSimple;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xCh3Dx on 17.09.2014.
 */
@Module(injects = {BeaconFilter.class})
public class BeaconModule {
	private Context mContext;

	public BeaconModule(Context context) {
		mContext = context;
	}

	@Provides
	@Singleton
	BeaconFilterAlgorithm provideFilterAlgorithm() {
		return new BeaconFilterAlgorithmSimple(mContext);
	}

}
