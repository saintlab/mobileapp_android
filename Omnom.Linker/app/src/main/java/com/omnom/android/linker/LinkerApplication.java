package com.omnom.android.linker;

import android.app.Application;
import android.content.Context;

import com.omnom.android.linker.activity.base.OmnomActivity;
import com.omnom.android.linker.model.UserProfile;
import com.omnom.android.linker.modules.AndroidModule;
import com.omnom.android.linker.modules.ApplicationModule;
import com.omnom.android.linker.modules.LinkerDataProviderModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Ch3D on 10.08.2014.
 */
public class LinkerApplication extends Application {

	public static LinkerApplication get(Context context) {
		return (LinkerApplication) context.getApplicationContext();
	}

	public static LinkerApplication get(OmnomActivity omnomActivity) {
		return get(omnomActivity.getActivity());
	}

	private final List<Object> injectList = new ArrayList<Object>();
	private ObjectGraph objectGraph;
	private UserProfile mUserProfile;

	protected List<Object> getModules() {
		return Arrays.asList(/*new StubDataProviderModule(),*/new LinkerDataProviderModule(), new AndroidModule(this),
		                     new ApplicationModule());
	}

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
		CalligraphyConfig.initDefault("fonts/Futura-OSF-Omnom-Regular.otf", R.attr.fontPath);

		objectGraph = ObjectGraph.create(getModules().toArray());
		for(final Object obj : injectList) {
			objectGraph.inject(obj);
		}
		injectList.clear();
		inject(this);
		int i = 1;
	}

	public void cacheUserProfile(UserProfile profile) {
		mUserProfile = profile;
	}

	public UserProfile getUserProfile() {
		return mUserProfile;
	}
}
