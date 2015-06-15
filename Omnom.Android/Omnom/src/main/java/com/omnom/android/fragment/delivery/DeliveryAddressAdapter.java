package com.omnom.android.fragment.delivery;

import android.content.Context;
import android.os.Parcel;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Ch3D on 25.03.2015.
 */
public class DeliveryAddressAdapter extends DeliveryDataAdapterBase<DeliveryAddressData, DeliveryAddressAdapter.ItemViewHolder> {

	public static class ItemViewHolder extends RecyclerView.ViewHolder {

		@Optional
		@InjectView(R.id.txt_title)
		protected TextView mTxtTitle;

		@Optional
		@InjectView(R.id.txt_info)
		protected TextView mTxtInfo;

		@Optional
		@InjectView(R.id.indicator)
		protected View mCheckedIndicator;

		public ItemViewHolder(final View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
		}
	}

	public static class FooterViewHolder extends ItemViewHolder {

		@InjectView(R.id.btn_send)
		protected TextView mTxtSend;

		public FooterViewHolder(final View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
		}
	}

	private static class Footer extends DeliveryAddressData {
		@Override
		public String name() {
			return StringUtils.EMPTY_STRING;
		}

		@Override
		public String address() {
			return StringUtils.EMPTY_STRING;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
		}
	}

	private static final int VIEW_TYPE_ITEM = 0;

	private static final int VIEW_TYPE_FOOTER = 1;

	public static DeliveryAddressAdapter create(final Context context, final ArrayList<DeliveryAddressData> data) {
		data.add(new Footer());
		return new DeliveryAddressAdapter(context, data);
	}

	private DeliveryAddressAdapter(final Context context, final ArrayList<DeliveryAddressData> data) {
		super(context, data);
	}

	@Override
	public ItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		switch(viewType) {
			case VIEW_TYPE_ITEM:
				return new DeliveryAddressAdapter.ItemViewHolder(mInflater.inflate(R.layout.item_dinner_address, parent, false));

			case VIEW_TYPE_FOOTER:
				return new DeliveryAddressAdapter.FooterViewHolder(mInflater.inflate(R.layout.item_dinner_address_footer, parent, false));
		}
		throw new IllegalArgumentException("Unable to instantiate holder for view type = " + viewType);
	}

	@Override
	public int getItemViewType(final int position) {
		final DeliveryAddressData item = getItem(position);
		if(item instanceof Footer) {
			return VIEW_TYPE_FOOTER;
		}
		return VIEW_TYPE_ITEM;
	}

	@Override
	public void onBindViewHolder(final ItemViewHolder holder, final int position) {
		final int itemViewType = getItemViewType(position);

		switch(itemViewType) {
			case VIEW_TYPE_ITEM:
				final DeliveryAddressData item = getItem(position);
				holder.mTxtTitle.setText(item.name());
				holder.mTxtInfo.setText(item.address());
				ViewUtils.setVisibleInvisible(holder.mCheckedIndicator, isItemChecked(position));
				break;

			case VIEW_TYPE_FOOTER:
				FooterViewHolder fvh = (FooterViewHolder) holder;
				fvh.mTxtSend.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(final View v) {
						AndroidUtils.sendFeedbackEmail(mContext, R.string.subject_add_my_address, R.string.subject_add_my_address);
					}
				});
				break;
		}
	}
}
