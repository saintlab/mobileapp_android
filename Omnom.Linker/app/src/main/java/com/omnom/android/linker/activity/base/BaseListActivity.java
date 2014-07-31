package com.omnom.android.linker.activity.base;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;

/**
 * Created by Ch3D on 31.07.2014.
 */
public abstract class BaseListActivity extends ListActivity implements OmnomActivity {
	private ActivityHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());
		mHelper = new ActivityHelper(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate();
	}

	public void startActivity(Class<?> cls, int delay) {
		mHelper.startActivity(cls, delay);
	}

	public void startActivity(Class<?> cls) {
		mHelper.startActivity(cls);
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
