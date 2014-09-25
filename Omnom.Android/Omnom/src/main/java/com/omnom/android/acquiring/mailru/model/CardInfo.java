package com.omnom.android.acquiring.mailru.model;

import android.content.Context;
import android.text.TextUtils;

import com.omnom.android.R;
import com.omnom.android.linker.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class CardInfo {
	public static CardInfo create(final Context context, final String id) {
		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardId(id);
		cardInfo.cvv = context.getString(R.string.acquiring_mailru_test_cvv);
		return cardInfo;
	}

	public static CardInfo create(final String pan, final String expirationDate, final String cvv) {
		final CardInfo cardInfo = new CardInfo();
		cardInfo.pan = pan;
		cardInfo.expDate = expirationDate;
		cardInfo.cvv = cvv;
		return cardInfo;
	}

	public static CardInfo create(String pan, String exp_date, String cvv, String holder) {
		CardInfo info = create(pan, exp_date, cvv);
		info.holder = holder;
		return info;
	}

	public static CardInfo createTestCard(Context context) {
		String holder = context.getString(R.string.acquiring_mailru_cardholder);
		String pan = "6011000000000004";
		String expDate = "12.2015";
		String cvv = "123";
		return create(pan, expDate, cvv, holder);
	}

	private String pan = StringUtils.EMPTY_STRING;
	private String expDate = StringUtils.EMPTY_STRING;
	private String cvv = StringUtils.EMPTY_STRING;
	private String cardId = StringUtils.EMPTY_STRING;
	private String holder = StringUtils.EMPTY_STRING;
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

	public void toMap(HashMap<String, String> map) {
		map.put("cardholder", getHolder());
		map.put("pan", getPan());
		map.put("cvv", getCvv());
		map.put("exp_date", getExpDate());
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

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public void storeCardId(HashMap<String, String> params) {
		params.put("card_id", cardId);
	}
}
