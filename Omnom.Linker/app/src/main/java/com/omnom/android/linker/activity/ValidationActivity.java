package com.omnom.android.linker.activity;

import android.app.ActivityOptions;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.omnom.android.linker.utils.AndroidUtils.showToast;

public class ValidationActivity extends BaseActivity {

	private static final String TAG = ValidationActivity.class.getSimpleName();
	private static final int REQUEST_CODE_ENABLE_BT = 100;

	@SuppressWarnings("UnusedDeclaration")
	public static void start(final Context context, Restaurant restaurant, int animation) {
		final Intent intent = new Intent(context, ValidationActivity.class);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_LOADER_ANIMATION, animation);
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

	private Subscription mErrValidationSubscription;
	private Subscription mAuthDataSubscription;
	private ErrorHelper mErrorHelper;
	private int mAnimationType;

	@Override
	protected void handleIntent(Intent intent) {
		mAnimationType = intent.getIntExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_UP);
	}

	@Override
	public void initUi() {
		mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
		mPassword = getIntent().getStringExtra(EXTRA_PASSWORD);
		cdt = AndroidUtils.createTimer(loader, new Runnable() {
			@Override
			public void run() {
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
		});
		mErrorHelper = new ErrorHelper(loader, txtError, btnSettings, errorViews, cdt);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validation;
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		postDelayed(350, new Runnable() {
			@Override
			public void run() {
				validate();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mErrValidationSubscription != null) {
			mErrValidationSubscription.unsubscribe();
		}
		if(mAuthDataSubscription != null) {
			mAuthDataSubscription.unsubscribe();
		}
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

	@Override
	protected void onStart() {
		super.onStart();
		loader.setColor(getResources().getColor(R.color.loader_bg));
		if(mAnimationType == EXTRA_LOADER_ANIMATION_SCALE_DOWN) {
			final int dpSize = ViewUtils.dipToPixels(this, 900);
			loader.setSize(dpSize, dpSize);
		} else {
			loader.setSize(0, 0);
		}
	}

	private void validate() {
		loader.animateLogo(R.drawable.ic_fork_n_knife);
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		loader.showProgress(false);
		loader.scaleDown(null, new Runnable() {
			@Override
			public void run() {
				startLoader();
			}
		});
	}

	private void startLoader() {
		cdt.start();
		mErrValidationSubscription = AndroidObservable.bindActivity(this, ValidationObservable.validate(this).map(
				new Func1<ValidationObservable.Error, Boolean>() {
					@Override
					public Boolean call(ValidationObservable.Error error) {
						switch(error) {
							case BLUETOOTH_DISABLED:
								mErrorHelper.showErrorBluetoothDisabled(getActivity(), REQUEST_CODE_ENABLE_BT);
								break;

							case NO_CONNECTION:
								mErrorHelper.showInternetError(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										validate();
									}
								});
								break;

							case LOCATION_DISABLED:
								mErrorHelper.showLocationError();
								break;
						}
						return false;
					}
				}).isEmpty()).subscribe(new Action1<Boolean>() {
			@Override
			public void call(Boolean hasNoErrors) {
				loader.jumpProgress(0.4f);
				if(hasNoErrors) {
					authenticateAndGetData();
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				mErrorHelper.showInternetError(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						validate();
					}
				});
			}
		});
	}

	private void authenticateAndGetData() {
		mAuthDataSubscription = AndroidObservable.bindActivity(this, api.authenticate(mUsername, mPassword).map(
				new Func1<String, Observable<RestaurantsResult>>() {
					@Override
					public Observable<RestaurantsResult> call(String s) {
						getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE).edit().putString(AUTH_TOKEN, s).commit();
						api.setAuthToken(s);
						return api.getRestaurants();
					}
				})).subscribe(new Action1<Observable<RestaurantsResult>>() {
			@Override
			public void call(Observable<RestaurantsResult> restaurantsResultObservable) {
				restaurantsResultObservable.subscribe(new Action1<RestaurantsResult>() {
					@Override
					public void call(RestaurantsResult restaurantsResult) {
						mRestaurants = restaurantsResult;
					}
				});
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				cdt.cancel();
				loader.showProgress(false);
				showToast(ValidationActivity.this, R.string.msg_error);
				if(throwable instanceof AuthenticationException) {
					onAuthError(throwable);
				}
				Log.e(TAG, "authenticate()", throwable);
			}
		});
	}

	private void onAuthError(Throwable e) {
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(EXTRA_ERROR_CODE, EXTRA_ERROR_WRONG_USERNAME);
		intent.putExtra(EXTRA_USERNAME, mUsername);
		intent.putExtra(EXTRA_PASSWORD, mPassword);
		startActivity(intent);
		finish();
	}
}
