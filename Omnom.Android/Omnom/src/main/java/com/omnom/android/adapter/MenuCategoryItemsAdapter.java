package com.omnom.android.adapter;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.menu.MenuSubcategoryActivity;
import com.omnom.android.menu.model.Category;
import com.omnom.android.menu.model.Child;
import com.omnom.android.menu.model.Details;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.MenuItemState;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.StickyListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Ch3D on 27.01.2015.
 */
public class MenuCategoryItemsAdapter extends BaseAdapter implements StickyListView.StickyListAdapter, View.OnClickListener {

	public static final int VIEW_TYPE_COUNT = 2;

	public static final int VIEW_TYPE_ITEM = 0;

	public static final int VIEW_TYPE_HEADER = 1;

	public static class ViewHolder {
		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		@InjectView(R.id.delimiter)
		@Optional
		protected View viewDelimiter;

		@InjectView(R.id.btn_apply)
		@Optional
		protected Button btnApply;

		@InjectView(R.id.txt_info)
		@Optional
		protected TextView txtDetails;

		@InjectView(R.id.img_icon)
		@Optional
		protected ImageView imgIcon;

		private MenuItemState mState = MenuItemState.NONE;

		public ViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}

		private Context getContext() {return btnApply.getContext();}

		public void bind(final Item item) {
			if(item == null) {
				return;
			}
			bindDetails(item);
			bindImage(item);
			txtTitle.setText(item.name());
			btnApply.setText(StringUtils.formatCurrency(item.price(), getContext().getString(R.string.currency_suffix_ruble)));
			btnApply.setTag(item);
			if(mState == MenuItemState.ADDED || mState == MenuItemState.ORDERED) {
				btnApply.setBackgroundResource(R.drawable.btn_rounded_blue);
			} else {
				btnApply.setBackgroundResource(R.drawable.btn_rounded_bordered_grey);
			}
		}

		public void setDelimiterVisible(final boolean visible) {
			ViewUtils.setVisible(viewDelimiter, visible);
		}

		public void updateState(final UserOrder order, final Item item) {
			mState = order.contains(item) ? MenuItemState.ADDED : MenuItemState.NONE;
		}

		private void bindImage(final Item item) {
			final String photo = item.photo();
			if(!TextUtils.isEmpty(photo)) {
				OmnomApplication.getPicasso(getContext()).load(photo).into(imgIcon);
			}
		}

		private void bindDetails(final Item item) {
			final Details details = item.details();
			final boolean hasDetails = details != null;
			ViewUtils.setVisible(txtDetails, hasDetails);
			if(hasDetails) {
				txtDetails.setText(getContext().getString(R.string.dish_details, details.energyTotal(), details.weight()));
			}
		}

	}

	private static class HeaderItem extends Item {
		private Child mCategory;

		HeaderItem(Child subCategory) {
			mCategory = subCategory;
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

	private final Context mContext;

	private final Category mCategory;

	private final Map<String, Item> mItems;

	private final LayoutInflater mInflater;

	private UserOrder mOrder;

	private ArrayList<Item> mInnerItems;

	public MenuCategoryItemsAdapter(Context context, final UserOrder order, Category category, Map<String, Item> items) {
		mContext = context;
		mOrder = order;
		mCategory = category;
		mItems = items;
		mInflater = LayoutInflater.from(mContext);
		initData();
	}

	private void initData() {
		if(mCategory != null) {
			final List<Child> childrens = mCategory.children();
			if(childrens != null && childrens.size() > 0) {
				int itemsCount = 0;

				for(final Child subCategory : childrens) {
					final List<String> items = subCategory.items();
					itemsCount += items != null ? items.size() : 0;
				}

				mInnerItems = new ArrayList<Item>(childrens.size() + itemsCount);
				for(final Child subCategory : childrens) {
					mInnerItems.add(new HeaderItem(subCategory));
					final List<String> items = subCategory.items();
					if(items != null) {
						for(final String itemId : items) {
							mInnerItems.add(mItems.get(itemId));
						}
					}
				}
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
		return getItem(position) instanceof HeaderItem ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
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
			}
			holder = new ViewHolder(convertView);
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
			holder.bind(item);
		}
	}

	@Override
	public void onClick(final View v) {
		if(v.getId() == R.id.btn_apply) {
			MenuSubcategoryActivity activity = (MenuSubcategoryActivity) mContext;
			activity.showAddFragment((Item) v.getTag());
		}
	}
}
