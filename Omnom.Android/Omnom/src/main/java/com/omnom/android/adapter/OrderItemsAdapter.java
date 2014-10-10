package com.omnom.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.restaurateur.model.order.OrderItem;

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

	private final LayoutInflater mInflater;
	private Context mContext;
	private List<OrderItem> mItems;
	private ViewHolder holder;

	public OrderItemsAdapter(final Context context, final List<OrderItem> orders) {
		mContext = context;
		mItems = orders;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public OrderItem getItem(int position) {
		return mItems.get(position);
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
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_order_item, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		bindView(convertView, getItem(position));
		return convertView;
	}

	private void bindView(View convertView, OrderItem item) {
		holder.txtTitle.setText(item.getTitle().toString());
		holder.txtPrice.setText(item.getPricePerItem().toString());
	}
}
