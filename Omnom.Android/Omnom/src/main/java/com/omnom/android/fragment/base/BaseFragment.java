package com.omnom.android.fragment.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.omnom.android.OmnomApplication;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by Ch3D on 03.02.2015.
 */
public abstract class BaseFragment extends Fragment {
	@Inject
	protected Bus mBus;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OmnomApplication.get(getActivity()).inject(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mBus.register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		mBus.unregister(this);
	}

}
