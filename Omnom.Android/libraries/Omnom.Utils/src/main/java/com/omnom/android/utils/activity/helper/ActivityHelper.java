package com.omnom.android.utils.activity.helper;

import com.omnom.android.utils.preferences.PreferenceProvider;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Ch3D on 17.11.2014.
 */
public interface ActivityHelper {

	PreferenceProvider getPreferences();

	void onPostCreate();

	void onStart();

	void onResume();

	void onPause();

	void onStop();

	void onDestroy();

	Observable bind(Observable observable);

	Subscription subscribe(final Observable observable, final Action1<? extends Object> onNext, final Action1<Throwable> onError);

	void unsubscribe(Subscription subscription);

}
