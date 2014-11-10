package com.omnom.android.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Ch3D on 07.11.2014.
 */
public class OrdersViewPager extends ViewPager {
	private boolean mEnabled = true;

	public OrdersViewPager(final Context context) {
		super(context);
	}

	public OrdersViewPager(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(this.mEnabled) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if(this.mEnabled) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}
}
