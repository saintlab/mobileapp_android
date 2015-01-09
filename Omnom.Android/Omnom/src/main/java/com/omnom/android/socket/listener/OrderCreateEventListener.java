package com.omnom.android.socket.listener;

import com.omnom.android.socket.event.OrderCloseSocketEvent;
import com.omnom.android.socket.event.OrderCreateSocketEvent;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.activity.OmnomActivity;
import com.squareup.otto.Subscribe;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class OrderCreateEventListener extends BaseEventListener {

    public interface OrderCreateListener {
		void onOrderCreateEvent(OrderCreateSocketEvent event);
	}

	private OrderCreateListener mListener;

	public OrderCreateEventListener(final OmnomActivity activity,
                                    final OrderCreateListener listener) {
		super(activity);
		mListener = listener;
	}

	@Subscribe
	public void onOrderCreateEvent(final OrderCreateSocketEvent event) {
		mActivity.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListener.onOrderCreateEvent(event);
			}
		});
	}

}
