package com.omnom.android.adapter;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.fragment.menu.MenuItemDetailsFragment;
import com.omnom.android.fragment.menu.MenuSubcategoryFragment;
import com.omnom.android.menu.model.Category;
import com.omnom.android.menu.model.Details;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.MenuItemState;
import com.omnom.android.menu.model.Modifier;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.StickyListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Ch3D on 27.01.2015.
 */
public class MenuCategoryItemsAdapter extends BaseAdapter implements StickyListView.StickyListAdapter, View.OnClickListener {

	public static final int VIEW_TYPE_COUNT = 3;

	public static final int VIEW_TYPE_ITEM = 0;

	public static final int VIEW_TYPE_HEADER = 1;

	public static final int VIEW_TYPE_SUBHEADER = 2;

	public static class ViewHolder {
		@Nullable
		private final View.OnClickListener mRecommendationClickListener;

		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		@InjectView(R.id.delimiter)
		@Optional
		protected View viewDelimiter;

		@InjectView(R.id.stub)
		@Optional
		protected ViewStub viewStub;

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

		private MenuItemState mState = MenuItemState.NONE;

		public ViewHolder(View convertView) {
			this(convertView, null);
		}

		public ViewHolder(final View convertView, final View.OnClickListener recommendationClickListener) {
			ButterKnife.inject(this, convertView);
			mRecommendationClickListener = recommendationClickListener;
		}

		private Context getContext() {return btnApply.getContext();}

		public void bind(final Item item, UserOrder order, Menu menu, boolean detailed) {
			if(item == null) {
				return;
			}

			bindDetails(item, detailed);
			bindImage(item);
			txtTitle.setText(item.name());
			btnApply.setTag(item);

			LinearLayout viewById = (LinearLayout) root.findViewById(R.id.panel_bottom);
			if(viewById != null) {
				MenuItemDetailsFragment.removeRecommendations(viewById);
				ViewUtils.setVisible(viewById, false);
			}

			if(isRecommendationsVisible()) {
				btnApply.setBackgroundResource(R.drawable.btn_wish_added);
				btnApply.setText(StringUtils.EMPTY_STRING);
				if(viewById == null) {
					if(viewStub != null) {
						ViewUtils.setVisible(viewStub, true);
					}
				}
				viewById = (LinearLayout) root.findViewById(R.id.panel_bottom);
				MenuItemDetailsFragment.addRecommendations(viewById.getContext(), viewById, menu, order, item,
				                                           mRecommendationClickListener);
			} else {
				btnApply.setBackgroundResource(R.drawable.btn_rounded_bordered_grey);
				btnApply.setText(StringUtils.formatCurrency(item.price(), getContext().getString(R.string.currency_suffix_ruble)));
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
	}

	private static class SubHeaderItem extends HeaderItem {
		SubHeaderItem(Category subCategory) {
			super(subCategory);
		}
	}

	private static class HeaderItem extends Item {
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

	private final MenuSubcategoryFragment mContext;

	private final Category mCategory;

	private final Map<String, Item> mItems;

	private final LayoutInflater mInflater;

	private View.OnClickListener mRecommendationClickListener;

	private Menu mMenu;

	private UserOrder mOrder;

	private ArrayList<Item> mInnerItems;

	public MenuCategoryItemsAdapter(MenuSubcategoryFragment fragment, Menu menu, final UserOrder order, Category category, Map<String,
			Item> items, View.OnClickListener recommendationClickListener) {
		mContext = fragment;
		mMenu = menu;
		mOrder = order;
		mCategory = category;
		mItems = items;
		mRecommendationClickListener = recommendationClickListener;
		mInflater = LayoutInflater.from(fragment.getActivity());
		initData();
	}

	private void initData() {
		if(mCategory != null) {
			final List<Category> childrens = mCategory.children();
			mInnerItems = new ArrayList<Item>();

			final List<String> categoryItems = mCategory.items();
			if(categoryItems != null && categoryItems.size() > 0) {
				for(final String itemId : categoryItems) {
					mInnerItems.add(mItems.get(itemId));
				}
			}

			if(childrens != null && childrens.size() > 0) {
				for(final Category subCategory : childrens) {
					mInnerItems.add(new HeaderItem(subCategory));
					addCategoryItems(subCategory);
					if(subCategory.children() != null && subCategory.children().size() > 0) {
						for(final Category subSubCategory : subCategory.children()) {
							mInnerItems.add(new SubHeaderItem(subSubCategory));
							addCategoryItems(subSubCategory);
						}
					}
				}
			}
		}
	}

	private void addCategoryItems(final Category subCategory) {
		final List<String> items = subCategory.items();
		if(items != null) {
			for(final String itemId : items) {
				mInnerItems.add(mItems.get(itemId));
			}
		}
	}

	@Override
	public boolean isItemViewTypePinned(final int viewType) {
		return viewType == VIEW_TYPE_HEADER;
	}

	@Override
	public int getCount() {
		return mInnerItems != null ? mInnerItems.size() : 0;
	}

	@Override
	public int getItemViewType(final int position) {
		if(getItem(position) instanceof HeaderItem) {
			return getItem(position) instanceof SubHeaderItem ? VIEW_TYPE_SUBHEADER : VIEW_TYPE_HEADER;
		} else {
			return VIEW_TYPE_ITEM;
		}
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public Item getItem(final int position) {
		return mInnerItems.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			switch(getItemViewType(position)) {
				case VIEW_TYPE_ITEM:
					convertView = mInflater.inflate(R.layout.item_menu_dish, parent, false);
					convertView.findViewById(R.id.btn_apply).setOnClickListener(this);
					break;

				case VIEW_TYPE_HEADER:
					convertView = mInflater.inflate(R.layout.item_menu_header, parent, false);
					break;

				case VIEW_TYPE_SUBHEADER:
					convertView = mInflater.inflate(R.layout.item_menu_subheader, parent, false);
					break;
			}
			holder = new ViewHolder(convertView, mRecommendationClickListener);
			convertView.setTag(holder);
		}

		bindView(convertView, position, getItem(position));
		return convertView;
	}

	private void bindView(final View convertView, final int position, final Item item) {
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		if(item instanceof HeaderItem) {
			holder.txtTitle.setText(item.name());
		} else {
			holder.updateState(mOrder, item);
			holder.bind(item, mOrder, mMenu, false);
			final int padding = ViewUtils.dipToPixels(convertView.getContext(), 16f);
			if(position == mInnerItems.size() - 1) {
				holder.showDivider(false);
			} else if(position + 1 < mInnerItems.size()) {
				final Item nextItem = mInnerItems.get(position + 1);
				final boolean isSubheaderNext = nextItem instanceof SubHeaderItem;
				final boolean isHeaderNext = nextItem instanceof HeaderItem;
				if(item.hasRecommendations() && holder.isRecommendationsVisible()) {
					holder.showDivider(false);
				} else {
					holder.showDivider(!isSubheaderNext);
				}
				holder.setDividerPadding(isHeaderNext && !isSubheaderNext ? 0 : padding);
			}
		}
	}

	@Override
	public void onClick(final View v) {
		if(v.getId() == R.id.btn_apply) {
			mContext.showAddFragment((Item) v.getTag());
		}
	}
}
