package com.omnom.android.linker.widget.loader;

import android.content.Context;
import android.os.CountDownTimer;

import java.util.concurrent.TimeoutException;

/**
 * Created by Ch3D on 13.08.2014.
 */
public abstract class LoaderTask {

	public interface ProgressListener {
		public void onError(Throwable e);

		public void onProgress(int progress);

		public void onFinished();
	}

	private final CountDownTimer cdt;

	private Context mContext;
	private int     mMaxTime;
	private int     mMaxProgress;
	private ProgressListener mListener;

	public LoaderTask(Context context, int maxTime, int maxProgress, ProgressListener listener) {
		mContext = context;
		mMaxTime = maxTime;
		mMaxProgress = maxProgress;
		mListener = listener;

		cdt = new CountDownTimer(maxTime, 10) {
			int j = 0;

			@Override
			public void onTick(long millisUntilFinished) {
				j += 100;
				postProgress(j);
			}

			@Override
			public void onFinish() {
				mListener.onError(new TimeoutException());
			}
		};
	}

	protected abstract void runTask();

	public final void execute() {
		cdt.start();
		runTask();
		cdt.cancel();
	}

	public final void postProgress(int progress) {
		mListener.onProgress(progress);
	}
}
