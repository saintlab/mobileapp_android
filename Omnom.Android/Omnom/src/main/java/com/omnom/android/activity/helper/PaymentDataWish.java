package com.omnom.android.activity.helper;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.WishResponse;

/**
 * Created by Ch3D on 07.05.2015.
 */
public class PaymentDataWish extends PaymentData {

	public static class Builder extends PaymentData.Builder<PaymentDataWish, PaymentDataWish.Builder> {
		protected UserOrder order;

		protected WishResponse wishResponse;

		public Builder setOrder(final UserOrder order) {
			this.order = order;
			return builder();
		}

		public Builder setWishResponse(final WishResponse wishResponse) {
			this.wishResponse = wishResponse;
			return builder();
		}

		@Override
		public PaymentDataWish build() {
			return new PaymentDataWish(details, cardInfo, restaurant, entranceData, order, wishResponse);
		}

	}

	private UserOrder order;

	private WishResponse wishResponse;

	protected PaymentDataWish(final OrderFragment.PaymentDetails details,
	                          final CardInfo cardInfo,
	                          final Restaurant restaurant,
	                          final EntranceData entranceData,
	                          UserOrder userOrder,
	                          WishResponse wishResponse) {
		super(details, cardInfo, restaurant, entranceData);
		this.order = userOrder;
		this.wishResponse = wishResponse;
	}

	public UserOrder getOrder() {
		return order;
	}

	public WishResponse getWishResponse() {
		return wishResponse;
	}
}
