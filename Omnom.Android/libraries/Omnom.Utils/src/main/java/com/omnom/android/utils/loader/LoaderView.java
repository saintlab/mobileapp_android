package com.omnom.android.utils.loader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.omnom.android.utils.R;
import com.omnom.android.utils.animation.BezierCubicInterpolation;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.LinkedList;
import java.util.List;

import hugo.weaving.DebugLog;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 30.07.2014.
 */
public class LoaderView extends FrameLayout {

	public enum Mode {
		NONE, ENTER_DATA
	}

	public static final double LOADER_WIDTH_SCALE = 0.6;

	public static final int WRONG_TABLE_NUMBER = -1;

	public static int getLoaderSizeDefault(Context context) {
		final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return (int) (displayMetrics.widthPixels * LOADER_WIDTH_SCALE + 0.5);
	}

	protected ImageView mImgLoader;

	protected ImageView mImgLogo;

	protected ProgressBar mProgressBar;

	protected EditText mEditTableNumber;

	private ValueAnimator mProgressAnimator;

	private int loaderSize;

	private int currentColor = getResources().getColor(R.color.loader_bg);

	private List<View> translationViews = new LinkedList<View>();

	private Interpolator interpolation;

	private int mProgressColor;

	private Target mTarget;

	private Transformation mScaleTransformation;

	private int mDefaultLoaderSize = -1;

	@SuppressWarnings("UnusedDeclaration")
	public LoaderView(Context context) {
		super(context);
		init(null);
	}

	@SuppressWarnings("UnusedDeclaration")
	public LoaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	@SuppressWarnings("UnusedDeclaration")
	public LoaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	@DebugLog
	private void init(AttributeSet attrs) {
		LayoutInflater.from(getContext()).inflate(R.layout.view_loader, this);

		if(attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LoaderView, 0, 0);
			final int color = getContext().getResources().getColor(android.R.color.white);
			mProgressColor = a.getColor(R.styleable.LoaderView_progress_color, color);
			a.recycle();
		}

		mImgLoader = findById(this, R.id.img_loader);
		mImgLogo = findById(this, R.id.img_logo);
		mProgressBar = findById(this, R.id.progress);
		setProgressColor(mProgressColor);

		mEditTableNumber = findById(this, R.id.edit_table_number);

		mScaleTransformation = new Transformation() {
			@Override
			public Bitmap transform(final Bitmap source) {
				int newWidth, newHeight;
				newHeight = Math.round(source.getHeight() * 0.5f);
				newWidth = Math.round(source.getWidth() * 0.5f);

				Bitmap result = Bitmap.createScaledBitmap(source, newWidth, newHeight, false);

				if(result != source) {
					source.recycle();
				}
				return result;
			}

			@Override
			public String key() {
				return "scale 0.5";
			}
		};

		loaderSize = getLoaderSizeDefault();
		updateProgressSize(loaderSize);

		currentColor = getDefaultBgColor();
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

	public int getLoaderSizeDefault() {
		if(mDefaultLoaderSize == -1) {
			mDefaultLoaderSize = getLoaderSizeDefault(getContext());
		}
		return mDefaultLoaderSize;
	}

	private void updateProgressSize(final int loaderSize) {
		final ViewGroup.LayoutParams layoutParams = mProgressBar.getLayoutParams();
		TypedValue outValue = new TypedValue();
		getResources().getValue(R.dimen.loader_progress_inner_radius, outValue, true);
		float innerRadiusRatio = outValue.getFloat();
		final int progressSize = (int) (loaderSize * innerRadiusRatio / 2 + 0.5) + ViewUtils.dipToPixels(getContext(), 1.0f);
		layoutParams.height = progressSize;
		layoutParams.width = progressSize;
	}

	private int getDefaultBgColor() {
		return getContext().getResources().getColor(R.color.loader_bg);
	}

	public void setBgColor(int color) {
		if(AndroidUtils.isJellyBean()) {
			setColor16(color);
		} else {
			setColor(color);
		}
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
				mImgLoader.getDrawable().mutate();

				final ValueAnimator colorAnimator = ValueAnimator.ofInt(startColor, endColor);
				colorAnimator.setDuration(duration);
				colorAnimator.setEvaluator(new ArgbEvaluator());
				colorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
				if(AndroidUtils.isJellyBean()) {
					colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							setColor16((Integer) animation.getAnimatedValue());
						}
					});
				} else {
					colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							setColor((Integer) animation.getAnimatedValue());
						}
					});
				}
				colorAnimator.start();
			}
		});
	}

	public void setColor(final int color) {
		ViewUtils.setDrawableColor((GradientDrawable) mImgLoader.getDrawable(), color);
		currentColor = color;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void setColor16(final int color) {
		final GradientDrawable sd = (GradientDrawable) mImgLoader.getDrawable();
		sd.setColors(new int[]{color, color});
		currentColor = color;
		sd.invalidateSelf();
	}

	public void setProgressColor(final int color) {
		final LayerDrawable progressDrawable = (LayerDrawable) mProgressBar.getProgressDrawable();
		if(progressDrawable != null) {
			final RotateDrawable rotateDrawable = (RotateDrawable) progressDrawable.findDrawableByLayerId(android.R.id.progress);
			ViewUtils.setDrawableColor((GradientDrawable) rotateDrawable.getDrawable(), color);
		}
	}

	public void showProgress(final boolean visible) {
		showProgress(visible, false);
	}

	public void showProgress(final boolean visible, boolean animate) {
		if(animate) {
			AnimationUtils.animateAlpha(mProgressBar, visible);
		} else {
			ViewUtils.setVisibleGone(mProgressBar, visible);
		}
	}

	public void showProgress(final boolean visible, boolean animate, Runnable callback) {
		if(animate) {
			AnimationUtils.animateAlpha(mProgressBar, visible, callback);
		} else {
			ViewUtils.setVisibleGone(mProgressBar, visible);
		}
	}

	public void scaleDown(final Runnable scaleDownUpdate) {
		scaleDown(scaleDownUpdate, null);
	}

	public void scaleDown(final Runnable scaleDownUpdate, final Runnable endAction) {
		scaleDown(loaderSize, scaleDownUpdate, endAction);
	}

	public void scaleDown(final long duration, final Runnable endAction) {
		scaleDown(loaderSize, duration, endAction);
	}

	public void scaleDown(int size, final Runnable scaleDownUpdate, final Runnable endAction) {
		AnimationUtils.scaleHeight(mImgLoader, size);
		AnimationUtils.scaleWidth(mImgLoader, size, scaleDownUpdate, endAction);
	}

	public void scaleDown(int size, final long duration, final Runnable endAction) {
		AnimationUtils.scale(mImgLoader, size, duration, endAction);
	}

	public void scaleDown(int size, final long duration, final boolean scaleLogo, final Runnable endAction) {
		AnimationUtils.scale(mImgLoader, size, duration, endAction);
		if(scaleLogo) {
			AnimationUtils.scale(mImgLogo, size, duration, new Runnable() {
				@Override
				public void run() {

				}
			});
		}
	}

	public void scaleDown() {
		setSize(loaderSize, loaderSize);
	}

	public void translateUp(final Runnable endCallback, final int translation) {
		AnimationUtils.translateUp(getContext(), translationViews, translation, endCallback);
	}

	public void translateDown(final Runnable endCallback, final int translation) {
		AnimationUtils.translateDown(getContext(), translationViews, translation, endCallback);
	}

	public void scaleUp(final long duration, final Runnable endCallback) {
		scaleUp(duration,
		        mImgLoader.getMeasuredWidth() * getResources().getInteger(R.integer.loader_scale_factor),
		        false, endCallback);
	}

	public void scaleUp(final long duration, final int size, final boolean scaleLogo, final Runnable endCallback) {
		AnimationUtils.scale(mImgLoader, size, duration, endCallback);
		if(scaleLogo) {
			AnimationUtils.scale(mImgLogo, size, duration, new Runnable() {
				@Override
				public void run() {

				}
			});
		}
	}

	public void scaleUp(final Runnable endCallback) {
		AnimationUtils.scale(mImgLoader,
		                     mImgLoader.getMeasuredWidth() * getResources().getInteger(R.integer.loader_scale_factor),
		                     endCallback);
	}

	public void scaleUp() {
		AnimationUtils.scale(mImgLoader,
		                     mImgLoader.getMeasuredWidth() * getResources().getInteger(R.integer.loader_scale_factor),
		                     null);
	}

	@DebugLog
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

	@DebugLog
	public void animateLogo(final int resId, final long duration) {
		final Object tag = mImgLogo.getTag(R.id.img_loader);
		if(tag != null && resId == (Integer) tag) {
			// skip
			return;
		}
		mImgLogo.setTag(R.id.img_loader, resId);
		mImgLogo.setTag(R.id.logo_url, null);
		if(duration == 0) {
			mImgLogo.setImageResource(resId);
		} else {
			AnimationUtils.animateAlpha(mImgLogo, false, new Runnable() {
				@Override
				public void run() {
					mImgLogo.setImageResource(resId);
					AnimationUtils.animateAlpha(mImgLogo, true, duration);
				}
			}, duration);
		}
	}

	@DebugLog
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

	@DebugLog
	public void setLogo(int resId) {
		mImgLogo.setTag(R.id.logo_url, null);
		final Object tag = mImgLogo.getTag(R.id.img_loader);
		if(tag != null && resId == (Integer) tag) {
			// skip
			return;
		}
		mImgLogo.setImageResource(resId);
		mImgLogo.setTag(R.id.img_loader, resId);
	}

	public void updateProgress(final int progress) {
		updateProgress(progress, true);
	}

	public void updateProgress(final int progress, boolean hideWhenDone) {
		final boolean progressVisible = progress < mProgressBar.getMax();
		if(hideWhenDone) {
			showProgress(progress > 0 && progressVisible, !progressVisible);
		}
		mProgressBar.setProgress(progress);
	}

	public int getTableNumber() {
		final String string = mEditTableNumber.getText().toString();
		if(TextUtils.isEmpty(string)) {
			return WRONG_TABLE_NUMBER;
		}
		final int value = Integer.parseInt(string);
		return value;
	}

	public void setSize(int width, int height) {
		setSize(mImgLoader, width, height);
		setSize(mImgLogo, width, height);
	}

	private void setSize(final ImageView imageView, final int width, final int height) {
		imageView.getLayoutParams().width = width;
		imageView.getLayoutParams().height = height;
		imageView.requestLayout();
	}

	public void resetMargins() {
		resetMargins(mImgLoader);
		resetMargins(mImgLogo);
	}

	private void resetMargins(final View view) {
		MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
		layoutParams.setMargins(0, 0, 0, 0);
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

	public ValueAnimator startProgressAnimation(long duration) {
		return startProgressAnimation(duration, null);
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
		return updateProgressMax(callback, true);
	}

	public ValueAnimator updateProgressMax(final Runnable callback, final boolean hideWhenDone) {
		mProgressAnimator = ValueAnimator.ofInt(mProgressBar.getProgress(), mProgressBar.getMax());
		mProgressAnimator.setDuration(getResources().getInteger(R.integer.default_animation_duration_medium));
		mProgressAnimator.setInterpolator(new AccelerateInterpolator());
		mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				updateProgress((Integer) animation.getAnimatedValue(), hideWhenDone);
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

	@DebugLog
	public void hideLogo() {
		AnimationUtils.animateAlpha(mImgLogo, false);
	}

	@DebugLog
	public void hideLogo(final Runnable callback) {
		AnimationUtils.animateAlpha(mImgLogo, false, callback);
	}

	@DebugLog
	public void showLogo() {
		AnimationUtils.animateAlpha(mImgLogo, true);
	}

	@DebugLog
	public void hideLogo(long duration) {
		AnimationUtils.animateAlpha(mImgLogo, false, duration);
	}

	@DebugLog
	public void showLogo(long duration) {
		AnimationUtils.animateAlpha(mImgLogo, true, duration);
	}

	public int getSize() {
		return mImgLoader.getLayoutParams().width;
	}

	public void animateLogo(final Bitmap bitmap) {
		animateLogo(bitmap, getResources().getInteger(R.integer.default_animation_duration_short));
	}

	@DebugLog
	public void animateLogo(final Bitmap bitmap, long duration) {
		mImgLogo.setTag(R.id.img_loader, 0);
		if(duration == 0) {
			mImgLogo.setImageBitmap(bitmap);
		} else {
			AnimationUtils.animateAlpha(mImgLogo, false, new Runnable() {
				@Override
				public void run() {
					mImgLogo.setImageBitmap(bitmap);
					AnimationUtils.animateAlpha(mImgLogo, true);
				}
			}, duration);
		}
	}

	@DebugLog
	public void animateLogo(final String logo, final int placeholderResId, final long duration) {
		if(TextUtils.isEmpty(logo)) {
			return;
		}
		final Object tag = mImgLogo.getTag(R.id.logo_url);
		if(tag != null && tag.equals(logo)) {
			// skip
			return;
		}

		RequestCreator requestCreator = Picasso.with(getContext())
		                                       .load(logo)
		                                       .placeholder(placeholderResId)
		                                       .resize(loaderSize, loaderSize)
		                                       .centerInside();
		mTarget = new Target() {
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
		};
		requestCreator.into(mTarget);
	}

	public void animateLogoFast(final String logo, int placeholder) {
		animateLogo(logo, placeholder, getResources().getInteger(R.integer.default_animation_duration_quick));
	}

	public void animateLogoInstant(final String logo, int placeholder) {
		animateLogo(logo, placeholder, 0);
	}

	public void animateLogo(final String logo, int placeholder) {
		animateLogo(logo, placeholder, getResources().getInteger(R.integer.default_animation_duration_short));
	}

	public void clearLogo() {
		mImgLogo.setImageBitmap(null);
	}

}