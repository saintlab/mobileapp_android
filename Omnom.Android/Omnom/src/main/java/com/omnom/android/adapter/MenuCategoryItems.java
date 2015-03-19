package com.omnom.android.adapter;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.fragment.menu.MenuItemDetailsFragment;
import com.omnom.android.menu.model.Category;
import com.omnom.android.menu.model.Details;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.MenuItemState;
import com.omnom.android.menu.model.Modifier;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewFilter;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 27.01.2015.
 */
public class MenuCategoryItems {

	public static class ViewHolder {
		@Nullable
		private final View.OnClickListener mRecommendationClickListener;

		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		@InjectView(R.id.txt_description)
		@Optional
		@Nullable
		protected TextView txtDescription;

		@InjectView(R.id.panel_description)
		@Optional
		@Nullable
		protected View panelDescription;

		@InjectView(R.id.delimiter)
		@Optional
		protected View viewDelimiter;

		@InjectView(R.id.btn_apply)
		@Optional
		protected Button btnApply;

		@InjectView(R.id.panel_bottom)
		@Optional
		protected LinearLayout panelRecommendations;

		@InjectView(R.id.txt_info)
		@Optional
		protected TextView txtDetails;

		@InjectView(R.id.root)
		@Optional
		protected View root;

		@InjectView(R.id.img_icon)
		@Optional
		protected ImageView imgIcon;

		private MenuItemState mState = MenuItemState.NONE;

		private View.OnClickListener mClickListener;

		public ViewHolder(View convertView) {
			this(convertView, null, null);
		}

		public ViewHolder(final View convertView, final View.OnClickListener applyClickListener,
		                  final View.OnClickListener clickListener) {
			ButterKnife.inject(this, convertView);
			mRecommendationClickListener = applyClickListener;
			mClickListener = clickListener;
		}

		private Context getContext() {return btnApply.getContext();}

		public void bindWithRecommendations(final Item item, UserOrder order, Menu menu, boolean detailed) {
			if(item == null) {
				return;
			}

			bindDetails(item, detailed);
			bindImage(item);
			MenuHelper.bindTitle(txtTitle, item);
			btnApply.setTag(item);

			final boolean hasRecommendations = item.hasRecommendations();
			ViewUtils.setVisible(panelRecommendations, hasRecommendations);

			if(hasRecommendations) {
				showRecommendations(item, order, menu);
			}

			if(isRecommendationsVisible()) {
				btnApply.setBackgroundResource(R.drawable.btn_wish_added);
				btnApply.setText(StringUtils.EMPTY_STRING);
			} else {
				btnApply.setBackgroundResource(R.drawable.btn_rounded_bordered_grey);
				btnApply.setText(AmountHelper.format(item.price()) + getContext().getString(R.string.currency_suffix_ruble));
			}
		}

		public void bind(final Item item, UserOrder order, Menu menu, int position, boolean detailed, boolean showRecommendations) {
			if(item == null) {
				return;
			}

			bindDetails(item, detailed);
			bindImage(item);
			bindDescription(item);

			txtTitle.setText(item.name());
			btnApply.setTag(item);
			btnApply.setTag(R.id.position, position);

			final boolean hasRecommendations = item.hasRecommendations();

			final boolean recommendationsAdded = hasRecommendations &&
					panelRecommendations.getChildCount() == item.recommendations().size() + 2;

			final boolean skipRecommendations = !showRecommendations || !hasRecommendations;
			if(skipRecommendations) {
				ViewUtils.setVisible(panelRecommendations, false);
			} else {
				if(!isRecommendationsVisible() && hasRecommendations && !recommendationsAdded) {
					ViewUtils.setVisible(panelRecommendations, false);
				}
				if(showRecommendations) {
					if(isRecommendationsVisible()) {
						if(!recommendationsAdded) {
							ViewUtils.setVisible(panelRecommendations, true);
							showRecommendations(item, order, menu);
						} else {
							updateRecommendations(order, menu);
						}
					} else {
						if(recommendationsAdded) {
							removeRecommendations(panelRecommendations, true);
						}
					}
				}
			}

			if(isRecommendationsVisible()) {
				btnApply.setBackgroundResource(R.drawable.btn_wish_added);
				btnApply.setText(StringUtils.EMPTY_STRING);
			} else {
				btnApply.setBackgroundResource(R.drawable.btn_rounded_bordered_grey);
				btnApply.setText(AmountHelper.format(item.price()) + getContext().getString(R.string.currency_suffix_ruble));
			}
		}

		private void bindDescription(final Item item) {
			if(panelDescription != null) {
				ViewUtils.setVisible(panelDescription, item.hasDescription());
				txtDescription.setText(item.description() + "..");
			}
		}

		private void updateRecommendations(final UserOrder order, final Menu menu) {
			final ArrayList<View> childs = ViewUtils.getChilds(panelRecommendations, new ViewFilter() {
				@Override
				public boolean filter(final View v) {
					return v.getId() != R.id.divider;
				}
			});

			for(final View child : childs) {
				final ViewHolder holder = (ViewHolder) child.getTag();
				final Item recommendedItem = (Item) child.getTag(R.id.item);
				if(holder != null && recommendedItem != null) {
					holder.updateState(order, recommendedItem);
					holder.bind(recommendedItem, order, menu, -1, false, false);
				}
			}
		}

		@DebugLog
		private void removeRecommendations(final LinearLayout container, final boolean animate) {
			MenuItemDetailsFragment.removeRecommendations(container, animate);
			if(ViewCompat.hasTransientState(root)) {
				ViewCompat.setHasTransientState(root, false);
			}
		}

		private void showRecommendations(final Item item, final UserOrder order, final Menu menu) {
			if(!ViewCompat.hasTransientState(root)) {
				ViewCompat.setHasTransientState(root, true);
			}
			ViewUtils.setHeight(panelRecommendations, 0);
			final int height = MenuItemDetailsFragment.addRecommendations(panelRecommendations.getContext(), panelRecommendations, menu,
			                                                              order,
			                                                              item,
			                                                              mRecommendationClickListener, mClickListener);
			final int dp16px = ViewUtils.dipToPixels(panelRecommendations.getContext(), 16);
			AnimationUtils.scaleHeight(panelRecommendations, height + (dp16px * 2), 350);
		}

		public boolean isRecommendationsVisible() {
			return mState == MenuItemState.ADDED || mState == MenuItemState.ORDERED;
		}

		public void updateState(final UserOrder order, final Item item) {
			mState = order.contains(item) ? MenuItemState.ADDED : MenuItemState.NONE;
		}

		private void bindImage(final Item item) {
			final String photo = item.photo();
			final boolean hasPhoto = !TextUtils.isEmpty(photo);
			ViewUtils.setVisible(imgIcon, hasPhoto);
			if(hasPhoto) {
				OmnomApplication.getPicasso(getContext()).load(photo).into(imgIcon);
			}
		}

		private void bindDetails(final Item item, boolean detailed) {
			MenuHelper.bindDetails(getContext(), item.details(), txtDetails, detailed);
		}

		public void showDivider(final boolean show) {
			ViewUtils.setVisible(viewDelimiter, show);
		}

		public void setDividerPadding(final int dividerPadding) {
			ViewUtils.setMargins(viewDelimiter, dividerPadding, 0, dividerPadding, 0);
		}
	}

	public static class SubHeaderItem extends HeaderItem {
		SubHeaderItem(Category subCategory) {
			super(subCategory);
		}
	}

	public static class HeaderItem extends Item {
		protected Category mCategory;

		HeaderItem(Category subCategory) {
			mCategory = subCategory;
		}

		@Nullable
		@Override
		public List<String> recommendations() {
			return Collections.EMPTY_LIST;
		}

		@Nullable
		@Override
		public String id() {
			return mCategory.name();
		}

		@Nullable
		@Override
		public String name() {
			return mCategory.name();
		}

		@Nullable
		@Override
		public String description() {
			return mCategory.description();
		}

		@Nullable
		@Override
		public String photo() {
			return StringUtils.EMPTY_STRING;
		}

		@Nullable
		@Override
		public List<Modifier> modifiers() {
			return Collections.EMPTY_LIST;
		}

		@Nullable
		@Override
		public long price() {
			return 0;
		}

		@Nullable
		@Override
		public Details details() {
			return null;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
		}
	}
}
