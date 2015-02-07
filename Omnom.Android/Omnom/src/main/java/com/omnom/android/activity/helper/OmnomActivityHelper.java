package com.omnom.android.activity.helper;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.omnom.android.OmnomApplication;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.model.AppLaunchMixpanelEvent;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.LocationUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

/**
 * Created by mvpotter on 1/27/2015.
 */
public class OmnomActivityHelper implements ActivityHelper, LocationListener {

	private static final String TAG = OmnomActivityHelper.class.getSimpleName();

	public static final int LOCATION_UPDATE_TIMEOUT = 5000;

	protected final Activity mActivity;

	protected final AuthService mAuthenticator;

	private Subscription mLocationSubscription;

	private Subscription mUserSubscription;

	private Location mCurrentLocation = null;

	public OmnomActivityHelper(Activity activity, AuthService authenticator) {
		this.mActivity = activity;
		this.mAuthenticator = authenticator;
	}

	@Override
	public PreferenceProvider getPreferences() {
		return OmnomApplication.get(mActivity).getPreferences();
	}

	@Override
	public void onPostCreate() {

	}

	@Override
	public void onStart() {
		final OmnomApplication app = OmnomApplication.get(mActivity);
		if (app.getUserProfile() == null) {
			final MixPanelHelper mixPanelHelper = OmnomApplication.getMixPanelHelper(mActivity);
			final String token = app.getAuthToken();
			mUserSubscription = AndroidObservable.bindActivity(mActivity, mAuthenticator.getUser(token)).subscribe(new Action1<UserResponse>() {
				@Override
				public void call(UserResponse userResponse) {
					app.cacheUserProfile(new UserProfile(userResponse));
					final Long currentTime = System.currentTimeMillis();
					final Long serverTime = userResponse.getTime() == null ? 0 : userResponse.getTime();
					final Long timeDiff = TimeUnit.SECONDS.toMillis(serverTime) - currentTime;
					mixPanelHelper.setTimeDiff(timeDiff);
				}
			}, new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					Log.e(TAG, "onStart", throwable);
				}
			});
		}
	}

	@Override
	public void onApplicationLaunch() {
		onApplicationLaunch(null);
	}

	@Override
	public void onApplicationLaunch(final ApplicationLaunchListener mListener) {
		subscribeForLocationUpdates();
		synchronizeTimeWithServer(mListener);
	}

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void onStop() {

	}

	@Override
	public void onDestroy() {
		OmnomObservable.unsubscribe(mUserSubscription);
		OmnomObservable.unsubscribe(mLocationSubscription);
	}

	@Override
	public Location getLocation() {
		if (mCurrentLocation == null) {
			mCurrentLocation = LocationUtils.getLastKnownLocation(mActivity);
		}
		return mCurrentLocation;
	}

	private void synchronizeTimeWithServer(final ApplicationLaunchListener mListener) {
		final OmnomApplication app = OmnomApplication.get(mActivity);
		final MixPanelHelper mixPanelHelper = OmnomApplication.getMixPanelHelper(mActivity);
		final String token = app.getAuthToken();
		mUserSubscription = AndroidObservable.bindActivity(mActivity, mAuthenticator.getUser(token)).subscribe(new Action1<UserResponse>() {
			@Override
			public void call(UserResponse userResponse) {
				correctMixpanelTime(userResponse.getTime() == null ? 0 : userResponse.getTime());
				app.cacheUserProfile(new UserProfile(userResponse));
				mixPanelHelper.track(MixPanelHelper.Project.OMNOM, new AppLaunchMixpanelEvent(userResponse.getUser()));
				logLocation(mListener);
			}
		}, new ObservableUtils.BaseOnErrorHandler(mActivity) {
			@Override
			public void onError(Throwable throwable) {
				Log.w(TAG, "synchronizeTimeWithServer", throwable);
				mixPanelHelper.track(MixPanelHelper.Project.OMNOM, new AppLaunchMixpanelEvent(UserHelper.getUserData(mActivity)));
				dataLoaded(mListener);
			}
		});
	}

	private void correctMixpanelTime(final long serverTime) {
		final Long currentTime = System.currentTimeMillis();
		final MixPanelHelper mixPanelHelper = OmnomApplication.getMixPanelHelper(mActivity);
		if (mixPanelHelper != null) {
			final Long timeDiff = TimeUnit.SECONDS.toMillis(serverTime) - currentTime;
			mixPanelHelper.setTimeDiff(timeDiff);
		}
	}

	private void logLocation(final ApplicationLaunchListener mListener) {
		final OmnomApplication app = OmnomApplication.get(mActivity);
		final String token = app.getAuthToken();
		if (getLocation() != null) {
			Observable<AuthResponse> locationObservable = mAuthenticator.logLocation(
					getLocation().getLongitude(),
					getLocation().getLatitude(),
					token);
			mLocationSubscription = AndroidObservable.bindActivity(mActivity, locationObservable).subscribe(
					new Action1<AuthResponse>() {
						@Override
						public void call(AuthResponse authResponse) {
							Log.d(TAG, authResponse.getStatus());
							dataLoaded(mListener);
						}
					},
					new ObservableUtils.BaseOnErrorHandler(mActivity) {
						@Override
						public void onError(Throwable throwable) {
							Log.d(TAG, "logLocation", throwable);
							dataLoaded(mListener);
						}
					});
		} else {
			Log.d(TAG, "logLocation unable to retrieve location");
			dataLoaded(mListener);
		}
	}

	private void dataLoaded(final ApplicationLaunchListener mListener) {
		if (mListener != null) {
			mListener.onDataLoaded();
		}
	}

	private void subscribeForLocationUpdates() {
		final LocationManager locationManager = LocationUtils.getLocationManager(mActivity);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}
		mActivity.findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				locationManager.removeUpdates(OmnomActivityHelper.this);
			}
		}, LOCATION_UPDATE_TIMEOUT);
	}

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		LocationUtils.getLocationManager(mActivity).removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

}
