package com.omnom.android.acquiring.mailru.response;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class CardRegisterPollingResponse extends AcquiringPollingResponse {

	private String cardId;

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
}
