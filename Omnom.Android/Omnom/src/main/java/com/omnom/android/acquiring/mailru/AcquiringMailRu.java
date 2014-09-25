package com.omnom.android.acquiring.mailru;

import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.BuildConfig;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.response.RegisterCardResponse;

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

	private final Gson gson;
	private final AcquiringServiceMailRu mAcquiringService;
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
	public void registerCard(RegisterCardRequest request) {
		HashMap<String, String> reqiredSignatureParams = new HashMap<String, String>();
		reqiredSignatureParams.put("merch_id", request.getMerch_id());
		reqiredSignatureParams.put("vterm_id", request.getVterm_id());
		reqiredSignatureParams.put("user_login", request.getUser_login());
		final String signature = getSignature(reqiredSignatureParams);
		request.setSignature(signature);

		final HashMap<String, String> parameters = reqiredSignatureParams;

		parameters.put("user_phone", request.getUser_phone());
		parameters.put("cardholder", mContext.getString(R.string.acquiring_mailru_cardholder));
		parameters.put("pan", request.getPan());
		parameters.put("cvv", request.getCvv());
		parameters.put("exp_date", request.getExp_date());
		parameters.put("signature", signature);

		mAcquiringService.registerCard(parameters)
				//		                 .map(new Func1<RegisterCardResponse, Pair<String, String>>() {
				//			                 @Override
				//			                 public Pair<String, String> call(RegisterCardResponse acquiringResponse) {
				//				                 return Pair.create(acquiringResponse.getCardId(), acquiringResponse.getUrl());
				//			                 }
				//		                 })
				.concatMap(new Func1<RegisterCardResponse, Observable<CardRegisterPollingResponse>>() {
					@Override
					public Observable<CardRegisterPollingResponse> call(RegisterCardResponse response) {
						return new PollingObservable(response);
					}
				})
				.subscribe(new Action1<CardRegisterPollingResponse>() {
					@Override
					public void call(CardRegisterPollingResponse response) {
						final String status = response.getStatus();
						final String cardId = response.getCardId();
						System.err.println("status = " + status + " cardId = " + cardId);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						Log.e(TAG, "registerCard", throwable);
					}
				});
	}

	private String getSignature(HashMap<String, String> reqiredSignatureParams) {return null;}
}
