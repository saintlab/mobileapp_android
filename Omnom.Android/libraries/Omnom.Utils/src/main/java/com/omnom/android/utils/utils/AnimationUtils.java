package com.omnom.android.utils.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.omnom.android.utils.R;

import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class AnimationUtils {

	/**
	 * Restricts that on animation end callback is launched only once.
	 */
	private static class OmnomAnimatorListenerAdapter extends AnimatorListenerAdapter {

		private final View view;

		private final Runnable callback;

		private boolean isCallbackLaunched;

		public OmnomAnimatorListenerAdapter(final View view) {
			this(view, null);
		}

		public OmnomAnimatorListenerAdapter(final View view, final Runnable callback) {
			this.view = view;
			this.callback = callback;
			isCallbackLaunched = false;
		}

		@Override
		public void onAnimationCancel(final Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			if(callback != null && !isCallbackLaunched) {
				view.post(callback);
				isCallbackLaunched = true;
			}
		}
	}

	public static void animateBackground(final View view, final int startColor, final int endColor, final long duration) {
		final Object tag = view.getTag(R.id.animating);
		if(tag != null) {
			final Boolean isAnimating = (Boolean) tag;
			if(isAnimating) {
				return;
			}
		}

		view.setTag(R.id.animating, true);
		final ValueAnimator colorAnimator = ValueAnimator.ofInt(startColor, endColor);
		colorAnimator.setDuration(duration);
		colorAnimator.setEvaluator(new ArgbEvaluator());
		colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				view.setBackgroundColor((Integer) animation.getAnimatedValue());
			}
		});
		colorAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				view.setTag(R.id.animating, false);
			}
		});
		colorAnimator.start();
	}

	public static void animateTextColor(final TextView view, final int startColor, final int endColor, final long duration) {
		final Object tag = view.getTag(R.id.animating);
		if(tag != null) {
			final Boolean isAnimating = (Boolean) tag;
			if(isAnimating) {
				return;
			}
		}

		view.setTag(R.id.animating, true);
		view.post(new Runnable() {
			@Override
			public void run() {
				final ValueAnimator colorAnimator = ValueAnimator.ofInt(startColor, endColor);
				colorAnimator.setDuration(duration);
				colorAnimator.setEvaluator(new ArgbEvaluator());
				colorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
				colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						view.setTextColor((Integer) animation.getAnimatedValue());
					}
				});
				colorAnimator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(final Animator animation) {
						view.setTag(R.id.animating, false);
					}
				});
				colorAnimator.start();
			}
		});
	}

	public static void animateTextColor(final TextView view, final int endColor, final long duration) {
		animateTextColor(view, view.getCurrentTextColor(), endColor, duration);
	}

	public static void animateAlpha(final View view, final boolean visible) {
		animateAlpha(view, visible, view.getResources().getInteger(R.integer.default_animation_duration_short));
	}

	/**
	 * Animate view's alpha skipping current visibility-tag value
	 */
	public static void animateAlphaSkipTag(final View view, final boolean visible) {
		animateAlphaSkipTag(view, visible, null, view.getResources().getInteger(R.integer.default_animation_duration_short));
	}

	public static void animateAlpha(final View view, final boolean visible, long duration) {
		animateAlpha(view, visible, null, duration);
	}

	public static void animateAlpha(final View view, final boolean visible, final Runnable callback) {
		animateAlpha(view, visible, callback, view.getResources().getInteger(R.integer.default_animation_duration_short));
	}

	public static void animateAlpha(final View view, final boolean visible, final Runnable callback, long duration) {
		if(view == null) {
			return;
		}
		final Boolean tag = (Boolean) view.getTag();
		if(tag != null && tag == visible) {
			view.post(callback);
			return;
		}

		view.setAlpha(visible ? 0 : 1);
		if(visible) {
			ViewUtils.setVisibleGone(view, visible);
		}
		view.setTag(visible);
		view.animate().setDuration(duration).
				setInterpolator(new AccelerateDecelerateInterpolator()).
				    setListener(new OmnomAnimatorListenerAdapter(view, callback)).
				    alpha(visible ? 1 : 0).start();
	}

	/**
	 * Animate view's alpha and set visibility to {@link android.view.View#VISIBLE} or {@link android.view.View#GONE}
	 */
	public static void animateAlphaGone(final View view, final boolean visible) {
		if(view == null) {
			return;
		}
		final Boolean tag = (Boolean) view.getTag();
		if(tag != null && tag == visible) {
			// skip
			return;
		}
		view.setAlpha(visible ? 0 : 1);
		if(visible) {
			ViewUtils.setVisibleGone(view, visible);
		}
		view.setTag(visible);
		view.animate().setDuration(view.getResources().getInteger(R.integer.default_animation_duration_short)).
				setInterpolator(new AccelerateDecelerateInterpolator()).
				    setListener(new AnimatorListenerAdapter() {
					    @Override
					    public void onAnimationEnd(Animator animation) {
						    if(!visible) {
							    ViewUtils.setVisibleGone(view, false);
						    }
					    }

					    @Override
					    public void onAnimationCancel(final Animator animation) {
						    ViewUtils.setVisibleGone(view, visible);
					    }
				    }).alpha(visible ? 1 : 0).start();
	}

	/**
	 * Animate view's alpha skipping current visibility-tag value
	 */
	public static void animateAlphaSkipTag(final View view, final boolean visible, final Runnable callback, long duration) {
		if(view == null) {
			return;
		}
		view.setAlpha(visible ? 0 : 1);
		if(visible) {
			ViewUtils.setVisibleInvisible(view, visible);
		}
		view.setTag(visible);
		view.animate().setDuration(duration).
				setInterpolator(new AccelerateDecelerateInterpolator()).
				    setListener(new OmnomAnimatorListenerAdapter(view, callback)).
				    alpha(visible ? 1 : 0).start();
	}

	public static void translateUp(final Context context, final Iterable<View> views, final int translation, final Runnable endCallback) {
		final AnimationBuilder builder = AnimationBuilder.create(context, views, 0, -translation);
		prepareTranslation(views, endCallback, builder).start();
	}

	public static void translateUp(final Context context, final Iterable<View> views, final int translation, final Runnable endCallback,
	                               final long duration) {
		final AnimationBuilder builder = AnimationBuilder.create(context, views, 0, -translation);
		builder.setDuration(duration);
		prepareTranslation(views, endCallback, builder).start();
	}

	public static void translateDown(final Context context, final Iterable<View> views, final int translation,
	                                 final Runnable endCallback) {
		final AnimationBuilder builder = AnimationBuilder.create(context, views, -translation, 0);
		prepareTranslation(views, endCallback, builder).start();
	}

	public static ValueAnimator prepareTranslation(final Iterable<View> views, final Runnable endCallback, AnimationBuilder builder) {
		return builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation1) {
				for(View v : views) {
					v.setTranslationY((Integer) animation1.getAnimatedValue());
				}
			}
		}).onEnd(endCallback).build();
	}

	public static void scaleWidth(final View view, final int width, final Runnable updateCallback, final Runnable endCallback) {
		AnimationBuilder builder = AnimationBuilder.create(view.getContext(), view, view.getMeasuredWidth(), width);
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				view.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				view.requestLayout();
				if(updateCallback != null) {
					view.post(updateCallback);
				}
			}
		});
		if(endCallback != null) {
			builder.onEnd(endCallback);
		}
		builder.build().start();
	}

	public static void scaleWidth(final View view, final int width, final long duration,
	                              final Runnable endCallback) {
		AnimationBuilder builder = AnimationBuilder.create(view.getContext(), view, view.getMeasuredWidth(), width);
		builder.setDuration(duration);
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				view.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				view.requestLayout();
			}
		});
		if(endCallback != null) {
			builder.onEnd(endCallback);
		}
		builder.build().start();
	}

	public static void scaleHeight(final View view, int height) {
		AnimationBuilder.create(view.getContext(), view, view.getMeasuredHeight(), height).addListener(
				new AnimationBuilder.UpdateLisetener() {
					@Override
					public void invoke(ValueAnimator animation) {
						ViewUtils.setHeight(view, (Integer) animation.getAnimatedValue());
					}
				}).build().start();
	}

	public static void scaleHeight(final View view, int height, Runnable endCallback) {
		scaleHeight(view, height, endCallback, view.getResources().getInteger(R.integer.default_animation_duration_long));
	}

	public static void scaleHeight(final View view, int height, Runnable endCallback, final long duration) {
		final AnimationBuilder animationBuilder = AnimationBuilder.create(view.getContext(), view, view.getMeasuredHeight(), height)
		                                                          .addListener(new AnimationBuilder.UpdateLisetener() {
			                                                          @Override
			                                                          public void invoke(ValueAnimator animation) {
				                                                          ViewUtils.setHeight(view, (Integer) animation
						                                                          .getAnimatedValue());
			                                                          }
		                                                          });
		animationBuilder.setDuration(duration);
		animationBuilder.onEnd(endCallback).build().start();
	}

	public static void scaleHeight(final View view, int height, long duration) {
		AnimationBuilder builder = AnimationBuilder.create(view.getContext(), view, view.getMeasuredHeight(), height);
		builder.setDuration(duration);
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				ViewUtils.setHeight(view, (Integer) animation.getAnimatedValue());
			}
		}).build().start();
	}

	public static void scale(final View view, int size, Runnable endCallback) {
		scaleHeight(view, size);
		scaleWidth(view, size, null, endCallback);
	}

	@DebugLog
	public static void scale(final View view, int size, long duration) {
		scale(view, size, duration, new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	@DebugLog
	public static void scale(final View view, int size, long duration, Runnable endCallback) {
		scaleHeight(view, size, duration);
		scaleWidth(view, size, duration, endCallback);
	}

	public static void animateBlinking(final View view) {
		final Animation animation = new AlphaAnimation(1, 0);
		animation.setDuration(view.getResources().getInteger(R.integer.default_animation_duration_long));
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.REVERSE);
		view.startAnimation(animation);
	}

	/**
	 * Performs drawable transition using alpha.
	 *
	 * @param backView  view from behind
	 * @param frontView front view
	 * @param drawable  drawable to animate
	 * @param duration  animation duration
	 */
	public static void animateDrawable(final View backView, final View frontView, final BitmapDrawable drawable, final int duration) {
		drawable.setAlpha(0);
		AndroidUtils.setBackground(frontView, drawable);
		ValueAnimator va = ValueAnimator.ofInt(0, 255);
		va.setDuration(duration);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				drawable.setAlpha((Integer) animation.getAnimatedValue());
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				AndroidUtils.setBackground(backView, null);
			}
		});
		va.start();
	}

	public static void smoothScrollToPositionFromTop(final AbsListView view, final int position, final int duration, final Runnable
			callback) {
		View child = getChildAtPosition(view, position);
		// There's no need to scroll if child is already at top or view is already scrolled to its end
		if((child != null) && ((child.getTop() == 0) || ((child.getTop() > 0) && !view.canScrollVertically(1)))) {
			if(callback != null) {
				view.post(callback);
			}
			return;
		}

		view.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(final AbsListView view, final int scrollState) {
				if(scrollState == SCROLL_STATE_IDLE) {
					view.setOnScrollListener(null);

					// Fix for scrolling bug
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							view.setSelection(position);
							if(callback != null) {
								view.post(callback);
							}
						}
					});
				}
			}

			@Override
			public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
			                     final int totalItemCount) { }
		});

		// Perform scrolling to position
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				view.smoothScrollToPositionFromTop(position, 0, duration);
			}
		});
	}

	private static View getChildAtPosition(final AdapterView view, final int position) {
		final int index = position - view.getFirstVisiblePosition();
		if((index >= 0) && (index < view.getChildCount())) {
			return view.getChildAt(index);
		} else {
			return null;
		}
	}

	public static void translationY(final int value, final View... views) {
		if(views == null || views.length < 1) {
			return;
		}
		for(final View view : views) {
			view.animate().translationY(value).setListener(new OmnomAnimatorListenerAdapter(view, null)).start();
		}
	}
}
