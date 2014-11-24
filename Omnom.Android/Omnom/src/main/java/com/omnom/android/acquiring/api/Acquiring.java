package com.omnom.android.acquiring.api;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;

import rx.Observable;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface Acquiring {
	public Observable<AcquiringResponse> pay(final MerchantData merchant, PaymentInfo paymentInfo);

	public Observable<AcquiringPollingResponse> checkResult(final AcquiringResponse acquiringResponse);

	public Observable<AcquiringResponse> deleteCard(MerchantData merchant, UserData user, CardInfo cardInfo);

	public Observable<AcquiringResponse> verifyCard(MerchantData merchant, UserData user, CardInfo cardInfo, double amount);

	public Observable<CardRegisterPollingResponse> registerCard(final MerchantData merchant, UserData user, final CardInfo cardInfo);
}

