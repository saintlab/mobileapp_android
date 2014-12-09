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
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;

import butterknife.ButterKnife;

/**
 * Created by mvpotter on 12/3/2014.
 */
public class PayOnceFragment extends Fragment {

	private static final String ARG_AMOUNT = "amount";

	private OnPayListener mPayListener;

	private VisibilityListener mVisibilityListener;

	private double mAmount;

	public static Fragment newInstance(final double amount) {
		final PayOnceFragment fragment = new PayOnceFragment();
		final Bundle args = new Bundle();
		args.putDouble(ARG_AMOUNT, amount);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mAmount = getArguments().getDouble(ARG_AMOUNT);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		addOnPayListener(activity);
		addFragmentVisibilityListener(activity);
		if (mVisibilityListener != null) {
			mVisibilityListener.onVisibilityChanged(true);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (mVisibilityListener != null) {
			mVisibilityListener.onVisibilityChanged(false);
		}
	}

	private void addOnPayListener(Activity activity) {
		try {
			mPayListener = (OnPayListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnPayListener");
		}
	}

	private void addFragmentVisibilityListener(Activity activity) {
		try {
			mVisibilityListener = (VisibilityListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement VisibilityListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_pay_once, container, false);
		ButterKnife.inject(this, view);
		ImageButton btnClose = (ImageButton) view.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		Button btnPay = (Button) view.findViewById(R.id.btn_pay);
		TextView txtPayOnce = (TextView) view.findViewById(R.id.txt_pay_once);
		if (mAmount > 0) {
			final String text = StringUtils.formatCurrency(mAmount) + getString(R.string.currency_ruble);
			btnPay.setText(getString(R.string.pay_amount, text));
			btnPay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mPayListener != null) {
						// remove visibility listener to keep keyboard hidden on frame close
						mVisibilityListener = null;
						mPayListener.pay();
					}
				}
			});
		} else {
			ViewUtils.setVisible(btnPay, false);
			ViewUtils.setVisible(txtPayOnce, false);
		}
		return view;
	}

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

}
