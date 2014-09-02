package com.omnom.android.linker.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.activity.bind.BindActivity;
import com.omnom.android.linker.activity.restaurant.RestaurantsListActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.UserProfile;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantsResponse;
import com.omnom.android.linker.observable.BaseErrorHandler;
import com.omnom.android.linker.observable.OmnomObservable;
import com.omnom.android.linker.observable.ValidationObservable;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.UserDataHolder;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.loader.LoaderView;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import hugo.weaving.DebugLog;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.omnom.android.linker.utils.AndroidUtils.showToastLong;

public class ValidationActivity extends BaseActivity {
	public static final int DURATION_VALIDATION = 5000;
	public static final int LOADER_SIZE_HUGE = 900;
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

	private String mUsername = null;
	private String mPassword = null;

	@Nullable
	private RestaurantsResponse mRestaurants = null;

	private Subscription mErrValidationSubscription;
	private Subscription mAuthDataSubscription;
	private ErrorHelper mErrorHelper;
	private int mAnimationType;

	private boolean mFirstRun = true;
	private boolean mAnimationFinished = false;
	private boolean mDataLoaded = false;

	@Override
	protected void handleIntent(Intent intent) {
		mAnimationType = intent.getIntExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_UP);
	}

	@Override
	public void initUi() {
		mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
		mPassword = getIntent().getStringExtra(EXTRA_PASSWORD);
		mErrorHelper = new ErrorHelper(loader, txtError, btnSettings, errorViews);
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
	protected void onPause() {
		super.onPause();
		loader.animateLogo(R.drawable.ic_fork_n_knife);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loader.onDestroy();
		OmnomObservable.unsubscribe(mErrValidationSubscription);
		OmnomObservable.unsubscribe(mAuthDataSubscription);
	}

	@Override
	public void finish() {
		loader.onDestroy();
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
		if(mFirstRun) {
			if(mAnimationType == EXTRA_LOADER_ANIMATION_SCALE_DOWN) {
				final int dpSize = ViewUtils.dipToPixels(this, LOADER_SIZE_HUGE);
				loader.setSize(dpSize, dpSize);
			} else {
				loader.setSize(0, 0);
			}
		}
	}

	private void validate() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		loader.showProgress(false);
		if(mFirstRun) {
			loader.scaleDown(null, new Runnable() {
				@Override
				public void run() {
					startLoader();
				}
			});
		} else {
			startLoader();
		}
		mFirstRun = false;
	}

	private void startLoader() {
		loader.startProgressAnimation(DURATION_VALIDATION, new Runnable() {
			@Override
			public void run() {
				onAnimationEnd();
			}
		});
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

	@DebugLog
	private void onAnimationEnd() {
		mAnimationFinished = true;
		if(mDataLoaded) {
			onTasksFinished();
		}
	}

	private void onTasksFinished() {
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				if(mRestaurants == null) {
					showToastLong(loader, R.string.error_server_unavailable_please_try_again);
					finish();
					return;
				}
				final List<Restaurant> items = mRestaurants.getItems();
				int size = items.size();
				if(items.isEmpty()) {
					showToastLong(loader, R.string.error_no_restaurants_please_try_again_later);
					finish();
					return;
				}

				if(size == 1) {
					BindActivity.start(getActivity(), items.get(0), false);
					finish();
				} else {
					loader.animateColor(Color.WHITE, AnimationUtils.DURATION_LONG);
					loader.scaleUp(new Runnable() {
						@Override
						public void run() {
							ViewUtils.setVisible(panelBottom, false);
							RestaurantsListActivity.start(getActivity(), items);
							finish();
						}
					});
				}
			}
		});
	}

	private void authenticateAndGetData() {
		mAuthDataSubscription = AndroidObservable.bindActivity(this, api.authenticate(mUsername, mPassword).map(
				new Func1<String, Observable<RestaurantsResponse>>() {
					@Override
					public Observable<RestaurantsResponse> call(String s) {
						getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE).edit().putString(AUTH_TOKEN, s).commit();
						api.setAuthToken(s);
						return Observable.combineLatest(
								api.getRestaurants(),
								api.getUserProfile(s),
								new Func2<RestaurantsResponse, UserProfile,
										RestaurantsResponse>() {
									@Override
									public RestaurantsResponse call(RestaurantsResponse restaurants,
									                                UserProfile profile) {
										LinkerApplication.get(getActivity()).cacheUserProfile(profile);
										return restaurants;
									}
								});
					}
				})).subscribe(
				new Action1<Observable<RestaurantsResponse>>() {
					@Override
					public void call(Observable<RestaurantsResponse> restaurantsResultObservable) {
						restaurantsResultObservable.subscribe(new Action1<RestaurantsResponse>() {
							@Override
							public void call(RestaurantsResponse restaurantsResult) {
								mRestaurants = restaurantsResult;
								mDataLoaded = true;
								if(mAnimationFinished) {
									onTasksFinished();
								}
							}
						});
					}
				}, new BaseErrorHandler(this,
				                        UserDataHolder
						                        .create(mUsername, mPassword)) {
					@Override
					protected void onThrowable(Throwable throwable) {
						showToastLong(getActivity(), R.string.error_unknown_server_error);
						finish();
					}
				});
	}
}
