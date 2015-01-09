package com.omnom.android.socket.event;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OrderUpdateSocketEvent extends BaseSocketEvent {

    private final Order mOrder;

    public OrderUpdateSocketEvent(final Order order) {
        mOrder = order;
    }

    public Order getOrder() {
        return mOrder;
    }

}
