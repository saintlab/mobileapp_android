package com.omnom.android.fragment.takeaway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omnom.android.R;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.view.NumberPicker;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TakeawayTimeFragment extends BaseFragment {

	public static final String[] DISPLAYED_VALUES = new String[]{
			"Через 5 минут", "Через 15 минут", "Через 30 минут",
			"Через час", "Через 1,5 часа", "Через 2 часа"
	};

	public static final int[] TIME_VALUES = new int[]{
			5, 15, 30, 60, 90, 120
	};

	public static TakeawayTimeFragment newInstance() {
		return new TakeawayTimeFragment();
	}

	public static void show(final FragmentManager fragmentManager, final @IdRes int containerId) {
		fragmentManager.beginTransaction()
		               .addToBackStack(null)
		               .setCustomAnimations(R.anim.fade_in,
		                                    R.anim.nothing_long,
		                                    R.anim.fade_in,
		                                    R.anim.nothing_long)
		               .add(containerId, TakeawayTimeFragment.newInstance())
		               .commit();
	}

	@InjectView(R.id.content)
	protected View contentView;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.picker)
	protected NumberPicker timePicker;

	private boolean mFirstStart = true;

	public TakeawayTimeFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		final View view = inflater.inflate(R.layout.fragment_takeaway_time, container, false);
		ButterKnife.inject(this, view);
		AndroidUtils.applyFont(view.getContext(), (ViewGroup) view, OmnomFont.LSF_LE_REGULAR);
		contentView.setTranslationY(AndroidUtils.getScreenHeightPixels(getActivity()));
		return view;
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		timePicker.setMinValue(0);
		timePicker.setMaxValue(5);
		timePicker.setDisplayedValues(DISPLAYED_VALUES);
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

	@OnClick(R.id.btn_ok)
	public void onOk() {
		final int timeValue = TIME_VALUES[timePicker.getValue()];
		mBus.post(new TakeawayTimePickedEvent(timeValue));
		getFragmentManager().popBackStack();
	}

	@OnClick(R.id.btn_close)
	public void onClose() {
		getFragmentManager().popBackStack();
	}

}
