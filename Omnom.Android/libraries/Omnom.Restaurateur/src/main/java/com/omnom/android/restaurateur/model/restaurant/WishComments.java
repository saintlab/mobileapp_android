package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 18.05.2015.
 */
public class WishComments {

	@Expose
	private int takeAwayIntervalMinutes;

	public int getTakeAwayIntervalMinutes() {
		return takeAwayIntervalMinutes;
	}

	public void setTakeAwayIntervalMinutes(final int takeAwayIntervalMinutes) {
		this.takeAwayIntervalMinutes = takeAwayIntervalMinutes;
	}

}
