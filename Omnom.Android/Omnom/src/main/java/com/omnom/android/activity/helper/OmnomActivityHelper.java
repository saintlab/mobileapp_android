package com.omnom.android.activity.helper;

import android.app.Activity;
import android.location.Location;

import com.omnom.android.OmnomApplication;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.LocationUtils;

<<<<<<< HEAD
=======
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;

>>>>>>> omnom/omnom_master_menu_merge
/**
 * Created by mvpotter on 1/27/2015.
 */
public class OmnomActivityHelper implements ActivityHelper {

	private static final String TAG = OmnomActivityHelper.class.getSimpleName();

	protected final Activity mActivity;

	private Location mCurrentLocation = null;

	public OmnomActivityHelper(Activity activity) {
		this.mActivity = activity;
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
<<<<<<< HEAD
=======
		final OmnomApplication app = OmnomApplication.get(mActivity);
		if(app.getUserProfile() == null) {
			final MixPanelHelper mixPanelHelper = OmnomApplication.getMixPanelHelper(mActivity);
			final String token = app.getAuthToken();
			mUserSubscription = AppObservable.bindActivity(mActivity, mAuthenticator.getUser(token)).subscribe(
					new Action1<UserResponse>() {
						@Override
						public void call(UserResponse userResponse) {
							app.cacheUserProfile(new UserProfile(userResponse));
							final Long currentTime = System.currentTimeMillis();
							final Long serverTime = userResponse.getServerTime() == null ? 0 : userResponse.getServerTime();
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
>>>>>>> omnom/omnom_master_menu_merge

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

	}

	@Override
	public Location getLocation() {
		if (mCurrentLocation == null) {
			mCurrentLocation = LocationUtils.getLastKnownLocation(mActivity);
		}
		return mCurrentLocation;
	}

<<<<<<< HEAD
=======
	private void synchronizeTimeWithServer(final ApplicationLaunchListener mListener) {
		final OmnomApplication app = OmnomApplication.get(mActivity);
		final MixPanelHelper mixPanelHelper = OmnomApplication.getMixPanelHelper(mActivity);
		final String token = app.getAuthToken();
		mUserSubscription = AppObservable.bindActivity(mActivity, mAuthenticator.getUser(token)).subscribe(new Action1<UserResponse>
				() {
			@Override
			public void call(UserResponse userResponse) {
				correctMixpanelTime(userResponse.getServerTime() == null ? 0 : userResponse.getServerTime());
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
		if(mixPanelHelper != null) {
			final Long timeDiff = TimeUnit.SECONDS.toMillis(serverTime) - currentTime;
			mixPanelHelper.setTimeDiff(timeDiff);
		}
	}

	private void logLocation(final ApplicationLaunchListener mListener) {
		final OmnomApplication app = OmnomApplication.get(mActivity);
		final String token = app.getAuthToken();
		if(getLocation() != null) {
			Observable<AuthResponse> locationObservable = mAuthenticator.logLocation(
					getLocation().getLongitude(),
					getLocation().getLatitude(),
					token);
			mLocationSubscription = AppObservable.bindActivity(mActivity, locationObservable).subscribe(
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
		if(mListener != null) {
			mListener.onDataLoaded();
		}
	}

	private void subscribeForLocationUpdates() {
		final LocationManager locationManager = LocationUtils.getLocationManager(mActivity);
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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

>>>>>>> omnom/omnom_master_menu_merge
}
