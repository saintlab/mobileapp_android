package com.omnom.android.acquiring.mailru.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.omnom.android.R;
import com.omnom.android.utils.utils.StringUtils;

import java.util.HashMap;

import io.card.payment.CreditCard;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class CardInfo implements Parcelable {

	public static final Creator<CardInfo> CREATOR = new Creator<CardInfo>() {

		@Override
		public CardInfo createFromParcel(Parcel in) {
			return new CardInfo(in);
		}

		@Override
		public CardInfo[] newArray(int size) {
			return new CardInfo[size];
		}
	};

	public static CardInfo create(final Context context, final String id, final String testCvv) {
		return create(context, id, StringUtils.EMPTY_STRING, testCvv);
	}

	public static CardInfo create(final Context context, final String id, final String pan, final String testCvv) {
		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardId(id);
		cardInfo.pan = pan;
		cardInfo.cvv = testCvv;
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

	public static CardInfo createTestCard(Context context, final CreditCard card) {
		String holder = context.getString(R.string.acquiring_mailru_cardholder);
		String pan = card.cardNumber;
		String expDate = card.expiryMonth + "." + card.expiryYear;
		String cvv = card.cvv;
		return create(pan, expDate, cvv, holder);
	}

	@Expose
	private String pan = StringUtils.EMPTY_STRING;

	@Expose
	private String expDate = StringUtils.EMPTY_STRING;

	@Expose
	private String cvv = StringUtils.EMPTY_STRING;

	@Expose
	private String cardId = StringUtils.EMPTY_STRING;

	@Expose
	private String holder = StringUtils.EMPTY_STRING;

	private boolean addCard = false;

	public CardInfo(Parcel parcel) {
		pan = parcel.readString();
		holder = parcel.readString();
		expDate = parcel.readString();
		cvv = parcel.readString();
		cardId = parcel.readString();
	}

	private CardInfo() {
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(pan);
		dest.writeString(holder);
		dest.writeString(expDate);
		dest.writeString(cvv);
		dest.writeString(cardId);
	}

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

	public HashMap<String, String> getCardInfoMap() {
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

	@Deprecated
	public String toGson(Gson gson) {
		return gson.toJson(this);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
