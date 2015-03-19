package com.omnom.android.utils.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Ch3D on 19.03.2015.
 */
public class OmnomRecyclerView extends RecyclerView {
	private static final String TAG = OmnomRecyclerView.class.getSimpleName();

	public OmnomRecyclerView(final Context context) {
		super(context);
	}

	public OmnomRecyclerView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public OmnomRecyclerView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void scrollTo(int x, int y) {
		Log.w(TAG, "OmnomRecycleView does not support scrolling to an absolute position.");
		// Either don't call super here or call just for some phones, or try catch it. From default implementation we have removed the
		// Runtime Exception trown
	}
}
