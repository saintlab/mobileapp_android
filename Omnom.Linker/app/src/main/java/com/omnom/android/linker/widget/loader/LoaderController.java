package com.omnom.android.linker.widget.loader;

import android.content.Context;

import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationUtils;

/**
 * Created by Ch3D on 07.08.2014.
 */
public class LoaderController {
	private final Context    context;
	private final LoaderView view;

	private LoaderView.Mode mMode = LoaderView.Mode.NONE;

	public LoaderController(Context context, LoaderView view) {
		this.context = context;
		this.view = view;
	}

	public void setMode(final LoaderView.Mode mode) {
		mMode = mode;
		switch(mode) {
			case ENTER_DATA:
				view.postDelayed(new Runnable() {
					@Override
					public void run() {
						AnimationUtils.animateAlpha(view.mEditTableNumber, true);
						AnimationUtils.animateAlpha(view.mImgLogo, false);
						AndroidUtils.showKeyboard(view.mEditTableNumber);
					}
				}, AnimationUtils.DURATION_LONG);
				break;

			case NONE:
				view.postDelayed(new Runnable() {
					@Override
					public void run() {
						AnimationUtils.animateAlpha(view.mEditTableNumber, false);
						AnimationUtils.animateAlpha(view.mImgLogo, true);
						AndroidUtils.hideKeyboard(view.mEditTableNumber);
					}
				}, AnimationUtils.DURATION_LONG);
				break;
		}
	}

	public void hideKeyboard() {
		AndroidUtils.hideKeyboard(view.mEditTableNumber);
	}
}
