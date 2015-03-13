package com.omnom.android.fragment.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItems;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewFilter;
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

	public static final String TAG = MenuItemDetailsFragment.class.getSimpleName();

	private static final ViewFilter sViewFilter = new ViewFilter() {
		@Override
		public boolean filter(final View v) {
			return v != null && v.getId() != R.id.divider;
		}
	};

	public static Fragment newInstance(Menu menu, final UserOrder order, final Item item, final int translationContent,
	                                   final int translationButton) {
		final MenuItemDetailsFragment fragment = new MenuItemDetailsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(Extras.EXTRA_ORDER, order);
		args.putInt(Extras.EXTRA_TRANSLATION_CONTENT, translationContent);
		args.putInt(Extras.EXTRA_TRANSLATION_BUTTON, translationButton);
		args.putParcelable(Extras.EXTRA_MENU_ITEM, item);
		args.putParcelable(Extras.EXTRA_RESTAURANT_MENU, menu);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager manager, Menu menu, final UserOrder order, final Item item, final int translationY,
	                        final int top) {
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.fade_in,
		                            R.anim.fade_out_medium,
		                            R.anim.fade_in,
		                            R.anim.fade_out_medium)
		       .add(R.id.root, MenuItemDetailsFragment.newInstance(menu, order, item, translationY, top), MenuItemDetailsFragment.TAG)
		       .commit();
	}

	public static int addRecommendations(final Context context, LinearLayout container, Menu menu, UserOrder order, Item item,
	                                     View.OnClickListener onApplyListener, final View.OnClickListener itemClickListener) {
		int totalHeight = 0;
		if(item.hasRecommendations()) {
			final List<String> recommendations = item.recommendations();
			final LayoutInflater inflater = LayoutInflater.from(context);
			int index = 0;
			for(String recId : recommendations) {
				index++;
				final Item recommendedItem = MenuHelper.getItem(menu, recId);

				final View itemView = inflater.inflate(R.layout.item_menu_dish, null, false);
				itemView.setOnClickListener(itemClickListener);

				MenuCategoryItems.ViewHolder holder = new MenuCategoryItems.ViewHolder(itemView);
				holder.updateState(order, recommendedItem);
				holder.bind(recommendedItem, order, menu, -1, false, false);
				holder.showDivider(index != recommendations.size());

				itemView.setTag(holder);
				itemView.setTag(R.id.item, recommendedItem);

				final View btnApply = itemView.findViewById(R.id.btn_apply);
				btnApply.setOnClickListener(onApplyListener);
				btnApply.setTag(R.id.item, recommendedItem);

				itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
				totalHeight += itemView.getMeasuredHeight();
				ViewUtils.setHeight(itemView, 0);
				container.addView(itemView);
			}
			inflater.inflate(R.layout.view_recommendations_footer, container, true);
		}
		return totalHeight;
	}

	public static void removeRecommendations(final LinearLayout container, final boolean animate) {
		if(animate) {
			AnimationUtils.scaleHeight(container, 0, new Runnable() {
				@Override
				public void run() {
					ViewUtils.removeChilds(container, sViewFilter);
				}
			}, 350);
		} else {
			ViewUtils.removeChilds(container, sViewFilter);
		}
	}

	@InjectView(R.id.txt_info_additional)
	protected TextView mTxtAdditional;

	@InjectView(R.id.txt_info_energy)
	protected TextView mTxtEnergy;

	@InjectView(R.id.scroll)
	protected ScrollView mScroll;

	@InjectView(R.id.panel_bottom)
	protected LinearLayout mPanelRecommendations;

	protected UserOrder mOrder;

	private MenuCategoryItems.ViewHolder holder;

	private Item mItem;

	private Menu mMenu;

	private View mView;

	private int mImgHeight;

	private int mTranslationTop;

	private TextView energy;

	private TextView additional;

	private TextView info;

	private TextView title;

	private View rl;

	private ImageView logo;

	private ImageView iv;

	private int mDuration;

	private int mPaddingTop;

	private int mApplyTop;

	private boolean mAnimate = true;

	@OnClick(R.id.btn_close)
	public void onClose() {
		if(mScroll.getScrollY() != 0) {
			mScroll.smoothScrollTo(0, 0);
			mView.postDelayed(new Runnable() {
				@Override
				public void run() {
					animateClose();
				}
			}, mDuration);
		} else {
			animateClose();
		}
	}

	private void animateClose() {
		final int imgSize = getResources().getDimensionPixelSize(R.dimen.menu_dish_image_height);

		final View btnApply = mView.findViewById(R.id.btn_apply);
		final int top = btnApply.getTop();

		final boolean noPhoto = TextUtils.isEmpty(mItem.photo());

		if(mApplyTop != 0 && noPhoto) {
			btnApply.animate().translationY(mApplyTop + (title.getTop() - top)).setDuration(mDuration).start();
		}

		if(mTranslationTop > 0) {
			mView.findViewById(R.id.root).animate().translationY(mTranslationTop).setDuration(mDuration).start();
		}

		if(!noPhoto) {
			ViewUtils.setVisible(info, false);
			ViewUtils.setVisible(additional, false);
			ViewUtils.setVisible(energy, false);

			AnimationUtils.scaleHeight(logo, imgSize, mDuration);
			rl.animate().translationY(mPaddingTop).setDuration(mDuration).start();
			title.animate().translationY(-imgSize).setDuration(mDuration).start();
		} else {
			ViewUtils.setVisible2(info, false);
			ViewUtils.setVisible2(additional, false);
			ViewUtils.setVisible2(energy, false);
		}

		title.setSingleLine(true);
		title.setMaxLines(1);

		iv.postDelayed(new Runnable() {
			@Override
			public void run() {
				getFragmentManager().popBackStack();
			}
		}, mDuration);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
	                         @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(Extras.EXTRA_ORDER);
			mItem = getArguments().getParcelable(Extras.EXTRA_MENU_ITEM);
			mMenu = getArguments().getParcelable(Extras.EXTRA_RESTAURANT_MENU);
			mTranslationTop = getArguments().getInt(Extras.EXTRA_TRANSLATION_CONTENT, 0);
			mApplyTop = getArguments().getInt(Extras.EXTRA_TRANSLATION_BUTTON, 0);
		}
		if(mOrder == null) {
			mOrder = UserOrder.create();
		}
		mDuration = getResources().getInteger(R.integer.default_animation_duration_short);
		mPaddingTop = getResources().getDimensionPixelSize(R.dimen.view_size_default);
		return getActivity().getLayoutInflater().inflate(R.layout.fragment_menu_item_details, null);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		mView = view;
		iv = (ImageView) mView.findViewById(R.id.img_icon);
		logo = (ImageView) mView.findViewById(R.id.img_logo);
		rl = mView.findViewById(R.id.panel_container);
		title = (TextView) mView.findViewById(R.id.txt_title);
		info = (TextView) mView.findViewById(R.id.txt_info);
		additional = (TextView) mView.findViewById(R.id.txt_info_additional);
		energy = (TextView) mView.findViewById(R.id.txt_info_energy);

		final View viewRoot = view.findViewById(R.id.root);
		ButterKnife.inject(this, view);
		holder = new MenuCategoryItems.ViewHolder(viewRoot, this, null);
		holder.showDivider(false);
		refresh();

		final String description = mItem.description();
		ViewUtils.setVisible(mTxtAdditional, !TextUtils.isEmpty(description));
		mTxtAdditional.setText(description);

		MenuHelper.bindNutritionalValue(view.getContext(), mItem.details(), mTxtEnergy);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(mAnimate) {
			if(mTranslationTop > 0) {
				mView.findViewById(R.id.root).animate().translationY(mTranslationTop).setDuration(0).start();
			}

			ViewUtils.setVisible(info, false);
			ViewUtils.setVisible(additional, false);
			ViewUtils.setVisible(energy, false);

			final boolean emptyPhoto = TextUtils.isEmpty(mItem.photo());
			if(emptyPhoto) {
				ViewUtils.setVisible(rl, false);
			} else {
				rl.animate().translationY(mPaddingTop).setDuration(0).start();
				title.animate().translationY(-getResources().getDimensionPixelSize(R.dimen.menu_dish_image_height)).setDuration(0).start();
			}
			OmnomApplication.getPicasso(getActivity()).load(mItem.photo()).into(logo);
			iv.post(new Runnable() {
				@Override
				public void run() {
					mImgHeight = iv.getHeight();
					ViewUtils.setVisible(iv, false);
				}
			});
			iv.postDelayed(new Runnable() {
				@Override
				public void run() {
					mView.findViewById(R.id.root).animate().translationY(0).setDuration(mDuration).start();
					rl.animate().translationY(0).setDuration(mDuration).start();
					title.animate().translationY(0).setDuration(mDuration).start();

					title.setSingleLine(false);
					title.setMaxLines(2);

					AnimationUtils.scaleHeight(logo, mImgHeight, mDuration);
					AnimationUtils.animateAlpha(info, info.getText().length() > 0);
					AnimationUtils.animateAlpha(additional, additional.getText().length() > 0);
					AnimationUtils.animateAlpha(energy, energy.getText().length() > 0);
				}
			}, emptyPhoto ? 0 : getResources().getInteger(R.integer.default_animation_duration_quick));
			mAnimate = false;
		}
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
		MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, mItem, -1);
	}

	@Override
	public void onClick(final View v) {
		if(v.getId() == R.id.btn_apply) {
			final Item item = (Item) v.getTag(R.id.item);
			if(item != null) {
				MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, item, -1);
			}
		}
	}
}
