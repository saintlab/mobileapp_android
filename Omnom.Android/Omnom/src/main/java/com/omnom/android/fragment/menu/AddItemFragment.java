package com.omnom.android.fragment.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.menu.MenuFragmentActivity;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddItemFragment extends Fragment {

	public static final int CONTENT_TRANSITION_Y = 400;

	private static final String ARG_ORDER = "order";

	private static final String ARG_ITEM = "item";

	public static AddItemFragment newInstance(UserOrder order, Item item) {
		final AddItemFragment fragment = new AddItemFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		args.putParcelable(ARG_ITEM, item);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager fragmentManager, @IdRes final int container, final UserOrder order, final Item item) {
		fragmentManager.beginTransaction()
		               .addToBackStack(null)
		               .setCustomAnimations(R.anim.fade_in,
		                                    R.anim.nothing_long,
		                                    R.anim.fade_in,
		                                    R.anim.nothing_long)
		               .replace(R.id.root, AddItemFragment.newInstance(order, item))
		               .commit();
	}

	@InjectView(R.id.content)
	protected View contentView;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.txt_count)
	protected TextView txtCount;

	private int mCount;

	private int mItemsMax;

	private int mItemsMin;

	private Item mItem;

	private UserOrder mOrder;

	private boolean mFirstStart = true;

	public AddItemFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
			mItem = getArguments().getParcelable(ARG_ITEM);
			if(mItem != null && mOrder != null && mOrder.itemsTable() != null) {
				final UserOrderData userOrderData = mOrder.itemsTable().get(mItem.id());
				mCount = userOrderData != null ? userOrderData.amount() : getResources().getInteger(R.integer.menu_order_items_default);
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if(mFirstStart) {
			contentView.postDelayed(new Runnable() {
				@Override
				public void run() {
					contentView.animate().translationY(0).start();
				}
			}, getResources().getInteger(R.integer.default_animation_duration_short));
		}
		mFirstStart = false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		contentView.animate().translationY(CONTENT_TRANSITION_Y).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				rootView.animate().alpha(0).start();
			}
		}).start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_menu_add_item, container, false);
		ButterKnife.inject(this, view);
		contentView.setTranslationY(CONTENT_TRANSITION_Y);
		return view;
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		mItemsMax = getResources().getInteger(R.integer.menu_order_items_max);
		mItemsMin = getResources().getInteger(R.integer.menu_order_items_min);
		mCount = getResources().getInteger(R.integer.menu_order_items_default);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		refreshUi();
	}

	@OnClick(R.id.btn_plus)
	protected void onPlus(View v) {
		increase();
		refreshUi();
	}

	@OnClick(R.id.btn_minus)
	protected void onMinus(View v) {
		decrease();
		refreshUi();
	}

	private void increase() {
		if(mCount < mItemsMax) {
			mCount++;
		}
	}

	private void decrease() {
		if(mCount > mItemsMin) {
			mCount--;
		}
	}

	@OnClick(R.id.btn_apply)
	public void onApply(View v) {
		if(mOrder != null && mItem != null) {
			MenuFragmentActivity activity = (MenuFragmentActivity) getActivity();
			if(activity != null) {
				activity.updateItem(mItem, mCount);
			}
		}
		getFragmentManager().popBackStack();
	}

	private void refreshUi() {
		txtCount.setText(Integer.toString(mCount));
	}
}
