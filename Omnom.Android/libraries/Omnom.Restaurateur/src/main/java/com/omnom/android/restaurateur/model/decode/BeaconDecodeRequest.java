package com.omnom.android.restaurateur.model.decode;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Ch3D on 23.12.2014.
 */
public class BeaconDecodeRequest extends DecodeRequest {

	@Expose
	private int duration;

	@Expose
	private List<BeaconRecord> beacons;

	public BeaconDecodeRequest(final int duration, List<BeaconRecord> records) {
		this.duration = duration;
		this.beacons = records;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(final int duration) {
		this.duration = duration;
	}

	public List<BeaconRecord> getBeacons() {
		return beacons;
	}

	public void addBeacon(BeaconRecord record) {
		beacons.add(record);
	}
}
