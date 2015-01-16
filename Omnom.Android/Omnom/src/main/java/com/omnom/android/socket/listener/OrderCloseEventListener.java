package com.omnom.android.socket.listener;

import com.omnom.android.socket.event.OrderCloseSocketEvent;
import com.omnom.android.utils.activity.OmnomActivity;
import com.squareup.otto.Subscribe;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class OrderCloseEventListener extends BaseEventListener {

    public interface OrderCloseListener {
		void onOrderCloseEvent(OrderCloseSocketEvent event);
	}

	private OrderCloseListener mListener;

	public OrderCloseEventListener(final OmnomActivity activity,
	                               final OrderCloseListener listener) {
		super(activity);
		mListener = listener;
	}

	@Subscribe
	public void onOrderCloseEvent(final OrderCloseSocketEvent event) {
		mActivity.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListener.onOrderCloseEvent(event);
			}
		});
	}

}
