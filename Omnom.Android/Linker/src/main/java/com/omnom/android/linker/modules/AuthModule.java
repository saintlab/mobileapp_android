package com.omnom.android.linker.modules;

import android.content.Context;

import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.WicketAuthenticator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 28.09.2014.
 */
@Module(complete = false, library = true)
public class AuthModule {
	private Context mContext;
	private int mEndpointId;

	public AuthModule(final Context context, final int endpointId) {
		mContext = context;
		mEndpointId = endpointId;
	}

	@Provides
	@Singleton
	AuthService providerAuthenticator() {
		return new WicketAuthenticator(mContext, mContext.getString(mEndpointId));
	}
}
