package com.omnom.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.utils.AndroidUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DinnerDetailsFragment extends BaseFragment {

	public static final int CONTENT_TRANSITION_Y = 800;

	private static final String ARG_RESTAURANT = "param1";

	public static DinnerDetailsFragment newInstance(Restaurant restaurant) {
		final DinnerDetailsFragment fragment = new DinnerDetailsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_RESTAURANT, restaurant);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager fragmentManager, final @IdRes int containerId, Restaurant restaurant) {
		fragmentManager.beginTransaction()
		               .addToBackStack(null)
		               .setCustomAnimations(R.anim.fade_in,
		                                    R.anim.nothing_long,
		                                    R.anim.fade_in,
		                                    R.anim.nothing_long)
		               .add(containerId, DinnerDetailsFragment.newInstance(restaurant))
		               .commit();
	}

	@InjectView(R.id.txt_info)
	protected TextView txtInfo;

	@InjectView(R.id.txt_address)
	protected TextView txtAddress;

	@InjectView(R.id.txt_date)
	protected TextView txtDate;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.content)
	protected View contentView;

	@Nullable
	private Restaurant mRestaurant;

	private boolean mFirstStart = true;

	public DinnerDetailsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mRestaurant = getArguments().getParcelable(ARG_RESTAURANT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_dinner_details, container, false);
		ButterKnife.inject(this, view);
		AndroidUtils.applyFont(view.getContext(), (ViewGroup) view, OmnomFont.LSF_LE_REGULAR);
		contentView.setTranslationY(CONTENT_TRANSITION_Y);
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

	@OnClick(R.id.txt_date)
	public void onDate() {
		// TODO:
	}

	@OnClick(R.id.txt_address)
	public void onAddress() {
		// TODO:
	}

	@OnClick(R.id.btn_close)
	public void onClose() {
		getFragmentManager().popBackStack();
	}

	@OnClick(R.id.btn_ok)
	public void onOk() {
		// TODO:
	}

}
