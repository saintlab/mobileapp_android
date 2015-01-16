package com.omnom.android.socket.event;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OrderUpdateSocketEvent extends BaseOrderSocketEvent {

    public OrderUpdateSocketEvent(final Order order) {
        super(order);
    }

}
