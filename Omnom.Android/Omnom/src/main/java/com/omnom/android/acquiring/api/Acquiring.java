package com.omnom.android.acquiring.api;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface Acquiring {
	public interface CardRegisterListener<T> {
		public void onCardRegistered(final T response);
	}

	public interface CardVerifyListener<T> {
		public void onCardVerified(final T response);
	}

	public interface CardDeleteListener<T> {
		public void onCardDeleted(final T response);
	}

	public interface PaymentListener<T> {
		public void onPaymentSettled(final T response);
		public void onError(Throwable throwable);
	}

	public void pay(final MerchantData merchant, PaymentInfo paymentInfo, PaymentListener listener);

	public void deleteCard(MerchantData merchant, UserData user, CardInfo cardInfo, CardDeleteListener listener);

	public void verifyCard(MerchantData merchant, UserData user, CardInfo cardInfo, double amount, CardVerifyListener listener);

	public void registerCard(final MerchantData merchant, UserData user, final CardInfo cardInfo, CardRegisterListener listener);
}

