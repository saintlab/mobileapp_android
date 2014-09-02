package com.omnom.android.linker.observable;

import android.content.Context;
import android.content.Intent;

import com.omnom.android.linker.activity.LoginActivity;
import com.omnom.android.linker.activity.base.Extras;
import com.omnom.android.linker.utils.UserDataHolder;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import retrofit.RetrofitError;
import rx.functions.Action1;

/**
 * Created by Ch3D on 02.09.2014.
 */
public abstract class BaseErrorHandler implements Action1<Throwable> {

	private Context mContext;
	@Nullable
	private UserDataHolder mDataHolder;

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
			RetrofitError cause = (RetrofitError) throwable;
			final int status = cause.getResponse().getStatus();
			if(status == HttpStatus.SC_UNAUTHORIZED) {
				Intent intent = new Intent(mContext, LoginActivity.class);
				if(mDataHolder != null) {
					intent.putExtra(Extras.EXTRA_USERNAME, mDataHolder.getUsername());
					intent.putExtra(Extras.EXTRA_PASSWORD, mDataHolder.getPassword());
				}
				intent.putExtra(Extras.EXTRA_ERROR_CODE, Extras.EXTRA_ERROR_AUTHTOKEN_EXPIRED);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				return;
			}
		}
		onThrowable(throwable);
	}

	protected abstract void onThrowable(Throwable throwable);
}

