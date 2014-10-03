package com.omnom.android.modules;

import android.content.Context;

import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.AcquiringMailRu;
import com.omnom.android.acquiring.mailru.AcquiringServiceMailRu;
import com.omnom.android.activity.MainActivity;
import com.omnom.android.interceptors.mixpanel.AcquiringMailRuMixpanel;
import com.omnom.android.MixPanelHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 24.09.2014.
 */
@Module(injects = {MainActivity.class}, complete = false, library = true)
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
		return new AcquiringMailRu(mContext);
	}

	@Provides
	@Singleton
	public AcquiringServiceMailRu provideAcquiringService() {
		return new AcquiringMailRuMixpanel(mContext, mHelper);
	}
}
