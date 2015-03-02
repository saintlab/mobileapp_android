package com.omnom.android.acquiring.api;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 24.09.2014.
 */
public interface PaymentInfo<T> {
	void setOrderMessage(String orderMessage);

	public UserData getUser();

	public String getOrderId();

	void setOrderId(String order_id);

	public double getOrderAmount();

	void setOrderAmount(double orderAmount);

	public String getOrderMessage();

	void setUser(UserData user);

	public CardInfo getCardInfo();

	public void setExtra(T extra);

	public T getExtra();

	void setCardInfo(CardInfo cardInfo);
}
