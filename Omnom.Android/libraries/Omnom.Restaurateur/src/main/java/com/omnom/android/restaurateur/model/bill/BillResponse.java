package com.omnom.android.restaurateur.model.bill;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;

/**
 * Created by Ch3D on 14.10.2014.
 */
public class BillResponse extends ResponseBase {

	public static final String STATUS_NEW = "new";

	public static final String STATUS_PAID = "paid";

	public static final String STATUS_ORDER_CLOSED = "order_closed";

	@Expose
	private String restaurantId;

	@Expose
	private String tableId;

	@Expose
	private String restaurateurOrderId;

	@Expose
	private String status;

	@Expose
	private int amount;

	@Expose
	private int closeAmount;

	@Expose
	private String createdAt;

	@Expose
	private String updatedAt;

	@Expose
	private int id;

	@Expose
	private String mailRestaurantId;

	@Expose
	private double amountCommission;

	@Expose
	private double tipCommission;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getCloseAmount() {
		return closeAmount;
	}

	public void setCloseAmount(int closeAmount) {
		this.closeAmount = closeAmount;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMailRestaurantId() {
		return mailRestaurantId;
	}

	public void setMailRestaurantId(String mailRestaurantId) {
		this.mailRestaurantId = mailRestaurantId;
	}

	public double getAmountCommission() {
		return amountCommission;
	}

	public void setAmountCommission(double amountCommission) {
		this.amountCommission = amountCommission;
	}

	public double getTipCommission() {
		return tipCommission;
	}

	public void setTipCommission(double tipCommission) {
		this.tipCommission = tipCommission;
	}
}
