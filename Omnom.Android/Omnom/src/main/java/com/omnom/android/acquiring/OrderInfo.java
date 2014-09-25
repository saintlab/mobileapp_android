package com.omnom.android.acquiring;

/**
 * Created by Ch3D on 24.09.2014.
 */
public interface OrderInfo {
	public String getOrderId();

	public void setOrderId(String orderId);

	public String getOrderMsg();

	public void setOrderMsg(String orderMsg);

	public double getAmount();

	public void setAmount(double amount);
}
