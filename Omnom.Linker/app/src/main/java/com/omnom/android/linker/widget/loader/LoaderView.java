package com.omnom.android.linker.widget.loader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.omnom.android.linker.R;
import com.omnom.android.linker.animation.BezierCubicInterpolation;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.ViewUtils;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 30.07.2014.
 */
public class LoaderView extends FrameLayout {

	public enum Mode {
		NONE, ENTER_DATA
	}

	@InjectView(R.id.img_loader)
	protected ImageView mImgLoader;

	@InjectView(R.id.img_logo)
	protected ImageView mImgLogo;

	@InjectView(R.id.progress)
	protected ProgressBar mProgressBar;

	@InjectView(R.id.edit_table_number)
	protected EditText mEditTableNumber;

	private ValueAnimator mProgressAnimator;
	private int loaderSize;
	private int currentColor = -1;
	private List<View> translationViews = new LinkedList<View>();
	private Interpolator interpolation;

	@SuppressWarnings("UnusedDeclaration")
	public LoaderView(Context context) {
		super(context);
		init();
	}

	@SuppressWarnings("UnusedDeclaration")
	public LoaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@SuppressWarnings("UnusedDeclaration")
	public LoaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_loader, this);
		ButterKnife.inject(this);
		currentColor = getDefaultBgColor();
		mImgLogo.setTag(R.id.img_loader, R.drawable.ic_fork_n_knife);
		loaderSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
		translationViews.add(mProgressBar);
		translationViews.add(mImgLoader);
		translationViews.add(mImgLogo);
		translationViews.add(mEditTableNumber);
		interpolation = new BezierCubicInterpolation(.53f, 1.25f, .61f, .89f);
		mEditTableNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() > 0) {
					mEditTableNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.loader_font_size_large));
				} else {
					mEditTableNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.loader_font_size_normal));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private int getDefaultBgColor() {
		return getContext().getResources().getColor(R.color.loader_bg);
	}

	public void animateColor(int endColor) {
		animateColor(currentColor, endColor, getResources().getInteger(R.integer.default_animation_duration_short));
	}

	public void animateColor(int endColor, long duration) {
		animateColor(currentColor, endColor, duration);
	}

	public void animateColorDefault() {
		animateColor(currentColor, getDefaultBgColor(), getResources().getInteger(R.integer.default_animation_duration_short));
	}

	public void animateColorDefault(long duration) {
		animateColor(currentColor, getDefaultBgColor(), duration);
	}

	public void animateColor(final int startColor, final int endColor, final long duration) {
		post(new Runnable() {
			@Override
			public void run() {
				ValueAnimator colorAnimator = ValueAnimator.ofInt(startColor, endColor);
				colorAnimator.setDuration(duration);
				colorAnimator.setEvaluator(new ArgbEvaluator());
				colorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
				colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						GradientDrawable sd = (GradientDrawable) mImgLoader.getDrawable();
						currentColor = (Integer) animation.getAnimatedValue();
						sd.setColors(new int[]{currentColor, currentColor});
						sd.invalidateSelf();
					}
				});
				colorAnimator.start();
			}
		});
	}

	public void setColor(final int color) {
		final GradientDrawable sd = (GradientDrawable) mImgLoader.getDrawable();
		sd.setColors(new int[]{color, color});
		currentColor = color;
		sd.invalidateSelf();
	}

	public void showProgress(final boolean visible) {
		showProgress(visible, false);
	}

	public void showProgress(final boolean visible, boolean animate) {
		if(animate) {
			AnimationUtils.animateAlpha(mProgressBar, visible);
		} else {
			ViewUtils.setVisible(mProgressBar, visible);
		}
	}

	public void scaleDown(final Runnable scaleDownUpdate) {
		scaleDown(scaleDownUpdate, null);
	}

	public void scaleDown(final Runnable scaleDownUpdate, final Runnable endAction) {
		AnimationUtils.scaleHeight(mImgLoader, loaderSize);
		AnimationUtils.scaleWidth(mImgLoader, loaderSize, scaleDownUpdate, endAction);
	}

	public void scaleDown(int size, final Runnable scaleDownUpdate, final Runnable endAction) {
		AnimationUtils.scaleHeight(mImgLoader, size);
		AnimationUtils.scaleWidth(mImgLoader, size, scaleDownUpdate, endAction);
	}

	public void scaleDown(int size, final long duration, final Runnable endAction) {
		AnimationUtils.scale(mImgLoader, size, duration, endAction);
	}

	public void scaleDown() {
		setSize(loaderSize, loaderSize);
	}

	public void translateUp(final Runnable endCallback, final int translation) {
		AnimationUtils.translateUp(translationViews, translation, endCallback);
	}

	public void translateDown(final Runnable endCallback, final int translation) {
		AnimationUtils.translateDown(translationViews, translation, endCallback);
	}

	public void scaleUp(final Runnable endCallback) {
		AnimationUtils.scale(mImgLoader,
		                     mImgLoader.getMeasuredWidth() * getResources().getInteger(R.integer.loader_scale_factor),
		                     endCallback);
	}

	public void animateLogo(final int resId) {
		final Object tag = mImgLogo.getTag(R.id.img_loader);
		if(tag != null && resId == (Integer) tag) {
			// skip
			return;
		}
		mImgLogo.setTag(R.id.img_loader, resId);
		AnimationUtils.animateAlpha(mImgLogo, false, new Runnable() {
			@Override
			public void run() {
				mImgLogo.setImageResource(resId);
				AnimationUtils.animateAlpha(mImgLogo, true);
			}
		});
	}

	public void setLogo(int resId) {
		final Object tag = mImgLogo.getTag(R.id.img_loader);
		if(tag != null && resId == (Integer) tag) {
			// skip
			return;
		}
		mImgLogo.setImageResource(resId);
		mImgLogo.setTag(R.id.img_loader, resId);
	}

	@DebugLog
	public void updateProgress(final int progress) {
		final boolean progressVisible = progress < mProgressBar.getMax();
		showProgress(progress > 0 && progressVisible, !progressVisible);
		mProgressBar.setProgress(progress);
	}

	public int getTableNumber() {
		return Integer.parseInt(mEditTableNumber.getText().toString());
	}

	public void setSize(int width, int height) {
		mImgLoader.getLayoutParams().width = width;
		mImgLoader.getLayoutParams().height = height;
		mImgLoader.requestLayout();
	}

	@DebugLog
	public void stopProgressAnimation() {
		stopProgressAnimation(false);
	}

	public void stopProgressAnimation(boolean hideProgress) {
		showProgress(!hideProgress, true);
		if(mProgressAnimator != null && mProgressAnimator.isRunning()) {
			mProgressBar.setTag(R.id.canceled, true);
			mProgressAnimator.cancel();
		}
	}

	public ValueAnimator startProgressAnimation(long duration, final Runnable callback) {
		final int limit = (mProgressBar.getMax() / 100) * getResources().getInteger(R.integer.loader_limit_percentage);
		mProgressAnimator = ObjectAnimator.ofInt(0, limit);
		mProgressAnimator.setInterpolator(interpolation);
		mProgressAnimator.setDuration(duration);
		mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				updateProgress((Integer) animation.getAnimatedValue());
			}
		});
		mProgressAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			@DebugLog
			public void onAnimationEnd(Animator animation) {
				final Object tag = mProgressBar.getTag(R.id.canceled);
				if(tag != null && (Boolean) tag) {
					// skip callback
					return;
				}
				if(callback != null) {
					callback.run();
				}
			}
		});
		mProgressBar.setTag(R.id.canceled, false);
		showProgress(true, true);
		mProgressAnimator.start();
		return mProgressAnimator;
	}

	public ValueAnimator updateProgressMax(final Runnable callback) {
		mProgressAnimator = ValueAnimator.ofInt(mProgressBar.getProgress(), mProgressBar.getMax());
		mProgressAnimator.setDuration(getResources().getInteger(R.integer.default_animation_duration_short));
		mProgressAnimator.setInterpolator(interpolation);
		mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				updateProgress((Integer) animation.getAnimatedValue());
			}
		});
		mProgressAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			@DebugLog
			public void onAnimationEnd(Animator animation) {
				final Object tag = mProgressBar.getTag(R.id.canceled);
				if(tag != null && (Boolean) tag) {
					// skip callback
					return;
				}
				if(callback != null) {
					callback.run();
				}
			}
		});
		mProgressAnimator.start();
		return mProgressAnimator;
	}

	public void onDestroy() {
		stopProgressAnimation(true);
	}

	public void hideLogo() {
		AnimationUtils.animateAlpha(mImgLogo, false);
	}

	public void showLogo() {
		AnimationUtils.animateAlpha(mImgLogo, true);
	}

	public void hideLogo(long duration) {
		AnimationUtils.animateAlpha(mImgLogo, false, duration);
	}

	public void showLogo(long duration) {
		AnimationUtils.animateAlpha(mImgLogo, true, duration);
	}

	public int getSize() {
		return mImgLoader.getLayoutParams().width;
	}
}