package com.omnom.android.activity.helper;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;

/**
 * Created by Ch3D on 07.05.2015.
 */
public class PaymentDataTable extends PaymentData {
	public static class Builder extends PaymentData.Builder<PaymentDataTable, PaymentDataTable.Builder> {
		protected Order order;

		public Builder setOrder(final Order order) {
			this.order = order;
			return builder();
		}

		@Override
		public PaymentDataTable build() {
			return new PaymentDataTable(details, cardInfo, restaurant, entranceData, order);
		}
	}

	protected Order order;

	protected PaymentDataTable(final OrderFragment.PaymentDetails details,
	                           final CardInfo cardInfo,
	                           final Restaurant restaurant,
	                           final EntranceData entranceData,
	                           final Order order) {
		super(details, cardInfo, restaurant, entranceData);
		this.order = order;
	}

	public Order getOrder() {
		return order;
	}
}
