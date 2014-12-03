package com.omnom.android.restaurateur.model.config;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by mvpotter on 12/3/2014.
 */
public class UuidsHolder {

	@Expose
	private List<String> active;

	@Expose
	private List<String> deprecated;

	public List<String> getActive() {
		return active;
	}

	public List<String> getDeprecated() {
		return deprecated;
	}

}
