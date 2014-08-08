package com.omnom.android.linker.widget.loader;

import android.content.Context;

import com.omnom.android.linker.R;

/**
 * Created by Ch3D on 07.08.2014.
 */
public class LoaderController {
	private final Context context;
	private final LoaderView view;
	private final int mProgressMax;

	public LoaderController(Context context, LoaderView view) {
		this.context = context;
		this.view = view;
		mProgressMax = context.getResources().getInteger(R.integer.loader_progress_max);
	}
}
