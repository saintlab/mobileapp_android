package com.omnom.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.modules.AcquiringModuleMailRuMixpanel;
import com.omnom.android.modules.AndroidModule;
import com.omnom.android.modules.ApplicationModule;
import com.omnom.android.modules.AuthMixpanelModule;
import com.omnom.android.preferences.PreferenceHelper;
import com.omnom.util.AuthTokenProvider;
import com.omnom.util.BaseOmnomApplication;
import com.omnom.util.preferences.PreferenceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import dagger.ObjectGraph;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class OmnomApplication extends BaseOmnomApplication implements AuthTokenProvider {
	private static final String MIXPANEL_TOKEN = "36038b10ddd9e23db4a28d0f8c21fd78";

	public static OmnomApplication get(Context context) {
		return (OmnomApplication) context.getApplicationContext();
	}

	public static MixpanelAPI getMixPanel(final Context context) {
		return OmnomApplication.get(context).mixPanel;
	}

	private final List<Object> injectList = new ArrayList<Object>();
	private ObjectGraph objectGraph;
	private PreferenceHelper preferenceHelper;
	private MixpanelAPI mixPanel;
	private Stack<Activity> activityStack = new Stack<Activity>();

	protected List<Object> getModules() {
		return Arrays.asList(new AndroidModule(this),
		                     new ApplicationModule(),
		                     new AcquiringModuleMailRuMixpanel(this, mixPanel),
		                     new AuthMixpanelModule(this, R.string.endpoint_auth, mixPanel));
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
		CalligraphyConfig.initDefault("fonts/Futura-OSF-Omnom-Regular.otf", R.attr.fontPath);

		mixPanel = MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);

		objectGraph = ObjectGraph.create(getModules().toArray());
		for(final Object obj : injectList) {
			objectGraph.inject(obj);
		}
		injectList.clear();
		inject(this);
		preferenceHelper = new PreferenceHelper();

		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				activityStack.push(activity);
			}

			@Override
			public void onActivityStarted(Activity activity) {

			}

			@Override
			public void onActivityResumed(Activity activity) {

			}

			@Override
			public void onActivityPaused(Activity activity) {

			}

			@Override
			public void onActivityStopped(Activity activity) {

			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

			}

			@Override
			public void onActivityDestroyed(Activity activity) {
				activityStack.pop();
				if(activityStack.isEmpty()) {
					mixPanel.flush();
				}
			}
		});
	}

	@Override
	public String getAuthToken() {
		return preferenceHelper.getAuthToken(this);
	}

	@Override
	public Context getContext() {
		return this;
	}
}
