package com.omnom.android.adapter;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.utils.utils.AmountHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;

/**
 * Created by root on 2/3/15.
 */
public class WishListAdapter extends BaseAdapter {

	public static final int VIEW_TYPE_COUNT = 4;

	public static final int VIEW_TYPE_WISH_ITEM = 0;

	public static final int VIEW_TYPE_WISH_FOOTER = 1;

	public static final int VIEW_TYPE_TABLE_HEADER = 2;

	public static final int VIEW_TYPE_TABLE_ITEM = 3;

	static class ViewHolder {

		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		@InjectView(R.id.txt_info)
		protected TextView txtInfo;

		@InjectView(R.id.txt_price)
		protected TextView txtPrice;

		public ViewHolder(final View convertView) {
			ButterKnife.inject(this, convertView);
		}
	}

	private class UserOrderDataFooter extends UserOrderData {
		@Nullable
		@Override
		public int amount() {
			return 0;
		}

		@Nullable
		@Override
		public List<String> modifiers() {
			return Collections.EMPTY_LIST;
		}

		@Nullable
		@Override
		public Item item() {
			return Item.NULL;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			// do nothing
		}
	}

	private class OrderItemHeader extends OrderItem {
		public OrderItemHeader() {
		}
	}

	private final Context mContext;

	private final UserOrder mOrder;

	private final ArrayList<OrderItem> mTableItems;

	private final LayoutInflater mInflater;

	private View.OnClickListener mClickListener;

	private List<UserOrderData> _lazy_selected_data;

	public WishListAdapter(Context context, UserOrder order, Collection<OrderItem> tableItems, View.OnClickListener clickListener) {
		mClickListener = clickListener;
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mOrder = order;
		mTableItems = new ArrayList<>(tableItems);
		if(mTableItems.size() > 0) {
			mTableItems.add(0, new OrderItemHeader());
		}
	}

	@Override
	public int getItemViewType(final int position) {
		final Object item = getItem(position);
		if(item instanceof UserOrderDataFooter) {
			return VIEW_TYPE_WISH_FOOTER;
		}
		if(item instanceof UserOrderData) {
			return VIEW_TYPE_WISH_ITEM;
		}
		if(item instanceof OrderItemHeader) {
			return VIEW_TYPE_TABLE_HEADER;
		}
		return VIEW_TYPE_TABLE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public int getCount() {
		return getSelectedItems().size() + mTableItems.size();
	}

	private List<UserOrderData> getSelectedItems() {
		if(_lazy_selected_data == null) {
			final List<UserOrderData> selectedItems = mOrder.getSelectedItems();
			selectedItems.add(new UserOrderDataFooter());
			_lazy_selected_data = selectedItems;
		}
		return _lazy_selected_data;
	}

	@Override
	public void notifyDataSetChanged() {
		_lazy_selected_data = null;
		super.notifyDataSetChanged();
	}

	@Override
	public Object getItem(final int position) {
		final int wishSize = getSelectedItems().size();
		if(position < wishSize) {
			return getSelectedItems().get(position);
		}
		return mTableItems.get(position - wishSize);
	}

	@Override
	public long getItemId(final int pos) {
		return getItem(pos).hashCode();
	}

	@Override
	@DebugLog
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		final int viewType = getItemViewType(position);
		if(convertView == null) {
			switch(viewType) {
				case VIEW_TYPE_WISH_ITEM:
				case VIEW_TYPE_TABLE_ITEM:
					convertView = mInflater.inflate(R.layout.item_wish, parent, false);
					holder = new ViewHolder(convertView);
					convertView.setTag(R.id.item, getItem(position));
					convertView.setTag(holder);
					break;

				case VIEW_TYPE_WISH_FOOTER:
					convertView = mInflater.inflate(R.layout.item_wish_footer, parent, false);
					convertView.setTag(R.id.item, getItem(position));
					break;

				case VIEW_TYPE_TABLE_HEADER:
					convertView = mInflater.inflate(R.layout.item_wish_table_header, parent, false);
					break;
			}
		}

		bindView(convertView, position, viewType, getItem(position));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@DebugLog
	private void bindView(final View convertView, final int position, final int itemType, final Object item) {
		switch(itemType) {
			case VIEW_TYPE_TABLE_HEADER:
				convertView.findViewById(R.id.btn_refresh).setOnClickListener(mClickListener);
				break;

			case VIEW_TYPE_WISH_ITEM:
				final ViewHolder holder = (ViewHolder) convertView.getTag();
				final UserOrderData data = (UserOrderData) item;
				holder.txtTitle.setText(data.item().name());

				final String price = AmountHelper.format(data.item().price()) + mContext.getString(R.string.currency_suffix_ruble);
				holder.txtPrice.setText(mContext.getString(R.string.wish_items_price_detailed, data.amount(), price));
				holder.txtPrice.setTag(data);
				holder.txtPrice.setOnClickListener(mClickListener);
				MenuHelper.bindDetails(mContext, data.item().details(), holder.txtInfo, false);
				break;

			case VIEW_TYPE_TABLE_ITEM:
				final ViewHolder tableItemHolder = (ViewHolder) convertView.getTag();
				final OrderItem orderItem = (OrderItem) item;
				tableItemHolder.txtTitle.setText(orderItem.getTitle());

				final String itemPrice = AmountHelper.format(orderItem.getPricePerItem()) + mContext.getString(
						R.string.currency_suffix_ruble);
				tableItemHolder.txtPrice.setText(itemPrice);
				tableItemHolder.txtPrice.setTag(orderItem);
				tableItemHolder.txtPrice.setOnClickListener(mClickListener);
				MenuHelper.bindDetails(mContext, null, tableItemHolder.txtInfo, false);
				break;

			case VIEW_TYPE_WISH_FOOTER:
				final Button btnClear = (Button) convertView.findViewById(R.id.btn_clear);
				final Button btnSend = (Button) convertView.findViewById(R.id.btn_send);
				btnClear.setOnClickListener(mClickListener);
				btnSend.setOnClickListener(mClickListener);
				final boolean enabled = getSelectedItems().size() > 1;
				btnClear.setEnabled(enabled);
				btnSend.setEnabled(enabled);
				break;
		}
	}

	public void remove(final Object tag) {
		getSelectedItems().remove(tag);
	}
}
