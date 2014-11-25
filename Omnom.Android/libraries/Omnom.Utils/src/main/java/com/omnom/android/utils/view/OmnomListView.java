package com.omnom.android.utils.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Ch3D on 15.10.2014.
 */
public class OmnomListView extends ListView {
	private int mPosition = -1;

	private boolean mEnabled = true;

	private ViewDragHelper mDragHelper;

	public OmnomListView(Context context) {
		super(context);
		init();
	}

	public OmnomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OmnomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
			@Override
			public boolean tryCaptureView(final View child, final int pointerId) {
				System.err.println("tryCaptureView >> ");
				return true;
			}

			@Override
			public void onViewPositionChanged(final View changedView, final int left, final int top, final int dx, final int dy) {
				System.err.println("onViewPositionChanged >> ");
				super.onViewPositionChanged(changedView, left, top, dx, dy);
			}

			@Override
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
				System.err.println("onViewReleased >> ");
				//int top = getPaddingTop();
				//if(yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
				//	top += mDragRange;
				//}
				//mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
			}

			@Override
			public int getViewVerticalDragRange(View child) {
				return OmnomListView.this.getHeight();
			}

			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				final int topBound = getPaddingTop();
				final int bottomBound = getViewVerticalDragRange(null);
				final int min = Math.min(Math.max(top, topBound), bottomBound);
				OmnomListView.this.setTranslationY(min);
				return min;
			}
		});
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);
		if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			mDragHelper.cancel();
			return false;
		}
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mDragHelper.processTouchEvent(ev);
		return true;
	}

	public void setScrollingEnabled(boolean enabled) {
		// mEnabled = enabled;
		mEnabled = true;
	}



	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;

		if(actionMasked == MotionEvent.ACTION_DOWN) {
			// Record the position of the finger is pressed
			mPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
			return super.dispatchTouchEvent(ev);
		}

		if(actionMasked == MotionEvent.ACTION_MOVE) {
			// The key, ignored MOVE event
			// ListView onTouch can't get MOVE event, so does not occur scroll event
			if(!mEnabled) {
				return true;
			} else {
				return super.dispatchTouchEvent(ev);
			}
		}

		// when Lift your finger
		if(actionMasked == MotionEvent.ACTION_UP
				|| actionMasked == MotionEvent.ACTION_CANCEL) {
			// Finger press and lift all in the same view, to the parent control handle, which is a click event
			if(pointToPosition((int) ev.getX(), (int) ev.getY()) == mPosition) {
				super.dispatchTouchEvent(ev);
			} else {
				// If the finger has moved Item pressed, indicating that scrolling behavior, cleaning Item pressed state
				setPressed(false);
				invalidate();
				return true;
			}
		}

		return super.dispatchTouchEvent(ev);
	}
}
