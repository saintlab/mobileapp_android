package com.omnom.android.linker.activity;

import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationBuilder;
import com.omnom.android.linker.utils.AnimationUtils;

import butterknife.InjectView;

public class ValidationActivity extends BaseActivity {

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
	public void initUi() {
		loaderSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validation;
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
		animateStart();
	}

	private void animateStart() {
		AnimationUtils.animateAlpha(progressBar, true);

		AnimationBuilder.create(imgLoader.getMeasuredWidth(), loaderSize).addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				imgLoader.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				imgLoader.requestLayout();
			}
		}).onEnd(new AnimationBuilder.Action() {
			@Override
			public void invoke() {
				new ValidationAsyncTask(ValidationActivity.this).execute();
			}
		}).build().start();

		AnimationBuilder.create(imgLoader.getMeasuredHeight(), loaderSize).addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				imgLoader.getLayoutParams().height = (Integer) animation.getAnimatedValue();
				imgLoader.requestLayout();
			}
		}).build().start();
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
