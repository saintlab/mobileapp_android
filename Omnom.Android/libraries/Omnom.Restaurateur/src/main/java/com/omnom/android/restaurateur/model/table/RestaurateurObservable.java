package com.omnom.android.restaurateur.model.table;

import android.content.Context;

import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.utils.utils.StringUtils;

import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;

import java.util.Collections;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Ch3D on 06.10.2014.
 */
public class RestaurateurObservable {

	public static Func1<Throwable, TableDataResponse> getTableOnError() {
		return new Func1<Throwable, TableDataResponse>() {
			@Override
			public TableDataResponse call(Throwable throwable) {
				if(throwable instanceof RetrofitError) {
					RetrofitError error = (RetrofitError) throwable;
					Response response = error.getResponse();
					if(response != null && response.getStatus() == HttpStatus.SC_NOT_FOUND) {
						return TableDataResponse.NULL;
					}
				}
				return null;
			}
		};
	}

	public static abstract class AuthAwareOnNext<T extends ResponseBase> implements Action1<T> {
		private Context mContext;

		public AuthAwareOnNext(final Context context) {
			mContext = context;
		}

		@Override
		public void call(T t) {
			if(t.hasAuthError()) {
				final String url = StringUtils.EMPTY_STRING;
				final String authError = t.getErrors().getAuthentication();
				final Response response = new Response(url,
				                                       HttpStatus.SC_UNAUTHORIZED,
				                                       authError,
				                                       Collections.EMPTY_LIST,
				                                       new TypedString(authError));
				final RetrofitError retrofitError = RetrofitError.httpError(url, response, null,
				                                                            AuthenticationException.class);
				throw retrofitError;
			} else {
				perform(t);
			}
		}

		public abstract void perform(T t);
	}
}
