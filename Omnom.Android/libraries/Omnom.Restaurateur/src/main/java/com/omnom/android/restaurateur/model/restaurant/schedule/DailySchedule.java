package com.omnom.android.restaurateur.model.restaurant.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 09.12.2014.
 */
public class DailySchedule implements Parcelable {
	public static final Creator<DailySchedule> CREATOR = new Creator<DailySchedule>() {

		@Override
		public DailySchedule createFromParcel(Parcel in) {
			return new DailySchedule(in);
		}

		@Override
		public DailySchedule[] newArray(int size) {
			return new DailySchedule[size];
		}
	};

	public static final DailySchedule NULL = new DailySchedule();

	@Expose
	@SerializedName("is_closed")
	private Boolean closed;

	@Expose
	private String openTime;

	@Expose
	private String closeTime;

	private DailySchedule() {
		closed = false;
		openTime = StringUtils.EMPTY_STRING;
		closeTime = StringUtils.EMPTY_STRING;
	}

	public DailySchedule(Parcel parcel) {
		closed = parcel.readInt() == 1;
		openTime = parcel.readString();
		closeTime = parcel.readString();
	}

	/**
	 * @return The closed
	 */
	public Boolean isClosed() {
		return closed;
	}

	/**
	 * @param closed The is_closed
	 */
	public void setClosed(Boolean closed) {
		this.closed = closed;
	}

	/**
	 * @return The openTime
	 */
	public String getOpenTime() {
		return openTime;
	}

	/**
	 * @param openTime The open_time
	 */
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	/**
	 * @return The closeTime
	 */
	public String getCloseTime() {
		return closeTime;
	}

	/**
	 * @param closeTime The close_time
	 */
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(closed ? 1 : 0);
		dest.writeString(openTime);
		dest.writeString(closeTime);
	}
}
