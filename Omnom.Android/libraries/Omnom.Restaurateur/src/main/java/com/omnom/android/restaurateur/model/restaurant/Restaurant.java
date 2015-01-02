package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.generation.AutoGson;

import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 31.07.2014.
 */
@AutoParcel
@AutoGson
public abstract class Restaurant implements Parcelable {
	public abstract String id();

	@Nullable
	public abstract String authCode();

	@Nullable
	public abstract String description();

	@Nullable
	public abstract String title();

	@Nullable
	public abstract Decoration decoration();

	@Nullable
	public abstract Address address();

	@Nullable
	public abstract int rssiThreshold();

	@Nullable
	public abstract Schedules schedules();

	@Nullable
	public abstract String phone();

	@Nullable
	public abstract Settings settings();

	@Nullable
	public abstract List<TableDataResponse> tables();

	@Nullable
	public abstract List<Order> orders();
}
