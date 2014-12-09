package com.omnom.android.restaurateur.model.cards;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;

/**
 * Created by mvpotter on 12/10/2014.
 */
public class CardDeleteResponse extends ResponseBase {

	public static final String STATUS_SUCCESS = "success";

	@Expose
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isSuccess() {
		return STATUS_SUCCESS.equals(status);
	}

}
