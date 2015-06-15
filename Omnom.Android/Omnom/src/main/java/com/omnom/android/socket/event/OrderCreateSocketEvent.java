package com.omnom.android.socket.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OrderCreateSocketEvent extends BaseOrderSocketEvent {

	public static final Parcelable.Creator<OrderCreateSocketEvent> CREATOR = new Parcelable.Creator<OrderCreateSocketEvent>() {

		@Override
		public OrderCreateSocketEvent createFromParcel(Parcel in) {
			return new OrderCreateSocketEvent(in);
		}

		@Override
		public OrderCreateSocketEvent[] newArray(int size) {
			return new OrderCreateSocketEvent[size];
		}
	};

	public OrderCreateSocketEvent(Parcel parcel) {
		super(parcel);
	}

	public OrderCreateSocketEvent(final Order order) {
		super(order);
	}

	@Override
	public String getType() {
		return EVENT_ORDER_CREATE;
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
