package com.omnom.android.utils;

import android.content.Context;

import com.omnom.android.OmnomApplication;

import java.util.Arrays;

import rx.functions.Action1;

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
			OmnomApplication.getMixPanelHelper(mContext).track("!Error." + throwable.getMessage(),
																Arrays.toString(throwable.getStackTrace()));
			onError(throwable);
		}

		protected abstract void onError(Throwable throwable);
	}
}
