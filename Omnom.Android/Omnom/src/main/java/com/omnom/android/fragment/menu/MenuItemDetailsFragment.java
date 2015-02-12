package com.omnom.android.fragment.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Ch3D on 03.02.2015.
 */
public class MenuItemDetailsFragment extends BaseFragment implements View.OnClickListener {

	public static Fragment newInstance(Menu menu, final UserOrder order, final Item item) {
		final MenuItemDetailsFragment fragment = new MenuItemDetailsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(Extras.EXTRA_ORDER, order);
		args.putParcelable(Extras.EXTRA_MENU_ITEM, item);
		args.putParcelable(Extras.EXTRA_RESTAURANT_MENU, menu);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager manager, Menu menu, final UserOrder order, final Item item) {
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.slide_in_right,
		                            R.anim.slide_out_right,
		                            R.anim.slide_in_right,
		                            R.anim.slide_out_right)
		       .add(R.id.root, MenuItemDetailsFragment.newInstance(menu, order, item))
		       .commit();
	}

	public static boolean addRecommendations(final Context context, LinearLayout container, Menu menu, UserOrder order, Item item,
	                                         View.OnClickListener onApplyListener, final View.OnClickListener itemClickListener) {
		final boolean b = item.recommendations() != null && item.recommendations().size() + 1 != container.getChildCount() && item
				.hasRecommendations();
		if(b) {
			removeRecommendations(container, false);
		}

		final boolean hasRecommendations = item.hasRecommendations();
		ViewUtils.setVisible(container, hasRecommendations);

		if(hasRecommendations && b) {
			final List<String> recommendations = item.recommendations();
			final LayoutInflater inflater = LayoutInflater.from(context);
			int index = 0;
			for(String recId : recommendations) {
				index++;
				final Item recommendedItem = MenuHelper.getItem(menu, recId);

				final View itemView = inflater.inflate(R.layout.item_menu_dish, null, false);
				itemView.setOnClickListener(itemClickListener);
				itemView.setTag(recommendedItem);

				MenuCategoryItemsAdapter.ViewHolder holder = new MenuCategoryItemsAdapter.ViewHolder(itemView);
				holder.updateState(order, recommendedItem);
				holder.bind(recommendedItem, order, menu, false, false);
				holder.showDivider(index != recommendations.size());

				itemView.setTag(holder);
				itemView.setTag(R.id.item, recommendedItem);

				final View btnApply = itemView.findViewById(R.id.btn_apply);
				btnApply.setOnClickListener(onApplyListener);
				btnApply.setTag(R.id.item, recommendedItem);

				final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

				itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

				final int height = itemView.getMeasuredHeight();
				ViewUtils.setHeight(itemView, 0);
				container.addView(itemView);
			}
			// container.addView(inflater.inflate(R.layout.view_recommendations_footer, null, false));
		}
		return hasRecommendations;
	}

	public static void removeRecommendations(final LinearLayout container, final boolean animate) {
		if(animate) {
			AnimationUtils.scaleHeight(container, 0, new Runnable() {
				@Override
				public void run() {
					final int childCount = container.getChildCount();
					for(int i = 0; i < childCount; i++) {
						final View childAt = container.getChildAt(i);
						if(childAt != null && childAt.getId() != R.id.divider) {
							container.removeView(childAt);
						}
					}
				}
			});
		} else {
			final int childCount = container.getChildCount();
			for(int i = 0; i < childCount; i++) {
				final View childAt = container.getChildAt(i);
				if(childAt != null && childAt.getId() != R.id.divider) {
					container.removeView(childAt);
				}
			}
		}
	}

	@InjectView(R.id.txt_info_additional)
	protected TextView mTxtAdditional;

	@InjectView(R.id.txt_info_energy)
	protected TextView mTxtEnergy;

	@InjectView(R.id.panel_bottom)
	protected LinearLayout mPanelRecommendations;

	protected UserOrder mOrder;

	private MenuCategoryItemsAdapter.ViewHolder holder;

	private Item mItem;

	private Menu mMenu;

	@OnClick(R.id.btn_close)
	public void onClose() {
		getFragmentManager().popBackStack();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
	                         @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(Extras.EXTRA_ORDER);
			mItem = getArguments().getParcelable(Extras.EXTRA_MENU_ITEM);
			mMenu = getArguments().getParcelable(Extras.EXTRA_RESTAURANT_MENU);
		}
		if(mOrder == null) {
			mOrder = UserOrder.create();
		}
		return getActivity().getLayoutInflater().inflate(R.layout.fragment_menu_item_details, null);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		final View viewRoot = view.findViewById(R.id.root);
		ButterKnife.inject(this, view);
		holder = new MenuCategoryItemsAdapter.ViewHolder(viewRoot, this, null);
		holder.showDivider(false);
		refresh();

		final String description = mItem.description();
		ViewUtils.setVisible(mTxtAdditional, !TextUtils.isEmpty(description));
		mTxtAdditional.setText(description);

		MenuHelper.bindNutritionalValue(view.getContext(), mItem.details(), mTxtEnergy);
	}

	private void refresh() {
		holder.updateState(mOrder, mItem);
		holder.bindWithRecommendations(mItem, mOrder, mMenu, true);
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		if(event == null || event.getItem() == null) {
			return;
		}
		refresh();
	}

	@OnClick(R.id.btn_apply)
	public void onApply() {
		MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, mItem);
	}

	@Override
	public void onClick(final View v) {
		if(v.getId() == R.id.btn_apply) {
			final Item item = (Item) v.getTag(R.id.item);
			if(item != null) {
				MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, item);
			}
		}
	}
}
