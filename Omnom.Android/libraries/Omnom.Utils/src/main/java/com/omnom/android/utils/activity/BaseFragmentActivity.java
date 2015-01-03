package com.omnom.android.utils.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.omnom.android.utils.Extras;
import com.omnom.android.utils.activity.helper.ActivityHelper;
import com.omnom.android.utils.activity.helper.ActivityHelperBase;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Ch3D on 31.07.2014.
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements OmnomActivity, Extras {
	@Inject
	protected Bus mBus;

	private ActivityHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());
		ButterKnife.inject(getActivity());
		mHelper = ActivityHelperBase.create(this);
		if(savedInstanceState != null) {
			handleSavedState(savedInstanceState);
		}
		handleIntent(getIntent());
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHelper.onPause();
		mBus.unregister(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHelper.onResume();
		mBus.register(this);
	}

	protected void handleSavedState(Bundle savedInstanceState) {
		// Do nothing
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
	public PreferenceProvider getPreferences() {
		return mHelper.getPreferences();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate();
	}

	protected final void postDelayed(long delay, Runnable action) {
		findViewById(android.R.id.content).postDelayed(action, delay);
	}

	public void start(Class<?> cls, int delay) {
		mHelper.start(cls, delay);
	}

	public void start(final Intent intent, int delay) {
		mHelper.start(intent, delay);
	}

	public void start(final Intent intent) {
		mHelper.start(intent);
	}

	public void start(Class<?> cls, int animIn, int animOut) {
		mHelper.start(cls, animIn, animOut);
	}

	public void start(Intent intent, int animIn, int animOut) {
		mHelper.start(intent, animIn, animOut);
	}

	public void start(Intent intent, int animIn, int animOut, boolean finish) {
		mHelper.start(intent, animIn, animOut, finish);
	}

	@Override
	public void startForResult(Intent intent, @AnimRes int animIn, @AnimRes int animOut, int code) {
		mHelper.startForResult(intent, animIn, animOut, code);
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
	public void start(Intent intent, boolean finish) {
		mHelper.start(intent, finish);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public abstract void initUi();

	@Override
	public abstract int getLayoutResource();
}
