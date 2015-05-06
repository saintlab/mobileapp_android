package com.omnom.android.restaurateur.api.observable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.restaurateur.api.ConfigDataService;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.restaurateur.model.config.Config;
import com.omnom.android.restaurateur.retrofit.RestaurateurRxSupport;
import com.omnom.android.restaurateur.serializer.MailRuSerializer;
import com.omnom.android.utils.generation.AutoParcelAdapterFactory;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 06.05.2015.
 */
public class ConfigDataProvider implements ConfigDataService {

	public static ConfigDataProvider create(final String dataEndPoint, final RequestInterceptor interceptor) {
		// final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;
		final Gson gson = new GsonBuilder()
				.registerTypeAdapter(AcquiringData.class, new MailRuSerializer())
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.registerTypeAdapterFactory(new AutoParcelAdapterFactory())
				.create();
		final GsonConverter converter = new GsonConverter(gson);

		final RestAdapter mRestAdapter = new RestAdapter.Builder().setRequestInterceptor(interceptor).setEndpoint(dataEndPoint)
		                                                          .setRxSupport(new RestaurateurRxSupport(interceptor))
		                                                          .setLogLevel(logLevel)
		                                                          .setConverter(converter).build();
		return new ConfigDataProvider(mRestAdapter.create(ConfigDataService.class));
	}

	private final ConfigDataService mDataService;

	public ConfigDataProvider(final ConfigDataService configDataService) {
		mDataService = configDataService;
	}

	@Override
	public Observable<Config> getConfig() {
		return mDataService.getConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}
