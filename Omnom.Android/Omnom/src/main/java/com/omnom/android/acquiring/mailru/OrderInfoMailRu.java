package com.omnom.android.acquiring.mailru;

import com.omnom.android.acquiring.OrderInfo;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class OrderInfoMailRu implements OrderInfo {
	public static OrderInfoMailRu create(double amount, String orderId, String orderMsg) {
		return new OrderInfoMailRu(amount, orderId, orderMsg);
	}

	private double amount;
	private String orderId;
	private String orderMsg;

	private OrderInfoMailRu(double amount, String orderId, String orderMsg) {
		this.amount = amount;
		this.orderId = orderId;
		this.orderMsg = orderMsg;
	}

	@Override
	public String getOrderId() {
		return orderId;
	}

	@Override
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public String getOrderMsg() {
		return orderMsg;
	}

	@Override
	public void setOrderMsg(String orderMsg) {
		this.orderMsg = orderMsg;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
