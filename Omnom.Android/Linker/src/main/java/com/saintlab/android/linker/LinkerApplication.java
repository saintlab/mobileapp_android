package com.saintlab.android.linker;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.omnom.android.auth.AuthModule;
import com.omnom.android.restaurateur.RestaurateurModule;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.AuthTokenProvider;
import com.omnom.android.utils.BaseOmnomApplication;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.saintlab.android.linker.modules.AndroidModule;
import com.saintlab.android.linker.modules.ApplicationModule;
import com.saintlab.android.linker.modules.BeaconModule;
import com.saintlab.android.linker.preferences.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Ch3D on 10.08.2014.
 */
public class LinkerApplication extends BaseOmnomApplication implements AuthTokenProvider {

	public static LinkerApplication get(Context context) {
		return (LinkerApplication) context.getApplicationContext();
	}

	public static LinkerApplication get(OmnomActivity omnomActivity) {
		return get(omnomActivity.getActivity());
	}

	private final List<Object> injectList = new ArrayList<Object>();
	private ObjectGraph objectGraph;
	private UserProfile mUserProfile;

	private PreferenceHelper mPrefsHelper;

	protected List<Object> getModules() {
		return Arrays.asList(/*new StubDataProviderModule(),*/new RestaurateurModule(this, R.string.config_data_endpoint), new AndroidModule(this),
		                     new ApplicationModule(), new BeaconModule(this),
		                     new AuthModule(this, R.string.config_auth_endpoint));
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
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		CalligraphyConfig.initDefault("fonts/Futura-OSF-Omnom-Regular.otf", R.attr.fontPath);

		objectGraph = ObjectGraph.create(getModules().toArray());
		for(final Object obj : injectList) {
			objectGraph.inject(obj);
		}
		injectList.clear();
		inject(this);
		mPrefsHelper = new PreferenceHelper();
	}

	public PreferenceProvider getPreferences() {
		return mPrefsHelper;
	}

	public void cacheUserProfile(UserProfile profile) {
		mUserProfile = profile;
	}

	public UserProfile getUserProfile() {
		return mUserProfile;
	}

	@Override
	public String getAuthToken() {
		return mPrefsHelper.getAuthToken(this);
	}

	@Override
	public Context getContext() {
		return this;
	}
}
