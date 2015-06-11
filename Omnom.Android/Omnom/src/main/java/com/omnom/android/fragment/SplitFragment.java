package com.omnom.android.fragment;

import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 11.11.2014.
 */
public interface SplitFragment {
	void updateAmount();

	void onOrderUpdate(final Order order);
}
