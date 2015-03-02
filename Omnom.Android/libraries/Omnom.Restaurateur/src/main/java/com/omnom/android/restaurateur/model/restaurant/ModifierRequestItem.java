package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 09.02.2015.
 */
public class ModifierRequestItem {

	@Expose
	public String id;

	public ModifierRequestItem() {
		this(StringUtils.EMPTY_STRING);
	}

	public ModifierRequestItem(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}
}
