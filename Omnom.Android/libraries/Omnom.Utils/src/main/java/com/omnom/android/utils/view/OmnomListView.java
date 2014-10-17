package com.omnom.android.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Ch3D on 15.10.2014.
 */
public class OmnomListView extends ListView {
	private int mPosition = -1;
	private boolean mEnabled = true;

	public OmnomListView(Context context) {
		super(context);
	}

	public OmnomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OmnomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setScrollingEnabled(boolean enabled) {
		mEnabled = enabled;
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
