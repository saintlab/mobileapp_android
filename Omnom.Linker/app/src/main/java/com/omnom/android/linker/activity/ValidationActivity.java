package com.omnom.android.linker.activity;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationBuilder;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.widget.LoaderView;

import butterknife.InjectView;

public class ValidationActivity extends BaseActivity {

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
			publishProgress(48);
			SystemClock.sleep(2000);
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
					loader.scaleUp(new LoaderView.Callback() {
						@Override
						public void execute() {
							startActivity(PlacesListActivity.class, AnimationUtils.DURATION_LONG);
						}
					});
					break;
			}
		}
	}

	@InjectView(R.id.loader)
	protected LoaderView loader;
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
			loader.showProgress(false);
		} else {
			loader.showProgress(true);
		}
		loader.updateProgress(progress);
	}

	@Override
	protected void onResume() {
		super.onResume();
		animateStart();
	}

	private void animateStart() {
		loader.showProgress(true);
		loader.scaleDown(null, new AnimationBuilder.Action() {
			@Override
			public void invoke() {
				new ValidationAsyncTask(ValidationActivity.this).execute();
			}
		});
	}

	private void setError(String s) {
		txtError.setText(s);
	}
}
