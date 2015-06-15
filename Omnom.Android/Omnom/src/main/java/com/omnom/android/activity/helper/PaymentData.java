package com.omnom.android.activity.helper;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;

/**
 * Created by Ch3D on 07.05.2015.
 */
public abstract class PaymentData {
	public abstract static class Builder<V extends PaymentData, T extends Builder<V, ?>> {
		protected OrderFragment.PaymentDetails details;

		protected CardInfo cardInfo;

		protected Restaurant restaurant;

		protected EntranceData entranceData;

		protected final T builder() {
			return (T) this;
		}

		public T setCardInfo(final CardInfo cardInfo) {
			this.cardInfo = cardInfo;
			return builder();
		}

		public T setRestaurant(final Restaurant restaurant) {
			this.restaurant = restaurant;
			return builder();
		}

		public T setEntranceData(final EntranceData entranceData) {
			this.entranceData = entranceData;
			return builder();
		}

		public T setDetails(OrderFragment.PaymentDetails details) {
			this.details = details;
			return builder();
		}

		public abstract V build();
	}

	protected OrderFragment.PaymentDetails details;

	protected CardInfo cardInfo;

	protected Restaurant restaurant;

	protected EntranceData entranceData;

	protected PaymentData(final OrderFragment.PaymentDetails details,
	                      final CardInfo cardInfo,
	                      final Restaurant restaurant,
	                      final EntranceData entranceData) {
		this.details = details;
		this.cardInfo = cardInfo;
		this.restaurant = restaurant;
		this.entranceData = entranceData;
	}

	public OrderFragment.PaymentDetails getDetails() {
		return details;
	}

	public CardInfo getCardInfo() {
		return cardInfo;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public EntranceData getEntranceData() {
		return entranceData;
	}
}
