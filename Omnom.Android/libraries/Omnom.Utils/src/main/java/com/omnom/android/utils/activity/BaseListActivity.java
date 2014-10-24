package com.omnom.android.utils.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.omnom.android.utils.Extras;
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
		mHelper = new ActivityHelper(this);
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
	public PreferenceProvider getPreferences() {
		return mHelper.getPreferences();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	}

	public void startActivity(Class<?> cls, int delay) {
		mHelper.startActivity(cls, delay);
	}

	@Override
	public void startActivity(Intent intent, boolean finish) {
		mHelper.startActivity(intent, finish);
	}

	@Override
	public void startActivity(Class<?> cls) {
		mHelper.startActivity(cls);
	}

	@Override
	public void startActivity(Class<?> cls, boolean finish) {
		mHelper.startActivity(cls, finish);
	}

	@Override
	public void startActivity(final Intent intent, final int animIn, final int animOut, final boolean finish) {
		mHelper.startActivity(intent, animIn, animOut, finish);
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
