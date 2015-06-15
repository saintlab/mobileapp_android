package com.omnom.android.view.subcategories;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import static com.omnom.android.view.subcategories.SubcategoriesView.OnCollapsedTouchListener;

/**
 * Created by Ch3D on 24.03.2015.
 */
public abstract class ItemTouchListenerBase implements RecyclerView.OnItemTouchListener {
	// protected Point mPoint = new Point();

	protected boolean mTouchEnabled = false;

	private SubcategoriesView mView;

	private GestureDetector mGestureDetector;

	private OnCollapsedTouchListener mCollapsedTouchListener;

	private Rect mRect;

	public ItemTouchListenerBase(SubcategoriesView view,
	                             GestureDetector gestureDetector,
	                             final OnCollapsedTouchListener collapsedTouchListener) {
		mView = view;
		mGestureDetector = gestureDetector;
		mCollapsedTouchListener = collapsedTouchListener;
	}

	public void setTouchEnabled(final boolean touchEnabled) {
		mTouchEnabled = touchEnabled;
	}

	protected void onActionUp(final MotionEvent e) {
		if(mRect == null) {
			// skip
			return;
		}
		if(mRect.contains((int) e.getX(), (int) e.getY())) {
			final View childViewUnder = mView.mListView.findChildViewUnder(e.getX(), e.getY());
			mView.onGroupClick(childViewUnder, true);
			if(mCollapsedTouchListener != null) {
				mCollapsedTouchListener.onCollapsedSubcategoriesTouch(e);
			}
		}
		mRect.set(-1, -1, -1, -1);
	}

	protected void onActionDown(final MotionEvent e) {
		final View childViewUnder = mView.mListView.findChildViewUnder(e.getX(), e.getY());
		if(childViewUnder != null) {
			mRect = new Rect(childViewUnder.getLeft(), childViewUnder.getTop(), childViewUnder.getRight(), childViewUnder.getBottom());
		}
	}

	protected boolean onGestureHandle(final MotionEvent e) {
		return mGestureDetector.onTouchEvent(e);
	}

	@Override
	public void onTouchEvent(final RecyclerView rv, final MotionEvent e) {
		// Do nothing
	}
}
