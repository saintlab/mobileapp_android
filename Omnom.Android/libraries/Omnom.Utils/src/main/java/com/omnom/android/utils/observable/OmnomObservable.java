package com.omnom.android.utils.observable;

import android.app.Activity;
import android.view.View;

import com.omnom.android.utils.ErrorHelper;
import com.omnom.android.utils.activity.OmnomActivity;

import java.util.Collections;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Ch3D on 02.09.2014.
 */
public class OmnomObservable {
	public static void unsubscribe(Subscription subscription) {
		if(isSubscribed(subscription)) {
			subscription.unsubscribe();
		}
	}

	public static boolean isSubscribed(Subscription subscription) {
		return subscription != null && !subscription.isUnsubscribed();
	}

	public static RetrofitError createRetrofitError(final String url, final String errorText, final int statusCode) {
		final Response response = new Response(url, statusCode, errorText, Collections.EMPTY_LIST, new TypedString(errorText));
		return RetrofitError.httpError(url, response, null, RetrofitError.class);
	}

	public static Func1<ValidationObservable.Error, Boolean> getValidationFunc(final Activity activity,
	                                                                           final ErrorHelper errorHelper,
	                                                                           final View.OnClickListener clickListener) {
		return new Func1<ValidationObservable.Error, Boolean>() {
			@Override
			public Boolean call(ValidationObservable.Error error) {
				switch(error) {
					case BLUETOOTH_DISABLED:
						errorHelper.showErrorBluetoothDisabled(activity, OmnomActivity.REQUEST_CODE_ENABLE_BLUETOOTH);
						break;

					case NO_CONNECTION:
						errorHelper.showInternetError(clickListener);
						break;

					case LOCATION_DISABLED:
						errorHelper.showLocationError();
						break;
				}
				return false;
			}
		};
	}
}
