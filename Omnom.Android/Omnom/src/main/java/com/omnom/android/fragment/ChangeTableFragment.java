package com.omnom.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.UserProfileActivity;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ClickSpan;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by Ch3D on 06.01.2015.
 */
public class ChangeTableFragment extends BaseFragment {

	private static final String ARG_TABLE_NUMBER = "table_number";

	public static Fragment newInstance(final int tableNumber) {
		final Fragment fragment = new ChangeTableFragment();
		final Bundle bundle = new Bundle();
		bundle.putInt(ARG_TABLE_NUMBER, tableNumber);
		fragment.setArguments(bundle);
		return fragment;
	}

	@InjectView(R.id.panel_bottom)
	protected View panelBottom;

	@InjectView(R.id.txt_table_number)
	protected TextView txtTableNumber;

	@InjectView(R.id.txt_info)
	protected TextView txtInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_change_table, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	@OnClick(R.id.btn_close)
	protected void onClose() {
		getActivity().onBackPressed();
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		txtTableNumber.setText(getString(R.string.table_number, getTableNumber()));
		AndroidUtils.clickify(txtInfo, getString(R.string.change_table_mark), new ClickSpan.OnClickListener() {
			@Override
			public void onClick() {
                getActivity().getSupportFragmentManager().popBackStack();
				((UserProfileActivity) getActivity()).changeTable();
			}
		});
		CalligraphyUtils.applyFontToTextView(getActivity(), txtInfo, "fonts/Futura-LSF-Omnom-LE-Regular.otf");
	}

	private int getTableNumber() {
		final Bundle arguments = getArguments();
		if (arguments != null) {
			return arguments.getInt(ARG_TABLE_NUMBER);
		} else {
			return -1;
		}
	}

}
