package com.omnom.android.acquiring;

import com.omnom.android.acquiring.api.PaymentInfo;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MailRuExtra;
import com.omnom.android.acquiring.mailru.model.PaymentInfoMailRu;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class PaymentInfoFactory {
	public static PaymentInfo create(AcquiringType type, UserData user, CardInfo card, ExtraData extra, OrderInfo order) {
		switch(type) {
			case MAIL_RU:
				return PaymentInfoMailRu.create(user, card, (MailRuExtra) extra, order.getAmount(), order.getOrderId(),
				                                order.getOrderMsg());

			default:
				throw new RuntimeException("Not Implemented");

		}
	}
}
