package com.omnom.android.restaurateur.model.decode;

import com.google.gson.annotations.Expose;
import com.omnom.android.utils.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ch3D on 23.12.2014.
 */
public class RssiRecord {
	private static final String DATE_FORMAT = "{yyyy-MM-dd'T'HH:mm:ssZ}";

	private static SimpleDateFormat sSimpleDateFormatter = new SimpleDateFormat(DATE_FORMAT);

	@Expose
	private int value;

	@Expose
	private String time = StringUtils.EMPTY_STRING;

	public RssiRecord(final int value, final long ts) {
		this.value = value;
		this.time = sSimpleDateFormatter.format(new Date(ts));
	}

	@Override
	public boolean equals(final Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}

		final RssiRecord that = (RssiRecord) o;

		if(value != that.value) {
			return false;
		}
		if(!time.equals(that.time)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = value;
		result = 31 * result + time.hashCode();
		return result;
	}

	public String getTime() {
		return time;
	}

	public void setTime(final String time) {
		this.time = time;
	}

	public int getValue() {
		return value;
	}

	public void setValue(final int value) {
		this.value = value;
	}
}
