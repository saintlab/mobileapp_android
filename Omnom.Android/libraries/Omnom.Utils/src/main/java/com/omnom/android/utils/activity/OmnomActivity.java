package com.omnom.android.utils.activity;

import android.app.Activity;
import android.content.Intent;

import com.omnom.android.utils.preferences.PreferenceProvider;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Ch3D on 31.07.2014.
 */
public interface OmnomActivity {
	public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 100;
	public static final int REQUEST_CODE_SCAN_QR = 101;

	void start(Intent intent, int animIn, int animOut, boolean finish);

	void startForResult(Intent intent, int animIn, int animOut, int code);

	void start(Class<?> cls);

	void start(Class<?> cls, boolean finish);

	void start(Intent intent, boolean finish);

	Activity getActivity();

	int getLayoutResource();

	void initUi();

	PreferenceProvider getPreferences();

	Subscription subscribe(final Observable observable, final Action1<? extends Object> onNext, final Action1<Throwable> onError);

	void unsubscribe(final Subscription subscription);

}
