package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.utils.utils.AmountHelper;

/**
 * Created by mvpotter on 21/11/14.
 */
public final class BillViewMixpanelEvent extends AbstractBaseMixpanelEvent {

	@Expose
	private String requestId;

	@Expose
	private int amount;

	@Expose
	@SerializedName("paid_amount")
	private int paidAmount;

	@Expose
	@SerializedName("paid_tip_amount")
	private int paidTipAmount;

	@Expose
	@SerializedName("order_id")
	private String orderId;

	@Expose
	@SerializedName("restaurant_id")
	private String restaurantId;

	@Expose
	@SerializedName("table_id")
	private String tableId;

	public BillViewMixpanelEvent(UserData userData, final String requestId, final Order order, final UserData user) {
		super(userData);
		this.requestId = requestId;
		if(order != null) {
			this.amount = AmountHelper.toInt(order.getTotalAmount());
			this.paidAmount = AmountHelper.toInt(order.getPaidAmount());
			this.paidTipAmount = AmountHelper.toInt(order.getPaidTip());
			this.orderId = order.getId();
			this.restaurantId = order.getRestaurantId();
			this.tableId = order.getTableId();
		}
	}

	@Override
	public String getName() {
		return "bill_view";
	}

	public String getRequestId() {
		return requestId;
	}

	public int getAmount() {
		return amount;
	}

	public int getPaidAmount() {
		return paidAmount;
	}

	public int getPaidTipAmount() {
		return paidTipAmount;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getTableId() {
		return tableId;
	}

}
