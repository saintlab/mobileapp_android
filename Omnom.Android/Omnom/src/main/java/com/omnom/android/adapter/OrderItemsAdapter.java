package com.omnom.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.utils.SparseBooleanArrayParcelable;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class OrderItemsAdapter extends BaseAdapter {

	public static final int COLOR_TEXT_SELECTED = Color.WHITE;

	public static final int COLOR_TEXT_NORMAL = Color.BLACK;

	static class ViewHolder {
		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		@InjectView(R.id.txt_price)
		protected TextView txtPrice;

		@InjectView(R.id.divider)
		protected View divider;

		private ViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}
	}

	private static final int TYPE_NORMAL = 0;

	private static final int TYPE_FAKE = 1;

	private final LayoutInflater mInflater;

	private final int mColorPriceNormal;

	protected SparseBooleanArray mCheckedStates;

	protected Context mContext;

	private boolean mAddFakeView;

	private List<OrderItem> mItems;

	public OrderItemsAdapter(final Context context, final List<OrderItem> orders, boolean addFakeView) {
		this(context, orders, new SparseBooleanArrayParcelable(), addFakeView);
	}

	public OrderItemsAdapter(final Context context, final List<OrderItem> items, final SparseBooleanArrayParcelable states, boolean
			addFakeView) {
		mContext = context;
		mAddFakeView = addFakeView;
		mItems = items;
		mInflater = LayoutInflater.from(mContext);
		mCheckedStates = states;
		mColorPriceNormal = mContext.getResources().getColor(R.color.order_item_price);
	}

	@Override
	public int getCount() {
		if(isFakeEnabled()) {
			return mItems.size() + 1;
		}
		return mItems.size();
	}

	private boolean isFakeEnabled() {
		return mAddFakeView;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public OrderItem getItem(int position) {
		if(isFakeEnabled()) {
			if(position == 0) {
				return null;
			} else {
				return mItems.get(position - 1);
			}
		}
		return mItems.get(position);
	}

	@Override
	public int getItemViewType(final int position) {
		return position == 0 && isFakeEnabled() ? TYPE_FAKE : TYPE_NORMAL;
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
		bindView(convertView, position, getItem(position));
		return convertView;
	}

	protected void bindView(View convertView, final int position, OrderItem item) {
		if(item == null) {
			return;
		}
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		if(mCheckedStates.get(position)) {
			ViewUtils.setVisible(holder.divider, false);
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.btn_pay_green));
			holder.txtTitle.setTextColor(COLOR_TEXT_SELECTED);
			holder.txtPrice.setTextColor(COLOR_TEXT_SELECTED);
		} else {
			ViewUtils.setVisible(holder.divider, position != getCount() - 1);
			convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
			holder.txtTitle.setTextColor(COLOR_TEXT_NORMAL);
			holder.txtPrice.setTextColor(mColorPriceNormal);
		}
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
			if(mCheckedStates.get(key) && key < getCount()) {
				result.add(mItems.get(key));
			}
		}
		return result;
	}

	public void setStates(final SparseBooleanArrayParcelable states) {
		mCheckedStates = states;
	}
}
