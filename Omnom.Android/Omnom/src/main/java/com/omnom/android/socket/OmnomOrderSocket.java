package com.omnom.android.socket;

import android.content.Context;

import com.omnom.android.restaurateur.model.order.Order;

import java.net.URISyntaxException;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OmnomOrderSocket extends OmnomSocketBase {

	private final Order mOrder;

	protected  OmnomOrderSocket(final Context context, Order order, final String url) throws URISyntaxException {
		super(context, url);
		mOrder = order;
	}
}
