package com.omnom.android.utils;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.auth.UserData;
import com.omnom.android.currency.Currency;
import com.omnom.android.currency.Money;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.order.PaymentData;

import java.math.BigDecimal;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by Ch3D on 02.12.2014.
 */
public class CroutonHelper {

	public static final BigDecimal DIVIDER = BigDecimal.valueOf(100);

	@Nullable
	public static Crouton createPaymentNotification(Activity activity, PaymentData data) {
		if(data == null || activity == null) {
			return null;
		}

		final View view = LayoutInflater.from(activity).inflate(com.omnom.android.R.layout.layout_balk_notification, null);
		final Crouton crouton = Crouton.make(activity, view).setConfiguration(getDefaultConfiguration(activity));

		final View.OnClickListener closeListener = new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				crouton.hide();
			}
		};

		String msg;

		final UserProfile userProfile = OmnomApplication.get(activity).getUserProfile();
		final UserData user = userProfile != null ? userProfile.getUser() : null;
		final boolean sameUser = user != null && data.getUser().getId() == user.getId();

		final Money tip = Money.createFractional(data.getTransaction().getTip(), Currency.RU);
		final Money amount = Money.createFractional(data.getTransaction().getAmount(), Currency.RU);
		final Money paid = amount.subtract(tip);

		if(sameUser && !tip.isNegativeOrZero() && paid.isNegativeOrZero()) {
			// current user paid only tip
			msg = activity.getString(com.omnom.android.R.string.balk_notification_user_tips_only,
			                         data.getUser().getName(),
			                         tip.getReadableValue());
		} else if(sameUser && !tip.isNegativeOrZero() && !paid.isNegativeOrZero()) {
			// current user paid some amount + tip
			msg = activity.getString(com.omnom.android.R.string.balk_notification_user_paid_tips,
			                         data.getUser().getName(),
			                         paid.getReadableValue(),
			                         tip.getReadableValue());

			// make text font smaller
			((TextView) view.findViewById(com.omnom.android.R.id.txt_message)).setTextSize(TypedValue.COMPLEX_UNIT_PX,
			                                                                               activity.getResources().getDimension(
					                                                                               R.dimen.font_small));
		} else {
			// another user paid
			msg = activity.getString(com.omnom.android.R.string.balk_notification_user_paid_,
			                         data.getUser().getName(),
			                         paid.getReadableValue());
		}

		((TextView) view.findViewById(com.omnom.android.R.id.txt_message)).setText(msg);
		view.findViewById(com.omnom.android.R.id.btn_close).setOnClickListener(closeListener);
		crouton.setOnClickListener(closeListener);
		return crouton;
	}

	private static Configuration getDefaultConfiguration(Context context) {
		if(sConfiguration == null) {
			final Configuration.Builder b = new Configuration.Builder();
			b.setDuration(context.getResources().getInteger(R.integer.balk_notification_duration));
			sConfiguration = b.build();
		}
		return sConfiguration;
	}

	public static Crouton showPaymentNotification(final Activity activity, final PaymentData paymentData) {
		final Crouton paymentNotification = createPaymentNotification(activity, paymentData);
		if(paymentNotification != null) {
			paymentNotification.show();
			MediaPlayer mediaPlayer = MediaPlayer.create(activity, com.omnom.android.R.raw.pay_done);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(final MediaPlayer mp) {
					mp.release();
				}
			});
			mediaPlayer.start();
		}
		return paymentNotification;
	}

	private static Configuration sConfiguration;

}