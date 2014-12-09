package com.omnom.android.restaurateur.model.restaurant.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ch3D on 09.12.2014.
 */
public class WeekSchedule implements Parcelable {
	public static final Creator<WeekSchedule> CREATOR = new Creator<WeekSchedule>() {

		@Override
		public WeekSchedule createFromParcel(Parcel in) {
			return new WeekSchedule(in);
		}

		@Override
		public WeekSchedule[] newArray(int size) {
			return new WeekSchedule[size];
		}
	};

	@Expose
	@SerializedName("sunday")
	private DailySchedule sunday;

	@Expose
	@SerializedName("saturday")
	private DailySchedule saturday;

	@Expose
	@SerializedName("friday")
	private DailySchedule friday;

	@Expose
	@SerializedName("thursday")
	private DailySchedule thursday;

	@Expose
	@SerializedName("wednesday")
	private DailySchedule wednesday;

	@Expose
	@SerializedName("tuesday")
	private DailySchedule tuesday;

	@Expose
	@SerializedName("monday")
	private DailySchedule monday;

	public WeekSchedule(Parcel parcel) {
		monday = parcel.readParcelable(DailySchedule.class.getClassLoader());
		tuesday = parcel.readParcelable(DailySchedule.class.getClassLoader());
		wednesday = parcel.readParcelable(DailySchedule.class.getClassLoader());
		thursday = parcel.readParcelable(DailySchedule.class.getClassLoader());
		friday = parcel.readParcelable(DailySchedule.class.getClassLoader());
		saturday = parcel.readParcelable(DailySchedule.class.getClassLoader());
		sunday = parcel.readParcelable(DailySchedule.class.getClassLoader());
	}

	/**
	 * @return The sunday
	 */
	public DailySchedule getSunday() {
		return sunday;
	}

	/**
	 * @param sunday The sunday
	 */
	public void setSunday(DailySchedule sunday) {
		this.sunday = sunday;
	}

	/**
	 * @return The saturday
	 */
	public DailySchedule getSaturday() {
		return saturday;
	}

	/**
	 * @param saturday The saturday
	 */
	public void setSaturday(DailySchedule saturday) {
		this.saturday = saturday;
	}

	/**
	 * @return The friday
	 */
	public DailySchedule getFriday() {
		return friday;
	}

	/**
	 * @param friday The friday
	 */
	public void setFriday(DailySchedule friday) {
		this.friday = friday;
	}

	/**
	 * @return The thursday
	 */
	public DailySchedule getThursday() {
		return thursday;
	}

	/**
	 * @param thursday The thursday
	 */
	public void setThursday(DailySchedule thursday) {
		this.thursday = thursday;
	}

	/**
	 * @return The wednesday
	 */
	public DailySchedule getWednesday() {
		return wednesday;
	}

	/**
	 * @param wednesday The wednesday
	 */
	public void setWednesday(DailySchedule wednesday) {
		this.wednesday = wednesday;
	}

	/**
	 * @return The tuesday
	 */
	public DailySchedule getTuesday() {
		return tuesday;
	}

	/**
	 * @param tuesday The tuesday
	 */
	public void setTuesday(DailySchedule tuesday) {
		this.tuesday = tuesday;
	}

	/**
	 * @return The monday
	 */
	public DailySchedule getMonday() {
		return monday;
	}

	/**
	 * @param monday The monday
	 */
	public void setMonday(DailySchedule monday) {
		this.monday = monday;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeParcelable(monday, flags);
		dest.writeParcelable(tuesday, flags);
		dest.writeParcelable(wednesday, flags);
		dest.writeParcelable(thursday, flags);
		dest.writeParcelable(friday, flags);
		dest.writeParcelable(saturday, flags);
		dest.writeParcelable(sunday, flags);
	}
}
