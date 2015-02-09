package com.omnom.android.menu.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.utils.generation.AutoGson;
import com.omnom.android.utils.utils.StringUtils;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Details implements Parcelable {

	public static Details NULL = create(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, StringUtils.EMPTY_STRING);

	public static Details create(int weight,
	                             int cookingTime,
	                             int volume,
	                             int persons,
	                             int protein100,
	                             int proteinTotal,
	                             int fat100,
	                             int fatTotal,
	                             int carbohydrate100,
	                             int carbohydrateTotal,
	                             int energy100,
	                             int energyTotal,
	                             String ingredients) {
		return new AutoParcel_Details(weight, cookingTime, volume, persons, protein100, proteinTotal, fat100, fatTotal, carbohydrate100,
		                              carbohydrateTotal, energy100, energyTotal, ingredients);
	}

	@Nullable
	public abstract int weight();

	@Nullable
	public abstract int cookingTime();

	@Nullable
	public abstract int volume();

	@Nullable
	public abstract int persons();

	@Nullable
	public abstract int protein100();

	@Nullable
	public abstract int proteinTotal();

	@Nullable
	public abstract int fat100();

	@Nullable
	public abstract int fatTotal();

	@Nullable
	public abstract int carbohydrate100();

	@Nullable
	public abstract int carbohydrateTotal();

	@Nullable
	public abstract int energy100();

	@Nullable
	public abstract int energyTotal();

	@Nullable
	public abstract String ingredients();
}
