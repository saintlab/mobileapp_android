package com.omnom.android.adapter;

import android.content.Context;
import android.os.Parcel;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.restaurateur.model.order.OrderItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class OrderItemsAdapter extends BaseAdapter {

	static class ViewHolder {
		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		@InjectView(R.id.txt_price)
		protected TextView txtPrice;

		private ViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}
	}

	private static final int TYPE_NORMAL = 0;

	private static final int TYPE_FAKE = 1;

	private class FakeOrder extends OrderItem {
		public FakeOrder(final Parcel parcel) {
			super(parcel);
		}
	}

	private final LayoutInflater mInflater;

	private final SparseBooleanArray mCheckedStates;

	private Context mContext;

	private List<OrderItem> mItems;

	private Object mSelectedItems;

	public OrderItemsAdapter(final Context context, final List<OrderItem> orders) {
		mContext = context;
		if(orders.size() < 4) {
			orders.add(0, new FakeOrder(Parcel.obtain()));
		}
		mItems = orders;
		mInflater = LayoutInflater.from(mContext);
		mCheckedStates = new SparseBooleanArray();
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public OrderItem getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public int getItemViewType(final int position) {
		final OrderItem item = getItem(position);
		return item instanceof FakeOrder ? TYPE_FAKE : TYPE_NORMAL;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {

			switch(getItemViewType(position)) {
				case TYPE_NORMAL:
					convertView = mInflater.inflate(R.layout.item_order_item, parent, false);
					holder = new ViewHolder(convertView);
					convertView.setTag(holder);
					break;

				case TYPE_FAKE:
					convertView = mInflater.inflate(R.layout.item_order_item_fake, parent, false);
					break;
			}
		}
		bindView(convertView, getItem(position));
		return convertView;
	}

	private void bindView(View convertView, OrderItem item) {
		if(item instanceof FakeOrder) {
			return;
		}
		final Boolean tagSelected = (Boolean) convertView.getTag(R.id.selected);
		if(tagSelected != null && tagSelected) {
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.btn_pay_green));
		} else {
			convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
		}
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.txtTitle.setText(item.getTitle());
		holder.txtPrice.setText(String.valueOf(item.getPricePerItem()));
	}

	public void setSelected(final int position, final boolean selected) {
		mCheckedStates.put(position, selected);
	}

	public boolean isSelected(final int position) {
		return mCheckedStates.get(position, false);
	}

	public void clearSelection() {
		mCheckedStates.clear();
	}

	public List<OrderItem> getSelectedItems() {
		final int size = mCheckedStates.size();
		ArrayList<OrderItem> result = new ArrayList<OrderItem>(size);
		for(int i = 0; i < size; i++) {
			int key = mCheckedStates.keyAt(i);
			if(mCheckedStates.get(key) && key <  getCount() - 1) {
				result.add(mItems.get(key));
			}
		}
		return result;
	}
}
