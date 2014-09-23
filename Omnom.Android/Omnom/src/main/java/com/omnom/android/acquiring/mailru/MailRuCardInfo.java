package com.omnom.android.acquiring.mailru;

import android.content.Context;
import android.text.TextUtils;

import com.omnom.android.R;
import com.omnom.android.linker.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class MailRuCardInfo {
	public static MailRuCardInfo create(final Context context, final String id) {
		final MailRuCardInfo cardInfo = new MailRuCardInfo();
		cardInfo.setCardId(id);
		cardInfo.cvv = context.getString(R.string.acquiring_mailru_test_cvv);
		return cardInfo;
	}

	public static MailRuCardInfo create(final String pan, final String expirationDate, final String cvv) {
		final MailRuCardInfo cardInfo = new MailRuCardInfo();
		cardInfo.pan = pan;
		cardInfo.expDate = expirationDate;
		cardInfo.cvv = cvv;
		return cardInfo;
	}

	private String pan = StringUtils.EMPTY_STRING;
	private String expDate = StringUtils.EMPTY_STRING;
	private String cvv = StringUtils.EMPTY_STRING;
	private String cardId = StringUtils.EMPTY_STRING;
	private boolean addCard = false;

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public boolean isAddCard() {
		return addCard;
	}

	public void setAddCard(boolean addCard) {
		this.addCard = addCard;
	}

	public HashMap<String, String> getCardInfo() {
		final HashMap<String, String> cardInfo = new HashMap<String, String>();

		if(!TextUtils.isEmpty(cardId)) {
			cardInfo.put("card_id", cardId);
		} else {
			cardInfo.put("pan", pan);
			cardInfo.put("exp_date", expDate);
			cardInfo.put("add_card", "1");
		}

		if(!TextUtils.isEmpty(cvv)) {
			cardInfo.put("cvv", cvv);
		}

		return cardInfo;
	}
}
