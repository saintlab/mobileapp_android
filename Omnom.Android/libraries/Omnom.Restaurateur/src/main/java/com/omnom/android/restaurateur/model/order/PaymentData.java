package com.omnom.android.restaurateur.model.order;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 02.12.2014.
 */
public class PaymentData {
	@Expose
	private Order order;

	@Expose
	private User user;

	@Expose
	private Transaction transaction;

	public Order getOrder() {
		return order;
	}

	public void setOrder(final Order order) {
		this.order = order;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(final Transaction transaction) {
		this.transaction = transaction;
	}
}
