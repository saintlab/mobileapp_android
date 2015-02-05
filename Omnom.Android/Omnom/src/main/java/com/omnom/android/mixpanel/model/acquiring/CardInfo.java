package com.omnom.android.mixpanel.model.acquiring;

import com.google.gson.annotations.Expose;

/**
 * Created by mvpotter on 2/5/2015.
 */
public class CardInfo {

	@Expose
	protected final String cardId;

	@Expose
	protected final String maskedPan;

	public CardInfo(String cardId, String maskedPan) {
		this.cardId = cardId;
		this.maskedPan = maskedPan;
	}

}
