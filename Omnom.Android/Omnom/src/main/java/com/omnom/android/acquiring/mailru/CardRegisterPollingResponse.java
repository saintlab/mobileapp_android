package com.omnom.android.acquiring.mailru;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class CardRegisterPollingResponse {
	@Expose
	private String status;

	@Expose
	private String url;

	private String cardId;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
}
