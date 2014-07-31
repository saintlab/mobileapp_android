package com.omnom.android.linker.activity.base;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class BaseListActivity extends ListActivity implements OmnomActivity {
	private ActivityHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());
		mHelper = new ActivityHelper(this);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public int getLayoutResource() {
		return 0;
	}

	@Override
	public void initUi() {

	}
}
