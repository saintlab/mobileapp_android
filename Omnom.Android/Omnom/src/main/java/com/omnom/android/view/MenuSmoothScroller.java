package com.omnom.android.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MenuSmoothScroller extends LinearSmoothScroller {

	/**
	 * Scrolls until a target view fully visible
	 */
	public static final int MODE_DEFAULT = 0;

	/**
	 * Scrolls target view to top of its container
	 */
	public static final int MODE_TOP = 1;

	private final LinearLayoutManager mLayoutManager;

	private final int mMode;

	public MenuSmoothScroller(final Context context, LinearLayoutManager layoutManager, int mode) {
		super(context);
		mLayoutManager = layoutManager;
		mMode = mode;
	}

	@Override
	public PointF computeScrollVectorForPosition(final int targetPosition) {
		return mLayoutManager.computeScrollVectorForPosition(targetPosition);
	}

	@Override
	public int calculateDyToMakeVisible(final View view, final int snapPreference) {
		if(mMode == MODE_DEFAULT) {
			return super.calculateDyToMakeVisible(view, snapPreference);
		} else {
			final RecyclerView.LayoutManager layoutManager = getLayoutManager();
			return !layoutManager.canScrollVertically() ? 0 : -view.getTop();
		}
	}
}
