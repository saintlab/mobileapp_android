package com.omnom.android.linker.widget.loader;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.omnom.android.linker.BuildConfig;
import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.AnimationUtils;

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

	private int loaderSize;
	private int currentColor = -1;

	private List<View> translationViews = new LinkedList<View>();
	private int mSpeedUpLimit;

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

	@DebugLog
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
	}

	@DebugLog
	private int getDefaultBgColor() {return getContext().getResources().getColor(R.color.loader_bg);}

	@DebugLog
	public void animateColor(int endColor) {
		animateColor(currentColor, endColor, AnimationUtils.DURATION_SHORT);
	}

	@DebugLog
	public void animateColor(int endColor, long duration) {
		animateColor(currentColor, endColor, duration);
	}

	@DebugLog
	public void animateColorDefault() {
		animateColor(currentColor, getDefaultBgColor(), AnimationUtils.DURATION_SHORT);
	}

	@DebugLog
	public void animateColorDefault(long duration) {
		animateColor(currentColor, getDefaultBgColor(), duration);
	}

	@DebugLog
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

	@DebugLog
	public void setColor(final int color) {
		final GradientDrawable sd = (GradientDrawable) mImgLoader.getDrawable();
		sd.setColors(new int[]{color, color});
		currentColor = color;
		sd.invalidateSelf();
	}

	public void showProgress(final boolean visible) {
		AnimationUtils.animateAlpha(mProgressBar, visible);
	}

	@DebugLog
	public void scaleDown(final Runnable scaleDownUpdate) {
		scaleDown(scaleDownUpdate, null);
	}

	@DebugLog
	public void scaleDown(final Runnable scaleDownUpdate, final Runnable endAction) {
		AnimationUtils.scaleHeight(mImgLoader, loaderSize);
		AnimationUtils.scaleWidth(mImgLoader, loaderSize, scaleDownUpdate, endAction);
	}

	@DebugLog
	public void scaleDown() {
		setSize(loaderSize, loaderSize);
	}

	@DebugLog
	public void translateUp(final Runnable endCallback, final int translation) {
		AnimationUtils.translateUp(translationViews, translation, endCallback);
	}

	@DebugLog
	public void translateDown(final Runnable endCallback, final int translation) {
		AnimationUtils.translateDown(translationViews, translation, endCallback);
	}

	@DebugLog
	public void scaleUp(final Runnable endCallback) {
		AnimationUtils.scaleHeight(mImgLoader, mImgLoader.getMeasuredHeight() * 10);
		AnimationUtils.scaleWidth(mImgLoader, mImgLoader.getMeasuredWidth() * 10, null, endCallback);
	}

	@DebugLog
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

	@DebugLog
	public void setLogo(int resId) {
		final Object tag = mImgLogo.getTag(R.id.img_loader);
		if(tag != null && resId == (Integer) tag) {
			// skip
			return;
		}
		mImgLogo.setImageResource(resId);
		mImgLogo.setTag(R.id.img_loader, resId);
	}

	public void jumpProgress(float fraction) {
		if(BuildConfig.DEBUG) {
			if(fraction > 1 || fraction < 0) {
				throw new AssertionError("progess must be between 0 and 1");
			}
		}
		float value = Math.max(0, Math.min(1, fraction));
		speedUpProgress((int) (mProgressBar.getMax() * value));
	}

	private void speedUpProgress(int i) {
		mSpeedUpLimit = i;
	}

	public void addProgress(final int i) {
		int progress = mProgressBar.getProgress();
		if(mSpeedUpLimit > progress) {
			updateProgress(progress + (int) (i * 1.8));
		} else {
			updateProgress(progress + i);
		}
	}

	public void updateProgress(final int progress) {
		mProgressBar.post(new Runnable() {
			@Override
			public void run() {
				showProgress(progress > 0 && progress < mProgressBar.getMax());
				mProgressBar.setProgress(progress);
			}
		});
	}

	@DebugLog
	public int getTableNumber() {
		return Integer.parseInt(mEditTableNumber.getText().toString());
	}

	public void setSize(int width, int height) {
		mImgLoader.getLayoutParams().width = width;
		mImgLoader.getLayoutParams().height = height;
		mImgLoader.requestLayout();
	}

	public int getProgress() {
		return mProgressBar.getProgress();
	}
}
