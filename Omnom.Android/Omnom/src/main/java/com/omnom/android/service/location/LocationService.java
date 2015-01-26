package com.omnom.android.service.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.omnom.android.utils.utils.LocationUtils;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by mvpotter on 2/11/2015.
 */
public class LocationService {

	protected Context context;

	protected LocationManager locationManager;

	public LocationService(final Context context) {
		this.context = context;
		this.locationManager = LocationUtils.getLocationManager(context);
	}

	public Observable<Location> getLocation() {
		final RxLocationListener locationListener = new RxLocationListener();
		return Observable.create(new Observable.OnSubscribe<Location>() {
			@Override
			public void call(final Subscriber<? super Location> subscriber) {
				locationListener.setSubscriber(subscriber);
				final Criteria locationCriteria = new Criteria();
				locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
				locationCriteria.setPowerRequirement(Criteria.POWER_LOW);
				final String locationProvider = locationManager.getBestProvider(locationCriteria, true);

				Looper.prepare();

				locationManager.requestSingleUpdate(locationProvider, locationListener, Looper.myLooper());

				Looper.loop();
			}
		}).timeout(new Func1<Location, Observable<Location>>() {
			@Override
			public Observable<Location> call(Location location) {
				locationManager.removeUpdates(locationListener);
				return Observable.just(LocationUtils.getLastKnownLocation(context));
			}
		});
	}

	private class RxLocationListener implements LocationListener {

		private Subscriber<? super Location> subscriber;

		public Subscriber<? super Location> getSubscriber() {
			return subscriber;
		}

		public void setSubscriber(Subscriber<? super Location> subscriber) {
			this.subscriber = subscriber;
		}

		public void onLocationChanged(final Location location) {
			if (subscriber != null) {
				subscriber.onNext(location);
				subscriber.onCompleted();
			}
			Looper.myLooper().quit();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) { }
		public void onProviderEnabled(String provider) { }
		public void onProviderDisabled(String provider) { }
	}

}
