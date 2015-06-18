package com.omnom.android.fragment.base;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.animation.Animation;

import com.omnom.android.OmnomApplication;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by Ch3D on 17.06.2015.
 */
public class BaseListFragment extends ListFragment {

	@Inject
	protected Bus mBus;

	@Override
	public Animation onCreateAnimation(final int transit, final boolean enter, final int nextAnim) {
		final Animation animation = super.onCreateAnimation(transit, enter, nextAnim);
		if(animation != null && getView() != null) {
			getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(final Animation animation) {
					// do nothing
				}

				@Override
				public void onAnimationEnd(final Animation animation) {
					getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
				}

				@Override
				public void onAnimationRepeat(final Animation animation) {
					// do nothing
				}
			});
		}
		return animation;
	}

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
