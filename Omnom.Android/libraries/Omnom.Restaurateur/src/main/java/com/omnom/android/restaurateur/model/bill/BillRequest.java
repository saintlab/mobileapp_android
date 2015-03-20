package com.omnom.android.restaurateur.model.bill;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class BillRequest {

	public static BillRequest create(String amount, String restaurantId, String tableId, String restaurateurOrderId) {
		final BillRequest billRequest = new BillRequest();
		billRequest.amount = amount;
		billRequest.restaurantId = restaurantId;
		billRequest.tableId = tableId;
		billRequest.restaurateurOrderId = restaurateurOrderId;
		return billRequest;
	}

	public static BillRequest createForWish(String restaurantId, String wishId) {
		final BillRequest billRequest = new BillRequest();
		billRequest.restaurantId = restaurantId;
		billRequest.wishId = wishId;
		return billRequest;
	}

	public static BillRequest create(double amount, Order order) {
		return create(Double.toString(amount), order.getRestaurantId(), order.getTableId(), order.getId());
	}

	@Expose
	private String description;

	@Expose
	private String wishId;

	@Expose
	private String amount;

	@Expose
	private String restaurantId;

	@Expose
	private String tableId;

	@Expose
	private String restaurateurOrderId;

	public String getWishId() {
		return wishId;
	}

	public void setWishId(final String wishId) {
		this.wishId = wishId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getRestaurateurOrderId() {
		return restaurateurOrderId;
	}

	public void setRestaurateurOrderId(String restaurateurOrderId) {
		this.restaurateurOrderId = restaurateurOrderId;
	}
}
