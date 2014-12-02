package com.omnom.android.socket;

import android.content.Context;
import android.net.Uri;

import com.omnom.android.OmnomApplication;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.utils.loader.LoaderView;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OmnomOrderSocket extends OmnomSocketBase {

	private final Order mOrder;

	protected OmnomOrderSocket(final Context context, Order order) {
		super(context);
		mOrder = order;
	}

	@Override
	protected String getRoomId() {
		return mOrder.getId();
	}
}
