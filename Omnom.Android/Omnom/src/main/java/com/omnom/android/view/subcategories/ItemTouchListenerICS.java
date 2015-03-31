package com.omnom.android.view.subcategories;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;

import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 24.03.2015.
 */
public class ItemTouchListenerICS extends ItemTouchListenerBase {

	public ItemTouchListenerICS(final SubcategoriesView view, final GestureDetector gestureDetector, final SubcategoriesView
			.OnCollapsedTouchListener collapsedTouchListener) {
		super(view, gestureDetector, collapsedTouchListener);
	}

	@Override
	@DebugLog
	public boolean onInterceptTouchEvent(final RecyclerView rv, final MotionEvent e) {
		if(!mTouchEnabled) {
			switch(e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					onActionDown(e);
					break;

				case MotionEvent.ACTION_UP:
					onActionUp(e);
					break;
			}
			return false;
		} else {
			return onGestureHandle(e);
		}
	}
}
