package com.omnom.android.linker.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.activity.bind.BindActivity;
import com.omnom.android.linker.activity.restaurant.RestaurantsListActivity;
import com.omnom.android.linker.api.Protocol;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.auth.AuthServiceException;
import com.omnom.android.linker.model.auth.LoginResponse;
import com.omnom.android.linker.model.auth.UserProfile;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantsResponse;
import com.omnom.android.linker.observable.BaseErrorHandler;
import com.omnom.android.linker.observable.OmnomObservable;
import com.omnom.android.linker.observable.ValidationObservable;
import com.omnom.android.linker.utils.StringUtils;
import com.omnom.android.linker.utils.UserDataHolder;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.loader.LoaderView;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.omnom.android.linker.utils.AndroidUtils.showToastLong;

public class ValidationActivity extends BaseActivity {
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

	private BaseErrorHandler onError = new BaseErrorHandler(this) {
		@Override
		protected void onThrowable(Throwable throwable) {
			loader.stopProgressAnimation(true);
			if(throwable instanceof RetrofitError) {
				final RetrofitError cause = (RetrofitError) throwable;
				if(cause.getResponse() != null) {
					// TODO: Refactor this ugly piece of ... code
					if(cause.getUrl().contains(Protocol.FIELD_LOGIN) && cause.getResponse().getStatus() != 200) {
						LoginActivity.start(getActivity(), mDataHolder, EXTRA_ERROR_WRONG_USERNAME);
						return;
					}
				}
			}
			if(throwable instanceof AuthServiceException) {
				final AuthServiceException authException = (AuthServiceException) throwable;
				getPreferences().setAuthToken(getActivity(), StringUtils.EMPTY_STRING);
				LoginActivity.start(getActivity(), mDataHolder, authException.getCode());
				return;
			}
			showToastLong(getActivity(), R.string.error_unknown_server_error);
			finish();
		}
	};
	@Nullable
	private RestaurantsResponse mRestaurants = null;

	private Subscription mErrValidationSubscription;
	private Subscription mAuthDataSubscription;
	private ErrorHelper mErrorHelper;
	private int mAnimationType;

	private boolean mFirstRun = true;
	private boolean mAnimationFinished = false;
	private boolean mDataLoaded = false;
	private Subscription mRestaurantsSubscription;

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
		postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
			@Override
			public void run() {
				validate();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		loader.animateLogoFast(R.drawable.ic_fork_n_knife);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loader.onDestroy();
		OmnomObservable.unsubscribe(mErrValidationSubscription);
		OmnomObservable.unsubscribe(mAuthDataSubscription);
		OmnomObservable.unsubscribe(mRestaurantsSubscription);
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
				final int dpSize = getResources().getDimensionPixelSize(R.dimen.loader_size_huge);
				loader.setSize(dpSize, dpSize);
			} else {
				loader.setSize(0, 0);
			}
		}
	}

	private void validate() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		loader.animateLogoFast(R.drawable.ic_fork_n_knife);
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
		loader.startProgressAnimation(getResources().getInteger(R.integer.validation_duration), new Runnable() {
			@Override
			public void run() {
				onAnimationEnd();
			}
		});
		mErrValidationSubscription = AndroidObservable
				.bindActivity(this, ValidationObservable.validate(this)
				                                        .map(OmnomObservable.getValidationFunc(
						                                             this,
						                                             mErrorHelper,
						                                             new View.OnClickListener() {
							                                             @Override
							                                             public void onClick(View v) {
								                                             validate();
							                                             }
						                                             })
				                                            ).isEmpty())
				.subscribe(
						new Action1<Boolean>() {
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
				startNextActivity();
			}
		});
	}

	private void startNextActivity() {
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
			loader.animateColor(Color.WHITE, getResources().getInteger(R.integer.default_animation_duration_long));
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

	private void authenticateAndGetData() {
		final String token = getPreferences().getAuthToken(this);
		onError.setDataHolder(UserDataHolder.create(mUsername, mPassword));
		if(TextUtils.isEmpty(token)) {
			mAuthDataSubscription = AndroidObservable
					.bindActivity(this, api.authenticate(mUsername, mPassword).flatMap(
							new Func1<LoginResponse, Observable<RestaurantsResponse>>() {
								@Override
								public Observable<RestaurantsResponse> call(LoginResponse response) {
									if(response.isError()) {
										throw new AuthServiceException(EXTRA_ERROR_WRONG_USERNAME | EXTRA_ERROR_WRONG_PASSWORD,
										                               response.getError());
									}
									final String token = response.getToken();
									getPreferences().setAuthToken(getActivity(), token);
									return Observable.combineLatest(api.getRestaurants(),
									                                api.getUserProfile(token),
									                                new Func2<RestaurantsResponse, UserProfile, RestaurantsResponse>() {
										                                @Override
										                                public RestaurantsResponse call(RestaurantsResponse restaurants,
										                                                                UserProfile profile) {
											                                LinkerApplication.get(getActivity()).cacheUserProfile(profile);
											                                return restaurants;
										                                }
									                                });
								}
							})).subscribe(new Action1<RestaurantsResponse>() {
						@Override
						public void call(RestaurantsResponse result) {
							onRestaurantsLoaded(result);
						}
					}, onError);
		} else {
			mRestaurantsSubscription = AndroidObservable.bindActivity(this, api.getUserProfile(token))
			                                            .flatMap(new Func1<UserProfile, Observable<RestaurantsResponse>>() {
				                                            @Override
				                                            public Observable<RestaurantsResponse> call(UserProfile userProfile) {
					                                            if(userProfile.isError()) {
						                                            throw new AuthServiceException(EXTRA_ERROR_AUTHTOKEN_EXPIRED,
						                                                                           userProfile.getError());
					                                            }
					                                            return api.getRestaurants();
				                                            }
			                                            })
			                                            .subscribe(new Action1<RestaurantsResponse>() {
				                                            @Override
				                                            public void call(RestaurantsResponse response) {
					                                            onRestaurantsLoaded(response);
				                                            }
			                                            }, onError);
		}
	}

	private void onRestaurantsLoaded(RestaurantsResponse result) {
		mRestaurants = result;
		mDataLoaded = true;
		if(mAnimationFinished) {
			onTasksFinished();
		} else {
			loader.stopProgressAnimation();
			loader.updateProgressMax(new Runnable() {
				@Override
				public void run() {
					startNextActivity();
				}
			});
		}
	}
}
