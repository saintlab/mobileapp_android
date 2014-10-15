package com.omnom.android.acquiring.api;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface Acquiring {
	public interface CardRegisterListener {
		public void onCardRegistered(final CardRegisterPollingResponse response);
	}

	public interface CardVerifyListener {
		public void onCardVerified(final AcquiringResponse response);
		public void onError(Throwable throwable);
	}

	public interface CardDeleteListener {
		public void onCardDeleted(final AcquiringResponse response);
	}

	public interface PaymentListener {
		public void onPaymentSettled(final AcquiringPollingResponse response);
		public void onError(Throwable throwable);
	}

	public void pay(final MerchantData merchant, PaymentInfo paymentInfo, PaymentListener listener);

	public void deleteCard(MerchantData merchant, UserData user, CardInfo cardInfo, CardDeleteListener listener);

	public void verifyCard(MerchantData merchant, UserData user, CardInfo cardInfo, double amount,
	                       CardVerifyListener listener);

	public void registerCard(final MerchantData merchant, UserData user, final CardInfo cardInfo,
	                         CardRegisterListener listener);
}

