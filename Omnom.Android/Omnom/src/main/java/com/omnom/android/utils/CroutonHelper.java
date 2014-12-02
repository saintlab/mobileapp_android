package com.omnom.android.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.restaurateur.model.order.PaymentData;
import com.omnom.android.utils.utils.StringUtils;

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
		if(data == null) {
			return null;
		}

		final View view = LayoutInflater.from(activity).inflate(com.omnom.android.R.layout.layout_balk_notification, null);

		final String name = data.getUser().getName();
		final BigDecimal bd = BigDecimal.valueOf(data.getTransaction().getAmount());
		final BigDecimal subtract = bd.divide(DIVIDER);
		final String msg = activity.getString(com.omnom.android.R.string.balk_notification_user_paid_,
		                                      name,
		                                      StringUtils.formatCurrency(subtract));
		((TextView) view.findViewById(com.omnom.android.R.id.txt_message)).setText(msg);
		final Crouton crouton = Crouton.make(activity, view).setConfiguration(getDefaulConfiguration(activity));
		crouton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				crouton.hide();
			}
		});
		return crouton;
	}

	private static Configuration getDefaulConfiguration(Context context) {
		if(sConfiguration == null) {
			final Configuration.Builder b = new Configuration.Builder();
			b.setDuration(context.getResources().getInteger(R.integer.balk_notification_duration));
			sConfiguration = b.build();
		}
		return sConfiguration;
	}

	private static Configuration sConfiguration;
}
