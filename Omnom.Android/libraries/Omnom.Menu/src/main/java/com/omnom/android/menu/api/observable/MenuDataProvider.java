package com.omnom.android.menu.api.observable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.menu.api.ItemsSerializer;
import com.omnom.android.menu.api.MenuDataService;
import com.omnom.android.menu.model.Items;
import com.omnom.android.menu.model.MenuResponse;
import com.omnom.android.utils.generation.AutoParcelAdapterFactory;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 26.01.2015.
 */
public class MenuDataProvider implements MenuObservableApi {

	public static MenuDataProvider create(final String dataEndPoint) {
		final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;
		final Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.registerTypeAdapterFactory(new AutoParcelAdapterFactory())
				.registerTypeAdapter(Items.class, new ItemsSerializer())
				.create();
		final GsonConverter converter = new GsonConverter(gson);

		final RestAdapter mRestAdapter = new RestAdapter.Builder().setEndpoint(dataEndPoint)
		                                                          .setLogLevel(logLevel)
		                                                          .setConverter(converter).build();
		return new MenuDataProvider(mRestAdapter.create(MenuDataService.class));
	}

	private MenuDataService mDataService;

	public MenuDataProvider(final MenuDataService dataService) {
		mDataService = dataService;
	}

	@Override
	public Observable<MenuResponse> getMenu(String restId) {
		return mDataService.getMenu(restId).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}
