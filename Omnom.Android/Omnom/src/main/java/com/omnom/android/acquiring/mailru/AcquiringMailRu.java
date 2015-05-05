package com.omnom.android.acquiring.mailru;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.acquiring.AcquiringResponseException;
import com.omnom.android.acquiring.AcquiringType;
import com.omnom.android.acquiring.ExtraData;
import com.omnom.android.acquiring.PaymentInfoFactory;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.api.PaymentInfo;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MailRuExtra;
import com.omnom.android.acquiring.mailru.model.PaymentInfoMailRu;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.acquiring.mailru.response.RegisterCardResponse;
import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.restaurateur.model.config.MerchantData;
import com.omnom.android.utils.EncryptionUtils;
import com.omnom.android.utils.utils.StringUtils;

import java.util.HashMap;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class AcquiringMailRu implements Acquiring {

	public static final int DEFAULT_RETRY_ATTEMPS = 5;

	public static final String PARAM_SIGNATURE = "signature";

	public static final String PARAM_ORDER_ID = "order_id";

	public static final String PARAM_ORDER_AMOUNT = "order_amount";

	public static final String PARAM_ORDER_MESSAGE = "order_message";

	public static final String PARAM_EXTRA = "extra";

	public static final String PARAM_CARDHOLDER = "cardholder";

	public static final String PARAM_AMOUNT = "amount";

	public static final double AMOUNT_ADD_CARD = 1.0;

	private static final String TAG = AcquiringMailRu.class.getSimpleName();

	private final Gson gson;

	protected AcquiringProxyMailRu mApiProxy;

	private Context mContext;

	public AcquiringMailRu(AcquiringProxyMailRu acquiringProxyMailRu) {
		mApiProxy = acquiringProxyMailRu;
		gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	public void changeEndpoint(final String url) {
		if(mApiProxy != null) {
			mApiProxy.changeEndpoint(url);
		}
	}

	@Override
	public Observable<AcquiringResponse> pay(final AcquiringData acquiringData, final PaymentInfo paymentInfo) {
		final PaymentInfoMailRu info = (PaymentInfoMailRu) paymentInfo;
		if(info == null) {
			throw new RuntimeException("PaymentInfo is null or not a PaymentInfoMailRu");
		}
		final HashMap<String, String> signatureParams = new HashMap<String, String>();
		acquiringData.getMerchantData().toMap(signatureParams);
		info.getUser().storeLogin(signatureParams);
		signatureParams.put(PARAM_ORDER_ID, info.getOrderId());
		signatureParams.put(PARAM_ORDER_AMOUNT, Double.toString(info.getOrderAmount()));
		signatureParams.put(PARAM_ORDER_MESSAGE, info.getOrderMessage());
		signatureParams.put(PARAM_EXTRA, info.getExtra().getExtra(gson));
		final String signature = EncryptionUtils.getSignature(acquiringData.getSecretKey(), signatureParams);

		final HashMap<String, String> parameters = signatureParams;
		parameters.put(PARAM_SIGNATURE, signature);
		parameters.put(PARAM_CARDHOLDER, acquiringData.getCardHolder());
		parameters.putAll(info.getCardInfo().getCardInfoMap());
		info.getUser().storePhone(parameters);

		return mApiProxy.pay(parameters);
	}

	@Override
	public Observable<AcquiringResponse> checkResult(final AcquiringResponse acquiringResponse) {
		return PollingObservable.create(acquiringResponse).retry(new Func2<Integer, Throwable, Boolean>() {
			@Override
			public Boolean call(final Integer integer, final Throwable throwable) {
				return integer - 1 < DEFAULT_RETRY_ATTEMPS;
			}
		});
	}

	@Override
	public Observable<com.omnom.android.acquiring.mailru.response.CardDeleteResponse> deleteCard(AcquiringData acquiringData,
	                                                                                             UserData user, CardInfo cardInfo) {
		final HashMap<String, String> signatureParams = new HashMap<String, String>();
		acquiringData.getMerchantData().toMap(signatureParams);
		user.storeLogin(signatureParams);
		cardInfo.storeCardId(signatureParams);
		final String signature = EncryptionUtils.getSignature(acquiringData.getSecretKey(), signatureParams);

		final HashMap<String, String> parameters = signatureParams;
		parameters.put(PARAM_SIGNATURE, signature);

		return mApiProxy.deleteCard(parameters);
	}

	@Override
	public Observable<AcquiringResponse> verifyCard(AcquiringData acquiringData, UserData user, CardInfo cardInfo, double amount) {
		final HashMap<String, String> signatureParams = new HashMap<String, String>();
		acquiringData.getMerchantData().toMap(signatureParams);
		user.storeLogin(signatureParams);
		cardInfo.storeCardId(signatureParams);

		final String signature = EncryptionUtils.getSignature(acquiringData.getSecretKey(), signatureParams);

		final HashMap<String, String> parameters = signatureParams;
		parameters.put(PARAM_SIGNATURE, signature);
		parameters.put(PARAM_AMOUNT, Double.toString(amount));

		return mApiProxy.verifyCard(parameters);
	}

	@Override
	public Observable<CardRegisterPollingResponse> registerCard(AcquiringData acquiringData, UserData user, final CardInfo cardInfo) {
		HashMap<String, String> reqiredSignatureParams = new HashMap<String, String>();
		acquiringData.getMerchantData().toMap(reqiredSignatureParams);
		user.storeLogin(reqiredSignatureParams);
		final String signature = EncryptionUtils.getSignature(acquiringData.getSecretKey(),
		                                                      reqiredSignatureParams);
		final HashMap<String, String> parameters = reqiredSignatureParams;
		user.storePhone(parameters);
		cardInfo.toMap(parameters);
		parameters.put(PARAM_SIGNATURE, signature);

		return mApiProxy.registerCard(parameters)
		                .concatMap(new Func1<RegisterCardResponse, Observable<CardRegisterPollingResponse>>() {
			                @Override
			                public Observable<CardRegisterPollingResponse> call(RegisterCardResponse response) {
				                if(response.getError() == null) {
					                return PollingObservable.create(response);
				                } else {
					                return Observable.error(new AcquiringResponseException(response.getError()));
				                }
			                }
		                });
	}

	@Override
	public Observable<AcquiringResponse> addCard(final AcquiringData acquiringData, final UserData user, final CardInfo cardInfo) {
		final ExtraData extra = MailRuExtra.create(0, StringUtils.EMPTY_STRING, MailRuExtra.PAYMENT_TYPE_ORDER);
		final OrderInfoMailRu order = OrderInfoMailRu.create(AMOUNT_ADD_CARD, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING);
		final PaymentInfo paymentInfo = PaymentInfoFactory.create(AcquiringType.MAIL_RU, user, cardInfo, extra, order);

		return pay(acquiringData, paymentInfo)
				.flatMap(new Func1<AcquiringResponse, Observable<AcquiringResponse>>() {
					@Override
					public Observable<AcquiringResponse> call(final AcquiringResponse acquiringResponse) {
						return checkResult(acquiringResponse);
					}
				}).flatMap(new Func1<AcquiringPollingResponse, Observable<AcquiringResponse>>() {
					@Override
					public Observable<AcquiringResponse> call(final AcquiringPollingResponse pollingResponse) {
						if(AcquiringPollingResponse.STATUS_OK.equals(pollingResponse.getStatus())) {
							final String orderId = pollingResponse.getOrderId();
							if(!TextUtils.isEmpty(orderId)) {
								return refund(acquiringData, acquiringData.getMerchantData(), orderId);
							}
						}
						return Observable.just(new AcquiringResponse());
					}
				});
	}

	@Override
	public Observable<AcquiringResponse> refund(AcquiringData acquiringData, final MerchantData merchantData, final String orderId) {
		HashMap<String, String> reqiredSignatureParams = new HashMap<String, String>();
		merchantData.toMap(reqiredSignatureParams);
		reqiredSignatureParams.put(PARAM_ORDER_ID, orderId);
		final String signature = EncryptionUtils.getSignature(acquiringData.getSecretKey(),
		                                                      reqiredSignatureParams);
		final HashMap<String, String> parameters = reqiredSignatureParams;
		parameters.put(PARAM_SIGNATURE, signature);

		return mApiProxy.refund(parameters);
	}
}
