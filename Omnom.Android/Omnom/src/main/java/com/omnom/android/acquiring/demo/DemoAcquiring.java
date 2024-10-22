package com.omnom.android.acquiring.demo;

import android.content.Context;

import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.api.PaymentInfo;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardDeleteResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.restaurateur.model.config.MerchantData;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Ch3D on 14.11.2014.
 */
public class DemoAcquiring implements Acquiring {

	public static final String HTTP_OMNOM_MENU = "http://omnom.menu";

	public static final String STATUS_SUCCESS = "success";

	public static final String CARD_ID = "4111";

	private Context mContext;

	public DemoAcquiring(final Context context) {
		mContext = context;
	}

	@Override
	public Observable<AcquiringResponse> pay(final AcquiringData acquiringData, final PaymentInfo paymentInfo) {
		return new Observable<AcquiringResponse>(new Observable.OnSubscribe<AcquiringResponse>() {
			@Override
			public void call(Subscriber<? super AcquiringResponse> subscriber) {
				final AcquiringResponse response = new AcquiringResponse();
				response.setUrl(HTTP_OMNOM_MENU);
				subscriber.onNext(response);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	public Observable<AcquiringResponse> checkResult(final AcquiringResponse acquiringResponse) {
		return new Observable<AcquiringResponse>(new Observable.OnSubscribe<AcquiringResponse>() {
			@Override
			public void call(Subscriber<? super AcquiringResponse> subscriber) {
				final AcquiringResponse resp = new AcquiringResponse();
				resp.setStatus(AcquiringPollingResponse.STATUS_OK);
				resp.setUrl(HTTP_OMNOM_MENU);
				subscriber.onNext(resp);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	public Observable<CardDeleteResponse> deleteCard(final AcquiringData acquiringData, final UserData user, final CardInfo cardInfo) {
		return new Observable<CardDeleteResponse>(new Observable.OnSubscribe<CardDeleteResponse>() {
			@Override
			public void call(Subscriber<? super CardDeleteResponse> subscriber) {
				CardDeleteResponse response = new CardDeleteResponse();
				response.setStatus(CardDeleteResponse.STATUS_SUCCESS);
				subscriber.onNext(response);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	public Observable<AcquiringResponse> verifyCard(final AcquiringData acquiringData, final UserData user, final CardInfo cardInfo, final
	double amount) {
		return new Observable<AcquiringResponse>(new Observable.OnSubscribe<AcquiringResponse>() {
			@Override
			public void call(Subscriber<? super AcquiringResponse> subscriber) {
				AcquiringResponse response = new AcquiringResponse();
				response.setUrl(HTTP_OMNOM_MENU);
				subscriber.onNext(response);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	public Observable<CardRegisterPollingResponse> registerCard(final AcquiringData acquiringData, final UserData user,
	                                                            final CardInfo cardInfo) {
		return new Observable<CardRegisterPollingResponse>(new Observable.OnSubscribe<CardRegisterPollingResponse>() {
			@Override
			public void call(Subscriber<? super CardRegisterPollingResponse> subscriber) {
				CardRegisterPollingResponse response = new CardRegisterPollingResponse();
				response.setUrl(HTTP_OMNOM_MENU);
				response.setStatus(STATUS_SUCCESS);
				response.setCardId(CARD_ID);
				subscriber.onNext(response);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	public Observable<AcquiringResponse> addCard(final AcquiringData acquiringData, final UserData user, final CardInfo cardInfo) {
		return null;
	}

	@Override
	public Observable<AcquiringResponse> refund(final AcquiringData acquiringData, final MerchantData merchantData, final String orderId) {
		return null;
	}
}
