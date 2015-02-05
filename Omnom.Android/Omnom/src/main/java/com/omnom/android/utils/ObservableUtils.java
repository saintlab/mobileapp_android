package com.omnom.android.utils;

import android.content.Context;

import com.omnom.android.OmnomApplication;

import rx.functions.Action1;

import static com.omnom.android.mixpanel.MixPanelHelper.Project.OMNOM_ANDROID;

/**
 * Created by Ch3D on 03.10.2014.
 */
public class ObservableUtils {

	public static abstract class BaseOnErrorHandler implements Action1<Throwable> {
		private Context mContext;

		public BaseOnErrorHandler(final Context context) {
			mContext = context;
		}

		@Override
		public void call(Throwable throwable) {
			OmnomApplication.getMixPanelHelper(mContext).track(OMNOM_ANDROID, "!Error." + throwable.getMessage(),
															   throwable.getStackTrace());
			onError(throwable);
		}

		protected abstract void onError(Throwable throwable);
	}
}
