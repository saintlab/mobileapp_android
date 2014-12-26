package com.omnom.android.activity.base;

import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.OmnomApplication;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.model.AppLaunchMixpanelEvent;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.activity.BaseFragmentActivity;
import com.omnom.android.utils.observable.OmnomObservable;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

/**
 * Created by Ch3D on 01.10.2014.
 */
public abstract class BaseOmnomFragmentActivity extends BaseFragmentActivity {

	private static final String TAG = BaseOmnomFragmentActivity.class.getSimpleName();

	@Inject
	protected AuthService authenticator;

	private Subscription mUserSubscription;

	private boolean isBusy;

	@Override
	public void onApplicationLaunch() {
		final OmnomApplication app = OmnomApplication.get(getActivity());
		final MixPanelHelper mixPanelHelper = OmnomApplication.getMixPanelHelper(this);
		final String token = app.getAuthToken();
		mUserSubscription = AndroidObservable.bindActivity(this, authenticator.getUser(token)).subscribe(new Action1<UserResponse>() {
			@Override
			public void call(UserResponse userResponse) {
				final Long currentTime = System.currentTimeMillis();
				final Long serverTime = userResponse.getTime() == null ? 0 : userResponse.getTime();
				final Long timeDiff = TimeUnit.SECONDS.toMillis(serverTime) - currentTime;
				app.cacheUserProfile(new UserProfile(userResponse));
				mixPanelHelper.setTimeDiff(timeDiff);
				mixPanelHelper.track(new AppLaunchMixpanelEvent(userResponse.getUser()));
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			public void onError(Throwable throwable) {
				Log.w(TAG, throwable.getMessage());
				mixPanelHelper.track(new AppLaunchMixpanelEvent(UserHelper.getUserData(BaseOmnomFragmentActivity.this)));
			}
		});
	}

	@Override
	protected void onDestroy() {
		getMixPanel().flush();
		OmnomObservable.unsubscribe(mUserSubscription);
		super.onDestroy();
	}

	public final MixpanelAPI getMixPanel() {
		return OmnomApplication.getMixPanel(this);
	}

	protected boolean isBusy() {
		return isBusy;
	}

	protected void busy(final boolean isBusy) {
		this.isBusy = isBusy;
	}

}
