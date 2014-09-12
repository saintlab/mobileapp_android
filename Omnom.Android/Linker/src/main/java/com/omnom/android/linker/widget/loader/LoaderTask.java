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

	public class Result {
		private boolean mIsOk;

		public Result(boolean isOk) {
			mIsOk = isOk;
		}

		public boolean isOk() {
			return mIsOk;
		}
	}

	public static final int MAX_PROGRESS = 100;
	public static final int COUNT_DOWN_INTERVAL = 10;

	private final CountDownTimer cdt;

	private Context mContext;
	private int mMaxTime;
	private int mMaxProgress;
	private ProgressListener mListener;
	private boolean mResult;

	public LoaderTask(Context context, final int maxTime, ProgressListener listener) {
		mContext = context;
		mMaxTime = maxTime;
		mListener = listener;

		cdt = new CountDownTimer(maxTime, COUNT_DOWN_INTERVAL) {
			final int ticks = maxTime / COUNT_DOWN_INTERVAL;
			float step = MAX_PROGRESS / ticks;
			int progress = 0;

			@Override
			public void onTick(long millisUntilFinished) {
				progress += step;
				mListener.onProgress(progress);
			}

			@Override
			public void onFinish() {
				onTimeout();
			}
		};
	}

	private void onTimeout() {
		mListener.onError(new TimeoutException());
	}

	protected abstract boolean runTask();

	public final LoaderTask.Result execute() {
		cdt.start();
		mResult = runTask();
		cdt.cancel();
		mListener.onFinished();
		return new Result(mResult);
	}
}
