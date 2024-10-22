package com.omnom.android.modules;

import android.content.Context;

import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.demo.DemoAcquiring;
import com.omnom.android.acquiring.mailru.AcquiringMailRu;
import com.omnom.android.acquiring.mailru.AcquiringServiceMailRu;
import com.omnom.android.interceptors.mixpanel.AcquiringMailRuMixpanel;
import com.omnom.android.mixpanel.MixPanelHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 24.09.2014.
 */
@Module(complete = false, library = true)
public class AcquiringModuleMailRuMixpanel {
	private Context mContext;

	private MixPanelHelper mHelper;

	public AcquiringModuleMailRuMixpanel(final Context context, MixPanelHelper helper) {
		mContext = context;
		mHelper = helper;
	}

	@Provides
	@Singleton
	public Acquiring provideAcquiring() {
		return new AcquiringMailRu(new AcquiringMailRuMixpanel(mContext, mHelper));
	}

	@Provides
	@Singleton
	public DemoAcquiring provideDemoAcquiring() {
		return new DemoAcquiring(mContext);
	}

	@Provides
	@Singleton
	public AcquiringServiceMailRu provideAcquiringService() {
		return new AcquiringMailRuMixpanel(mContext, mHelper);
	}
}
