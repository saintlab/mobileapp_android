package com.omnom.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.adapter.BillSplitPagerAdapter;
import com.omnom.android.fragment.events.OrderSplitCommitEvent;
import com.omnom.android.fragment.events.SplitHideEvent;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.SparseBooleanArrayParcelable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.HeaderView;
import com.squareup.otto.Bus;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 11.11.2014.
 */
public class BillSplitFragment extends Fragment {
	public static final String TAG = BillSplitFragment.class.getSimpleName();

	public static final int SPLIT_TYPE_PERSON = 1;

	public static final int SPLIT_TYPE_ITEMS = 2;

	private static final String ARG_TYPE = "type";

	private static final String ARG_ORDER = "order";

	private static final String ARG_STATES = "states";

	private static final String ARG_GUESTS = "guests";

	public static BillSplitFragment newInstance(final int splitType, final Order order,
	                                            final SparseBooleanArrayParcelable checkedStates,
	                                            final int guestsCount) {
		final BillSplitFragment fragment = new BillSplitFragment();
		final Bundle args = new Bundle();
		args.putInt(ARG_TYPE, splitType);
		args.putParcelable(ARG_ORDER, order);
		args.putParcelable(ARG_STATES, checkedStates);
		args.putInt(ARG_GUESTS, guestsCount);
		fragment.setArguments(args);
		return fragment;
	}

	@Inject
	protected Bus mBus;

	@InjectView(R.id.pager)
	protected ViewPager mPager;

	@InjectView(R.id.pager_title)
	protected PagerTabStrip mPagerTitle;

	@InjectView(R.id.btn_commit)
	protected Button mBtnCommit;

	@InjectView(R.id.panel_top)
	protected HeaderView mHeader;

	private int mType;

	private Order mOrder;

	private View mFragmentView;

	private SparseBooleanArrayParcelable mStates;

	private int mGuestsCount;

	private int mListHeight;

	private BillSplitPagerAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final View view = inflater.inflate(R.layout.fragment_bill_split, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		OmnomApplication.get(getActivity()).inject(this);
		mBus.register(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mBus.unregister(this);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		mFragmentView = view;
		mListHeight = getResources().getDimensionPixelSize(R.dimen.order_list_height);
		mFragmentView.setTranslationY(-mListHeight);
		mFragmentView.setAlpha(0.5f);
		mFragmentView.animate()
		             .alpha(1)
		             .translationY(0)
		             .start();

		AndroidUtils.applyFont(getActivity(), mBtnCommit, OmnomFont.LSF_LE_REGULAR);

		Class pagerTabStrip = PagerTabStrip.class;
		resetPageTitleSpacing(pagerTabStrip);
		mPagerTitle.setPadding((int) getResources().getDimension(R.dimen.bill_split_person_title_padding), 0,
		                       (int) getResources().getDimension(R.dimen.bill_split_person_title_padding), 0);
		for(int i = 0; i < mPagerTitle.getChildCount(); ++i) {
			View nextChild = mPagerTitle.getChildAt(i);
			if(nextChild instanceof TextView) {
				TextView textViewToConvert = (TextView) nextChild;
				AndroidUtils.applyFont(getActivity(), textViewToConvert, OmnomFont.OSF_REGULAR);
				textViewToConvert.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_normal));
			}
		}

		mBtnCommit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final BigDecimal tag = (BigDecimal) mBtnCommit.getTag(R.id.edit_amount);
				final int tagSplitType = (Integer) mBtnCommit.getTag(R.id.split_type);
				final Object guestsCountTag = mBtnCommit.getTag(R.id.picker);
				final int guestsCount = guestsCountTag == null ? 1 : (Integer) guestsCountTag;
				if(tag != null) {
					mBus.post(new OrderSplitCommitEvent(mOrder.getId(), guestsCount, mStates, tag, tagSplitType));
					hide();
				}
			}
		});

		ViewUtils.setBackgroundDrawableColor(mBtnCommit, getResources().getColor(R.color.btn_pay_green));

		mHeader.setTxtTitleMedium(R.string.split_the_bill)
		       .setButtonLeftDrawable(R.drawable.ic_cross_black, new View.OnClickListener() {
			       @Override
			       public void onClick(final View v) {
				       hide();
			       }
		       });

		mAdapter = new BillSplitPagerAdapter(getChildFragmentManager(), mOrder, mStates, mGuestsCount);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(final int i, final float v, final int i2) {
				final Fragment currentFragment = mAdapter.getCurrentFragment(i);
				if(currentFragment instanceof SplitFragment) {
					final SplitFragment f = (SplitFragment) currentFragment;
					f.updateAmount();
				}
			}

			@Override
			public void onPageSelected(final int i) {
				final Fragment currentFragment = mAdapter.getCurrentFragment(i);
				if(currentFragment instanceof SplitFragment) {
					final SplitFragment f = (SplitFragment) currentFragment;
					f.updateAmount();
				}
			}

			@Override
			public void onPageScrollStateChanged(final int i) {
				// Do nothing
			}
		});
		mPager.setCurrentItem(2 - mType);
	}

	// Dirty hack to reset PagerTabStrip spacing as titles went out screen border on mdpi devices.
	private void resetPageTitleSpacing(Class pagerTabStrip) {
		try {
			Field minTextSpacing = pagerTabStrip.getDeclaredField("mMinTextSpacing");
			minTextSpacing.setAccessible(true);
			minTextSpacing.set(mPagerTitle, 0);
		} catch(NoSuchFieldException e) {
			Log.d(TAG, e.getMessage());
		} catch(IllegalAccessException e) {
			Log.d(TAG, e.getMessage());
		}
		mPagerTitle.setTextSpacing(0);
	}

	public void hide() {
		final AnimatorSet as = new AnimatorSet();
		final ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFragmentView, View.TRANSLATION_X, mFragmentView.getTranslationX(),
		                                                     getResources().getDisplayMetrics().widthPixels);

		// FIXME: Hardcoded tag!
		final View order_page_0 = getActivity().getWindow().getDecorView().findViewWithTag("order_page_0");
		if(order_page_0 != null) {
			order_page_0.setTranslationX(-200);
			final ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(order_page_0, View.TRANSLATION_X, -200, 0);
			as.playTogether(scaleX, scaleX2);
		} else {
			as.playTogether(scaleX);
		}
		as.setInterpolator(new AccelerateDecelerateInterpolator());
		as.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				FragmentManager fragmentManager = getFragmentManager();
				if(fragmentManager != null) {
					fragmentManager.beginTransaction().remove(BillSplitFragment.this).commit();
				}

			}
		});
		as.start();

		mBus.post(new SplitHideEvent(mOrder.getId()));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mType = getArguments().getInt(ARG_TYPE, SPLIT_TYPE_ITEMS);
			mOrder = getArguments().getParcelable(ARG_ORDER);
			mStates = getArguments().getParcelable(ARG_STATES);
			mGuestsCount = getArguments().getInt(ARG_GUESTS, 1);
		}
	}

	public void onOrderUpdate(final Order order) {
		mOrder = order;
		final Fragment item1 = mAdapter.getCurrentFragment(0);
		if(item1 instanceof SplitFragment && !item1.isDetached() && !item1.isHidden()) {
			((SplitFragment) item1).onOrderUpdate(order);
		}
		final Fragment item2 = mAdapter.getCurrentFragment(1);
		if(item2 instanceof SplitFragment && !item2.isDetached() && !item2.isHidden()) {
			((SplitFragment) item2).onOrderUpdate(order);
		}
	}
}
