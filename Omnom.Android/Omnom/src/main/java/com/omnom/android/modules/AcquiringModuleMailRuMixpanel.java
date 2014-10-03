package com.omnom.android.modules;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.MainActivity;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.AcquiringMailRu;
import com.omnom.android.acquiring.mailru.AcquiringServiceMailRu;
import com.omnom.android.interceptors.mixpanel.AcquiringMailRuMixpanel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 24.09.2014.
 */
@Module(injects = {MainActivity.class}, complete = false, library = true)
public class AcquiringModuleMailRuMixpanel {
	private Context mContext;
	private MixpanelAPI mMixpanelAPI;

	public AcquiringModuleMailRuMixpanel(final Context context, MixpanelAPI mixpanelAPI) {
		mContext = context;
		mMixpanelAPI = mixpanelAPI;
	}

	@Provides
	@Singleton
	public Acquiring provideAcquiring() {
		return new AcquiringMailRu(mContext);
	}

	@Provides
	@Singleton
	public AcquiringServiceMailRu provideAcquiringService() {
		return new AcquiringMailRuMixpanel(mContext, mMixpanelAPI);
	}
}
