package com.omnom.android.utils.generation;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;

/**
 * Created by Ch3D on 25.12.2014.
 */
public class AutoParcelAdapterFactory implements TypeAdapterFactory {

	public static final String SUFFIX_AUTO_PARCEL = ".AutoParcel_";

	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(final Gson gson, final com.google.gson.reflect.TypeToken<T> type) {
		Class<? super T> rawType = type.getRawType();
		if(!rawType.isAnnotationPresent(AutoGson.class)) {
			return null;
		}

		String packageName = rawType.getPackage().getName();
		String className = rawType.getName().substring(packageName.length() + 1).replace('$', '_');
		String autoValueName = packageName + SUFFIX_AUTO_PARCEL + className;

		try {
			Class<?> autoValueType = Class.forName(autoValueName);
			return (TypeAdapter<T>) gson.getAdapter(autoValueType);
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("Could not load AutoParcel type " + autoValueName, e);
		}
	}
}
