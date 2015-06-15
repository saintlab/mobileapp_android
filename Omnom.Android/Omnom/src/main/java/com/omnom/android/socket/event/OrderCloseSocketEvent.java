package com.omnom.android.socket.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OrderCloseSocketEvent extends BaseOrderSocketEvent {

	public static final Parcelable.Creator<OrderCloseSocketEvent> CREATOR = new Parcelable.Creator<OrderCloseSocketEvent>() {

		@Override
		public OrderCloseSocketEvent createFromParcel(Parcel in) {
			return new OrderCloseSocketEvent(in);
		}

		@Override
		public OrderCloseSocketEvent[] newArray(int size) {
			return new OrderCloseSocketEvent[size];
		}
	};

	public OrderCloseSocketEvent(Parcel parcel) {
		super(parcel);
	}

	public OrderCloseSocketEvent(Order order) {
		super(order);
	}

	@Override
	public String getType() {
		return EVENT_ORDER_CLOSE;
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
