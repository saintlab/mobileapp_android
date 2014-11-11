package com.omnom.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.view.NumberPicker;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 11.11.2014.
 */
public class BillSplitPersonsFragment extends Fragment implements NumberPicker.OnValueChangeListener, SplitFragment {
	private static final String ARG_ORDER = "order";

	public static Fragment newInstance(final Order order) {
		final BillSplitPersonsFragment fragment = new BillSplitPersonsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(R.id.picker)
	protected NumberPicker mPicker;

	@InjectView(R.id.txt_info)
	protected TextView mTxtInfo;

	private Order mOrder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
		}
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		mPicker.setMinValue(1);
		mPicker.setMaxValue(40);
		mPicker.setOnValueChangedListener(this);
		mPicker.setValue(1);
		mPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		onValueChanged(1);
		updateAmount();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
	                         @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final View view = inflater.inflate(R.layout.fragment_bill_split_person, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	@Override
	public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
		onValueChanged(newVal);
	}

	private void onValueChanged(final int newVal) {
		mTxtInfo.setText(String.valueOf(newVal));
		updateAmount();
	}

	private BigDecimal getAmount() {
		final int value = mPicker.getValue();
		final double amountToPay = mOrder.getAmountToPay();
		final double v = amountToPay / value;
		final BigDecimal bigDecimal = new BigDecimal(String.valueOf(v));
		return bigDecimal.setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public void updateAmount() {
		final Button btnCommit = (Button) getActivity().findViewById(R.id.btn_commit);
		btnCommit.setText(getString(R.string.bill_split_amount_, StringUtils.formatCurrency(getAmount())));
		AnimationUtils.animateAlpha(btnCommit, true);
	}
}
