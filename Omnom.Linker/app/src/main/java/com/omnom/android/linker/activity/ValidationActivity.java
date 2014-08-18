package com.omnom.android.linker.activity;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.activity.base.ValidationObservable;
import com.omnom.android.linker.activity.bind.BindActivity;
import com.omnom.android.linker.activity.restaurant.RestaurantsListActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsResult;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.loader.LoaderView;

import org.apache.http.auth.AuthenticationException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;

import altbeacon.beacon.BleNotAvailableException;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.omnom.android.linker.utils.AndroidUtils.showToast;

public class ValidationActivity extends BaseActivity /*implements Observer<String>*/ {

	private static final String TAG = ValidationActivity.class.getSimpleName();

	private static final int REQUEST_CODE_ENABLE_BT = 100;

	@SuppressWarnings("UnusedDeclaration")
	public static void start(final Context context, Restaurant restaurant) {
		final Intent intent = new Intent(context, ValidationActivity.class);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fake_fade_out).toBundle());
	}

	@InjectView(R.id.loader)
	protected LoaderView loader;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

	@InjectView(R.id.btn_bottom)
	protected Button btnSettings;

	@InjectViews({R.id.txt_error, R.id.panel_bottom})
	protected List<View> errorViews;

	@InjectView(R.id.panel_bottom)
	protected View panelBottom;

	@Inject
	protected LinkerObeservableApi api;

	private CountDownTimer cdt;
	private String mUsername = null;
	private String mPassword = null;

	@Nullable
	private RestaurantsResult mRestaurants = null;

	@Override
	public void initUi() {
		mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
		mPassword = getIntent().getStringExtra(EXTRA_PASSWORD);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validation;
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		validate();
	}

	@Override
	public void finish() {
		cdt.cancel();
		if(mRestaurants == null) {
			super.finish();
			return;
		}
		if(mRestaurants.getItems().size() == 1) {
			super.finish();
			overridePendingTransition(android.R.anim.fade_in, R.anim.fake_fade_out);
		} else {
			loader.animateColor(Color.WHITE);
			loader.scaleUp(new Runnable() {
				@Override
				public void run() {
					ValidationActivity.super.finish();
					overridePendingTransition(android.R.anim.fade_in, R.anim.fake_fade_out);
				}
			});
		}
	}

	private void validate() {
		loader.animateColor(getResources().getColor(R.color.loader_bg));
		loader.animateLogo(R.drawable.ic_fork_n_knife);
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		initCountDownTimer();
		loader.showProgress(false);
		loader.scaleDown(null, new Runnable() {
			@Override
			public void run() {
				startLoader();
			}
		});

		/*final LoaderController loaderController = new LoaderController(this, loader);
		new LoaderObservable.Builder(this).addTask(new LoaderTask(this, 500, loaderController) {
			@Override
			protected boolean runTask() {
				SystemClock.sleep(1000);
				return AndroidUtils.hasConnection(getActivity());
			}
		}).build().all(new Func1<LoaderTask.Result, Boolean>() {
			@Override
			public Boolean call(LoaderTask.Result result) {
				return result.isOk();
			}
		}).onErrorReturn(new Func1<Throwable, Boolean>() {
			@Override
			public Boolean call(Throwable throwable) {
				return false;
			}
		}).subscribe();*/
	}

	private void startLoader() {
		cdt.start();
		ValidationObservable.validate(this).all(new Func1<ValidationObservable.Error, Boolean>() {
			@Override
			public Boolean call(ValidationObservable.Error o) {
				if(o == ValidationObservable.Error.OK) {
					loader.jumpProgress(.5f);
				}
				return true;
			}
		}).onErrorReturn(new Func1<Throwable, Boolean>() {
			@Override
			public Boolean call(Throwable throwable) {
				return showError(throwable);
			}
		}).subscribe(new Action1<Boolean>() {
			@Override
			public void call(Boolean valid) {
				if(valid) {
					authenticateAndGetData();
				}
			}
		});
	}

	private boolean showError(Throwable throwable) {
		if(throwable instanceof BleNotAvailableException) {
			showErrorBluetoothDisabled();
		}
		if(throwable instanceof LocationException) {
			showLocationError();
		}
		if(throwable instanceof ConnectionExecption) {
			showInternetError();
		}
		return false;
	}

	private void authenticateAndGetData() {
		api.authenticate(mUsername, mPassword).map(new Func1<String, Observable<RestaurantsResult>>() {
			@Override
			public Observable<RestaurantsResult> call(String s) {
				api.setAuthToken(s);
				return api.getRestaurants();
			}
		}).onErrorReturn(new Func1<Throwable, Observable<RestaurantsResult>>() {
			@Override
			public Observable<RestaurantsResult> call(Throwable throwable) {
				cdt.cancel();
				loader.showProgress(false);
				showToast(ValidationActivity.this, R.string.msg_error);
				Log.e(TAG, "validate()", throwable);
				if(throwable instanceof AuthenticationException) {
					onAuthError(throwable);
				}
				return Observable.empty();
			}
		}).subscribe(new Action1<Observable<RestaurantsResult>>() {
			@Override
			public void call(Observable<RestaurantsResult> restaurantsResultObservable) {
				restaurantsResultObservable.subscribe(new Observer<RestaurantsResult>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						cdt.cancel();
						Log.e(TAG, "getRestaurants()", e);
					}

					@Override
					public void onNext(RestaurantsResult restaurantsResult) {
						mRestaurants = restaurantsResult;
					}
				});
			}
		});
	}

	private void initCountDownTimer() {
		final int progressMax = getResources().getInteger(R.integer.loader_progress_max);
		final int timeMax = getResources().getInteger(R.integer.loader_time_max);
		final int tick = getResources().getInteger(R.integer.loader_tick_interval);
		final int ticksCount = timeMax / tick;
		final int magic = progressMax / ticksCount;

		cdt = new CountDownTimer(timeMax, tick) {
			@Override
			public void onTick(long millisUntilFinished) {
				loader.addProgress(magic * 2);
			}

			@Override
			public void onFinish() {
				loader.updateProgress(progressMax);
				if(mRestaurants == null) {
					// TODO: error happened
					return;
				}
				final List<Restaurant> items = mRestaurants.getItems();
				int size = items.size();
				if(items.isEmpty()) {
					return;
				}

				if(size == 1) {
					BindActivity.start(ValidationActivity.this, items.get(0), false);
					finish();
				} else {
					loader.animateColor(Color.WHITE, AnimationUtils.DURATION_LONG);
					loader.scaleUp(new Runnable() {
						@Override
						public void run() {
							ViewUtils.setVisible(panelBottom, false);
							RestaurantsListActivity.start(ValidationActivity.this, items);
							finish();
						}
					});
				}

			}
		};
	}

	private void onAuthError(Throwable e) {
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(EXTRA_ERROR_CODE, EXTRA_ERROR_WRONG_USERNAME);
		intent.putExtra(EXTRA_USERNAME, mUsername);
		intent.putExtra(EXTRA_PASSWORD, mPassword);
		startActivity(intent);
		finish();
	}

	private void showError(int logoResId, int errTextResId, int btnTextResId, View.OnClickListener onClickListener) {
		loader.updateProgress(0);
		loader.showProgress(false);
		cdt.cancel();
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, true);
		loader.animateLogo(logoResId);
		txtError.setText(errTextResId);
		btnSettings.setText(btnTextResId);
		btnSettings.setOnClickListener(onClickListener);
	}

	private void showInternetError() {
		showError(R.drawable.ic_no_connection, R.string.error_you_have_no_internet_connection, R.string.try_once_again,
		          new View.OnClickListener() {
			          @Override
			          public void onClick(View v) {
				          validate();
			          }
		          });
	}

	private void showErrorBluetoothDisabled() {
		showError(R.drawable.ic_bluetooth_white, R.string.error_bluetooth_disabled, R.string.open_settings, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loader.scaleDown(null);
				ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
				startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_ENABLE_BT);
			}
		});
	}

	private void showLocationError() {
		showError(R.drawable.ic_geolocation_white, R.string.error_location_disabled, R.string.open_settings, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loader.scaleDown(null);
				ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
				AndroidUtils.startLocationSettings(v.getContext());
			}
		});
	}
}
