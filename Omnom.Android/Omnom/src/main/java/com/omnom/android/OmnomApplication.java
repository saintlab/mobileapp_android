package com.omnom.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.omnom.android.menu.MenuModule;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.modules.AcquiringModuleMailRuMixpanel;
import com.omnom.android.modules.AndroidModule;
import com.omnom.android.modules.AuthMixpanelModule;
import com.omnom.android.modules.BeaconModule;
import com.omnom.android.modules.OmnomApplicationModule;
import com.omnom.android.modules.PushWooshNotificationsModule;
import com.omnom.android.modules.RestaurateurMixpanelModule;
import com.omnom.android.notifier.NotifierModule;
import com.omnom.android.notifier.api.observable.NotifierObservableApi;
import com.omnom.android.preferences.JsonPreferenceProvider;
import com.omnom.android.preferences.PreferenceHelperAdapter;
import com.omnom.android.push.PushNotificationManager;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.beacon.BeaconFindRequest;
import com.omnom.android.restaurateur.model.config.Config;
import com.omnom.android.utils.AuthTokenProvider;
import com.omnom.android.utils.BaseOmnomApplication;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.StringUtils;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import dagger.ObjectGraph;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class OmnomApplication extends BaseOmnomApplication implements AuthTokenProvider {

	private static final String TAG = OmnomApplication.class.getSimpleName();

	public static OmnomApplication get(Context context) {
		return (OmnomApplication) context.getApplicationContext();
	}

	public static MixPanelHelper getMixPanelHelper(final Context context) {
		return OmnomApplication.get(context).mixPanelHelper;
	}

	public static Picasso getPicasso(final Context context) {
		return OmnomApplication.get(context.getApplicationContext()).getOrCreatePicasso();
	}

	private final List<Object> injectList = new ArrayList<Object>();

	@Inject
	protected PushNotificationManager mPushManager;

	@Inject
	protected NotifierObservableApi notifierApi;

	private ObjectGraph objectGraph;

	private JsonPreferenceProvider preferenceHelper;

	private Stack<Activity> activityStack = new Stack<Activity>();

	private MixPanelHelper mixPanelHelper;

	private Config cachedConfig;

	private UserProfile cachedUser;

	private BeaconFindRequest cachedBeacon;

	private Picasso _lazy_Picasso;

	protected List<Object> getModules() {
		return Arrays.asList(new AndroidModule(this),
		                     new OmnomApplicationModule(),
		                     new BeaconModule(this),
		                     new RestaurateurMixpanelModule(this, R.string.endpoint_restaurateur, mixPanelHelper),
		                     new MenuModule(this, R.string.endpoint_menu),
		                     new AcquiringModuleMailRuMixpanel(this, mixPanelHelper),
		                     new PushWooshNotificationsModule(this),
		                     new NotifierModule(this, R.string.endpoint_restaurateur),
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
		// FIXME: possible migration to bugsense
		// Fabric.with(this, new Crashlytics());
		CalligraphyConfig.initDefault("fonts/Futura-OSF-Omnom-Regular.otf", R.attr.fontPath);
		mixPanelHelper = new MixPanelHelper();

		objectGraph = ObjectGraph.create(getModules().toArray());
		for(final Object obj : injectList) {
			objectGraph.inject(obj);
		}

		injectList.clear();
		inject(this);
		preferenceHelper = new PreferenceHelperAdapter();

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
			}
		});
	}

	public boolean hasActivities() {
		return !activityStack.isEmpty();
	}

	public void cacheAuthToken(final String token) {
		preferenceHelper.setAuthToken(this, token);
	}

	@Override
	public String getAuthToken() {
		return preferenceHelper.getAuthToken(this);
	}

	public void logout() {
		notifierApi.unregister().subscribe(new Action1() {
			@Override
			public void call(final Object o) {
				Log.d(TAG, "notifierApi.unregister : " + o);
				clearUserData();
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(final Throwable throwable) {
				Log.d(TAG, "notifierApi.unregister", throwable);
				clearUserData();
			}
		});
	}

	public void clearUserData() {
		preferenceHelper.setAuthToken(this, StringUtils.EMPTY_STRING);
		cacheUserProfile(null);
	}

	@Override
	public Context getContext() {
		return this;
	}

	public void cacheConfig(final Config config) {
		cachedConfig = config;
		preferenceHelper.setConfig(this, config);
	}

	public Config getConfig() {
		if(cachedConfig == null) {
			cachedConfig = preferenceHelper.getConfig(this);
		}
		return cachedConfig;
	}

	public void cacheUserProfile(UserProfile user) {
		cachedUser = user;
		preferenceHelper.setUserProfile(this, user);
	}

	public UserProfile getUserProfile() {
		if(cachedUser == null) {
			cachedUser = preferenceHelper.getUserProfile(this);
		}
		return cachedUser;
	}

	public void cacheBeacon(BeaconFindRequest beacon) {
		cachedBeacon = beacon;
	}

	public BeaconFindRequest getBeacon() {
		return cachedBeacon;
	}

	public Picasso getOrCreatePicasso() {
		if(_lazy_Picasso == null) {
			_lazy_Picasso = new Picasso.Builder(getApplicationContext()).indicatorsEnabled(BuildConfig.DEBUG)
			                                                            .loggingEnabled(BuildConfig.DEBUG)
			                                                            .memoryCache(new LruCache(getCacheSize()))
			                                                            .build();
		}
		return _lazy_Picasso;
	}

	private int getCacheSize() {
		final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		final int memoryClass = am.getMemoryClass();
		// Target ~25% of the available heap.
		return 1024 * 1024 * memoryClass / 4;
	}
}
