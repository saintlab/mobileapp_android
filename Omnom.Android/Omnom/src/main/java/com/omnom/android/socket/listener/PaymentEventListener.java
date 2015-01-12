package com.omnom.android.socket.listener;

import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.CroutonHelper;
import com.omnom.android.utils.activity.OmnomActivity;
import com.squareup.otto.Subscribe;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by Ch3D on 02.12.2014.
 *
 * Listen to #PaymentSocketEvent and notifies a user with #Crouton
 */
public class PaymentEventListener extends BaseEventListener {

	public PaymentEventListener(final OmnomActivity activity) {
		super(activity);
	}

	@Subscribe
	public void onPaymentEvent(final PaymentSocketEvent event) {
		mActivity.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				CroutonHelper.showPaymentNotification(mActivity.getActivity(), event.getPaymentData());
			}
		});
	}

	public void onDestroy() {
        super.onDestroy();
		Crouton.cancelAllCroutons();
	}
}
