package com.omnom.android.linker.widget.loader;

import android.content.Context;

import java.util.concurrent.LinkedBlockingQueue;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Ch3D on 17.08.2014.
 */
public final class LoaderObservable extends Observable<LoaderTask.Result> {

	public static class Builder {
		private Context mContext;
		private LinkedBlockingQueue<LoaderTask> taskQueue = new LinkedBlockingQueue<LoaderTask>();

		public Builder(final Context context) {
			mContext = context;
		}

		public Builder addTask(LoaderTask task) {
			taskQueue.add(task);
			return this;
		}

		public LoaderObservable build() {
			return new LoaderObservable(mContext, taskQueue);
		}
	}

	private Context mContext;

	public Context getContext() {
		return mContext;
	}

	protected LoaderObservable(final Context context, final LinkedBlockingQueue<LoaderTask> taskQueue) {
		super(new Observable.OnSubscribe<LoaderTask.Result>() {
			@Override
			public void call(Subscriber<? super LoaderTask.Result> subscriber) {
				try {
					while(!taskQueue.isEmpty()) {
						LoaderTask task = taskQueue.poll();
						subscriber.onNext(task.execute());
					}
					subscriber.onCompleted();
				} catch(Throwable e) {
					subscriber.onError(e);
				}
			}
		});
		mContext = context;
	}
}
