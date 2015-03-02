package com.omnom.android.notifier.api.observable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.notifier.api.NotifierDataService;
import com.omnom.android.notifier.model.RegisterRequest;
import com.omnom.android.utils.generation.AutoParcelAdapterFactory;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 28.02.2015.
 */
public class NotifierDataProvider implements NotifierObservableApi {

	public static NotifierDataProvider create(final String dataEndPoint, final RequestInterceptor interceptor) {
		// final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;
		final Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.registerTypeAdapterFactory(new AutoParcelAdapterFactory())
				.create();
		final GsonConverter converter = new GsonConverter(gson);

		final RestAdapter mRestAdapter = new RestAdapter.Builder().setRequestInterceptor(interceptor).setEndpoint(dataEndPoint)
		                                                          .setLogLevel(logLevel)
		                                                          .setConverter(converter).build();
		return new NotifierDataProvider(mRestAdapter.create(NotifierDataService.class));
	}

	private NotifierDataService mDataService;

	public NotifierDataProvider(final NotifierDataService dataService) {
		mDataService = dataService;
	}

	@Override
	public Observable register(final String pushtoken) {
		return mDataService.register(new RegisterRequest(pushtoken)).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable tableIn(final String restId, final String tableId) {
		return mDataService.tableIn(restId, tableId).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable tableOut(final String restId, final String tableId) {
		return mDataService.tableOut(restId, tableId).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable unregister() {
		return mDataService.unregister().subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}
