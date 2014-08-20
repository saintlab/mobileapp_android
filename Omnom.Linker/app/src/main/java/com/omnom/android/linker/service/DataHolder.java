package com.omnom.android.linker.service;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Ch3D on 16.08.2014.
 */
public class DataHolder implements Parcelable {

	public static final Creator<DataHolder> CREATOR = new Creator<DataHolder>() {

		@Override
		public DataHolder createFromParcel(Parcel in) {
			return new DataHolder(in);
		}

		@Override
		public DataHolder[] newArray(int size) {
			return new DataHolder[size];
		}
	};

	public final UUID   serviceId;
	public final UUID   charId;
	public byte[] data;

	public DataHolder(Parcel parcel) {
		serviceId = UUID.fromString(parcel.readString());
		charId = UUID.fromString(parcel.readString());
		parcel.readByteArray(data);
	}

	public DataHolder(UUID serviceId, UUID charId, int data) {
		this.serviceId = serviceId;
		this.charId = charId;
		this.data = new byte[2];

		int m1 = data / 256;
		int m2 = data % 256;
		this.data[0] = (byte) (m1 & 0xff);
		this.data[1] = (byte) (m2 & 0xff);
	}

	public DataHolder(UUID serviceId, UUID charId, byte[] data) {
		this.serviceId = serviceId;
		this.charId = charId;
		this.data = data;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(serviceId.toString());
		dest.writeString(charId.toString());
		dest.writeByteArray(data);
	}
}
