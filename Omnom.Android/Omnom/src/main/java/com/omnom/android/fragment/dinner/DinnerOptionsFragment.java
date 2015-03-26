package com.omnom.android.fragment.dinner;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.DateUtils;
import com.omnom.android.view.support.ItemClickSupport;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DinnerOptionsFragment extends BaseFragment {
	public static final int TYPE_ADDRESS = 0;

	public static final int TYPE_DATE = 1;

	private static final String ARG_TITLE = "title";

	private static final String ARG_INFO = "info";

	private static final String ARG_TYPE = "type";

	private static final String ARG_DATE_DATA = "data_date";

	private static final String ARG_ADDRESS_DATA = "data_address";

	public static void showDate(final FragmentManager fragmentManager, final @IdRes int containerId, ArrayList<String> data) {
		int titleId = R.string.when;
		int infoId = R.string.choose_when_you_want_to_pick_your_order;
		fragmentManager.beginTransaction()
		               .addToBackStack(null)
		               .setCustomAnimations(R.anim.fade_in,
		                                    R.anim.slide_out_down,
		                                    R.anim.fade_in,
		                                    R.anim.slide_out_down)
		               .add(containerId, DinnerOptionsFragment.newInstanceDate(TYPE_DATE, titleId, infoId, data))
		               .commit();
	}

	public static void showAddress(final FragmentManager fragmentManager, final @IdRes int containerId, ArrayList<AddressData> data) {
		int titleId = R.string.where;
		int infoId = R.string.choose_delivery_locations;
		fragmentManager.beginTransaction()
		               .addToBackStack(null)
		               .setCustomAnimations(R.anim.fade_in,
		                                    R.anim.slide_out_down,
		                                    R.anim.fade_in,
		                                    R.anim.slide_out_down)
		               .add(containerId, DinnerOptionsFragment.newInstanceAddress(TYPE_ADDRESS, titleId, infoId, data))
		               .commit();
	}

	public static DinnerOptionsFragment newInstanceDate(int type, @StringRes int titleId, @StringRes int infoId, ArrayList<String> data) {
		DinnerOptionsFragment fragment = new DinnerOptionsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TITLE, titleId);
		args.putInt(ARG_INFO, infoId);
		args.putInt(ARG_TYPE, type);
		args.putStringArrayList(ARG_DATE_DATA, data);
		fragment.setArguments(args);
		return fragment;
	}

	public static DinnerOptionsFragment newInstanceAddress(int type, @StringRes int titleId, @StringRes int infoId,
	                                                       ArrayList<AddressData> data) {
		DinnerOptionsFragment fragment = new DinnerOptionsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TITLE, titleId);
		args.putInt(ARG_INFO, infoId);
		args.putInt(ARG_TYPE, type);
		args.putParcelableArrayList(ARG_ADDRESS_DATA, data);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(R.id.txt_title)
	protected TextView mTxtTitle;

	@InjectView(R.id.txt_info)
	protected TextView mTxtInfo;

	@InjectView(android.R.id.list)
	protected RecyclerView mRecyclerView;

	@InjectView(R.id.root)
	protected View rootView;

	private int mTitleId;

	private int mInfoId;

	private DinnerDataAdapterBase mAdapter;

	private boolean mFirstStart = true;

	private int mType;

	private ArrayList<String> mDataDate;

	private ArrayList<AddressData> mAddressData;

	public DinnerOptionsFragment() {
		// Required empty public constructor
	}

	@OnClick(R.id.btn_previous)
	public void onBack() {
		switch(mType) {
			case TYPE_ADDRESS:
				final AddressData addressData = (AddressData) mAdapter.getSelectedItem();
				if(addressData != null) {
					mBus.post(addressData);
				}
				break;

			case TYPE_DATE:
				final String selectedItem = (String) mAdapter.getSelectedItem();
				if(!TextUtils.isEmpty(selectedItem)) {
					final Date date = DateUtils.parseDate(DateUtils.DATE_FORMAT_DDMMYYYY, selectedItem);
					mBus.post(date);
				}
				break;
		}
		getFragmentManager().popBackStack();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mTitleId = getArguments().getInt(ARG_TITLE, -1);
			mInfoId = getArguments().getInt(ARG_INFO, -1);
			mType = getArguments().getInt(ARG_TYPE, -1);
			mDataDate = getArguments().getStringArrayList(ARG_DATE_DATA);
			mAddressData = getArguments().getParcelableArrayList(ARG_ADDRESS_DATA);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_dinner_options, container, false);
		ButterKnife.inject(this, view);
		AndroidUtils.applyFont(view.getContext(), (ViewGroup) view, OmnomFont.LSF_LE_REGULAR);
		AndroidUtils.applyFont(view.getContext(), mTxtTitle, OmnomFont.OSF_MEDIUM);
		rootView.setTranslationY(AndroidUtils.getScreenHeightPixels(getActivity()));
		return view;
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if(mTitleId != -1) {
			mTxtTitle.setText(getResources().getString(mTitleId));
		}
		if(mInfoId != -1) {
			mTxtInfo.setText(getResources().getString(mInfoId));
		}

		final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);
		itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClick(final RecyclerView parent, final View view, final int position, final long id) {
				mAdapter.setItemChecked(position, !mAdapter.isItemChecked(position));
				mAdapter.notifyItemChanged(position);
			}
		});

		if(mType == TYPE_ADDRESS) {
			mAdapter = new DinnerAddressAdapter(getActivity(), mAddressData);
		}
		if(mType == TYPE_DATE) {
			mAdapter = new DinnerDateAdapter(getActivity(), mDataDate);
		}

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
	}

	@Override
	public void onStart() {
		super.onStart();
		if(mFirstStart) {
			rootView.postDelayed(new Runnable() {
				@Override
				public void run() {
					rootView.animate().translationY(0).start();
				}
			}, getResources().getInteger(R.integer.default_animation_duration_short));
		}
		mFirstStart = false;
	}
}
