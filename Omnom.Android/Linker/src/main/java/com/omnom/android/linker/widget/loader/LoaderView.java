package com.omnom.android.linker.widget.loader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.omnom.android.linker.R;
import com.omnom.android.linker.animation.BezierCubicInterpolation;
import com.omnom.util.utils.AnimationUtils;
import com.omnom.util.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
		mImgLogo.setTag(R.id.logo_url, null);
		AnimationUtils.animateAlpha(mImgLogo, false, new Runnable() {
			@Override
			public void run() {
				mImgLogo.setImageResource(resId);
				AnimationUtils.animateAlpha(mImgLogo, true);
			}
		});
	}

	public void animateLogoFast(final int resId) {
		animateLogo(resId, getResources().getInteger(R.integer.default_animation_duration_quick));
	}

	public void animateLogo(final int resId, final long duration) {
		final Object tag = mImgLogo.getTag(R.id.img_loader);
		if(tag != null && resId == (Integer) tag) {
			// skip
			return;
		}
		mImgLogo.setTag(R.id.img_loader, resId);
		mImgLogo.setTag(R.id.logo_url, null);
		AnimationUtils.animateAlpha(mImgLogo, false, new Runnable() {
			@Override
			public void run() {
				mImgLogo.setImageResource(resId);
				AnimationUtils.animateAlpha(mImgLogo, true, duration);
			}
		}, duration);
	}

	public void animateLogo2(final int resId) {
		mImgLogo.setTag(R.id.img_loader, resId);
		mImgLogo.setTag(R.id.logo_url, null);
		if(mImgLogo.getVisibility() == GONE || mImgLogo.getAlpha() == 0) {
			mImgLogo.setImageResource(resId);
			AnimationUtils.animateAlpha(mImgLogo, true);
		} else {
			AnimationUtils.animateAlpha(mImgLogo, false, new Runnable() {
				@Override
				public void run() {
					mImgLogo.setImageResource(resId);
					AnimationUtils.animateAlpha(mImgLogo, true);
				}
			});
		}
	}

	public void setLogo(int resId) {
		final Object tag = mImgLogo.getTag(R.id.img_loader);
		if(tag != null && resId == (Integer) tag) {
			// skip
			return;
		}
		mImgLogo.setImageResource(resId);
		mImgLogo.setTag(R.id.img_loader, resId);
		mImgLogo.setTag(R.id.logo_url, null);
	}

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
		showProgress(true, true);
		mProgressBar.setTag(R.id.canceled, false);
		mProgressAnimator.start();
		return mProgressAnimator;
	}

	public ValueAnimator updateProgressMax(final Runnable callback) {
		mProgressAnimator = ValueAnimator.ofInt(mProgressBar.getProgress(), mProgressBar.getMax());
		mProgressAnimator.setDuration(getResources().getInteger(R.integer.default_animation_duration_short));
		mProgressAnimator.setInterpolator(new AccelerateInterpolator());
		mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				updateProgress((Integer) animation.getAnimatedValue());
			}
		});
		mProgressAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
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

	public void animateLogo(final Bitmap bitmap) {
		animateLogo(bitmap, getResources().getInteger(R.integer.default_animation_duration_short));
	}

	public void animateLogo(final Bitmap bitmap, long duration) {
		mImgLogo.setTag(R.id.img_loader, 0);
		AnimationUtils.animateAlpha(mImgLogo, false, new Runnable() {
			@Override
			public void run() {
				mImgLogo.setImageBitmap(bitmap);
				AnimationUtils.animateAlpha(mImgLogo, true);
			}
		}, duration);
	}

	public void animateLogo(final String logo, final int placeholderResId, final long duration) {
		if(TextUtils.isEmpty(logo)) {
			return;
		}
		final Object tag = mImgLogo.getTag(R.id.logo_url);
		if(tag != null && tag.equals(logo)) {
			// skip
			return;
		}
		Picasso.with(getContext()).load(logo).placeholder(placeholderResId).into(new Target() {
			@Override
			public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
				mImgLogo.setTag(R.id.logo_url, logo);
				animateLogo(bitmap, duration);
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {
				mImgLogo.setTag(R.id.logo_url, null);
				animateLogo(placeholderResId, duration);
			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {

			}
		});
	}

	public void animateLogoFast(final String logo, int placeholder) {
		animateLogo(logo, placeholder, getResources().getInteger(R.integer.default_animation_duration_quick));
	}

	public void animateLogo(final String logo, int placeholder) {
		animateLogo(logo, placeholder, getResources().getInteger(R.integer.default_animation_duration_short));
	}
}