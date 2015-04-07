package com.omnom.android.restaurateur.model.restaurant;

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
	public static final String ENTRANCE_MODE_TAKEAWAY = "takeaway";

	public static final String ENTRANCE_MODE_BAR = "bar";

	public static final String ENTRANCE_MODE_LUNCH = "lunch";

	public static final String ENTRANCE_MODE_DEFAULT = "default";

	public static final String ENTRANCE_MODE_NONE = "none";

	@Nullable
	public abstract String id();

	@Nullable
	public abstract String authCode();

	@Nullable
	public abstract Settings settings();

	@Nullable
	public abstract String description();

	@Nullable
	public abstract String title();

	@Nullable
	public abstract Decoration decoration();

	@Nullable
	public abstract Address address();

	@Nullable
	public abstract Location location();

	@Nullable
	public abstract Double distance();

	public abstract int rssiThreshold();

	@Nullable
	public abstract Schedules schedules();

	@Nullable
	public abstract Schedules orderSchedules();

	@Nullable
	public abstract String phone();

	@Nullable
	public abstract List<TableDataResponse> tables();

	@Nullable
	public abstract List<Order> orders();

	@Nullable
	public abstract String entranceMode();

	public boolean isBar() {
		return ENTRANCE_MODE_BAR.equals(entranceMode());
	}

	public boolean isLunch() {
		return ENTRANCE_MODE_LUNCH.equals(entranceMode());
	}

	public boolean isTakeAway() {
		return ENTRANCE_MODE_TAKEAWAY.equals(entranceMode());
	}

	@Nullable
	public abstract boolean available();

	@Nullable
	public abstract String ordersPaidUrl();
}
