package com.omnom.android.linker.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.AnimationBuilder;
import com.omnom.android.linker.utils.AnimationUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.omnom.android.linker.utils.AnimationUtils.DURATION_LONG;

/**
 * Created by Ch3D on 30.07.2014.
 */
public class LoaderView extends FrameLayout {
	public static final int PROGRESS_MAX = 100;

	@InjectView(R.id.img_loader)
	protected ImageView mImgLoader;

	@InjectView(R.id.img_logo)
	protected ImageView mImgLogo;

	@InjectView(R.id.progress)
	protected ProgressBar mProgressBar;

	private int loaderSize;

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
		loaderSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
	}

	public void updateProgress(int progress) {
		mProgressBar.setProgress(progress);
	}

	public void showProgress(boolean visible) {
		AnimationUtils.animateAlpha(mProgressBar, visible);
	}

	public void scaleDown(final Callback translateEnded, final Callback scaleDownUpdate) {
		scaleDownHeight();
		scaleDownWidth(scaleDownUpdate);
		translateUp(translateEnded);
	}

	private void translateUp(final Callback endCallback) {
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
				endCallback.execute();
			}
		}).build().start();
	}

	private void scaleDownWidth(final Callback callback) {
		AnimationBuilder.create(mImgLoader.getMeasuredWidth(), loaderSize).
				addListener(new AnimationBuilder.UpdateLisetener() {
					@Override
					public void invoke(ValueAnimator animation) {
						mImgLoader.getLayoutParams().width = (Integer) animation.getAnimatedValue();
						mImgLoader.requestLayout();
						callback.execute();
					}
				}).build().start();
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

	public void scaleUp(final Callback callback) {
		scaleUpHeight();
		scaleUpWidth(callback);
	}

	private void scaleUpWidth(final Callback endCallback) {
		AnimationBuilder.create(mImgLoader.getMeasuredWidth(), mImgLoader.getMeasuredWidth() * 10)
				.addListener(new AnimationBuilder.UpdateLisetener() {
					@Override
					public void invoke(ValueAnimator animation) {
						mImgLoader.getLayoutParams().width = (Integer) animation.getAnimatedValue();
						mImgLoader.requestLayout();
					}
				}).onEnd(new AnimationBuilder.Action() {
			@Override
			public void invoke() {
				mProgressBar.setProgress(PROGRESS_MAX);
				showProgress(false);
				endCallback.execute();
			}
		}).build().start();
	}

	private void scaleUpHeight() {
		AnimationBuilder.create(mImgLoader.getMeasuredHeight(), mImgLoader.getMeasuredHeight() * 10)
				.addListener(new AnimationBuilder.UpdateLisetener() {
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
				updateProgress(DURATION_LONG);
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

	public interface Callback {
		public void execute();
	}
}