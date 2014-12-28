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
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.view.NumberPicker;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by Ch3D on 11.11.2014.
 */
public class BillSplitPersonsFragment extends Fragment implements NumberPicker.OnValueChangeListener, SplitFragment {
	private static final String ARG_ORDER = "order";

	private static final String ARG_GUESTS = "guests";

	public static Fragment newInstance(final Order order, final int guestsCount) {
		final BillSplitPersonsFragment fragment = new BillSplitPersonsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		args.putInt(ARG_GUESTS, guestsCount);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(R.id.picker)
	protected NumberPicker mPicker;

	@InjectView(R.id.txt_question)
	protected TextView mTxtQuestion;

	@InjectView(R.id.txt_info)
	protected TextView mTxtInfo;

	private Order mOrder;

	private int mGuestsCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
			mGuestsCount = getArguments().getInt(ARG_GUESTS, 1);
		}
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		CalligraphyUtils.applyFontToTextView(getActivity(), mTxtQuestion, "fonts/Futura-OSF-Omnom-Regular.otf");
		mPicker.setMinValue(1);
		mPicker.setMaxValue(40);
		mPicker.setOnValueChangedListener(this);
		mPicker.setValue(mGuestsCount);
		mPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		// onValueChanged(1);
		// updateAmount();
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
		final double amountToPay = mOrder.getTotalAmount();
		final double v = amountToPay / value;
		final BigDecimal bigDecimal = new BigDecimal(String.valueOf(v));
		return bigDecimal.setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public void updateAmount() {
		final Button btnCommit = (Button) getActivity().findViewById(R.id.btn_commit);
		final BigDecimal amount = getAmount();
		btnCommit.setText(getString(R.string.bill_split_amount_, AmountHelper.format(amount)));
		btnCommit.setTag(R.id.edit_amount, amount);
		btnCommit.setTag(R.id.picker, mPicker.getValue());
		btnCommit.setTag(R.id.split_type, BillSplitFragment.SPLIT_TYPE_PERSON);
		AnimationUtils.animateAlpha(btnCommit, true);
	}
}
