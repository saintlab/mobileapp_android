package com.saintlab.android.linker.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.auth.request.UserAuthLoginPassRequest;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.protocol.Protocol;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.restaurateur.model.table.RestaurateurObservable;
import com.omnom.android.utils.ErrorHelper;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.UserDataHolder;
import com.omnom.android.utils.utils.ViewUtils;
import com.saintlab.android.linker.LinkerApplication;
import com.saintlab.android.linker.R;
import com.saintlab.android.linker.activity.bind.BindActivity;
import com.saintlab.android.linker.activity.restaurant.RestaurantsListActivity;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.omnom.android.utils.utils.AndroidUtils.showToastLong;

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
	protected RestaurateurObservableApi api;

	@Inject
	protected AuthService mAuthenticator;

	private String mUsername = null;
	private String mPassword = null;

	private BaseErrorHandler onError = new LinkerBaseErrorHandler(this) {
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
				loader.scaleDown();
			} else {
				loader.setSize(0, 0);
			}
		}
	}

	private void validate() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		final int size = getResources().getDimensionPixelSize(R.dimen.loader_logo_size);
		loader.setSize(size, size);
		loader.animateLogoFast(com.omnom.android.utils.R.drawable.ic_fork_n_knife);
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
		mErrValidationSubscription = AppObservable
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
					ViewUtils.setVisibleGone(panelBottom, false);
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
			mAuthDataSubscription = AppObservable
					.bindActivity(this, mAuthenticator.authenticate(new UserAuthLoginPassRequest(mUsername, mPassword)).flatMap(
							new Func1<AuthResponse, Observable<RestaurantsResponse>>() {
								@Override
								public Observable<RestaurantsResponse> call(AuthResponse response) {
									if(response.hasError()) {
										throw new AuthServiceException(EXTRA_ERROR_WRONG_USERNAME | EXTRA_ERROR_WRONG_PASSWORD,
										                               response.getError());
									}
									final String token = response.getToken();
									getPreferences().setAuthToken(getActivity(), token);
									return Observable.combineLatest(getRestaurantsObservable(),
									                                mAuthenticator.getUser(token),
									                                new Func2<RestaurantsResponse, UserResponse, RestaurantsResponse>() {
										                                @Override
										                                public RestaurantsResponse call(RestaurantsResponse restaurants,
										                                                                UserResponse userResponse) {
											                                LinkerApplication.get(getActivity()).cacheUserProfile(
													                                new UserProfile(userResponse));
											                                return restaurants;
										                                }
									                                });
								}
							})).subscribe(new RestaurateurObservable.AuthAwareOnNext<RestaurantsResponse>(getActivity()) {
						@Override
						public void perform(RestaurantsResponse result) {
							onRestaurantsLoaded(result);
						}
					}, onError);
		} else {
			mRestaurantsSubscription = AppObservable.bindActivity(this, mAuthenticator.getUser(token))
			                                            .flatMap(new Func1<UserResponse, Observable<RestaurantsResponse>>() {
				                                            @Override
				                                            public Observable<RestaurantsResponse> call(UserResponse userProfile) {
					                                            if(userProfile.hasError()) {
						                                            throw new AuthServiceException(EXTRA_ERROR_AUTHTOKEN_EXPIRED,
						                                                                           userProfile.getError());
					                                            }
					                                            return getRestaurantsObservable();
				                                            }
			                                            })
			                                            .subscribe(new RestaurateurObservable.AuthAwareOnNext<RestaurantsResponse>(getActivity
					                                                                                                                ()) {
				                                            @Override
				                                            public void perform(RestaurantsResponse response) {
					                                            onRestaurantsLoaded(response);
				                                            }
			                                            }, onError);
		}
	}

	private Observable<RestaurantsResponse> getRestaurantsObservable() {
		return api.getRestaurantsAll();
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
