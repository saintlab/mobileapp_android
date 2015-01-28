package com.saintlab.android.linker.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.utils.utils.StringUtils;
import com.saintlab.android.linker.BuildConfig;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Ch3D on 16.08.2014.
 */
public class CharacteristicHolder implements Parcelable {
	public static final Creator<CharacteristicHolder> CREATOR = new Creator<CharacteristicHolder>() {
		@Override
		public CharacteristicHolder createFromParcel(Parcel in) {
			return new CharacteristicHolder(in);
		}

		@Override
		public CharacteristicHolder[] newArray(int size) {
			return new CharacteristicHolder[size];
		}
	};

	public static CharacteristicHolder createTx(byte[] data) {
		if(BuildConfig.DEBUG && data.length != 1) {
			throw new InvalidParameterException("data should contain only one byte: data" + Arrays.toString(data));
		}
		return new CharacteristicHolder(BeaconAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                                BeaconAttributes.UUID_BLE_REDBEAR_BEACON_SIGNAL_TX,
		                                data);
	}

	public static CharacteristicHolder createMajorId(int major) {
		return new CharacteristicHolder(BeaconAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                                BeaconAttributes.UUID_BLE_REDBEAR_BEACON_MAJOR_ID,
		                                major);
	}

	public static CharacteristicHolder createUuid(String uuid) {
		return new CharacteristicHolder(BeaconAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                                BeaconAttributes.UUID_BLE_REDBEAR_BEACON_UUID,
		                                uuid.replace("-", StringUtils.EMPTY_STRING));
	}

	public static CharacteristicHolder createBattery(byte[] value) {
		return new CharacteristicHolder(BeaconAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                                BeaconAttributes.UUID_BLE_REDBEAR_BATTERY_LEVEL,
		                                value);
	}

	public static CharacteristicHolder createPassword(byte[] data) {
		return new CharacteristicHolder(BeaconAttributes.UUID_BLE_REDBEAR_PASSWORD_SERVICE,
		                                BeaconAttributes.UUID_BLE_REDBEAR_PASSWORD,
		                                data);
	}

	public static CharacteristicHolder createMinorId(int minor) {
		return new CharacteristicHolder(BeaconAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                                BeaconAttributes.UUID_BLE_REDBEAR_BEACON_MINOR_ID,
		                                minor);
	}

	private final UUID serviceId;

	private final UUID charId;

	private byte[] data;

	public CharacteristicHolder(Parcel parcel) {
		serviceId = UUID.fromString(parcel.readString());
		charId = UUID.fromString(parcel.readString());
		parcel.readByteArray(data);
	}

	public CharacteristicHolder(UUID serviceId, UUID charId, int value) {
		this(serviceId, charId, new byte[]{(byte) ((value / 256) & 0xff), (byte) ((value % 256) & 0xff)});
	}

	public CharacteristicHolder(UUID serviceId, UUID charId, String data) {
		this(serviceId, charId, StringUtils.hexStringToByteArray(data));
	}

	public CharacteristicHolder(UUID serviceId, UUID charId, byte[] data) {
		this.serviceId = serviceId;
		this.charId = charId;
		this.data = data;
	}

	public UUID getServiceId() {
		return serviceId;
	}

	public UUID getCharId() {
		return charId;
	}

	public byte[] getData() {
		return data;
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
