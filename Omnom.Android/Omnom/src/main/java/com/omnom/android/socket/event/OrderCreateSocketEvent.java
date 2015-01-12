package com.omnom.android.socket.event;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OrderCreateSocketEvent extends OrderUpdateSocketEvent {

    public OrderCreateSocketEvent(final Order order) {
        super(order);
    }

}
