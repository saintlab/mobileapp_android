package com.omnom.android.fragment.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.animation.Animation;

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
	public Animation onCreateAnimation(final int transit, final boolean enter, final int nextAnim) {
		final Animation animation = super.onCreateAnimation(transit, enter, nextAnim);
		if(animation != null && getView() != null) {
			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(final Animation animation) {
					// do nothing
				}

				@Override
				public void onAnimationEnd(final Animation animation) {
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
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		OmnomApplication.get(getActivity()).inject(this);
		mBus.register(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mBus.unregister(this);
	}
}
