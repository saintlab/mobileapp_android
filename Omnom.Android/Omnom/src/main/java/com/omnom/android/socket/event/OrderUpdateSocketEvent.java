package com.omnom.android.socket.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OrderUpdateSocketEvent extends BaseOrderSocketEvent {

	public static final Parcelable.Creator<OrderUpdateSocketEvent> CREATOR = new Parcelable.Creator<OrderUpdateSocketEvent>() {

		@Override
		public OrderUpdateSocketEvent createFromParcel(Parcel in) {
			return new OrderUpdateSocketEvent(in);
		}

		@Override
		public OrderUpdateSocketEvent[] newArray(int size) {
			return new OrderUpdateSocketEvent[size];
		}
	};

	public OrderUpdateSocketEvent(Parcel parcel) {
		super(parcel);
	}

	public OrderUpdateSocketEvent(final Order order) {
		super(order);
	}

	@Override
	public String getType() {
		return EVENT_ORDER_UPDATE;
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
