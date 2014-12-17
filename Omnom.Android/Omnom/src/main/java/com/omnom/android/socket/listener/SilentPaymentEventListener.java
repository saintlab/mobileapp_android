package com.omnom.android.socket.listener;

import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.activity.OmnomActivity;
import com.squareup.otto.Subscribe;

/**
 * Created by Ch3D on 02.12.2014.
 * <p/>
 * Similar to #PaymentEventListener but do not show Crouton automatically
 * instead just notifies #PaymentListener
 */
public class SilentPaymentEventListener extends PaymentEventListener {
	public interface PaymentListener {
		public void onPaymentEvent(final PaymentSocketEvent event);
	}

	private PaymentListener mListener;

	public SilentPaymentEventListener(final OmnomActivity activity, PaymentListener listener) {
		super(activity);
		mListener = listener;
	}

	@Subscribe
	public void onPaymentEvent(final PaymentSocketEvent event) {
		mActivity.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListener.onPaymentEvent(event);
			}
		});
	}
}
