package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by mvpotter on 21/11/14.
 */
public final class BillViewMixpanelEvent extends AbstractBaseMixpanelEvent {

	@Expose
	private String requestId;

	@Expose
	private double amount;

	@Expose
	@SerializedName("paid_amount")
	private double paidAmount;

	@Expose
	@SerializedName("paid_tip_amount")
	private double paidTipAmount;

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
			this.amount = order.getTotalAmount();
			this.paidAmount = order.getPaidAmount();
			this.paidTipAmount = order.getPaidTip();
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

	public double getAmount() {
		return amount;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public double getPaidTipAmount() {
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
