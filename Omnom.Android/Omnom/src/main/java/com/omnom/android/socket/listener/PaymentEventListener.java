package com.omnom.android.socket.listener;

import android.content.Context;
import android.content.Intent;

import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.Extras;
import com.squareup.otto.Subscribe;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by Ch3D on 02.12.2014.
 * <p/>
 * Listen to #PaymentSocketEvent and notifies a user with #Crouton
 */
public class PaymentEventListener extends BaseEventListener {

	public PaymentEventListener(final Context context) {
		super(context);
	}

	@Subscribe
	public void onPaymentEvent(final PaymentSocketEvent event) {
		final Intent intent = new Intent(Extras.ACTION_EVENT_PAYMENT);
		intent.putExtra(Extras.EXTRA_PAYMENT_EVENT, event);
		mActivity.sendOrderedBroadcast(intent, null);
		//		mActivity.getActivity().runOnUiThread(new Runnable() {
		//			@Override
		//			public void run() {
		//				CroutonHelper.showPaymentNotification(mActivity.getActivity(), event.getPaymentData());
		//			}
		//		});
	}

	public void onDestroy() {
		super.onDestroy();
		Crouton.cancelAllCroutons();
	}
}
