package com.omnom.android.modules;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.auth.AuthService;
import com.omnom.android.interceptors.mixpanel.WicketMixpanelAuthenticator;

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
	private MixpanelAPI mMixpanelAPI;

	public AuthMixpanelModule(final Context context, final int endpointId, MixpanelAPI mixpanelAPI) {
		if(context == null || endpointId <= 0 || mixpanelAPI == null) {
			throw new RuntimeException();
		}
		mContext = context;
		mEndpointId = endpointId;
		mMixpanelAPI = mixpanelAPI;
	}

	@Provides
	@Singleton
	AuthService providerAuthenticator() {
		return new WicketMixpanelAuthenticator(mContext, mContext.getString(mEndpointId), mMixpanelAPI);
	}
}
