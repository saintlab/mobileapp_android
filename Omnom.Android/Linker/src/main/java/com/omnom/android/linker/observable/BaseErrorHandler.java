package com.omnom.android.linker.observable;

import android.content.Context;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.activity.LoginActivity;
import com.omnom.util.utils.StringUtils;
import com.omnom.util.utils.UserDataHolder;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.functions.Action1;

/**
 * Created by Ch3D on 02.09.2014.
 */
public abstract class BaseErrorHandler implements Action1<Throwable> {

	private Context mContext;

	@Nullable
	protected UserDataHolder mDataHolder;

	public BaseErrorHandler(Context context) {
		mContext = context;
	}

	public BaseErrorHandler(Context context, UserDataHolder dataHolder) {
		mContext = context;
		mDataHolder = dataHolder;
	}

	@Override
	public final void call(Throwable throwable) {
		if(throwable instanceof RetrofitError) {
			final RetrofitError cause = (RetrofitError) throwable;
			final Response response = cause.getResponse();
			if(response != null && response.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
				LinkerApplication.get(mContext).getPreferences().setAuthToken(mContext, StringUtils.EMPTY_STRING);
				LoginActivity.start(mContext, mDataHolder);
				return;
			}
		}
		onThrowable(throwable);
	}

	protected abstract void onThrowable(Throwable throwable);

	public void setDataHolder(UserDataHolder userDataHolder) {
		mDataHolder = userDataHolder;
	}
}

