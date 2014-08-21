package com.omnom.android.linker.widget.loader;

import android.content.Context;

import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.StringUtils;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Ch3D on 07.08.2014.
 */
public class LoaderController implements LoaderTask.ProgressListener {
	private final Context context;
	private final LoaderView view;
	private LinkedBlockingQueue<LoaderTask> taskQueue = new LinkedBlockingQueue<LoaderTask>();

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
						view.mEditTableNumber.setText(StringUtils.EMPTY_STRING);
						AnimationUtils.animateAlpha(view.mEditTableNumber, true);
						view.mEditTableNumber.setFocusable(true);
						view.mEditTableNumber.setFocusableInTouchMode(true);
						AnimationUtils.animateAlpha(view.mImgLogo, false);
						AndroidUtils.showKeyboard(view.mEditTableNumber);
					}
				}, AnimationUtils.DURATION_SHORT);
				break;

			case NONE:
				view.postDelayed(new Runnable() {
					@Override
					public void run() {
						AnimationUtils.animateAlpha(view.mEditTableNumber, false);
						view.mEditTableNumber.setFocusable(false);
						view.mEditTableNumber.setFocusableInTouchMode(false);
						AnimationUtils.animateAlpha(view.mImgLogo, true);
						AndroidUtils.hideKeyboard(view.mEditTableNumber);
					}
				}, AnimationUtils.DURATION_SHORT);
				break;
		}
	}

	public void addTask(final LoaderTask task) {
		taskQueue.add(task);
	}

	public void executeTasks() {
		while(!taskQueue.isEmpty()) {
			final LoaderTask poll = taskQueue.poll();
			poll.execute();
		}
	}

	@Override
	public void onError(Throwable e) {

	}

	@Override
	public void onProgress(int progress) {

	}

	@Override
	public void onFinished() {

	}
}
