package com.omnom.android.fragment.menu;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.adapter.MultiLevelRecyclerAdapter;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.MenuItemState;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Ch3D on 20.02.2015.
 */
public class MenuAdapter extends MultiLevelRecyclerAdapter {

	public static final int VIEW_TYPE_CATEGORY = 0;

	public static final int VIEW_TYPE_SUBCATEGORY = 1;

	public static final int VIEW_TYPE_ITEM = 2;

	public static final int VIEW_TYPE_RECOMMENDATION_TOP = 3;

	public static final int VIEW_TYPE_RECOMMENDATION_BOTTOM = 4;

	public class CategoryViewHolder extends RecyclerView.ViewHolder {

		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		public CategoryViewHolder(final View v) {
			super(v);
			ButterKnife.inject(this, v);
		}
	}

	public class ItemViewHolder extends RecyclerView.ViewHolder {

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

		@InjectView(R.id.txt_info)
		@Optional
		protected TextView txtDetails;

		@InjectView(R.id.root)
		@Optional
		protected View root;

		@InjectView(R.id.img_icon)
		@Optional
		protected ImageView imgIcon;

		private View.OnClickListener mApplyClickListener;

		private View.OnClickListener mClickListener;

		private MenuItemState mState = MenuItemState.NONE;

		public ItemViewHolder(final View v) {
			this(v, null, null);
		}

		public ItemViewHolder(final View convertView, final View.OnClickListener applyClickListener,
		                      final View.OnClickListener clickListener) {
			super(convertView);
			ButterKnife.inject(this, convertView);
			mApplyClickListener = applyClickListener;
			mClickListener = clickListener;
		}

		private Context getContext() {return btnApply.getContext();}

		public void bind(final Item item, UserOrder order, Menu menu, int position, boolean detailed) {
			if(item == null) {
				return;
			}

			bindDetails(item, detailed);
			bindImage(item);
			bindDescription(item);

			if(position < getItemCount() - 1) {
				final Data itemAt = getItemAt(position + 1);
				ViewUtils.setVisible(viewDelimiter, !(itemAt instanceof CategoryData));
			} else {
				ViewUtils.setVisible(viewDelimiter, false);
			}

			txtTitle.setText(item.name());
			btnApply.setTag(item);
			btnApply.setTag(R.id.position, position);
			btnApply.setOnClickListener(mApplyClickListener);

			if(isRecommendationsVisible()) {
				btnApply.setBackgroundResource(R.drawable.btn_wish_added);
				btnApply.setText(StringUtils.EMPTY_STRING);
			} else {
				btnApply.setBackgroundResource(R.drawable.btn_rounded_bordered_grey);
				btnApply.setText(StringUtils.formatCurrency(item.price(), getContext().getString(R.string.currency_suffix_ruble)));
			}
		}

		private void bindDescription(final Item item) {
			if(panelDescription != null) {
				ViewUtils.setVisible(panelDescription, item.hasDescription());
				txtDescription.setText(item.description() + "..");
			}
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
			// viewDelimiter.setPadding(dividerPadding, 0, dividerPadding, 0);
		}

		public void bindDivider(final View convertView, final ArrayList<Item> innerItems, final Item item, final int position) {
			final int padding = ViewUtils.dipToPixels(convertView.getContext(), 16f);
			if(position == innerItems.size() - 1) {
				showDivider(false);
			} else if(position + 1 < innerItems.size()) {
				final Item nextItem = innerItems.get(position + 1);
				final boolean isSubheaderNext = nextItem instanceof MenuCategoryItemsAdapter.SubHeaderItem;
				final boolean isHeaderNext = nextItem instanceof MenuCategoryItemsAdapter.HeaderItem;
				if(item.hasRecommendations() && isRecommendationsVisible()) {
					showDivider(false);
				} else {
					showDivider(!isSubheaderNext);
				}
				// setDividerPadding(isHeaderNext && !isSubheaderNext ? 0 : padding);
			}
		}
	}

	private final LayoutInflater mInflater;

	private final Menu mMenu;

	private final UserOrder mUserOrder;

	private final View.OnClickListener mItemClickListener;

	private final View.OnClickListener mApplyClickListener;

	private Context mContext;

	private View.OnClickListener mListener;

	public MenuAdapter(Context context, Menu menu, UserOrder userOrder, View.OnClickListener listener,
	                   View.OnClickListener itemClickListener, View.OnClickListener applyClickListener) {
		mContext = context;
		mMenu = menu;
		mUserOrder = userOrder;
		mListener = listener;
		mItemClickListener = itemClickListener;
		mApplyClickListener = applyClickListener;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		View v;
		RecyclerView.ViewHolder viewHolder;
		switch(viewType) {
			case VIEW_TYPE_ITEM:
				v = mInflater.inflate(R.layout.item_menu_dish, parent, false);
				viewHolder = new ItemViewHolder(v, mApplyClickListener, mItemClickListener);
				v.setOnClickListener(mItemClickListener);
				break;

			case VIEW_TYPE_RECOMMENDATION_TOP:
				v = mInflater.inflate(R.layout.item_menu_dish_top, parent, false);
				viewHolder = new ItemViewHolder(v, mApplyClickListener, mItemClickListener);
				v.setOnClickListener(mItemClickListener);
				break;

			case VIEW_TYPE_RECOMMENDATION_BOTTOM:
				v = mInflater.inflate(R.layout.item_menu_dish_bottom, parent, false);
				viewHolder = new ItemViewHolder(v, mApplyClickListener, mItemClickListener);
				v.setOnClickListener(mItemClickListener);
				break;

			case VIEW_TYPE_CATEGORY:
				v = mInflater.inflate(R.layout.item_menu_header, parent, false);
				viewHolder = new CategoryViewHolder(v);
				v.setOnClickListener(mListener);
				break;

			case VIEW_TYPE_SUBCATEGORY:
				v = mInflater.inflate(R.layout.item_menu_subheader, parent, false);
				viewHolder = new CategoryViewHolder(v);
				v.setOnClickListener(mListener);
				break;

			default:
				throw new IllegalStateException("unknown viewType");
		}

		return viewHolder;

	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
		int viewType = getItemViewType(position);
		switch(viewType) {
			case VIEW_TYPE_ITEM:
			case VIEW_TYPE_RECOMMENDATION_BOTTOM:
			case VIEW_TYPE_RECOMMENDATION_TOP:
				ItemViewHolder ivh = (ItemViewHolder) viewHolder;
				ItemData itemData = (ItemData) getItemAt(position);
				final Item item = itemData.getItem();
				ivh.updateState(mUserOrder, item);
				ivh.bind(item, mUserOrder, mMenu, position, false);
				ivh.txtTitle.setText(itemData.getName());
				if(viewType == VIEW_TYPE_ITEM && item.hasRecommendations() && ivh.isRecommendationsVisible()) {
					ViewUtils.setVisible(ivh.viewDelimiter, false);
				}
				break;

			case VIEW_TYPE_CATEGORY:
			case VIEW_TYPE_SUBCATEGORY:
				CategoryViewHolder cvh = (CategoryViewHolder) viewHolder;
				CategoryData category = (CategoryData) getItemAt(position);
				cvh.txtTitle.setText(category.getName());
				break;
		}
	}

	@Override
	public int getItemViewType(int position) {
		final Data data = getItemAt(position);
		if(data instanceof CategoryData) {
			final CategoryData categoryData = (CategoryData) data;
			if(categoryData.getLevel() == 0) {
				return VIEW_TYPE_CATEGORY;
			} else {
				return VIEW_TYPE_SUBCATEGORY;
			}
		}
		if(data instanceof ItemData) {
			final ItemData itemData = (ItemData) data;
			switch(itemData.getType()) {
				case ItemData.TYPE_NORMAL:
					return VIEW_TYPE_ITEM;

				case ItemData.TYPE_RECOMMENDATION_TOP:
					return VIEW_TYPE_RECOMMENDATION_TOP;

				case ItemData.TYPE_RECOMMENDATION_BOTTOM:
					return VIEW_TYPE_RECOMMENDATION_BOTTOM;
			}

		}
		throw new RuntimeException("wrong item type");
	}
}
