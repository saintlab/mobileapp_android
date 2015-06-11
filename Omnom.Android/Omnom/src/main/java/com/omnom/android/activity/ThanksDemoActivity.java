package com.omnom.android.activity;

import android.app.Activity;
import android.content.Intent;

import com.omnom.android.R;
import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.CroutonHelper;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.AmountHelper;

/**
 * Created by Ch3D on 04.12.2014.
 */
public class ThanksDemoActivity extends ThanksActivity {

	public static void start(final Activity activity,
	                         final Order order,
	                         final int code,
	                         final int color,
	                         final double amount,
	                         final int tips) {
		final Intent intent = new Intent(activity, ThanksDemoActivity.class);
		intent.putExtra(EXTRA_ACCENT_COLOR, color);
		intent.putExtra(EXTRA_ORDER, order);
		intent.putExtra(EXTRA_ORDER_AMOUNT, amount);
		intent.putExtra(EXTRA_ORDER_TIPS, tips);
		activity.startActivityForResult(intent, code);
	}

	private double mAmount;

	private int mTips;

	private boolean mFirstRun = true;

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mAmount = intent.getDoubleExtra(Extras.EXTRA_ORDER_AMOUNT, -1);
		mTips = intent.getIntExtra(Extras.EXTRA_ORDER_TIPS, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mFirstRun && mAmount != -1) {
			postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
				@Override
				public void run() {
					UserData user = getApp().getUserProfile().getUser();
					if(user == UserData.NULL) {
						user = UserData.createDemoUser(getString(R.string.demo_user_name));
					}
					final PaymentSocketEvent demoEvent = PaymentSocketEvent.createDemoEvent(user,
					                                                                        mAmount,
					                                                                        (int) AmountHelper.toDouble(mTips));
					CroutonHelper.showPaymentNotification(getActivity(), demoEvent.getPaymentData());
					mFirstRun = false;
				}
			});
		}
	}
}
