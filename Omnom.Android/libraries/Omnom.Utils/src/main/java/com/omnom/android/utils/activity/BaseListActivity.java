package com.omnom.android.utils.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;

import com.omnom.android.utils.Extras;
import com.omnom.android.utils.activity.helper.ActivityHelper;
import com.omnom.android.utils.activity.helper.ActivityHelperBase;
import com.omnom.android.utils.preferences.PreferenceProvider;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Ch3D on 31.07.2014.
 */
public abstract class BaseListActivity extends ListActivity implements OmnomActivity, Extras {
	private ActivityHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());
		ButterKnife.inject(getActivity());
		mHelper = ActivityHelperBase.create(this);
		handleIntent(getIntent());
	}

	protected void handleIntent(Intent intent) {
		// Do nothing
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate();
	}

	@Override
	public void onApplicationLaunch() {
		// do nothing
	}

	@Override
	protected void onPause() {
		super.onStart();
		mHelper.onPause();
	}

	@Override
	protected void onResume() {
		super.onStop();
		mHelper.onResume();
	}

	@Override
	public PreferenceProvider getPreferences() {
		return mHelper.getPreferences();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	}

	public void startActivity(Class<?> cls, int delay) {
		mHelper.start(cls, delay);
	}

	@Override
	public void start(Intent intent, boolean finish) {
		mHelper.start(intent, finish);
	}

	@Override
	public void start(Class<?> cls) {
		mHelper.start(cls);
	}

	@Override
	public void start(Class<?> cls, boolean finish) {
		mHelper.start(cls, finish);
	}

	@Override
	public void start(final Intent intent, final int animIn, final int animOut, final boolean finish) {
		mHelper.start(intent, animIn, animOut, finish);
	}

	@Override
	public void startForResult(Intent intent, @AnimRes int animIn, @AnimRes int animOut, int code) {
		mHelper.startForResult(intent, animIn, animOut, code);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public abstract int getLayoutResource();

	@Override
	public void initUi() {

	}
}
