package com.omnom.android.restaurateur.model.decode;

import com.google.gson.annotations.Expose;
import com.omnom.android.utils.utils.StringUtils;

import java.util.ArrayList;

import altbeacon.beacon.Beacon;

/**
 * Created by Ch3D on 23.12.2014.
 */
public class BeaconRecord {

	public static BeaconRecord create(Beacon beacon) {
		final BeaconRecord record = new BeaconRecord();
		record.setUuid(beacon.getIdValue(0));
		record.setMajor(beacon.getIdValue(1));
		record.setMinor(beacon.getIdValue(2));
		return record;
	}

	@Expose
	private String uuid = StringUtils.EMPTY_STRING;

	@Expose
	private String major = StringUtils.EMPTY_STRING;

	@Expose
	private String minor = StringUtils.EMPTY_STRING;

	@Expose
	private ArrayList<RssiRecord> rssi = new ArrayList<RssiRecord>();

	public void addRssi(RssiRecord record) {
		rssi.add(record);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean equals(final Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}

		final BeaconRecord that = (BeaconRecord) o;

		if(!major.equals(that.major)) {
			return false;
		}
		if(!minor.equals(that.minor)) {
			return false;
		}
		if(!rssi.equals(that.rssi)) {
			return false;
		}
		if(!uuid.equals(that.uuid)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = uuid.hashCode();
		result = 31 * result + major.hashCode();
		result = 31 * result + minor.hashCode();
		result = 31 * result + rssi.hashCode();
		return result;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(final String major) {
		this.major = major;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(final String minor) {
		this.minor = minor;
	}

	public ArrayList<RssiRecord> getRssi() {
		return rssi;
	}

	public void setRssi(final ArrayList<RssiRecord> rssi) {
		this.rssi = rssi;
	}
}
