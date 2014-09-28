package com.omnom.android.linker.observable;

import android.app.Activity;
import android.view.View;

import com.omnom.android.linker.activity.ErrorHelper;
import com.omnom.util.activity.OmnomActivity;
import com.omnom.android.linker.model.table.TableDataResponse;

import org.apache.http.HttpStatus;

import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Ch3D on 02.09.2014.
 */
public class OmnomObservable {
	public class ChainObservable<T> extends Observable<T> {
		protected ChainObservable(OnSubscribe<T> f) {
			super(f);
		}
	}

	public static void unsubscribe(Subscription subscription) {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}

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
