package com.omnom.android.utils.activity.helper;

import android.content.Intent;
import android.os.Build;

import com.omnom.android.utils.BaseOmnomApplication;
import com.omnom.android.utils.R;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.preferences.PreferenceProvider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Ch3D on 17.11.2014.
 */
public abstract class ActivityHelperBase implements ActivityHelperWithAnimation {

	public static ActivityHelperWithAnimation create(OmnomActivity activity) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			return new ActivityHelperJB(activity);
		}
		return new ActivityHelperICS(activity);
	}

	protected final OmnomActivity mActivity;

	private HashSet<Subscription> mSubscriptions = new HashSet<Subscription>();

	public ActivityHelperBase(OmnomActivity activity) {
		this.mActivity = activity;
		BaseOmnomApplication.get(activity).inject(activity);
	}

	@Override
	public void onPostCreate() {
		mActivity.initUi();
	}

	@Override
	public void onStart() {

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
		final Iterator<Subscription> iterator = mSubscriptions.iterator();
		while(iterator.hasNext()) {
			unsubscribe(iterator.next());
			iterator.remove();
		}
	}

	@Override
	public PreferenceProvider getPreferences() {
		return BaseOmnomApplication.get(mActivity).getPreferences();
	}

	@Override
	public void start(Class<?> cls) {
		start(cls, R.anim.fade_in, R.anim.fake_fade_out);
	}

	@Override
	public void start(Class<?> cls, boolean finish) {
		start(cls, R.anim.fade_in, R.anim.fake_fade_out, finish);
	}

	@Override
	public void start(Class<?> cls, int animIn, int aninOut) {
		start(cls, animIn, aninOut, true);
	}

	@Override
	public void start(final Class<?> cls, int delay) {
		mActivity.getActivity().findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				start(cls);
			}
		}, delay);
	}

	@Override
	public void start(final Intent intent, int delay) {
		mActivity.getActivity().findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				start(intent);
			}
		}, delay);
	}

	@Override
	public Observable bind(final Observable observable) {
		return AppObservable.bindActivity(mActivity.getActivity(), observable);
	}

	@Override
	public Subscription subscribe(final Observable observable, final Action1<? extends Object> onNext, final Action1<Throwable>
			onError) {
		final Subscription subscription = bind(observable).subscribe(onNext, onError);
		mSubscriptions.add(subscription);
		return subscription;
	}

	@Override
	public void unsubscribe(final Subscription subscription) {
		OmnomObservable.unsubscribe(subscription);
	}


}
