package com.omnom.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omnom.android.R;
import com.omnom.android.activity.UserProfileEditActivity;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.utils.utils.AndroidUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserPhotoOptionsFragment extends BaseFragment {

	public static void show(final FragmentManager fragmentManager, @IdRes int containerId) {
		fragmentManager.beginTransaction()
		               .addToBackStack(null)
		               .setCustomAnimations(R.anim.fade_in,
		                                    R.anim.nothing_long,
		                                    R.anim.fade_in,
		                                    R.anim.nothing_long)
		               .add(containerId, UserPhotoOptionsFragment.newInstance())
		               .commit();
	}

	private static Fragment newInstance() {
		return new UserPhotoOptionsFragment();
	}

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.content)
	protected View contentView;

	private boolean mFirstStart = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_user_photo_options, container, false);
		ButterKnife.inject(this, view);
		contentView.setTranslationY(AndroidUtils.getScreenHeightPixels(view.getContext()));
		rootView.requestFocus();
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		if(mFirstStart) {
			contentView.postDelayed(new Runnable() {
				@Override
				public void run() {
					contentView.animate().translationY(0).start();
				}
			}, getResources().getInteger(R.integer.default_animation_duration_short));
		}
		mFirstStart = false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		contentView.animate().translationY(rootView.getHeight()).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				rootView.animate().alpha(0).start();
			}
		}).start();
	}

	@OnClick(R.id.txt_photo)
	protected void onTakePhoto() {
		getFragmentManager().popBackStack();
		((UserProfileEditActivity) getActivity()).takePhoto();
	}

	@OnClick(R.id.txt_media)
	protected void onLoadMedia() {
		getFragmentManager().popBackStack();
		((UserProfileEditActivity) getActivity()).loadMedia();
	}

	@OnClick(R.id.txt_delete)
	protected void onDelete() {
		getFragmentManager().popBackStack();
		((UserProfileEditActivity) getActivity()).deleteCurrent();
	}

	@OnClick(R.id.root)
	protected void onTouchOutside() {
		getFragmentManager().popBackStack();
	}
}

