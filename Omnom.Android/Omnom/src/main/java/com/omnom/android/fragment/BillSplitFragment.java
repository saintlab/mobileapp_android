package com.omnom.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.adapter.BillSplitPagerAdapter;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.view.HeaderView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 11.11.2014.
 */
public class BillSplitFragment extends Fragment {
	public static final String TAG = BillSplitFragment.class.getSimpleName();

	private static final String ARG_ORDER = "order";

	public static BillSplitFragment newInstance(final Order order) {
		final BillSplitFragment fragment = new BillSplitFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(R.id.pager)
	protected ViewPager mPager;

	@InjectView(R.id.pager_title)
	protected PagerTabStrip mPagerTitle;

	@InjectView(R.id.btn_commit)
	protected Button mBtnCommit;

	@InjectView(R.id.panel_top)
	protected HeaderView mHeader;

	private Order mOrder;

	private View mFragmentView;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final View view = inflater.inflate(R.layout.fragment_bill_split, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		mFragmentView = view;
		mFragmentView.setTranslationY(-800);
		mFragmentView.setAlpha(0);
		mFragmentView.animate().alpha(1).translationY(0).start();

		mHeader.setTitleBig(R.string.split_the_bill);
		mHeader.setButtonLeftDrawable(R.drawable.com_mixpanel_android_close, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				getActivity().onBackPressed();
			}
		});

		final BillSplitPagerAdapter adapter = new BillSplitPagerAdapter(getChildFragmentManager(), mOrder);
		mPager.setAdapter(adapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(final int i, final float v, final int i2) {
				final Fragment currentFragment = adapter.getCurrentFragment();
				if(currentFragment instanceof SplitFragment) {
					final SplitFragment f = (SplitFragment) currentFragment;
					f.updateAmount();
				}
			}

			@Override
			public void onPageSelected(final int i) {
			}

			@Override
			public void onPageScrollStateChanged(final int i) {
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
		}
	}
}
