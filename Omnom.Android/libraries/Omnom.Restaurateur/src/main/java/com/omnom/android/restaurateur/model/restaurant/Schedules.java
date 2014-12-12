package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.restaurateur.model.restaurant.schedule.WeekSchedule;

/**
 * Created by Ch3D on 09.12.2014.
 */
public class Schedules implements Parcelable {

	public static final Creator<Schedules> CREATOR = new Creator<Schedules>() {
		@Override
		public Schedules createFromParcel(Parcel in) {
			return new Schedules(in);
		}

		@Override
		public Schedules[] newArray(int size) {
			return new Schedules[size];
		}
	};

	@Expose
	@SerializedName("work")
	private WeekSchedule mWorkingSchedule;

	public Schedules(Parcel parcel) {
		mWorkingSchedule = parcel.readParcelable(WeekSchedule.class.getClassLoader());
	}

	public WeekSchedule getWorkingSchedule() {
		return mWorkingSchedule;
	}

	public void setWorkingSchedule(final WeekSchedule workingSchedule) {
		mWorkingSchedule = workingSchedule;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeParcelable(mWorkingSchedule, flags);
	}
}
