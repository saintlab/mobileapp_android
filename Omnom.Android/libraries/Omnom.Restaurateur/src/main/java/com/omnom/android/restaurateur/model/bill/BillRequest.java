package com.omnom.android.restaurateur.model.bill;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class BillRequest {
	@Expose
	private String description;

	@Expose
	private String amount;

	@Expose
	private String restaurantId;

	@Expose
	private String tableId;

	@Expose
	private String restaurateurOrderId;

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
