package com.omnom.android.modules;

import android.content.Context;

import com.omnom.android.auth.AuthService;
import com.omnom.android.interceptors.mixpanel.WicketMixpanelAuthenticator;
import com.omnom.android.mixpanel.MixPanelHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 03.10.2014.
 */
@Module(complete = false, library = true)
public class AuthMixpanelModule {
	private Context mContext;
	private int mEndpointId;
	private MixPanelHelper mHelper;

	public AuthMixpanelModule(final Context context, final int endpointId, MixPanelHelper helper) {
		if(context == null || endpointId <= 0 || helper == null) {
			throw new RuntimeException();
		}
		mContext = context;
		mEndpointId = endpointId;
		mHelper = helper;
	}

	@Provides
	@Singleton
	AuthService providerAuthenticator() {
		return new WicketMixpanelAuthenticator(mContext, mContext.getString(mEndpointId), mHelper);
	}
}
