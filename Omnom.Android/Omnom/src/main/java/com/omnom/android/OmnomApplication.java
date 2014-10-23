package com.omnom.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.modules.AcquiringModuleMailRuMixpanel;
import com.omnom.android.modules.AndroidModule;
import com.omnom.android.modules.AuthMixpanelModule;
import com.omnom.android.modules.BeaconModule;
import com.omnom.android.modules.OmnomApplicationModule;
import com.omnom.android.preferences.PreferenceHelper;
import com.omnom.android.restaurateur.RestaurateurModule;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.AuthTokenProvider;
import com.omnom.android.utils.BaseOmnomApplication;
import com.omnom.android.utils.preferences.PreferenceProvider;

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

	public static MixPanelHelper getMixPanelHelper(final Context context) {
		return OmnomApplication.get(context).mixPanelHelper;
	}

	private final List<Object> injectList = new ArrayList<Object>();

	private ObjectGraph objectGraph;

	private PreferenceHelper preferenceHelper;

	private MixpanelAPI mixPanel;

	private Stack<Activity> activityStack = new Stack<Activity>();

	private MixPanelHelper mixPanelHelper;

	private UserProfile cachedUser;

	protected List<Object> getModules() {
		return Arrays.asList(new AndroidModule(this),
		                     new OmnomApplicationModule(),
		                     new BeaconModule(this),
		                     new RestaurateurModule(this, R.string.endpoint_restaurateur),
		                     new AcquiringModuleMailRuMixpanel(this, mixPanelHelper),
		                     new AuthMixpanelModule(this, R.string.endpoint_auth, mixPanelHelper));
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
		mixPanelHelper = new MixPanelHelper(mixPanel);

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

	public void cacheUserProfile(UserProfile user) {
		cachedUser = user;
	}

	public UserProfile getUserProfile() {
		return cachedUser;
	}
}
