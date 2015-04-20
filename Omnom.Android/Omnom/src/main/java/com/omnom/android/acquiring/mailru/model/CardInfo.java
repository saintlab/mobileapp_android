package com.omnom.android.acquiring.mailru.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
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

	public static class Builder {

		private String cardId = StringUtils.EMPTY_STRING;

		private String pan = StringUtils.EMPTY_STRING;

		private String mixpanelPan = StringUtils.EMPTY_STRING;

		private String expDate = StringUtils.EMPTY_STRING;

		private String cvv = StringUtils.EMPTY_STRING;

		private String holder = StringUtils.EMPTY_STRING;

		private boolean addCard = false;

		public Builder cardId(final String val) {
			cardId = val;
			return this;
		}

		public Builder pan(final String val) {
			pan = val;
			return this;
		}

		public Builder mixpanelPan(final String val) {
			mixpanelPan = val;
			return this;
		}

		public Builder expDate(final String val) {
			expDate = val;
			return this;
		}

		public Builder cvv(final String val) {
			cvv = val;
			return this;
		}

		public Builder holder(final String val) {
			holder = val;
			return this;
		}

		public Builder addCard(final boolean val) {
			addCard = val;
			return this;
		}

		public CardInfo build() {
			return new CardInfo(this);
		}

	}

	private static final String TEST_CARD_HOLDER = "Omnom";

	public static CardInfo createTestCard() {
		return new Builder()
				.holder(TEST_CARD_HOLDER)
				.pan("6011000000000004")
				.expDate("12.2015")
				.cvv("123")
				.build();
	}

	public static CardInfo createTestCard(final CreditCard card) {
		return new Builder()
				.holder(TEST_CARD_HOLDER)
				.pan(card.cardNumber)
				.expDate(card.expiryMonth + "." + card.expiryYear)
				.cvv(card.cvv)
				.build();
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

	private transient String mixpanelPan;

	private boolean addCard = false;

	private CardInfo() {
	}

	private CardInfo(final Builder builder) {
		cardId = builder.cardId;
		pan = builder.pan;
		mixpanelPan = builder.mixpanelPan;
		expDate = builder.expDate;
		cvv = builder.cvv;
		holder = builder.holder;
		addCard = builder.addCard;
	}

	public CardInfo(Parcel parcel) {
		pan = parcel.readString();
		mixpanelPan = parcel.readString();
		holder = parcel.readString();
		expDate = parcel.readString();
		cvv = parcel.readString();
		cardId = parcel.readString();
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(pan);
		dest.writeString(mixpanelPan);
		dest.writeString(holder);
		dest.writeString(expDate);
		dest.writeString(cvv);
		dest.writeString(cardId);
	}

	public String getPan() {
		return pan;
	}

	public String getMixpanelPan() {
		return mixpanelPan;
	}

	public String getExpDate() {
		return expDate;
	}

	public String getCvv() {
		return cvv;
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

	public String getHolder() {
		return holder;
	}

	public void storeCardId(HashMap<String, String> params) {
		params.put("card_id", cardId);
	}

	public void toMap(HashMap<String, String> map) {
		map.put("cardholder", getHolder());
		map.put("pan", getPan());
		map.put("cvv", getCvv());
		map.put("exp_date", getExpDate());
		map.put("add_card", Boolean.toString(addCard));
	}

	public HashMap<String, String> getCardInfoMap() {
		final HashMap<String, String> cardInfo = new HashMap<String, String>();

		if(!TextUtils.isEmpty(cardId)) {
			cardInfo.put("card_id", cardId);
		} else {
			cardInfo.put("pan", pan);
			cardInfo.put("exp_date", expDate);
			cardInfo.put("add_card", Boolean.toString(addCard));
		}

		if(!TextUtils.isEmpty(cvv)) {
			cardInfo.put("cvv", cvv);
		}

		return cardInfo;
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
