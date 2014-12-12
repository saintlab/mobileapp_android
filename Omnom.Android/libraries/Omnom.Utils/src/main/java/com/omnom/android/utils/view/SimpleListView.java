package com.omnom.android.utils.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Ch3D on 12.12.2014.
 */
public class SimpleListView extends ListView {
	private boolean mScrollEnabled = true;

	public SimpleListView(final Context context) {
		super(context);
	}

	public SimpleListView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public SimpleListView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setScrollEnabled(boolean enabled) {
		mScrollEnabled = enabled;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);
		if(action == MotionEvent.ACTION_MOVE) {
			if(!mScrollEnabled) {
				return true;
			}
		}
		return super.onTouchEvent(ev);
	}
}
