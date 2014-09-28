package com.omnom.android;

import android.content.Context;

import com.omnom.android.auth.AuthModule;
import com.omnom.android.modules.AcquiringModuleMailRu;
import com.omnom.android.modules.AndroidModule;
import com.omnom.android.modules.ApplicationModule;
import com.omnom.android.preferences.PreferenceHelper;
import com.omnom.util.BaseOmnomApplication;
import com.omnom.util.preferences.PreferenceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class OmnomApplication extends BaseOmnomApplication {
	public static OmnomApplication get(Context context) {
		return (OmnomApplication) context.getApplicationContext();
	}

	private final List<Object> injectList = new ArrayList<Object>();
	private ObjectGraph objectGraph;
	private PreferenceHelper preferenceHelper;

	protected List<Object> getModules() {
		return Arrays.asList(new AndroidModule(this), new ApplicationModule(), new AcquiringModuleMailRu(this),
		                     new AuthModule(this, R.string.endpoint_auth));
	}

	@Override
	public void inject(final Object object) {
		if(objectGraph == null) {
			injectList.add(object);
		} else {
			objectGraph.inject(object);
		}
	}

	@Override
	public PreferenceProvider getPreferences() {
		return preferenceHelper;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		objectGraph = ObjectGraph.create(getModules().toArray());
		for(final Object obj : injectList) {
			objectGraph.inject(obj);
		}
		injectList.clear();
		inject(this);
		preferenceHelper = new PreferenceHelper();
	}
}
