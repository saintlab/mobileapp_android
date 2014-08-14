package com.omnom.android.linker.widget.loader;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.AnimationBuilder;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.omnom.android.linker.utils.AnimationUtils.DURATION_LONG;

/**
 * Created by Ch3D on 30.07.2014.
 */
public class LoaderView extends FrameLayout {

	public enum Mode {
		NONE, ENTER_DATA
	}

	public interface Callback {
		public void execute();
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
	private Mode mCurrentMode = Mode.NONE;
	private int  currentColor = -1;

	public LoaderView(Context context) {
		super(context);
		init();
	}

	public LoaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

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

	public void updateProgress(final int progress) {
		if(progress == 0 || progress >= getResources().getInteger(R.integer.loader_progress_max)) {
			showProgress(false);
		} else {
			showProgress(true);
		}
		mProgressBar.post(new Runnable() {
			@Override
			public void run() {
				mProgressBar.setProgress(progress);
			}
		});
	}

	public void showProgress(boolean visible) {
		AnimationUtils.animateAlpha(mProgressBar, visible);
	}

	public void scaleDown(final Callback scaleDownUpdate) {
		scaleDown(scaleDownUpdate, null);
	}

	public void scaleDown(final Callback scaleDownUpdate, final AnimationBuilder.Action endAction) {
		scaleDownHeight();
		scaleDownWidth(scaleDownUpdate, endAction);
	}

	public void translateUp(final Callback endCallback) {
		final AnimationBuilder builder = AnimationBuilder.create(0, -loaderSize);
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation1) {
				mProgressBar.setTranslationY((Integer) animation1.getAnimatedValue());
				mImgLoader.setTranslationY((Integer) animation1.getAnimatedValue());
				mImgLogo.setTranslationY((Integer) animation1.getAnimatedValue());
			}
		}).onEnd(new AnimationBuilder.Action() {
			@Override
			public void invoke() {
				if(endCallback != null) {
					endCallback.execute();
				}
			}
		}).build().start();
	}

	private void scaleDownWidth(final Callback updateCallback, final AnimationBuilder.Action endCallback) {
		AnimationBuilder builder = AnimationBuilder.create(mImgLoader.getMeasuredWidth(), loaderSize);
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				mImgLoader.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				mImgLoader.requestLayout();
				if(updateCallback != null) {
					updateCallback.execute();
				}
			}
		});
		if(endCallback != null) {
			builder.onEnd(endCallback);
		}
		builder.build().start();
	}

	private void scaleDownHeight() {
		AnimationBuilder.create(mImgLoader.getMeasuredHeight(), loaderSize).addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				mImgLoader.getLayoutParams().height = (Integer) animation.getAnimatedValue();
				mImgLoader.requestLayout();
			}
		}).build().start();
	}

	public void scaleUp(final Callback endCallback) {
		scaleUpHeight();
		scaleUpWidth(endCallback);
	}

	private void scaleUpWidth(final Callback endCallback) {
		AnimationBuilder.create(mImgLoader.getMeasuredWidth(), mImgLoader.getMeasuredWidth() * 10).addListener(
				new AnimationBuilder.UpdateLisetener() {
					@Override
					public void invoke(ValueAnimator animation) {
						mImgLoader.getLayoutParams().width = (Integer) animation.getAnimatedValue();
						mImgLoader.requestLayout();
					}
				}).onEnd(new AnimationBuilder.Action() {
			@Override
			public void invoke() {
				showProgress(false);
				if(endCallback != null) {
					endCallback.execute();
				}
			}
		}).build().start();
	}

	private void scaleUpHeight() {
		AnimationBuilder.create(mImgLoader.getMeasuredHeight(), mImgLoader.getMeasuredHeight() * 10).addListener(
				new AnimationBuilder.UpdateLisetener() {
					@Override
					public void invoke(ValueAnimator animation) {
						mImgLoader.getLayoutParams().height = (Integer) animation.getAnimatedValue();
						mImgLoader.requestLayout();
					}
				}).build().start();
	}

	private void simulateLoading(final Callback callback) {
		new CountDownTimer(DURATION_LONG, 10) {
			@Override
			public void onTick(long millisUntilFinished) {
				updateProgress((int) ((DURATION_LONG - millisUntilFinished) / 10));
			}

			@Override
			public void onFinish() {
				updateProgress((int) DURATION_LONG);
				scaleUp(callback);
			}
		}.start();
	}

	public void performLogin(final Callback callback) {
		AnimationBuilder.create(-loaderSize, 0).addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				mProgressBar.setTranslationY((Integer) animation.getAnimatedValue());
				mImgLoader.setTranslationY((Integer) animation.getAnimatedValue());
				mImgLogo.setTranslationY((Integer) animation.getAnimatedValue());
			}
		}).onEnd(new AnimationBuilder.Action() {
			@Override
			public void invoke() {
				simulateLoading(callback);
			}
		}).build().start();
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

	public void jumpProgress(float v) {
		assert v <= 1;
		addProgress((int) (mProgressBar.getMax() * v));
	}

	protected void setMode(Mode m) {
		mCurrentMode = m;
		switch(mCurrentMode) {
			case ENTER_DATA:
				ViewUtils.setVisible(mEditTableNumber, true);
				break;

			case NONE:
				ViewUtils.setVisible(mEditTableNumber, false);
				break;
		}
	}

	public void addProgress(int i) {
		updateProgress(mProgressBar.getProgress() + i);
	}
}
