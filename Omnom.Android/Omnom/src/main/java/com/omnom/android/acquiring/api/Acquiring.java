package com.omnom.android.acquiring.api;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardDeleteResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.config.AcquiringData;

import rx.Observable;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface Acquiring {
	public Observable<AcquiringResponse> pay(AcquiringData acquiringData, PaymentInfo paymentInfo);

	public Observable<AcquiringPollingResponse> checkResult(final AcquiringResponse acquiringResponse);

	public Observable<CardDeleteResponse> deleteCard(AcquiringData acquiringData, UserData user, CardInfo cardInfo);

	public Observable<AcquiringResponse> verifyCard(AcquiringData acquiringData, UserData user, CardInfo cardInfo, double amount);

	public Observable<CardRegisterPollingResponse> registerCard(AcquiringData acquiringData, UserData user, final CardInfo cardInfo);
}

