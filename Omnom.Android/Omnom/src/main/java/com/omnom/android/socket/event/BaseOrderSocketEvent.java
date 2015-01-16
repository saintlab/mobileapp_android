package com.omnom.android.socket.event;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class BaseOrderSocketEvent extends BaseSocketEvent {

    private final Order mOrder;

    public BaseOrderSocketEvent(final Order order) {
        mOrder = order;
    }

    public Order getOrder() {
        return mOrder;
    }

}
