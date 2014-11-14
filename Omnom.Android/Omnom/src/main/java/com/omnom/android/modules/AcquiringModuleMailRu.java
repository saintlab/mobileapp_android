package com.omnom.android.modules;

import android.content.Context;

import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.demo.DemoAcquiring;
import com.omnom.android.acquiring.mailru.AcquiringMailRu;
import com.omnom.android.acquiring.mailru.AcquiringProxyMailRu;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 24.09.2014.
 */
@Module(complete = false, library = true)
public class AcquiringModuleMailRu {
	private Context mContext;

	public AcquiringModuleMailRu(final Context context) {
		mContext = context;
	}

	@Provides
	@Singleton
	public Acquiring provideAcquiring() {
		return new AcquiringMailRu(mContext, new AcquiringProxyMailRu(mContext));
	}

	@Provides
	@Singleton
	public DemoAcquiring provideDemoAcquiring() {
		return new DemoAcquiring(mContext);
	}
}
