package com.omnom.android.socket;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.CroutonHelper;
import com.omnom.android.utils.Extras;

/**
 * Created by Ch3D on 22.05.2015.
 */
public class ActivityPaymentBroadcastReceiver extends BroadcastReceiver {

	private final Activity mActivity;

	public ActivityPaymentBroadcastReceiver(Activity activity) {
		mActivity = activity;
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		final PaymentSocketEvent paymentSocketEvent = intent.getParcelableExtra(Extras.EXTRA_PAYMENT_EVENT);
		CroutonHelper.showPaymentNotification(mActivity, paymentSocketEvent.getPaymentData());
		abortBroadcast();
	}
}
