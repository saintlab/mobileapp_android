package com.omnom.android.linker.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.omnom.android.linker.utils.AnimationUtils.DURATION_LONG;

public class ValidationActivity extends Activity {

	@InjectView(R.id.img_loader)
	protected ImageView imgLoader;

	@InjectView(R.id.img_logo)
	protected ImageView imgLogo;

	@InjectView(R.id.progress)
	protected ProgressBar progressBar;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

	private int loaderSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validation);
		ButterKnife.inject(this);
		loaderSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			animateStart();
		}
	}

	public void onProgress(int progress) {
		if (progress == 0 || progress >= 100) {
			AnimationUtils.animateAlpha(progressBar, false);
		} else {
			AnimationUtils.animateAlpha(progressBar, true);
		}
		progressBar.setProgress(progress);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void animateStart() {
		AnimationUtils.animateAlpha(progressBar, true);
		final ValueAnimator widthAnimator = prepareIntValueAnimator(imgLoader.getMeasuredWidth(), loaderSize);
		final ValueAnimator heightAnimator = prepareIntValueAnimator(imgLoader.getMeasuredHeight(), loaderSize);
		widthAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				startValidation();
			}
		});

		heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgLoader.getLayoutParams().height = (Integer) animation.getAnimatedValue();
				imgLoader.requestLayout();
			}
		});
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgLoader.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				imgLoader.requestLayout();
			}
		});
		widthAnimator.start();
		heightAnimator.start();
	}

	private void startValidation() {
		ValidationAsyncTask task = new ValidationAsyncTask(this);
		task.execute();
	}

	private ValueAnimator prepareIntValueAnimator(int... values) {
		ValueAnimator widthAnimator = ValueAnimator.ofInt(values);
		widthAnimator.setDuration(DURATION_LONG);
		widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		return widthAnimator;
	}

	private void setError(String s) {
		txtError.setText(s);
	}

	private class ValidationAsyncTask extends AsyncTask<Void, Integer, Integer> {
		private final int ERROR_CODE_EMPTY = -1;
		private final int ERROR_CODE_NETWORK = 0;
		private final int ERROR_CODE_SERVER = 1;
		private ValidationActivity activity;

		public ValidationAsyncTask(ValidationActivity activity) {
			this.activity = activity;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			publishProgress(0);
			if (!AndroidUtils.hasConnection(activity)) {
				publishProgress(100);
				return ERROR_CODE_NETWORK;
			}
			publishProgress(100);
			return ERROR_CODE_EMPTY;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			activity.onProgress(values[0]);
		}

		@Override
		protected void onPostExecute(final Integer errorCode) {
			switch (errorCode) {
				case ERROR_CODE_NETWORK:
					activity.setError("Пожалуйста проверьте подключение к сети");
					break;

				default:
					activity.setError("OK");
					break;
			}
		}
	}
}
