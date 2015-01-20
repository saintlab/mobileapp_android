package com.omnom.android.restaurateur.model.decode;

import com.google.gson.annotations.Expose;

/**
 * Created by mvpotter on 20.01.2015.
 */
public class HashDecodeRequest extends DecodeRequest {
	@Expose
	private String hash;

	public HashDecodeRequest(final String hash) {
		this.hash = hash;
	}

	public String getHash() {
		return hash;
	}

	public void setHah(final String hash) {
		this.hash = hash;
	}
}
