package com.omnom.android.socket.listener;

import com.omnom.android.socket.event.OrderCloseSocketEvent;
import com.omnom.android.socket.event.OrderUpdateSocketEvent;
import com.omnom.android.utils.activity.OmnomActivity;
import com.squareup.otto.Subscribe;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class OrderUpdateEventListener extends BaseEventListener {

    public interface OrderUpdateListener {
		void onOrderUpdateEvent(OrderUpdateSocketEvent event);
	}

	private OrderUpdateListener mListener;

	public OrderUpdateEventListener(final OmnomActivity activity,
                                    final OrderUpdateListener listener) {
		super(activity);
		mListener = listener;
	}

	@Subscribe
	public void onOrderUpdateEvent(final OrderUpdateSocketEvent event) {
		mActivity.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListener.onOrderUpdateEvent(event);
			}
		});
	}

}
