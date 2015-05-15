package com.omnom.android.fragment.menu;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.animation.MenuItemTransitionController;
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

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Ch3D on 03.02.2015.
 */
public class MenuItemDetailsFragment extends BaseFragment implements View.OnClickListener {

	public static final String TAG = MenuItemDetailsFragment.class.getSimpleName();

	public static class TransitionParams implements Parcelable {
		public static final Creator<TransitionParams> CREATOR = new Creator<TransitionParams>() {

			@Override
			public TransitionParams createFromParcel(Parcel in) {
				return new TransitionParams(in);
			}

			@Override
			public TransitionParams[] newArray(int size) {
				return new TransitionParams[size];
			}
		};

		public static TransitionParams create(final int titleSize,
		                                      final int translationContent,
		                                      final int translationButton,
		                                      final int btnMarginTop) {
			return new TransitionParams(titleSize, translationContent, translationButton, btnMarginTop);
		}

		private int mTranslationTop;

		private int mApplyTop;

		private int mApplyMarginTop;

		private int mPaddingTop;

		private TransitionParams(final int paddingTop, final int translationTop, final int applyTop, final int applyMarginTop) {
			mTranslationTop = translationTop;
			mApplyTop = applyTop;
			mApplyMarginTop = applyMarginTop;
			mPaddingTop = paddingTop;
		}

		public TransitionParams(final Parcel parcel) {
			mTranslationTop = parcel.readInt();
			mApplyTop = parcel.readInt();
			mApplyMarginTop = parcel.readInt();
			mPaddingTop = parcel.readInt();
		}

		@Override
		public String toString() {
			return "TransitionParams{" +
					"mTranslationTop=" + mTranslationTop +
					", mApplyTop=" + mApplyTop +
					", mApplyMarginTop=" + mApplyMarginTop +
					", mPaddingTop=" + mPaddingTop +
					'}';
		}

		public int getTranslationTop() {
			return mTranslationTop;
		}

		public int getApplyTop() {
			return mApplyTop;
		}

		public int getApplyMarginTop() {
			return mApplyMarginTop;
		}

		public int getPaddingTop() {
			return mPaddingTop;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			dest.writeInt(mTranslationTop);
			dest.writeInt(mApplyTop);
			dest.writeInt(mApplyMarginTop);
			dest.writeInt(mPaddingTop);
		}
	}

	private static final String ARG_POSITION = "position";

	private static final ViewFilter sViewFilter = new ViewFilter() {
		@Override
		public boolean filter(final View v) {
			return v != null && v.getId() != R.id.divider;
		}
	};

	public static Fragment newInstance(Menu menu, final UserOrder order, final Item item,
	                                   final int position,
	                                   TransitionParams params) {
		final MenuItemDetailsFragment fragment = new MenuItemDetailsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(Extras.EXTRA_ORDER, order);
		args.putInt(ARG_POSITION, position);
		args.putParcelable(Extras.EXTRA_MENU_ITEM, item);
		args.putParcelable(Extras.EXTRA_RESTAURANT_MENU, menu);
		args.putParcelable(Extras.EXTRA_TRANSITION_PARAMS, params);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager manager, Menu menu, final UserOrder order, final Item item,
	                        final int position,
	                        final int titleSize,
	                        final int translationY,
	                        final int top,
	                        final int btnMarginTop) {

		final TransitionParams params = TransitionParams.create(titleSize, translationY, top, btnMarginTop);
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.fade_in,
		                            R.anim.fade_out_medium,
		                            R.anim.fade_in,
		                            R.anim.fade_out_medium)
		       .add(R.id.root, MenuItemDetailsFragment.newInstance(menu, order, item, position, params),
		            MenuItemDetailsFragment.TAG)
		       .commit();
	}

	public static void show(final FragmentManager manager, Menu menu, final UserOrder order,
	                        final Item item, final int position, int titleSize) {
		final TransitionParams params = TransitionParams.create(titleSize, 0, 0, 0);
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.fade_in,
		                            R.anim.fade_out_medium,
		                            R.anim.fade_in,
		                            R.anim.fade_out_medium)
		       .add(R.id.root, MenuItemDetailsFragment.newInstance(menu, order, item, position, params), MenuItemDetailsFragment.TAG)
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

	private int mPosition;

	private Item mItem;

	private Menu mMenu;

	private View mView;

	private int mImgHeight;

	private TextView energy;

	private TextView additional;

	private TextView info;

	private TextView title;

	private View rl;

	private ImageView logo;

	private ImageView iv;

	private int mDuration;

	private boolean mAnimate = true;

	private TransitionParams mTransitionParams;

	private MenuItemTransitionController mTransitionController;

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
		mTransitionController.animateClose(mTransitionParams, !TextUtils.isEmpty(mItem.photo()), mDuration);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
	                         @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		mDuration = getResources().getInteger(R.integer.default_animation_duration_short);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(Extras.EXTRA_ORDER);
			mPosition = getArguments().getInt(ARG_POSITION);
			mItem = getArguments().getParcelable(Extras.EXTRA_MENU_ITEM);
			mMenu = getArguments().getParcelable(Extras.EXTRA_RESTAURANT_MENU);

			mTransitionParams = getArguments().getParcelable(Extras.EXTRA_TRANSITION_PARAMS);
		}
		if(mOrder == null) {
			mOrder = UserOrder.create();
		}
		mTransitionController = new MenuItemTransitionController(new WeakReference<FragmentActivity>(getActivity()));
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
			mTransitionController.onResume(mTransitionParams, mItem, mDuration);
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
		MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, mItem, mPosition);
	}

	@Override
	public void onClick(final View v) {
		if(v.getId() == R.id.btn_apply) {
			final Item item = (Item) v.getTag(R.id.item);
			if(item != null) {
				MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, item, mPosition);
			}
		}
	}
}
