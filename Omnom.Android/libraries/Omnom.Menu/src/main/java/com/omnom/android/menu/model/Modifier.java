package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;
import com.omnom.android.utils.utils.StringUtils;

import java.util.Collections;
import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 05.02.2015.
 */
@AutoParcel
@AutoGson
public abstract class Modifier implements Parcelable {
	public static Modifier create(final String id, final String name, final List<String> list, final String type) {
		return new AutoParcel_Modifier(id, name, StringUtils.EMPTY_STRING, list, type);
	}

	public static Modifier create(final String id, final String type) {
		return new AutoParcel_Modifier(id, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING, Collections.EMPTY_LIST, type);
	}

	@Nullable
	public abstract String id();

	@Nullable
	public abstract String name();

	@Nullable
	public abstract String parent();

	@Nullable
	public abstract List<String> list();

	@Nullable
	public abstract String type();

}
