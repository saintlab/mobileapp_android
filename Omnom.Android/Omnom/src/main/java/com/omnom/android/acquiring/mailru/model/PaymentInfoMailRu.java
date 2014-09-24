package com.omnom.android.acquiring.mailru.model;

import com.omnom.android.acquiring.api.PaymentInfo;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class PaymentInfoMailRu implements PaymentInfo<MailRuExtra> {
	public static PaymentInfoMailRu create(UserData user, CardInfo cardInfo, MailRuExtra extra, double amount, String orderId,
	                                       String orderMessage) {
		final PaymentInfoMailRu paymentInfo = new PaymentInfoMailRu();
		paymentInfo.setUser(user);
		paymentInfo.setCardInfo(cardInfo);
		paymentInfo.setExtra(extra);
		paymentInfo.setOrderAmount(amount);
		paymentInfo.setOrderId(orderId);
		paymentInfo.setOrderMessage(orderMessage);
		return paymentInfo;
	}

	private MailRuExtra extra;
	private CardInfo cardInfo;
	private UserData user;
	private String orderId;
	private double orderAmount;
	private String orderMessage;

	private PaymentInfoMailRu() {
	}

	@Override
	public MailRuExtra getExtra() {
		return extra;
	}

	@Override
	public void setExtra(MailRuExtra extra) {
		this.extra = extra;
	}

	@Override
	public String getOrderId() {
		return orderId;
	}

	@Override
	public void setOrderId(String order_id) {
		this.orderId = order_id;
	}

	@Override
	public double getOrderAmount() {
		return orderAmount;
	}

	@Override
	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}

	@Override
	public String getOrderMessage() {
		return orderMessage;
	}

	@Override
	public void setOrderMessage(String orderMessage) {
		this.orderMessage = orderMessage;
	}

	@Override
	public UserData getUser() {
		return user;
	}

	@Override
	public void setUser(UserData user) {
		this.user = user;
	}

	@Override
	public CardInfo getCardInfo() {
		return cardInfo;
	}

	@Override
	public void setCardInfo(CardInfo cardInfo) {
		this.cardInfo = cardInfo;
	}
}
