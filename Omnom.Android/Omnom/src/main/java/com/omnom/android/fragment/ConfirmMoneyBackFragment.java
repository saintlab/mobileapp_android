package com.omnom.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.fragment.PayOnceFragment.VisibilityListener;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.utils.AndroidUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ConfirmMoneyBackFragment extends Fragment {

	public static Fragment newInstance() {
		return new ConfirmMoneyBackFragment();
	}

	@InjectView(R.id.txt_sms_notifications_off)
	protected TextView txtInfo;

	@InjectView(R.id.txt_title)
	protected TextView txtTitle;

	private VisibilityListener mVisibilityListener;

	public ConfirmMoneyBackFragment() {
		// Required empty public constructor
	}

	@OnClick(R.id.btn_close)
	protected void onClose() {
		getActivity().onBackPressed();
		mVisibilityListener = null;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		addFragmentVisibilityListener(activity);
		if(mVisibilityListener != null) {
			mVisibilityListener.onVisibilityChanged(true);
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
	public void onDetach() {
		super.onDetach();
		if(mVisibilityListener != null) {
			mVisibilityListener.onVisibilityChanged(false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_confirm_money_back, container, false);
		ButterKnife.inject(this, view);
		AndroidUtils.applyFont(getActivity(), txtInfo, OmnomFont.LSF_LE_REGULAR);
		AndroidUtils.applyFont(getActivity(), txtTitle, OmnomFont.LSF_LE_REGULAR);
		return view;
	}
}
