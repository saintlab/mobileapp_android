package com.omnom.android.acquiring.mailru;

import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.BuildConfig;
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

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class AcquiringMailRu implements Acquiring {
	private static final String TAG = AcquiringMailRu.class.getSimpleName();

	private final AcquiringServiceMailRu mAcquiringService;
	private final Gson gson;
	private Context mContext;

	public AcquiringMailRu(final Context context) {
		mContext = context;

		// TODO: this code must be the same across all the rest services -> refactore it!
		final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		RestAdapter mRestAdapter = new RestAdapter.Builder().setEndpoint(context.getString(R.string.acquiring_mailru_acquiring_base_url))
		                                                    .setLogLevel(logLevel).setConverter(new GsonConverter(gson)).build();
		mAcquiringService = mRestAdapter.create(AcquiringServiceMailRu.class);
	}

	@Override
	public void pay(MerchantData merchant, PaymentInfo paymentInfo, final PaymentListener listener) {
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
		info.getCardInfo().storeCardId(parameters);
		parameters.put("cardholder", mContext.getString(R.string.acquiring_mailru_cardholder));
		info.getUser().storePhone(parameters);

		mAcquiringService.pay(parameters)
		                 .concatMap(new Func1<AcquiringResponse, Observable<AcquiringPollingResponse>>() {
			                 @Override
			                 public Observable<AcquiringPollingResponse> call(AcquiringResponse response) {
				                 if(response.getError() == null) {
					                 return PollingObservable.create(response);
				                 } else {
					                 return Observable.error(new RuntimeException(response.getError().toString()));
				                 }
			                 }
		                 })
		                 .subscribe(new Action1<AcquiringPollingResponse>() {
			                 @Override
			                 public void call(AcquiringPollingResponse acquiringPollingResponse) {
				                 listener.onPaymentSettled(acquiringPollingResponse);
			                 }
		                 }, new Action1<Throwable>() {
			                 @Override
			                 public void call(Throwable throwable) {
				                 Log.e(TAG, "pay", throwable);
			                 }
		                 });
	}

	@Override
	public void deleteCard(MerchantData merchant, UserData user, CardInfo cardInfo, final CardDeleteListener listener) {
		final HashMap<String, String> signatureParams = new HashMap<String, String>();
		merchant.toMap(signatureParams);
		user.storeLogin(signatureParams);
		cardInfo.storeCardId(signatureParams);
		final String signature = EncryptionUtils.getSignature(mContext.getString(R.string.acquiring_mailru_secret_key), signatureParams);

		final HashMap<String, String> parameters = signatureParams;
		parameters.put("signature", signature);

		mAcquiringService.deleteCard(parameters)
		                 .subscribe(new Action1<AcquiringResponse>() {
			                 @Override
			                 public void call(AcquiringResponse pollingResponse) {
				                 listener.onCardDeleted(pollingResponse);
			                 }
		                 }, new Action1<Throwable>() {
			                 @Override
			                 public void call(Throwable throwable) {
				                 Log.e(TAG, "deleteCard", throwable);
			                 }
		                 });
	}

	@Override
	public void verifyCard(MerchantData merchant, UserData user, CardInfo cardInfo, double amount, final CardVerifyListener listener) {
		final HashMap<String, String> signatureParams = new HashMap<String, String>();
		merchant.toMap(signatureParams);
		user.storeLogin(signatureParams);
		cardInfo.storeCardId(signatureParams);

		final String signature = EncryptionUtils.getSignature(mContext.getString(R.string.acquiring_mailru_secret_key), signatureParams);

		final HashMap<String, String> parameters = signatureParams;
		parameters.put("signature", signature);
		parameters.put("amount", Double.toString(amount));

		mAcquiringService.verifyCard(parameters)
		                 .subscribe(new Action1<AcquiringResponse>() {
			                 @Override
			                 public void call(AcquiringResponse response) {
				                 listener.onCardVerified(response);
			                 }
		                 }, new Action1<Throwable>() {
			                 @Override
			                 public void call(Throwable throwable) {
				                 Log.e(TAG, "verifyCard", throwable);
			                 }
		                 });
	}

	@Override
	public void registerCard(final MerchantData merchant, UserData user, final CardInfo cardInfo, final CardRegisterListener listener) {
		HashMap<String, String> reqiredSignatureParams = new HashMap<String, String>();
		merchant.toMap(reqiredSignatureParams);
		user.storeLogin(reqiredSignatureParams);
		final String signature = EncryptionUtils.getSignature(mContext.getString(R.string.acquiring_mailru_secret_key),
		                                                      reqiredSignatureParams);
		final HashMap<String, String> parameters = reqiredSignatureParams;
		user.storePhone(parameters);
		cardInfo.toMap(parameters);
		parameters.put("signature", signature);

		mAcquiringService.registerCard(parameters)
		                 .concatMap(new Func1<RegisterCardResponse, Observable<CardRegisterPollingResponse>>() {
			                 @Override
			                 public Observable<CardRegisterPollingResponse> call(RegisterCardResponse response) {
				                 if(response.getError() == null) {
					                 return PollingObservable.create(response);
				                 } else {
					                 return Observable.error(new RuntimeException(response.getError().toString()));
				                 }
			                 }
		                 })
		                 .subscribe(new Action1<CardRegisterPollingResponse>() {
			                 @Override
			                 public void call(CardRegisterPollingResponse response) {
				                 listener.onCardRegistered(response);
			                 }
		                 }, new Action1<Throwable>() {
			                 @Override
			                 public void call(Throwable throwable) {
				                 Log.e(TAG, "registerCard", throwable);
			                 }
		                 });
	}
}