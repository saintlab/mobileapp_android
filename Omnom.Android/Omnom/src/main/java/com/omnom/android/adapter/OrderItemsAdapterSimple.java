package com.omnom.android.adapter;

import android.content.Context;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.utils.SparseBooleanArrayParcelable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.List;

/**
 * Created by Ch3D on 02.12.2014.
 */
public class OrderItemsAdapterSimple extends OrderItemsAdapter {
	private final int mTextColorDefault;

	private final int mTextColorSelected;

	private boolean mHasChanges = true;

	private boolean mHasSelection = false;

	public OrderItemsAdapterSimple(final Context context,
	                               final List<OrderItem> items,
	                               final SparseBooleanArrayParcelable states,
	                               final boolean addFakeView) {
		super(context, items, states, addFakeView);
		mTextColorDefault = mContext.getResources().getColor(R.color.order_content_unselected);
		mTextColorSelected = COLOR_TEXT_NORMAL;
	}

	@Override
	protected void bindView(final View convertView, final int position, final OrderItem item) {
		if(item == null) {
			return;
		}
		if(mHasChanges) {
			mHasSelection = AndroidUtils.hasSelectedItems(mCheckedStates, getCount());
			mHasChanges = false;
		}
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		ViewUtils.setVisible(holder.divider, position != getCount() - 1);
		if(mCheckedStates.get(position) || !mHasSelection) {
			holder.txtTitle.setTextColor(mTextColorSelected);
			holder.txtPrice.setTextColor(mTextColorSelected);
		} else {
			holder.txtTitle.setTextColor(mTextColorDefault);
			holder.txtPrice.setTextColor(mTextColorDefault);
		}
		holder.txtTitle.setText(item.getTitle());
		holder.txtPrice.setText(String.valueOf(item.getPricePerItem()));
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mHasChanges = true;
	}
}