package com.omnom.android.acquiring.mailru.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class RegisterCardResponse extends AcquiringResponse {
	@Expose
	@SerializedName("card_id")
	private String cardId;

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
}
