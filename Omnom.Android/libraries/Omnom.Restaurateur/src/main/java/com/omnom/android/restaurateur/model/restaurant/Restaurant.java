package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcelable;

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

	public abstract String authCode();

	public abstract String description();

	public abstract String title();

	public abstract Decoration decoration();

	public abstract Address address();

	public abstract int rssiThreshold();

	public abstract Schedules schedules();

	public abstract String phone();

	public abstract List<TableDataResponse> tables();

	public abstract List<Order> orders();
}
