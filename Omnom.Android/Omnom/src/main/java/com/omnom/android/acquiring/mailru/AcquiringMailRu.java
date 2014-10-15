package com.omnom.android.acquiring.mailru;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.api.PaymentInfo;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.PaymentInfoMailRu;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.acquiring.mailru.response.RegisterCardResponse;
import com.omnom.android.utils.EncryptionUtils;

import java.util.HashMap;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class AcquiringMailRu implements Acquiring {
	private static final String TAG = AcquiringMailRu.class.getSimpleName();
	private final Gson gson;
	protected AcquiringServiceMailRu mApiProxy;
	private Context mContext;

	public AcquiringMailRu(final Context context, AcquiringProxyMailRu acquiringProxyMailRu) {
		mContext = context;
		mApiProxy = acquiringProxyMailRu;
		gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	@Override
	public Observable<AcquiringPollingResponse> pay(MerchantData merchant, PaymentInfo paymentInfo) {
		final PaymentInfoMailRu info = (PaymentInfoMailRu) paymentInfo;
		if(info == null) {
			throw new RuntimeException("PaymentInfo is null or not a PaymentInfoMailRu");
		}
		final HashMap<String, String> signatureParams = new HashMap<String, String>();
		merchant.toMap(signatureParams);
		info.getUser().storeLogin(signatureParams);
		signatureParams.put("order_id", info.getOrderId());
		signatureParams.put("order_amount", Double.toString(info.getOrderAmount()));
		signatureParams.put("order_message", info.getOrderMessage());
		signatureParams.put("extra", info.getExtra().getExtra(gson));
		final String signature = EncryptionUtils.getSignature(mContext.getString(R.string.acquiring_mailru_secret_key), signatureParams);

		final HashMap<String, String> parameters = signatureParams;
		parameters.put("signature", signature);
		parameters.put("cardholder", mContext.getString(R.string.acquiring_mailru_cardholder));
		parameters.putAll(info.getCardInfo().getCardInfoMap());
		info.getUser().storePhone(parameters);

		return mApiProxy.pay(parameters)
		         .concatMap(new Func1<AcquiringResponse, Observable<AcquiringPollingResponse>>() {
			         @Override
			         public Observable<AcquiringPollingResponse> call(AcquiringResponse response) {
				         if(response.getError() == null) {
					         return PollingObservable.create(response);
				         } else {
					         return Observable.error(new RuntimeException(response.getError().toString()));
				         }
			         }
		         });
	}

	@Override
	public Observable<AcquiringResponse> deleteCard(MerchantData merchant, UserData user, CardInfo cardInfo) {
		final HashMap<String, String> signatureParams = new HashMap<String, String>();
		merchant.toMap(signatureParams);
		user.storeLogin(signatureParams);
		cardInfo.storeCardId(signatureParams);
		final String signature = EncryptionUtils.getSignature(mContext.getString(R.string.acquiring_mailru_secret_key), signatureParams);

		final HashMap<String, String> parameters = signatureParams;
		parameters.put("signature", signature);

		return mApiProxy.deleteCard(parameters);
	}

	@Override
	public Observable<AcquiringResponse> verifyCard(MerchantData merchant, UserData user, CardInfo cardInfo, double amount) {
		final HashMap<String, String> signatureParams = new HashMap<String, String>();
		merchant.toMap(signatureParams);
		user.storeLogin(signatureParams);
		cardInfo.storeCardId(signatureParams);

		final String signature = EncryptionUtils.getSignature(mContext.getString(R.string.acquiring_mailru_secret_key), signatureParams);

		final HashMap<String, String> parameters = signatureParams;
		parameters.put("signature", signature);
		parameters.put("amount", Double.toString(amount));

		return mApiProxy.verifyCard(parameters);
	}

	@Override
	public Observable<CardRegisterPollingResponse> registerCard(final MerchantData merchant, UserData user, final CardInfo cardInfo) {
		HashMap<String, String> reqiredSignatureParams = new HashMap<String, String>();
		merchant.toMap(reqiredSignatureParams);
		user.storeLogin(reqiredSignatureParams);
		final String signature = EncryptionUtils.getSignature(mContext.getString(R.string.acquiring_mailru_secret_key),
		                                                      reqiredSignatureParams);
		final HashMap<String, String> parameters = reqiredSignatureParams;
		user.storePhone(parameters);
		cardInfo.toMap(parameters);
		parameters.put("signature", signature);

		return mApiProxy.registerCard(parameters)
		                .concatMap(new Func1<RegisterCardResponse, Observable<CardRegisterPollingResponse>>() {
			                @Override
			                public Observable<CardRegisterPollingResponse> call(RegisterCardResponse response) {
				                if(response.getError() == null) {
					                return PollingObservable.create(response);
				                } else {
					                return Observable.error(new RuntimeException(response.getError().toString()));
				                }
			                }
		                });
	}
}
