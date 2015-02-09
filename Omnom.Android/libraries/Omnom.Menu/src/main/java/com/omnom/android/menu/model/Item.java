package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;
import com.omnom.android.utils.utils.StringUtils;

import java.util.Collections;
import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Item implements Parcelable {

	public static Item NULL = create(StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING,
	                                 StringUtils.EMPTY_STRING, Collections.EMPTY_LIST, 0, Details.NULL, Collections.EMPTY_LIST);

	public static Item create(final String id, final String name, final String descr, final String photo, final List<Modifier> modifiers,
	                          final long price, final Details details, final List<String> recommends) {
		return new AutoParcel_Item(id, name, descr, photo, modifiers, price, details, recommends);
	}

	@Nullable
	public abstract String id();

	@Nullable
	public abstract String name();

	@Nullable
	public abstract String description();

	@Nullable
	public abstract String photo();

	@Nullable
	public abstract List<Modifier> modifiers();

	@Nullable
	public abstract long price();

	@Nullable
	public abstract Details details();

	@Nullable
	public abstract List<String> recommendations();
}
