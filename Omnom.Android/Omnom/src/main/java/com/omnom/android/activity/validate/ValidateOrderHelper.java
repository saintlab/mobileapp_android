package com.omnom.android.activity.validate;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import com.omnom.android.R;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.ViewUtils;

import java.math.BigDecimal;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 13.03.2015.
 */
public class ValidateOrderHelper {
	private final ValidateActivity mActivity;

	private final ValidateViewHelper mViewHelper;

	private UserOrder mOrder;

	public ValidateOrderHelper(ValidateActivity activity, ValidateViewHelper viewHelper, UserOrder order) {
		mActivity = activity;
		mViewHelper = viewHelper;
		mOrder = order;
	}

	public ValidateOrderHelper(final ValidateActivity activity, ValidateViewHelper viewHelper) {
		this(activity, viewHelper, UserOrder.create());
	}

	protected void updateOrderData(final OrderUpdateEvent event) {
		if(mOrder == null) {
			return;
		}
		final Item item = event.getItem();
		mOrder.addItem(item, event.getCount());
		mViewHelper.menuCategories.refresh(event);
		updateWishUi();
	}

	protected final UserOrder insureOrder() {
		if(mOrder == null) {
			mOrder = UserOrder.create();
		}
		return mOrder;
	}

	protected void updateWishUi() {
		final Button btnOrder = (Button) mActivity.findViewById(R.id.btn_order);
		if(mOrder == null || btnOrder == null) {
			return;
		}

		final BigDecimal totalAmount = mOrder.getTotalPrice();
		final boolean hasWishItems = mOrder != null && totalAmount.compareTo(BigDecimal.ZERO) > 0;

		if(hasWishItems) {
			ViewUtils.setVisibleGone(btnOrder, true);
			btnOrder.setText(AmountHelper.format(totalAmount) + mActivity.getString(R.string.currency_suffix_ruble));
			btnOrder.setTextColor(Color.WHITE);
			btnOrder.setBackgroundResource(R.drawable.btn_rounded_blue);
		} else {
			ViewUtils.setVisibleGone(btnOrder, false);
			btnOrder.setTextColor(Color.GRAY);
			btnOrder.setText(mActivity.getString(R.string.your_order));
			btnOrder.setBackgroundResource(R.drawable.btn_rounded_bordered_grey);
		}

		if(mActivity.getBottomView() != null) {
			View btnBill = findById(mActivity.getBottomView(), R.id.btn_bill);
			ViewUtils.setVisibleGone(btnBill, !hasWishItems);
		}
	}

	public void updateData(final UserOrder resultOrder) {
		mOrder.updateData(resultOrder);
		updateWishUi();
	}

	public void clearOrder() {
		mOrder.itemsTable().clear();
	}
}
