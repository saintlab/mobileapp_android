package com.omnom.android.fragment.menu;

import android.content.Context;
import android.graphics.Color;
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
import com.omnom.android.adapter.MultiLevelRecyclerAdapter;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.MenuItemState;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.subcategories.HeaderItemDecorator;

import java.util.ArrayList;
import java.util.HashMap;

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

	public static final int VIEW_TYPE_SUBSUBCATEGORY = 5;

	public static final int COLOR_SUBCATEGORY = Color.parseColor("#59ffffff");

	public static final int COLOR_SUBSUBCATEGORY = Color.parseColor("#aaffffff");

	public static final int COLOR_CATEGORY = Color.TRANSPARENT;

	public static class CategoryViewHolder extends RecyclerView.ViewHolder {

		@InjectView(R.id.txt_title)
		public TextView txtTitle;

		public CategoryViewHolder(final View v) {
			super(v);
			ButterKnife.inject(this, v);
			sTitleViews.add(txtTitle);
		}
	}

	private static final ArrayList<TextView> sTitleViews = new ArrayList<TextView>();

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

		/**
		 * This view is used to draw bottom border when there is only one recommended item itself
		 */
		@InjectView(R.id.delimiter_bottom)
		@Optional
		protected View viewDelimiterTopBottom;

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

		public ItemViewHolder(final View convertView, final View.OnClickListener applyClickListener, final View.OnClickListener
				clickListener) {
			super(convertView);
			ButterKnife.inject(this, convertView);
			mApplyClickListener = applyClickListener;
			mClickListener = clickListener;
		}

		private Context getContext() {
			return btnApply.getContext();
		}

		public void bind(final Item item, UserOrder order, Menu menu, int position, boolean detailed) {
			if(item == null) {
				return;
			}

			bindDetails(item, detailed);
			bindImage(item);
			bindDescription(item);

			if(position < getItemCount() - 1) {
				final Data itemAt = getItemAt(position + 1);
				ViewUtils.setVisibleGone(viewDelimiter, !(itemAt instanceof CategoryData));
			} else {
				ViewUtils.setVisibleGone(viewDelimiter, false);
			}

			MenuHelper.bindTitle(txtTitle, item);
			btnApply.setTag(item);
			btnApply.setTag(R.id.position, position);
			btnApply.setOnClickListener(mApplyClickListener);

			bindApply(item, position);
		}

		private void bindApply(Item item, int position) {
			if(isRecommendationsVisible()) {
				btnApply.setBackgroundResource(R.drawable.btn_wish_added);
				btnApply.setText(StringUtils.EMPTY_STRING);
			} else {
				if(selected == position) {
					btnApply.setBackgroundResource(R.drawable.bg_rounded_blue_normal);
					btnApply.setText(AmountHelper.format(item.price()) + getContext().getString(R.string.currency_suffix_ruble));
					btnApply.setTextColor(Color.WHITE);
				} else {
					btnApply.setBackgroundResource(R.drawable.btn_rounded_menu_price);
					btnApply.setTextColor(mContext.getResources().getColorStateList(R.drawable.text_color_selector_menu_price));
					btnApply.setText(AmountHelper.format(item.price()) + getContext().getString(R.string.currency_suffix_ruble));
				}
			}
		}

		private void bindDescription(final Item item) {
			if(panelDescription != null && item != null) {
				ViewUtils.setVisibleGone(panelDescription, item.hasDescription());
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
			ViewUtils.setVisibleGone(imgIcon, hasPhoto);
			if(hasPhoto) {
				OmnomApplication.getPicasso(getContext()).load(photo).into(imgIcon);
			}
		}

		private void bindDetails(final Item item, boolean detailed) {
			MenuHelper.bindDetails(getContext(), item.details(), txtDetails, detailed);
		}
	}

	public static int getCategoryColor(final int viewType) {
		switch(viewType) {
			case VIEW_TYPE_CATEGORY:
				return COLOR_CATEGORY;

			case VIEW_TYPE_SUBCATEGORY:
				return COLOR_SUBCATEGORY;

			case VIEW_TYPE_SUBSUBCATEGORY:
				return COLOR_SUBSUBCATEGORY;
		}
		throw new IllegalArgumentException("wrong viewType = " + viewType);
	}

	public static boolean isHeader(final RecyclerView.ViewHolder header) {
		return header.getItemViewType() == VIEW_TYPE_CATEGORY || header.getItemViewType() == VIEW_TYPE_SUBCATEGORY
				|| header.getItemViewType() == VIEW_TYPE_SUBSUBCATEGORY;
	}

	private final LayoutInflater mInflater;

	private final Menu mMenu;

	private final UserOrder mUserOrder;

	private final View.OnClickListener mItemClickListener;

	private final View.OnClickListener mApplyClickListener;

	/**
	 * Index of item for which MenuItemAddFragment is shown
	 */
	private int selected = -1;

	private HeaderItemDecorator mDecorator;

	private Context mContext;

	private HashMap<Data, RecyclerView.ViewHolder> mHeaderStore;

	private View.OnClickListener mListener;

	public MenuAdapter(Context context, Menu menu, UserOrder userOrder, final HashMap<Data, RecyclerView.ViewHolder> headerStore, View
			.OnClickListener listener, View.OnClickListener itemClickListener, View.OnClickListener applyClickListener) {
		mContext = context;
		mMenu = menu;
		mUserOrder = userOrder;
		mHeaderStore = headerStore;
		mListener = listener;
		mItemClickListener = itemClickListener;
		mApplyClickListener = applyClickListener;
		mInflater = LayoutInflater.from(mContext);
	}

	public void resetState() {
		if(selected != -1) {
			notifyItemChanged(selected);
		}
		selected = -1;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	@Override
	public long getItemId(final int position) {
		final Data itemAt = getItemAt(position);
		return itemAt.hashCode();
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

			case VIEW_TYPE_SUBSUBCATEGORY:
				v = mInflater.inflate(R.layout.item_menu_subsubheader, parent, false);
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

				if(viewType == VIEW_TYPE_ITEM && item.hasRecommendations() && ivh.isRecommendationsVisible()) {
					ViewUtils.setVisibleGone(ivh.viewDelimiter, false);
				}
				if(viewType == VIEW_TYPE_RECOMMENDATION_TOP && !isLast(position) && getItemViewType(position + 1)
						!= VIEW_TYPE_RECOMMENDATION_BOTTOM) {
					ViewUtils.setVisibleGone(ivh.viewDelimiterTopBottom, true);
					ViewUtils.setVisibleGone(ivh.viewDelimiter, false);
				} else {
					ViewUtils.setVisibleGone(ivh.viewDelimiterTopBottom, false);
				}
				break;

			case VIEW_TYPE_CATEGORY:
			case VIEW_TYPE_SUBCATEGORY:
			case VIEW_TYPE_SUBSUBCATEGORY:
				CategoryViewHolder cvh = (CategoryViewHolder) viewHolder;
				CategoryData category = (CategoryData) getItemAt(position);
				if(mDecorator != null) {
					if(!category.isGroup() && category.getChildren() != null && category.getChildren().size() > 0) {
						mDecorator.drawSelectedBackground(cvh.itemView);
					} else {
						mDecorator.restoreHeaderView(cvh);
					}
				} else {
					cvh.itemView.setBackgroundColor(getCategoryColor(viewType));
				}
				cvh.txtTitle.setText(category.getName());
				cvh.itemView.setTag(R.id.level, category.getLevel());
				mHeaderStore.put(category, cvh);
				break;
		}
	}

	private boolean isLast(final int position) {
		return position == getItemCount() - 1;
	}

	@Override
	public int getItemViewType(int position) {
		final Data data = getItemAt(position);
		if(data instanceof CategoryData) {
			final CategoryData categoryData = (CategoryData) data;
			switch(categoryData.getLevel()) {
				case 0:
					return VIEW_TYPE_CATEGORY;
				case 1:
					return VIEW_TYPE_SUBCATEGORY;
				case 2:
					return VIEW_TYPE_SUBSUBCATEGORY;
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

	public int getItemPosition(final Item item) {
		for(int i = 0; i < getItemCount(); i++) {
			final Data data = getItemAt(i);
			if(data instanceof ItemData && ((ItemData) data).getItem().id().equals(item.id())) {
				return i;
			}
		}
		return 0;
	}

	public void setDecorator(final HeaderItemDecorator headerItemDecorator) {
		mDecorator = headerItemDecorator;
	}

	public ArrayList<Integer> getItemPositions(final Item item) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < getItemCount(); i++) {
			final Data data = getItemAt(i);
			if(data instanceof ItemData && ((ItemData) data).getItem().id().equals(item.id())) {
				result.add(i);
			}
		}
		return result;
	}
}
