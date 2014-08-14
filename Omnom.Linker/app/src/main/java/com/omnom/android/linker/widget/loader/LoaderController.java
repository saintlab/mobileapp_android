package com.omnom.android.linker.widget.loader;

import android.content.Context;

/**
 * Created by Ch3D on 07.08.2014.
 */
public class LoaderController {
	private final Context context;
	private final LoaderView view;

	public LoaderController(Context context, LoaderView view) {
		this.context = context;
		this.view = view;
	}

	public void setMode(LoaderView.Mode mode) {
		view.setMode(mode);
	}
}
