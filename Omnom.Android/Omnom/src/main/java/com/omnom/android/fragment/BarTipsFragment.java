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
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.restaurateur.model.order.OrderHelper;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.view.NumberPicker;

import java.math.BigDecimal;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Ch3D on 08.05.2015.
 */
public class BarTipsFragment extends BaseFragment {

	public static final int MIN_VALUE = 0;

	public static final int MAX_VALUE = 200;

	public static final String PERCENT_SUFFIX = "%";

	public static final String SYM_PLUS = "+";

	private static final String ARG_VALUE = "tips_value";

	private static final String ARG_AMOUNT = "order_amount";

	public static BarTipsFragment newInstance(int currenValue, final double amount) {
		final BarTipsFragment fragment = new BarTipsFragment();
		final Bundle args = new Bundle();
		args.putInt(ARG_VALUE, currenValue);
		args.putDouble(ARG_AMOUNT, amount);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager fragmentManager, final @IdRes int containerId, final double amount, final int tipValue) {
		fragmentManager.beginTransaction()
		               .addToBackStack(null)
		               .setCustomAnimations(R.anim.fade_in,
		                                    R.anim.nothing_long,
		                                    R.anim.fade_in,
		                                    R.anim.nothing_long)
		               .add(containerId, BarTipsFragment.newInstance(tipValue, amount))
		               .commit();
	}

	@InjectView(R.id.content)
	protected View contentView;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.btn_ok)
	protected Button btnOk;

	@InjectView(R.id.txt_info)
	protected TextView txtInfo;

	@InjectView(R.id.picker)
	protected NumberPicker tipsPicker;

	private boolean mFirstStart = true;

	private double mAmount;

	private int mTipsValue;

	public BarTipsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mAmount = getArguments().getDouble(ARG_AMOUNT, 0);
			mTipsValue = getArguments().getInt(ARG_VALUE, 0);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_bar_tips, container, false);
		ButterKnife.inject(this, view);
		AndroidUtils.applyFont(view.getContext(), (ViewGroup) view, OmnomFont.LSF_LE_REGULAR);
		contentView.setTranslationY(AndroidUtils.getScreenHeightPixels(getActivity()));
		return view;
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tipsPicker.setMinValue(MIN_VALUE);
		tipsPicker.setMaxValue(MAX_VALUE);

		final String[] displayedValues = new String[MAX_VALUE + 1];
		for(int i = 0; i < MAX_VALUE + 1; i++) {
			displayedValues[i] = String.valueOf(i) + PERCENT_SUFFIX;
		}

		tipsPicker.setDisplayedValues(displayedValues);
		tipsPicker.setWrapSelectorWheel(false);
		tipsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
				mTipsValue = newVal;
				updateOkButton();
			}
		});
		tipsPicker.setValue(mTipsValue);
		updateOkButton();
	}

	private void updateOkButton() {
		final int tipsAmount = OrderHelper.getTipsAmount(BigDecimal.valueOf(mAmount), mTipsValue);
		btnOk.setText(SYM_PLUS + AmountHelper.format(tipsAmount) + getString(R.string.currency_suffix_ruble));
		txtInfo.setText(getString(tipsAmount > 0 ? R.string.barmen_tips_positive : R.string.barmen_tips_negative));
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
		final int tipValue = tipsPicker.getValue();
		mBus.post(new BarTipsEvent(tipValue));
		getFragmentManager().popBackStack();
	}

	@OnClick(R.id.btn_close)
	public void onClose() {
		getFragmentManager().popBackStack();
	}

}
