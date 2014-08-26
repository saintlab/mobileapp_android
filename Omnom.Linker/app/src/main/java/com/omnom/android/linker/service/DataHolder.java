package com.omnom.android.linker.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.linker.BuildConfig;

import java.security.InvalidParameterException;
import java.util.Arrays;
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

	public static DataHolder createTx(byte[] data) {
		if(BuildConfig.DEBUG && data.length != 1) {
			throw new InvalidParameterException("data should contain only one byte: data" + Arrays.toString(data));
		}
		return new DataHolder(RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                        RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_SIGNAL_TX, data);
	}

	public static DataHolder createMajorId(int major) {
		return new DataHolder(RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                      RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_MAJOR_ID, major);
	}

	public static DataHolder createPassword(byte[] data) {
		return new DataHolder(RBLBluetoothAttributes.UUID_BLE_REDBEAR_PASSWORD_SERVICE,
		                      RBLBluetoothAttributes.UUID_BLE_REDBEAR_PASSWORD, data);
	}

	public static DataHolder createMinorId(int minor) {
		return new DataHolder(RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                      RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_MINOR_ID, minor);
	}

	public final UUID serviceId;
	public final UUID charId;
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
