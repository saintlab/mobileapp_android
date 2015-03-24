package com.omnom.android.view.subcategories;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;

import hugo.weaving.DebugLog;

import static com.omnom.android.view.subcategories.SubcategoriesView.OnCollapsedTouchListener;

/**
 * Created by Ch3D on 24.03.2015.
 */
public class ItemTouchListenerJB extends ItemTouchListenerBase {

	public ItemTouchListenerJB(final SubcategoriesView view,
	                           final GestureDetector gestureDetector,
	                           final OnCollapsedTouchListener collapsedTouchListener) {
		super(view, gestureDetector, collapsedTouchListener);
	}

	@Override
	@DebugLog
	public boolean onInterceptTouchEvent(final RecyclerView rv, final MotionEvent e) {
		System.err.println(">>>> action = " + e);
		if(!mTouchEnabled) {
			switch(e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					onActionDown(e);
					break;

				case MotionEvent.ACTION_UP:
					onActionUp(e);
					break;
			}
		} else {
			return onGestureHandle(e);
		}
		return !mTouchEnabled;
	}
}
