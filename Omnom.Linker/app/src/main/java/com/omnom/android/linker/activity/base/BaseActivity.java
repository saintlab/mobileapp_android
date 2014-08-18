package com.omnom.android.linker.activity.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Ch3D on 31.07.2014.
 */
public abstract class BaseActivity extends Activity implements OmnomActivity, Extras {
	private ActivityHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());
		ButterKnife.inject(getActivity());
		mHelper = new ActivityHelper(this);
		if(savedInstanceState != null) {
			handleSavedState(savedInstanceState);
		}
		handleIntent(getIntent());
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

	public void startActivity(Class<?> cls, int delay) {
		mHelper.startActivity(cls, delay);
	}

	public void startActivity(final Intent intent, int delay) {
		mHelper.startActivity(intent, delay);
	}

	public void startActivity(final Intent intent) {
		mHelper.startActivity(intent);
	}

	public void startActivity(Class<?> cls, int animIn, int animOut) {
		mHelper.startActivity(cls, animIn, animOut);
	}

	public void startActivity(Class<?> cls) {
		mHelper.startActivity(cls);
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
