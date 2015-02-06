package com.omnom.android.fragment.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.adapter.MenuModifiersAdapter;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Modifier;
import com.omnom.android.menu.model.Modifiers;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.utils.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MenuItemAddFragment extends BaseFragment {

	public static final int CONTENT_TRANSITION_Y = 600;

	private static final String ARG_ORDER = "order";

	private static final String ARG_ITEM = "item";

	private static final String ARG_MODIFIERS = "modifiers";

	public static MenuItemAddFragment newInstance(final Modifiers modifiers, UserOrder order, Item item) {
		final MenuItemAddFragment fragment = new MenuItemAddFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		args.putParcelable(ARG_ITEM, item);
		args.putParcelable(ARG_MODIFIERS, modifiers);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager fragmentManager, Modifiers modifiers, final UserOrder order, final Item item) {
		fragmentManager.beginTransaction()
		               .addToBackStack(null)
		               .setCustomAnimations(R.anim.fade_in,
		                                    R.anim.nothing_long,
		                                    R.anim.fade_in,
		                                    R.anim.nothing_long)
		               .add(R.id.root, MenuItemAddFragment.newInstance(modifiers, order, item))
		               .commit();
	}

	@InjectView(R.id.content)
	protected View contentView;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.txt_count)
	protected TextView txtCount;

	@InjectView(android.R.id.list)
	protected ExpandableListView mExpandableListView;

	private int mCount;

	private int mItemsMax;

	private int mItemsMin;

	private Item mItem;

	private UserOrder mOrder;

	private boolean mFirstStart = true;

	private int mDefaultCount;

	private Modifiers mModifiers;

	private MenuModifiersAdapter adapter;

	public MenuItemAddFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
			mItem = getArguments().getParcelable(ARG_ITEM);
			mModifiers = getArguments().getParcelable(ARG_MODIFIERS);
			if(mItem != null && mOrder != null && mOrder.itemsTable() != null) {
				final UserOrderData userOrderData = mOrder.itemsTable().get(mItem.id());
				mCount = getInitialCount(userOrderData);
			}
		}
	}

	private int getInitialCount(final UserOrderData userOrderData) {
		if(userOrderData != null) {
			final int amount = userOrderData.amount();
			return amount > 0 ? amount : mDefaultCount;
		} else {
			return mDefaultCount;
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
		contentView.animate().translationY(rootView.getHeight()).setListener(new AnimatorListenerAdapter() {
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
		mDefaultCount = getResources().getInteger(R.integer.menu_order_items_default);
		mCount = mDefaultCount;
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		List<Modifier> dishModifiers = new ArrayList<Modifier>();
		if(true) {
			// TODO: Debug code - remove it when done
			final ArrayList<String> idList = new ArrayList<String>();
			idList.add("28-in-saintlab-rkeeper-v6");
			idList.add("15-in-saintlab-rkeeper-v6");
			final Modifier modifier = Modifier.create(StringUtils.EMPTY_STRING, "Кухня-1", idList, "multiselect");
			final Modifier modifier2 = Modifier.create("15-in-saintlab-rkeeper-v6", "checkbox");
			dishModifiers.add(modifier2);
			dishModifiers.add(modifier);
		} else {
			dishModifiers = mItem.modifiers();
		}

		adapter = new MenuModifiersAdapter(view.getContext(), mModifiers, dishModifiers);
		mExpandableListView.setAdapter(adapter);
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
			mBus.post(new OrderUpdateEvent(mItem, mCount));
		}
		getFragmentManager().popBackStack();
	}

	private void refreshUi() {
		txtCount.setText(Integer.toString(mCount));
	}
}
