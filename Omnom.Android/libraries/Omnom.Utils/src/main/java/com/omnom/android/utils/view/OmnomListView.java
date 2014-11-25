package com.omnom.android.utils.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ListView;

/**
 * Created by Ch3D on 15.10.2014.
 */
public class OmnomListView extends ListView {

	public static final int DURATION_ANIMATE_TO_START = 300;

	public interface SwipeListener {
		public void onRefresh();
	}

	private static final String TAG = OmnomListView.class.getSimpleName();

	private static final int INVALID_POINTER = -1;

	private int mActivePointerId = INVALID_POINTER;

	private static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;

	private static final int REFRESH_TRIGGER_DISTANCE = 120;

	private class BaseAnimationListener implements Animation.AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}

	private int mPosition = -1;

	private boolean mEnabled = true;

	private float mLastMotionY;

	private int mDefaultOverscrollOffset;

	private float mInitialMotionY;

	private boolean mIsBeingDragged;

	private boolean mReturningToStart;

	private float mTouchSlop;

	private float mDistanceToTriggerSync = -1;

	private int mCurrentTargetOffsetTop;

	private final Animation.AnimationListener mReturnToStartPositionListener = new BaseAnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
			mCurrentTargetOffsetTop = 0;
		}
	};

	private final Runnable mCancel = new Runnable() {
		@Override
		public void run() {
			mReturningToStart = true;
			animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(), mReturnToStartPositionListener);
		}
	};

	private int mOriginalOffsetTop = -1;

	private SwipeListener mListener;

	private boolean mRefreshing = false;

	private boolean mSwipeEnabled;

	private int mFrom;

	private final Animation mAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			int targetTop = 0;
			if (mFrom != mOriginalOffsetTop) {
				targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
			} else {
				return;
			}
			int offset = targetTop - getTop();
			final int currentTop = getTop();
			if (offset + currentTop <= 0) {
				offset = 0 - currentTop;
			}
			setTargetOffsetTopAndBottom(offset);
		}
	};

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

	public void setSwipeEnabled(boolean swipeEnabled) {
		mSwipeEnabled = swipeEnabled;
	}

	public void setSwipeListener(SwipeListener listener) {
		mListener = listener;
	}

	@Override
	protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();
		final int childLeft = getPaddingLeft();
		final int childTop = mCurrentTargetOffsetTop + getPaddingTop();
		final int childWidth = width - getPaddingLeft() - getPaddingRight();
		final int childHeight = height - getPaddingTop() - getPaddingBottom();
		super.onLayout(changed, childLeft, childTop, childLeft + childWidth, childTop + childHeight);
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent ev) {
		ensureTarget();
		if (mSwipeEnabled) {

			final int action = MotionEventCompat.getActionMasked(ev);

			if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
				mReturningToStart = false;
			}

			if (!isEnabled() || mReturningToStart) {
				// Fail fast if we're not in a state where a swipe is possible
				return false;
			}

			switch (action) {
				case MotionEvent.ACTION_DOWN:
					mLastMotionY = mInitialMotionY = ev.getY();
					mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
					mIsBeingDragged = false;
					break;

				case MotionEvent.ACTION_MOVE:
					if (mActivePointerId == INVALID_POINTER) {
						Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
						return false;
					}

					final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
					if (pointerIndex < 0) {
						Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
						return false;
					}

					final float y = MotionEventCompat.getY(ev, pointerIndex);
					final float yDiff = y - mInitialMotionY;
					if (yDiff > mTouchSlop) {
						mLastMotionY = y;
						mIsBeingDragged = true;
					}
					break;

				case MotionEventCompat.ACTION_POINTER_UP:
					onSecondaryPointerUp(ev);
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					mIsBeingDragged = false;
					mActivePointerId = INVALID_POINTER;
					break;
			}
			if (!mIsBeingDragged) {
				return super.onInterceptTouchEvent(ev);
			}
			return mIsBeingDragged;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private void ensureTarget() {
		// Don't bother getting the parent height if the parent hasn't been laid out yet.
		if (mOriginalOffsetTop == -1) {
			mOriginalOffsetTop = getTop() + getPaddingTop();
		}
		if (mDistanceToTriggerSync == -1) {
			if (getHeight() > 0) {
				final DisplayMetrics metrics = getResources().getDisplayMetrics();
				mDistanceToTriggerSync =
						(int) Math.min(getHeight() * MAX_SWIPE_DISTANCE_FACTOR, REFRESH_TRIGGER_DISTANCE * metrics.density);
			}
		}
	}

	private void init() {
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() / 8;
		mDefaultOverscrollOffset = (int) getResources().getDisplayMetrics().density;
	}

	public void setScrollingEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	private void updateContentOffsetTop(int targetTop) {
		final int currentTop = getTop();
		if (targetTop > 0 && targetTop + currentTop > mDistanceToTriggerSync) {
			setTargetOffsetTopAndBottom(mDefaultOverscrollOffset);
			return;
		}
		if (targetTop < 0) {
			if (targetTop + currentTop > mOriginalOffsetTop) {
				setTargetOffsetTopAndBottom(targetTop);
				return;
			} else {
				setTargetOffsetTopAndBottom(-mDefaultOverscrollOffset);
				return;
			}
		}
		if (targetTop > mDistanceToTriggerSync) {
			targetTop = (int) mDistanceToTriggerSync;
		} else if (targetTop < 0) {
			targetTop = 0;
		}
		setTargetOffsetTopAndBottom(targetTop);
	}

	private void setTargetOffsetTopAndBottom(int offset) {
		offsetTopAndBottom(offset);
		mCurrentTargetOffsetTop = getTop();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;

		if (actionMasked == MotionEvent.ACTION_DOWN) {
			return super.dispatchTouchEvent(ev);
		}

		if (actionMasked == MotionEvent.ACTION_MOVE) {
			if (!mEnabled && !mSwipeEnabled) {
				return true;
			} else {
				return super.dispatchTouchEvent(ev);
			}
		}

		if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
			return super.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);

		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
			mReturningToStart = false;
		}

		if (!isEnabled() || mReturningToStart) {
			// Fail fast if we're not in a state where a swipe is possible
			return false;
		}

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
				mLastMotionY = mInitialMotionY = ev.getY();
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				mIsBeingDragged = false;
				break;

			case MotionEvent.ACTION_MOVE:
				final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				if (pointerIndex < 0) {
					Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
					return false;
				}

				final float y = MotionEventCompat.getY(ev, pointerIndex);
				final float yDiff = y - mInitialMotionY;

				if (!mIsBeingDragged && Math.abs(yDiff) > mTouchSlop) {
					mIsBeingDragged = true;
				}

				if (mIsBeingDragged) {
					if (yDiff > mDistanceToTriggerSync) {
						startRefresh();
					} else {
						updateContentOffsetTop((int) (yDiff));
						if (mLastMotionY > y && getTop() == getPaddingTop()) {
							// If the user puts the view back at the top, we
							// don't need to. This shouldn't be considered
							// cancelling the gesture as the user can restart from the top.
							removeCallbacks(mCancel);
						} else {
							updatePositionTimeout();
						}
					}
					mLastMotionY = y;
				}
				break;

			case MotionEventCompat.ACTION_POINTER_DOWN: {
				final int index = MotionEventCompat.getActionIndex(ev);
				mLastMotionY = MotionEventCompat.getY(ev, index);
				mActivePointerId = MotionEventCompat.getPointerId(ev, index);
				break;
			}

			case MotionEventCompat.ACTION_POINTER_UP:
				onSecondaryPointerUp(ev);
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (!mIsBeingDragged && pointToPosition((int) ev.getX(), (int) ev.getY()) == mPosition) {
					super.onTouchEvent(ev);
				}
				mIsBeingDragged = false;
				mActivePointerId = INVALID_POINTER;
				return true;
		}

		return true;
	}

	private void updatePositionTimeout() {
		removeCallbacks(mCancel);
		postDelayed(mCancel, DURATION_ANIMATE_TO_START / 2);
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
		}
	}

	private void startRefresh() {
		removeCallbacks(mCancel);
		if (!mRefreshing) {
			mRefreshing = true;
			mListener.onRefresh();
		}
	}

	private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
		mFrom = from;
		mAnimateToStartPosition.reset();
		mAnimateToStartPosition.setDuration(DURATION_ANIMATE_TO_START);
		mAnimateToStartPosition.setAnimationListener(listener);
		mAnimateToStartPosition.setInterpolator(new DecelerateInterpolator());
		startAnimation(mAnimateToStartPosition);
	}

	public void cancelRefreshing() {
		mRefreshing = false;
		mCancel.run();
	}
}
