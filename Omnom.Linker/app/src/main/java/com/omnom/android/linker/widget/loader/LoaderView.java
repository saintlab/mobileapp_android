package com.omnom.android.linker.widget.loader;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Property;
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

/**
 * Created by Ch3D on 30.07.2014.
 */
public class LoaderView extends FrameLayout {

	public enum Mode {
		NONE, ENTER_DATA
	}

	public static final float PROGRESS_INCREMENT_FACTOR = 1.5f;
	public static final Property<View, Integer> PROPERTY_PROGRESS = new Property<View, Integer>(Integer.class, "progress") {
		@Override
		public void set(View object, Integer value) {
			((ProgressBar) object).setProgress(value);
		}

		@Override
		public Integer get(View object) {
			return ((ProgressBar) object).getProgress();
		}
	};
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
	private Interpolator interpolation;
	private int mInterEdge;

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
					mEditTableNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
				} else {
					mEditTableNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private int getDefaultBgColor() {return getContext().getResources().getColor(R.color.loader_bg);}

	public void animateColor(int endColor) {
		animateColor(currentColor, endColor, AnimationUtils.DURATION_SHORT);
	}

	public void animateColor(int endColor, long duration) {
		animateColor(currentColor, endColor, duration);
	}

	public void animateColorDefault() {
		animateColor(currentColor, getDefaultBgColor(), AnimationUtils.DURATION_SHORT);
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
		AnimationUtils.scaleHeight(mImgLoader, mImgLoader.getMeasuredHeight() * 10);
		AnimationUtils.scaleWidth(mImgLoader, mImgLoader.getMeasuredWidth() * 10, null, endCallback);
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

	public void addProgress(final int increment, final int real) {
		int progress = mProgressBar.getProgress();
		if(mSpeedUpLimit >= progress) {
			updateProgress(progress + (int) (increment * PROGRESS_INCREMENT_FACTOR), real);
		} else {
			updateProgress(progress + increment, real);
		}
	}

	public void updateProgress(final int progress, final int realProgress) {
		mProgressBar.post(new Runnable() {
			@Override
			public void run() {
				final int max = mProgressBar.getMax();
				showProgress(progress > 0 && progress < max, progress > max);
				// TODO: Improve
				int edge = max - (max / 4);
				final float endPeriod = max - edge;
				if(realProgress > edge && realProgress < max) {
					float i = realProgress - mInterEdge;
					float fraction = i / (max - mInterEdge);
					float interpolation1 = interpolation.getInterpolation(fraction);
					int value = (int) (interpolation1 * (realProgress - edge));
					mProgressBar.setProgress(mProgressBar.getProgress() + value);
				} else if(progress > edge && progress < max) {
					int progress1 = mProgressBar.getProgress() + 1;
					mProgressBar.setProgress(progress1);
					mInterEdge = progress1;
				} else if(progress < edge) {
					mProgressBar.setProgress(progress);
				}
			}
		});
	}

	public void updateProgress(final int progress) {
		mProgressBar.post(new Runnable() {
			@Override
			public void run() {
				final int max = mProgressBar.getMax();
				showProgress(progress > 0 && progress < max, progress >= max);
				final int edge = max - (max / 10);
				final float endPeriod = max - edge;
				if(progress > edge && progress < max) {
					float i = progress - edge;
					float fraction = i / endPeriod;
					float interpolation1 = interpolation.getInterpolation(fraction);
					int value = (int) (interpolation1 * (progress - edge));
					mProgressBar.setProgress(edge + value);
				} else {
					mProgressBar.setProgress(progress);
				}
			}
		});
	}

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

	public void startProgressAnimation(long duration) {
		mProgressBar.setVisibility(VISIBLE);
		final ObjectAnimator progressAnimator = ObjectAnimator.ofInt(mProgressBar, PROPERTY_PROGRESS, 0, mProgressBar.getMax());
		progressAnimator.setInterpolator(interpolation);
		progressAnimator.setDuration(duration);
		progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				updateProgress((Integer) animation.getAnimatedValue());
			}
		});
		progressAnimator.start();
	}
}