package com.omnom.android.socket.event;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OrderCloseSocketEvent extends BaseOrderSocketEvent {

	public OrderCloseSocketEvent(Order order) {
		super(order);
	}

}
