package com.omnom.android.interceptors.mixpanel;

import android.content.Context;

import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.acquiring.mailru.AcquiringProxyMailRu;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringTransactionExtendedResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringTransactionResponse;
import com.omnom.android.acquiring.mailru.response.CardDeleteResponse;
import com.omnom.android.acquiring.mailru.response.RegisterCardResponse;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

import static com.omnom.android.mixpanel.MixPanelHelper.Project.OMNOM_ANDROID;

/**
 * Created by Ch3D on 03.10.2014.
 */
public class AcquiringMailRuMixpanel extends AcquiringProxyMailRu {
	private MixPanelHelper mMixHelper;

	public AcquiringMailRuMixpanel(Context context, MixPanelHelper helper) {
		super(context);
		mMixHelper = helper;
	}

	@Override
	public Observable<AcquiringResponse> pay(HashMap<String, String> params) {
		mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.pay ->", params);
		return super.pay(params).doOnNext(new Action1<AcquiringResponse>() {
			@Override
			public void call(AcquiringResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.pay <-", response);
				mMixHelper.flush();
			}
		});
	}

	@Override
	public Observable<RegisterCardResponse> registerCard(Map<String, String> params) {
		mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.registerCard ->", params);
		return super.registerCard(params).doOnNext(new Action1<RegisterCardResponse>() {
			@Override
			public void call(RegisterCardResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.registerCard <-", response);
				mMixHelper.flush();
			}
		});
	}

	@Override
	public Observable<AcquiringResponse> verifyCard(HashMap<String, String> params) {
		mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.verifyCard ->", params);
		return super.verifyCard(params).doOnNext(new Action1<AcquiringResponse>() {
			@Override
			public void call(AcquiringResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.verifyCard <-", response);
				mMixHelper.flush();
			}
		});
	}

	@Override
	public Observable<CardDeleteResponse> deleteCard(HashMap<String, String> params) {
		mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.deleteCard ->", params);
		return super.deleteCard(params).doOnNext(new Action1<CardDeleteResponse>() {
			@Override
			public void call(CardDeleteResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.deleteCard <-", response);
				mMixHelper.flush();
			}
		});
	}

	@Override
	public Observable<AcquiringTransactionResponse> checkTransaction(HashMap<String, String> params) {
		mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.checkTransaction ->", params);
		return super.checkTransaction(params).doOnNext(new Action1<AcquiringTransactionResponse>() {
			@Override
			public void call(AcquiringTransactionResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.checkTransaction <-", response);
				mMixHelper.flush();
			}
		});
	}

	@Override
	public Observable<AcquiringTransactionExtendedResponse> checkTransactionExtended(HashMap<String, String> params) {
		mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.checkTransactionExtended ->", params);
		return super.checkTransactionExtended(params).doOnNext(new Action1<AcquiringTransactionExtendedResponse>() {
			@Override
			public void call(AcquiringTransactionExtendedResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "acquiring.mail.checkTransactionExtended <-", response);
				mMixHelper.flush();
			}
		});
	}
}
