package com.omnom.android.service.configuration;

import android.content.Context;
import android.location.Location;

import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.auth.AuthError;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.request.UserLogLocationRequest;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.restaurateur.api.ConfigDataService;
import com.omnom.android.restaurateur.model.config.Config;
import com.omnom.android.service.location.LocationService;
import com.omnom.android.utils.utils.LocationUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by mvpotter on 2/12/2015.
 */
public class ConfigurationService {

	public static final int LOCATION_UPDATE_TIMEOUT = 2000;

	private static final int RETRY_COUNT = 5;

	protected final AuthService authenticator;

	protected final ConfigDataService restaurantApi;

	protected final Acquiring acquiring;

	protected final String authToken;

	protected LocationService locationService;

	protected Context context;

	public ConfigurationService(final Context context,
	                            final AuthService authenticator,
	                            final ConfigDataService configApi,
	                            final Acquiring acquiring,
	                            final String authToken) {
		this.context = context;
		this.authenticator = authenticator;
		this.restaurantApi = configApi;
		this.acquiring = acquiring;
		this.authToken = authToken;
		this.locationService = new LocationService(context);
	}

	/**
	 * Returns zip observable of all configuration items: config, user, location, log location result.
	 *
	 * @return observable of all configuration items
	 */
	public Observable<ConfigurationResponse> getConfigurationObservable() {
		return Observable.zip(getConfigObservable(),
		                      getCombinedUserAndLocationObservables(),
		                      new Func2<Config, ConfigurationResponse, ConfigurationResponse>() {
			                      @Override
			                      public ConfigurationResponse call(final Config config, final ConfigurationResponse
					                      configurationResponse) {
				                      return new ConfigurationResponse(configurationResponse.getUserResponse(),
				                                                       config,
				                                                       configurationResponse.getLocation(),
				                                                       configurationResponse.getLogLocationResponse());
			                      }
		                      }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	private Observable<Config> getConfigObservable() {
		return restaurantApi.getConfig().retry(RETRY_COUNT);
	}

	/**
	 * Returns user profile download observable.
	 *
	 * @return user profile observable
	 */
	private Observable<UserResponse> getUserObservable() {
		return authenticator.getUser(authToken)
		                    .retry(RETRY_COUNT)
		                    .flatMap(new Func1<UserResponse, Observable<UserResponse>>() {
			                    @Override
			                    public Observable<UserResponse> call(final UserResponse userResponse) {
				                    if(userResponse.hasError()) {
					                    throw new IllegalStateException("User is required");
				                    }
				                    userResponse.setResponseTime(System.currentTimeMillis());
				                    return Observable.just(userResponse);
			                    }
		                    });
	}

	/**
	 * Returns observable that tries to update location.
	 *
	 * @return observable of current location or null if not detected
	 */
	private Observable<Location> getLocationObservable() {
		return locationService.getLocation()
		                      .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
		                      .timeout(LOCATION_UPDATE_TIMEOUT, TimeUnit.MILLISECONDS,
		                               Observable.just(LocationUtils.getLastKnownLocation(context)))
		                      .onErrorReturn(new Func1<Throwable, Location>() {
			                      @Override
			                      public Location call(Throwable throwable) {
				                      return LocationUtils.getLastKnownLocation(context);
			                      }
		                      });
	}

	/**
	 * Returns observable that tries to log current location to server.
	 *
	 * @param location location to be logged
	 * @return observable of location log result
	 */
	private Observable<AuthResponse> getLogLocationObservable(final Location location) {
		if(location == null) {
			AuthResponse authResponse = new AuthResponse();
			authResponse.setStatus(AuthResponse.STATUS_ERROR);
			return Observable.just(authResponse);
		} else {
			return authenticator.logLocation(
					new UserLogLocationRequest(location.getLongitude(), location.getLatitude(), authToken))
			                    .onErrorReturn(new Func1<Throwable, AuthResponse>() {
				                    @Override
				                    public AuthResponse call(Throwable throwable) {
					                    final AuthError authError = new AuthError(0, throwable.getMessage());
					                    final AuthResponse authResponse = new AuthResponse();
					                    authResponse.setStatus(AuthResponse.STATUS_ERROR);
					                    authResponse.setError(authError);
					                    return authResponse;
				                    }
			                    });
		}
	}

	/**
	 * Combines user retreivement with location update.
	 * The observable tries to log the location to server right after getting user and location.
	 *
	 * @return observable that retrieves user with current location and logs location to server.
	 */
	private Observable<ConfigurationResponse> getCombinedUserAndLocationObservables() {
		return Observable.zip(getUserObservable(),
		                      getLocationObservable(),
		                      new Func2<UserResponse, Location, ConfigurationResponse>() {
			                      @Override
			                      public ConfigurationResponse call(UserResponse userResponse, Location location) {
				                      return new ConfigurationResponse(userResponse, null, location, null);
			                      }
		                      })
		                 .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
		                 .flatMap(new Func1<ConfigurationResponse, Observable<? extends ConfigurationResponse>>() {
			                 @Override
			                 public Observable<? extends ConfigurationResponse> call(final ConfigurationResponse configurationResponse) {
				                 return getLogLocationObservable(configurationResponse.getLocation())
						                 .map(new Func1<AuthResponse, ConfigurationResponse>() {
							                 @Override
							                 public ConfigurationResponse call(final AuthResponse authResponse) {
								                 return new ConfigurationResponse(configurationResponse.getUserResponse(),
								                                                  null,
								                                                  configurationResponse.getLocation(),
								                                                  authResponse);
							                 }
						                 });
			                 }
		                 });
	}

	public void onDestroy() {
		locationService.onDestroy();
		locationService = null;
	}
}
