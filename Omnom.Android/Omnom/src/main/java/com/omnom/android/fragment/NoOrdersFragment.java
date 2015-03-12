package com.omnom.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.ValidateActivity;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ClickSpan;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Ch3D on 06.01.2015.
 */
public class NoOrdersFragment extends Fragment {

	private static final String ARG_TABLE_NUMBER = "arg.tablenumber";

	public static Fragment newInstance(final int tableNumber) {
		final NoOrdersFragment fragment = new NoOrdersFragment();
		final Bundle args = new Bundle();
		args.putInt(ARG_TABLE_NUMBER, tableNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(R.id.panel_bottom)
	protected View panelBottom;

	@InjectView(R.id.txt_info)
	protected TextView txtInfo;

	private int mTableNumber;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mTableNumber = getArguments().getInt(ARG_TABLE_NUMBER);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_no_orders, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	@OnClick(R.id.btn_close)
	protected void onClose() {
		getFragmentManager().popBackStack();
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		txtInfo.setText(getString(R.string.no_order_info, String.valueOf(mTableNumber)));
		AndroidUtils.clickify(txtInfo, getString(R.string.no_order_info_mark), new ClickSpan.OnClickListener() {
			@Override
			public void onClick() {
				final ValidateActivity activity = (ValidateActivity) getActivity();
				if(activity == null) {
					getFragmentManager().popBackStack();
				} else {
					getFragmentManager().popBackStack();
					activity.changeTable();
				}
			}
		});
		AndroidUtils.applyFont(getActivity(), txtInfo, OmnomFont.LSF_LE_REGULAR);
	}
}
