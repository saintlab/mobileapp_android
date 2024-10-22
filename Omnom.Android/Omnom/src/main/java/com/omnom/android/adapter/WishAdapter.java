package com.omnom.android.adapter;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.entrance.DeliveryEntranceData;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 03.03.2015.
 */
public class WishAdapter extends RecyclerView.Adapter {

	public static final SimpleDateFormat LUNCH_DATE_FORMAT = new SimpleDateFormat("dd MMMM", AndroidUtils.russianLocale);

	public static final int VIEW_TYPE_WISH_ITEM = 0;

	public static final int VIEW_TYPE_WISH_FOOTER = 1;

	public static final int VIEW_TYPE_WISH_FOOTER_LABEL = 2;

	public static final int VIEW_TYPE_TABLE_ITEM = 3;

	public static final int VIEW_TYPE_TABLE_HEADER = 4;

	static class FooterViewHolder extends RecyclerView.ViewHolder {

		@InjectView(R.id.txt_amount)
		protected TextView txtAmount;

		public FooterViewHolder(final View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
		}
	}

	static class HeaderViewHolder extends RecyclerView.ViewHolder {
		public HeaderViewHolder(final View itemView) {
			super(itemView);
		}
	}

	static class ItemViewHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		@InjectView(R.id.txt_info)
		protected TextView txtInfo;

		@InjectView(R.id.btn_apply)
		protected Button btnApply;

		@InjectView(R.id.divider)
		protected View viewDivider;

		public ItemViewHolder(final View convertView) {
			super(convertView);
			ButterKnife.inject(this, convertView);
		}
	}

	private class UserOrderDataFooterLabel extends UserOrderDataFooter {

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

	private final View.OnClickListener mClickListener;

	private final LayoutInflater mInflater;

	private ArrayList<OrderItem> mTableItems;

	private EntranceData mEntranceData;

	private List<UserOrderData> _lazy_selected_data;

	public WishAdapter(Context context, UserOrder order, Collection<OrderItem> tableItems,
	                   EntranceData entranceData, View.OnClickListener clickListener) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mOrder = order;
		mEntranceData = entranceData;
		mClickListener = clickListener;
		mTableItems = new ArrayList<>(tableItems);
		if(mTableItems.size() > 0) {
			mTableItems.add(0, new OrderItemHeader());
		}
	}

	@Override
	public int getItemViewType(int position) {
		final Object data = getItemAt(position);
		if(data instanceof UserOrderDataFooterLabel) {
			return VIEW_TYPE_WISH_FOOTER_LABEL;
		} else if(data instanceof UserOrderDataFooter) {
			return VIEW_TYPE_WISH_FOOTER;
		} else if(data instanceof UserOrderData) {
			return VIEW_TYPE_WISH_ITEM;
		} else if(data instanceof OrderItemHeader) {
			return VIEW_TYPE_TABLE_HEADER;
		} else if(data instanceof OrderItem) {
			return VIEW_TYPE_TABLE_ITEM;
		}
		throw new RuntimeException("wrong item type");
	}

	public Object getItemAt(final int position) {
		final int wishSize = getSelectedItems().size();
		if(position < wishSize) {
			return getSelectedItems().get(position);
		}
		return mTableItems.get(position - wishSize);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		View v;
		RecyclerView.ViewHolder viewHolder = null;
		switch(viewType) {
			case VIEW_TYPE_WISH_FOOTER_LABEL:
				v = mInflater.inflate(R.layout.item_wish_footer_label, parent, false);
				viewHolder = new FooterViewHolder(v);
				break;

			case VIEW_TYPE_WISH_FOOTER:
				v = mInflater.inflate(R.layout.item_wish_footer, parent, false);
				viewHolder = new FooterViewHolder(v);
				break;

			case VIEW_TYPE_WISH_ITEM:
				v = mInflater.inflate(R.layout.item_wish, parent, false);
				viewHolder = new ItemViewHolder(v);
				break;
			case VIEW_TYPE_TABLE_ITEM:
				v = mInflater.inflate(R.layout.item_wish, parent, false);
				final Button btnApply = (Button) v.findViewById(R.id.btn_apply);
				btnApply.setBackgroundResource(R.drawable.btn_wish_on_table);
				btnApply.setText(StringUtils.EMPTY_STRING);
				viewHolder = new ItemViewHolder(v);
				break;

			case VIEW_TYPE_TABLE_HEADER:
				v = mInflater.inflate(R.layout.item_wish_table_header, parent, false);
				viewHolder = new HeaderViewHolder(v);
				break;
		}
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		final int itemType = getItemViewType(position);
		switch(itemType) {
			case VIEW_TYPE_TABLE_HEADER:
				holder.itemView.findViewById(R.id.btn_refresh).setOnClickListener(mClickListener);
				break;

			case VIEW_TYPE_WISH_ITEM:
				final ItemViewHolder ivh = (ItemViewHolder) holder;
				final UserOrderData data = (UserOrderData) getItemAt(position);
				ivh.txtTitle.setText(data.item().name());

				final String price = AmountHelper.format(data.item().price()) + mContext.getString(R.string.currency_suffix_ruble);
				ivh.btnApply.setText(mContext.getString(R.string.wish_items_price_detailed, data.amount(), price));
				ivh.btnApply.setTag(data);
				ivh.btnApply.setOnClickListener(mClickListener);
				MenuHelper.bindDetails(mContext, data.item().details(), ivh.txtInfo, false);
				ViewUtils.setVisibleGone(ivh.viewDivider, getItemViewType(position + 1) != VIEW_TYPE_WISH_FOOTER_LABEL);
				ViewUtils.setVisibleGone(ivh.viewDivider, getItemViewType(position + 1) != VIEW_TYPE_WISH_FOOTER);
				break;

			case VIEW_TYPE_TABLE_ITEM:
				final ItemViewHolder tivh = (ItemViewHolder) holder;
				final OrderItem orderItem = (OrderItem) getItemAt(position);
				tivh.txtTitle.setText(orderItem.getTitle());

				tivh.btnApply.setTag(orderItem);
				tivh.btnApply.setOnClickListener(mClickListener);
				MenuHelper.bindDetails(mContext, null, tivh.txtInfo, false);
				break;

			case VIEW_TYPE_WISH_FOOTER_LABEL:
				final TextView lunchOrderInfo = (TextView) holder.itemView.findViewById(R.id.lunch_order_info);
				final DeliveryEntranceData entranceData = (DeliveryEntranceData) mEntranceData;
				lunchOrderInfo.setText(mContext.getString(R.string.wish_lunch_order_info,
				                                          LUNCH_DATE_FORMAT.format(entranceData.deliveryTime()),
				                                          entranceData.deliveryAddress()));
				break;

			case VIEW_TYPE_WISH_FOOTER:
				final Button btnClear = (Button) holder.itemView.findViewById(R.id.btn_clear);
				final Button btnSend = (Button) holder.itemView.findViewById(R.id.btn_send);
				final TextView txtAmount = (TextView) holder.itemView.findViewById(R.id.txt_amount);
				final View panelAmount = holder.itemView.findViewById(R.id.panel_top);

				btnClear.setOnClickListener(mClickListener);
				btnSend.setOnClickListener(mClickListener);
				txtAmount.setText(AmountHelper.format(mOrder.getTotalPrice()) + mContext.getString(R.string.currency_suffix_ruble));

				final boolean enabled = getSelectedItems().size() > 1;
				ViewUtils.setVisibleGone(panelAmount, enabled);
				btnClear.setEnabled(enabled);
				// TODO:
				// btnSend.setEnabled(enabled);
				break;
		}
	}

	@Override
	public int getItemCount() {
		return getSelectedItems().size() + mTableItems.size();
	}

	private List<UserOrderData> getSelectedItems() {
		if(_lazy_selected_data == null) {
			final List<UserOrderData> selectedItems = mOrder.getSelectedItems();
			if(mEntranceData instanceof DeliveryEntranceData) {
				selectedItems.add(new UserOrderDataFooterLabel());
			}
			selectedItems.add(new UserOrderDataFooter());
			_lazy_selected_data = selectedItems;
		}
		return _lazy_selected_data;
	}

	public void remove(final Object tag) {
		final List<UserOrderData> selectedItems = getSelectedItems();
		final int position = selectedItems.indexOf(tag);
		if(position >= 0) {
			selectedItems.remove(tag);
			notifyItemRemoved(position);
		}
		if(selectedItems.size() == 1) {
			notifyItemChanged(0);
		} else {
			// update total amount
			final int itemCount = getItemCount();
			for(int i = 0; i < itemCount; i++) {
				if(getItemViewType(i) == VIEW_TYPE_WISH_FOOTER) {
					if(i > 0) {
						// update pre-footer item to hide delimiter
						notifyItemChanged(i - 1);
					}
					notifyItemChanged(i);
					break;
				}
			}
		}
	}

	private int itemIndex(Item item) {
		final List<UserOrderData> selectedItems = getSelectedItems();
		for(int i = 0; i < selectedItems.size(); i++) {
			final UserOrderData orderData = selectedItems.get(i);
			if(orderData.item().id().equals(item.id())) {
				return i;
			}
		}
		return -1;
	}

	public void removeItem(final Item item) {
		final int index = itemIndex(item);
		if(index >= 0) {
			remove(getSelectedItems().get(index));
		}
	}

	public void clearCache() {
		_lazy_selected_data = null;
	}
}
