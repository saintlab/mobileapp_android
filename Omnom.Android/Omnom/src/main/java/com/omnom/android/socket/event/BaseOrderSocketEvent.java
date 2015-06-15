package com.omnom.android.socket.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 26.11.2014.
 */
public abstract class BaseOrderSocketEvent extends BaseSocketEvent implements Parcelable {

	private final Order mOrder;

	public BaseOrderSocketEvent(final Order order) {
		mOrder = order;
	}

	public BaseOrderSocketEvent(final Parcel parcel) {
		mOrder = parcel.readParcelable(Order.class.getClassLoader());
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeParcelable(mOrder, flags);
	}

	public Order getOrder() {
		return mOrder;
	}
}
