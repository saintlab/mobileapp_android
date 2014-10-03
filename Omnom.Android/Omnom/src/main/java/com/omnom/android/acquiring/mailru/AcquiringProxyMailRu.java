package com.omnom.android.acquiring.mailru;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.BuildConfig;
import com.omnom.android.R;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringTransactionExtendedResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringTransactionResponse;
import com.omnom.android.acquiring.mailru.response.RegisterCardResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

/**
 * Created by Ch3D on 03.10.2014.
 */
public class AcquiringProxyMailRu implements AcquiringServiceMailRu {

	private final Context mContext;
	private final Gson gson;
	private final AcquiringServiceMailRu mAcquiringService;

	public AcquiringProxyMailRu(Context context) {
		mContext = context;

		// TODO: this code must be the same across all the rest services -> refactore it!
		final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		RestAdapter mRestAdapter = new RestAdapter.Builder().setEndpoint(context.getString(R.string.acquiring_mailru_acquiring_base_url))
		                                                    .setLogLevel(logLevel).setConverter(new GsonConverter(gson)).build();
		mAcquiringService = mRestAdapter.create(AcquiringServiceMailRu.class);
	}

	@Override
	public Observable<AcquiringResponse> pay(HashMap<String, String> params) {
		return mAcquiringService.pay(params);
	}

	@Override
	public Observable<RegisterCardResponse> registerCard(Map<String, String> params) {
		return mAcquiringService.registerCard(params);
	}

	@Override
	public Observable<AcquiringResponse> verifyCard(HashMap<String, String> params) {
		return mAcquiringService.verifyCard(params);
	}

	@Override
	public Observable<AcquiringResponse> deleteCard(HashMap<String, String> params) {
		return mAcquiringService.deleteCard(params);
	}

	@Override
	public Observable<AcquiringTransactionResponse> checkTransaction(HashMap<String, String> params) {
		return mAcquiringService.checkTransaction(params);
	}

	@Override
	public Observable<AcquiringTransactionExtendedResponse> checkTransactionExtended(HashMap<String, String> params) {
		return mAcquiringService.checkTransactionExtended(params);
	}
}