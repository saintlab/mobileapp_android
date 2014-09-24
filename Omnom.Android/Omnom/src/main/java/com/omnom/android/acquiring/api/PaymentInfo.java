package com.omnom.android.acquiring.api;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.UserData;

/**
 * Created by Ch3D on 24.09.2014.
 */
public interface PaymentInfo {
	public UserData getUser();

	public String getOrderId();

	public double getOrderAmount();

	public String getOrderMessage();

	public CardInfo getCardInfo();
}
