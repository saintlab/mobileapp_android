package com.omnom.android.menu.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 26.01.2015.
 */
public class MenuResponse {

	@Expose
	private Menu menu;

	@Nullable
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(final Menu menu) {
		this.menu = menu;
	}
}
