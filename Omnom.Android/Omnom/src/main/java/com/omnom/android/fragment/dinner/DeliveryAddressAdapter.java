package com.omnom.android.fragment.dinner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 25.03.2015.
 */
public class DeliveryAddressAdapter extends DeliveryDataAdapterBase<DeliveryAddressData, DeliveryAddressAdapter.ItemViewHolder> {

	public static class ItemViewHolder extends RecyclerView.ViewHolder {

		@InjectView(R.id.txt_title)
		protected TextView mTxtTitle;

		@InjectView(R.id.txt_info)
		protected TextView mTxtInfo;

		@InjectView(R.id.indicator)
		protected View mCheckedIndicator;

		public ItemViewHolder(final View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
		}
	}

	public DeliveryAddressAdapter(final Context context, final ArrayList<DeliveryAddressData> data) {
		super(context, data);
	}

	@Override
	public ItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		return new DeliveryAddressAdapter.ItemViewHolder(mInflater.inflate(R.layout.item_dinner_address, parent, false));
	}

	@Override
	public void onBindViewHolder(final ItemViewHolder holder, final int position) {
		final DeliveryAddressData item = getItem(position);
		holder.mTxtTitle.setText(item.name());
		holder.mTxtInfo.setText(item.address());
		ViewUtils.setVisible2(holder.mCheckedIndicator, isItemChecked(position));
	}
}
