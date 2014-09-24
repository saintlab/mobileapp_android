package com.omnom.android.acquiring.api;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface Acquiring {

	public interface CardRegisterListener {
		public void onCardRegistered(final String status, final String cardId);
	}

	public interface CardVerifyListener {
		public void onCardVerified(final AcquiringResponse response);
	}

	public interface CardDeleteListener {
		public void onCardDeleted(final AcquiringResponse response);
	}

	public interface PaymentListener {
		public void onPaymentSettled(final AcquiringPollingResponse response);
	}

	public void registerCard(final MerchantData merchant, UserData user, final CardInfo cardInfo, CardRegisterListener listener);
}

