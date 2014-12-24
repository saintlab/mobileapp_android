package com.omnom.android.activity.base;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.OmnomApplication;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.UserData;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.model.AppLaunchMixpanelEvent;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.observable.OmnomObservable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

/**
 * Created by Ch3D on 01.10.2014.
 */
public abstract class BaseOmnomActivity extends BaseActivity {

	private static final String TAG = BaseOmnomFragmentActivity.class.getSimpleName();

	private static final String TAG_MIXPANEL = MixpanelAPI.class.getSimpleName();

	private Gson mGson;

	@Inject
	protected AuthService authenticator;

	private Subscription mUserSubscription;

	public final MixpanelAPI getMixPanel() {
		return OmnomApplication.getMixPanel(this);
	}

	public final MixPanelHelper getMixPanelHelper() {
		return OmnomApplication.getMixPanelHelper(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGson = new Gson();
	}

	@Override
	public void onApplicationLaunch() {
		final OmnomApplication app = OmnomApplication.get(getActivity());
		final MixPanelHelper mixPanelHelper = getMixPanelHelper();
		final String token = app.getAuthToken();
		mUserSubscription = AndroidObservable.bindActivity(this, authenticator.getUser(token)).subscribe(new Action1<UserResponse>() {
			@Override
			public void call(UserResponse userResponse) {
				correctMixpanelTime(userResponse.getTime() == null ? 0 : userResponse.getTime());
				app.cacheUserProfile(new UserProfile(userResponse));
				mixPanelHelper.track(new AppLaunchMixpanelEvent(userResponse.getUser()));
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			public void onError(Throwable throwable) {
				Log.w(TAG, throwable.getMessage());
				mixPanelHelper.track(new AppLaunchMixpanelEvent(UserHelper.getUserData(BaseOmnomActivity.this)));
			}
		});
	}

	protected void correctMixpanelTime(final long serverTime) {
		final Long currentTime = System.currentTimeMillis();
		final MixPanelHelper mixPanelHelper = getMixPanelHelper();
		if (mixPanelHelper != null) {
			final Long timeDiff = TimeUnit.SECONDS.toMillis(serverTime) - currentTime;
			mixPanelHelper.setTimeDiff(timeDiff);
		}
	}

	protected final void track(final String eventName, final Object o) {
		final String s = mGson.toJson(o);
		try {
			getMixPanel().track(eventName, new JSONObject(s));
		} catch(JSONException e) {
			Log.e(TAG_MIXPANEL, "track", e);
		}
	}

	protected final void track(final String eventName, final String s) {
		try {
			getMixPanel().track(eventName, new JSONObject(s));
		} catch(JSONException e) {
			Log.e(TAG_MIXPANEL, "track", e);
		}
	}

	@Override
	protected void onDestroy() {
		getMixPanel().flush();
		OmnomObservable.unsubscribe(mUserSubscription);
		super.onDestroy();
	}

	protected UserData getUserData() {
		return UserHelper.getUserData(this);
	}
}
