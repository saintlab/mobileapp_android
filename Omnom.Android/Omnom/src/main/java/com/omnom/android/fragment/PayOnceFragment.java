package com.omnom.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.CardConfirmActivity;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mvpotter on 12/3/2014.
 */
public class PayOnceFragment extends BaseFragment {

	/**
	 * Listener for pay button click event.
	 */
	public interface OnPayListener {
		void pay();
	}

	/**
	 * Listener for fragment visibility state.
	 */
	public interface VisibilityListener {
		void onVisibilityChanged(boolean isVisible);
	}

	private static final String ARG_AMOUNT = "amount";

	private static final String ARG_TYPE = "type";

	public static Fragment newInstance(final double amount, final int type) {
		final PayOnceFragment fragment = new PayOnceFragment();
		final Bundle args = new Bundle();
		args.putDouble(ARG_AMOUNT, amount);
		args.putInt(ARG_TYPE, type);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(R.id.btn_pay)
	protected Button btnPay;

	@InjectView(R.id.txt_sms_notifications_off)
	protected TextView txtSmsNotificationsOff;

	@InjectView(R.id.txt_pay_once)
	protected TextView txtPayOnce;

	@InjectView(R.id.txt_title)
	protected TextView txtTitle;

	@InjectView(R.id.btn_close)
	protected ImageButton btnClose;

	private OnPayListener mPayListener;

	private VisibilityListener mVisibilityListener;

	private double mAmount;

	private int mType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mAmount = getArguments().getDouble(ARG_AMOUNT);
			mType = getArguments().getInt(ARG_TYPE);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		addOnPayListener(activity);
		addFragmentVisibilityListener(activity);
		if(mVisibilityListener != null) {
			mVisibilityListener.onVisibilityChanged(true);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if(mVisibilityListener != null) {
			mVisibilityListener.onVisibilityChanged(false);
		}
	}

	private void addOnPayListener(Activity activity) {
		try {
			mPayListener = (OnPayListener) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnPayListener");
		}
	}

	private void addFragmentVisibilityListener(Activity activity) {
		try {
			mVisibilityListener = (VisibilityListener) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement VisibilityListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_pay_once, container, false);
		ButterKnife.inject(this, view);
		AndroidUtils.applyFont(getActivity(), btnPay, OmnomFont.LSF_LE_REGULAR);
		AndroidUtils.applyFont(getActivity(), txtSmsNotificationsOff, OmnomFont.LSF_LE_REGULAR);
		AndroidUtils.applyFont(getActivity(), txtPayOnce, OmnomFont.LSF_LE_REGULAR);
		AndroidUtils.applyFont(getActivity(), txtTitle, OmnomFont.LSF_LE_REGULAR);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		if(mAmount > 0) {
			final String text = AmountHelper.format(mAmount) + getActivity().getString(R.string.currency_suffix_ruble);
			if(mType == CardConfirmActivity.TYPE_BIND_CONFIRM) {
				btnPay.setText(getString(R.string.pay_amount, text));
			} else {
				btnPay.setText(getString(R.string.enter_card_data));
			}
			btnPay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mPayListener != null) {
						// remove visibility listener to keep keyboard hidden on frame close
						mVisibilityListener = null;
						mPayListener.pay();
					}
				}
			});

			ViewUtils.setBackgroundDrawableColor(btnPay, getResources().getColor(R.color.btn_pay_green));
		} else {
			ViewUtils.setVisibleGone(btnPay, false);
			ViewUtils.setVisibleGone(txtPayOnce, false);
		}
	}

}
