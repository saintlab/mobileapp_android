package com.omnom.android.utils.observable;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.omnom.android.utils.utils.UserDataHolder;

import org.apache.http.HttpStatus;

import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.functions.Action1;

/**
 * Created by Ch3D on 02.09.2014.
 */
public abstract class BaseErrorHandler implements Action1<Throwable> {

	protected Activity mActivity;

	@Nullable
	protected UserDataHolder mDataHolder;

	public BaseErrorHandler(Activity activity) {
		mActivity = activity;
	}

	public BaseErrorHandler(Activity activity, UserDataHolder dataHolder) {
		mActivity = activity;
		mDataHolder = dataHolder;
	}

	@Override
	public final void call(Throwable throwable) {
		if(throwable instanceof RetrofitError) {
			final RetrofitError cause = (RetrofitError) throwable;
			final Response response = cause.getResponse();
			if(response != null && response.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
				onTokenExpired();
				return;
			}
		}
		onThrowable(throwable);
	}

	protected abstract void onTokenExpired();

	protected abstract void onThrowable(Throwable throwable);

	public void setDataHolder(UserDataHolder userDataHolder) {
		mDataHolder = userDataHolder;
	}
}

