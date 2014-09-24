package com.omnom.android.acquiring.mailru.model;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class PaymentInfo {
	public static PaymentInfo create(UserData user, CardInfo cardInfo, MailRuExtra extra, long amount, String orderId,
	                                 String orderMessage) {
		final PaymentInfo paymentInfo = new PaymentInfo();
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
	private long orderAmount;
	private String orderMessage;

	private PaymentInfo() {
	}

	public MailRuExtra getExtra() {
		return extra;
	}

	public void setExtra(MailRuExtra extra) {
		this.extra = extra;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String order_id) {
		this.orderId = order_id;
	}

	public long getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(long orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getOrderMessage() {
		return orderMessage;
	}

	public void setOrderMessage(String orderMessage) {
		this.orderMessage = orderMessage;
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public CardInfo getCardInfo() {
		return cardInfo;
	}

	public void setCardInfo(CardInfo cardInfo) {
		this.cardInfo = cardInfo;
	}
}
