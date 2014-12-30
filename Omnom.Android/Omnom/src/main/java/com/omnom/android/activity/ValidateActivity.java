package com.omnom.android.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.auth.UserData;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.OmnomErrorHelper;
import com.omnom.android.protocol.Protocol;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.WaiterCallResponse;
import com.omnom.android.restaurateur.model.config.Config;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrdersResponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.table.DemoTableData;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.socket.listener.PaymentEventListener;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.activity.BaseFragmentActivity;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.BluetoothUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 08.10.2014.
 */
public abstract class ValidateActivity extends BaseOmnomActivity {

	public static final int REQUEST_CODE_ORDERS = 100;

	/**
	 * Used when there is an active auth token during validation process
	 */
	public static final int TYPE_DEFAULT = -1;

	private static final String TAG = ValidateActivity.class.getSimpleName();

	protected BaseErrorHandler onError = new OmnomBaseErrorHandler(this) {
		@Override
		protected void onThrowable(Throwable throwable) {
			Log.e(TAG, throwable.getMessage());
			loader.stopProgressAnimation(true);
			if(throwable instanceof RetrofitError) {
				throwable.printStackTrace();
				final RetrofitError cause = (RetrofitError) throwable;
				if(cause.getResponse() != null) {
					// TODO: Refactor this ugly piece of ... code
					if(cause.getUrl().contains(Protocol.FIELD_LOGIN) && cause.getResponse().getStatus() != 200) {
						EnteringActivity.start(ValidateActivity.this, true);
						return;
					}
				}
			}
			if(throwable instanceof AuthServiceException) {
				getPreferences().setAuthToken(getActivity(), StringUtils.EMPTY_STRING);
				EnteringActivity.start(ValidateActivity.this, true);
				return;
			}
			throwable.printStackTrace();
			mErrorHelper.showBackendError(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clearErrors(true);
					startLoader();
				}
			});
		}
	};

	public static void startDemo(BaseActivity context, int enterAnim, int exitAnim, int animationType) {
		start(context, enterAnim, exitAnim, animationType, true, -1);
	}

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType, int userEnterType) {
		start(context, enterAnim, exitAnim, animationType, false, userEnterType);
	}

	private static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType, boolean isDemo,
	                          final int userEnterType) {
		Intent intent = createIntent(context, animationType, isDemo, userEnterType);
		if(context instanceof ConfirmPhoneActivity) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.start(intent, enterAnim, exitAnim, !isDemo);
	}

	public static void start(BaseFragmentActivity context, int enterAnim, int exitAnim, int animationType, final int userEnterType) {
		Intent intent = createIntent(context, animationType, false, userEnterType);
		context.start(intent, enterAnim, exitAnim, true);
	}

	private static Intent createIntent(Context context, int animationType, boolean isDemo, int userEnterType) {
		final boolean hasBle = BluetoothUtils.hasBleSupport(context);
		final Intent intent = new Intent(context, hasBle ? ValidateActivityBle.class : ValidateActivityCamera.class);
		intent.putExtra(EXTRA_LOADER_ANIMATION, animationType);
		intent.putExtra(EXTRA_DEMO_MODE, isDemo);
		intent.putExtra(EXTRA_CONFIRM_TYPE, userEnterType);
		return intent;
	}

	protected final View.OnClickListener mInternetErrorClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			startLoader();
		}
	};

	protected final View.OnClickListener mInternetErrorClickBillListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBill(findViewById(R.id.btn_bill));
		}
	};

	@InjectView(R.id.loader)
	protected LoaderView loader;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

	@InjectView(R.id.btn_bottom)
	protected View btnErrorRepeat;

	@InjectView(R.id.txt_bottom)
	protected TextView txtErrorRepeat;

	@InjectView(R.id.btn_demo)
	protected View btnDemo;

	@InjectView(R.id.btn_down)
	protected Button btnDownPromo;

	@InjectView(R.id.stub_bottom_menu)
	protected ViewStub stubBottomMenu;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.error_bg_layer)
	protected View errorBgView;

	@InjectViews({R.id.txt_error, R.id.panel_errors})
	protected List<View> errorViews;

	@InjectView(R.id.img_profile)
	protected ImageView imgProfile;

	@InjectView(R.id.txt_demo_leave)
	protected TextView txtLeave;

	@Inject
	protected RestaurateurObeservableApi api;

	protected OmnomErrorHelper mErrorHelper;

	protected Target mTarget;

	protected boolean mFirstRun = true;

	protected Restaurant mRestaurant;

	protected TableDataResponse mTable;

	protected boolean mIsDemo = false;

	protected Func1<Restaurant, Restaurant> mPreloadBackgroundFunction;

	/**
	 * ConfirmPhoneActivity.TYPE_LOGIN or ConfirmPhoneActivity.TYPE_REGISTER
	 */
	private int mType;

	private int mAnimationType;

	private boolean mWaiterCalled;

	private Subscription mOrdersSubscription;

	private Subscription mWaiterCallSubscribtion;

	private Subscription mUserSubscription;

	private Subscription mGuestSubscribtion;

	private Subscription mAcquiringConfigSubscribtion;

	private View bottomView;

	private com.omnom.android.utils.drawable.TransitionDrawable bgTransitionDrawable;

	private Picasso mPicasso;

	private PaymentEventListener mPaymentListener;

	@Override
	protected void handleIntent(Intent intent) {
		mAnimationType = intent.getIntExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
		mIsDemo = intent.getBooleanExtra(EXTRA_DEMO_MODE, false);
		mType = intent.getIntExtra(EXTRA_CONFIRM_TYPE, TYPE_DEFAULT);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		postDelayed(getResources().getInteger(R.integer.default_animation_duration_quick), new Runnable() {
			@Override
			public void run() {
				validate();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPaymentListener.onPause();
		if(mRestaurant == null) {
			loader.animateLogoFast(R.drawable.ic_fork_n_knife);
		}
	}

	@OnClick(R.id.txt_demo_leave)
	protected void onLeave() {
		onBackPressed();
	}

	protected void clearErrors(boolean animateLogo) {
		hideProfile();
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		if(animateLogo) {
			if(mRestaurant == null) {
				loader.animateLogo(R.drawable.ic_fork_n_knife);
			} else {
				loader.animateLogo(RestaurantHelper.getLogo(mRestaurant), R.drawable.ic_fork_n_knife);
			}
		}
		if(bgTransitionDrawable.isTransitioned()) {
			bgTransitionDrawable.reverseTransition();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loader.onDestroy();
		OmnomObservable.unsubscribe(mOrdersSubscription);
		OmnomObservable.unsubscribe(mWaiterCallSubscribtion);
		OmnomObservable.unsubscribe(mUserSubscription);
		OmnomObservable.unsubscribe(mGuestSubscribtion);
		OmnomObservable.unsubscribe(mAcquiringConfigSubscribtion);
		mPaymentListener.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mFirstRun) {
			if(mAnimationType == EXTRA_LOADER_ANIMATION_SCALE_DOWN) {
				loader.scaleDown();
			} else {
				loader.setSize(0, 0);
			}
		}
	}

	@Override
	public void initUi() {
		mPaymentListener = new PaymentEventListener(this);
		bgTransitionDrawable = new com.omnom.android.utils.drawable.TransitionDrawable(
				getResources().getInteger(R.integer.default_animation_duration_short),
				new Drawable[]{new ColorDrawable(getResources().getColor(R.color.transparent)),
						new ColorDrawable(getResources().getColor(R.color.error_bg_white_transparent))});

		bgTransitionDrawable.setCrossFadeEnabled(true);
		errorBgView.setBackgroundDrawable(bgTransitionDrawable);
		mPicasso = Picasso.with(getApplicationContext());
		btnDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				mErrorHelper.hideError();
				ValidateActivity.startDemo(ValidateActivity.this, R.anim.fake_fade_in_instant, R.anim.fake_fade_out_instant,
				                           EXTRA_LOADER_ANIMATION_SCALE_DOWN);
			}
		});
		mErrorHelper = new OmnomErrorHelper(loader, txtError, btnErrorRepeat, txtErrorRepeat, btnDemo, errorViews);

		mAcquiringConfigSubscribtion = AndroidObservable.bindActivity(this, api.getConfig()).subscribe(new Action1<Config>() {
			@Override
			public void call(Config config) {
				OmnomApplication.get(getActivity()).cacheConfig(config);
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			public void onError(Throwable throwable) {
				Log.w(TAG, "Unable to load config: " + throwable.getMessage());
			}
		});

		mPreloadBackgroundFunction = new Func1<Restaurant, Restaurant>() {
			@Override
			public Restaurant call(final Restaurant restaurant) {
				final String bgImgUrl = RestaurantHelper.getBackground(restaurant, getResources().getDisplayMetrics());
				if(!TextUtils.isEmpty(bgImgUrl)) {
					try {
						OmnomApplication.getPicasso(getActivity()).load(bgImgUrl).get();
					} catch(IOException e) {
						Log.e(TAG, "unable to load img = " + bgImgUrl);
					}
				}
				return restaurant;
			}
		};

		mTarget = new Target() {
			@Override
			public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
				final BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
				drawable.setAlpha(0);
				final View root = findViewById(R.id.root);
				root.setBackgroundDrawable(drawable);
				ValueAnimator va = ValueAnimator.ofInt(0, 255);
				va.setDuration(1000);
				va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						drawable.setAlpha((Integer) animation.getAnimatedValue());
					}
				});
				va.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						getWindow().getDecorView().setBackgroundDrawable(null);
					}
				});
				va.start();
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {
			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {
			}
		};
	}

	protected void validate() {
		if(mFirstRun || mRestaurant == null) {
			ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
			hideProfile();
			loader.animateLogoFast(R.drawable.ic_fork_n_knife);
			loader.showProgress(false);
			loader.scaleDown(null, new Runnable() {
				@Override
				public void run() {
					startLoader();
				}
			});
		}
		if(mTable != null) {
			mPaymentListener.initTableSocket(mTable);
		}
		mFirstRun = false;
	}

	protected abstract void startLoader();

	public void onBill(final View v) {
		v.setEnabled(false);
		hideProfile();
		ViewUtils.setVisible(getPanelBottom(), false);
		ViewUtils.setVisible(txtLeave, false);
		loader.setLogo(R.drawable.ic_bill_white_normal);
		loader.startProgressAnimation(10000, new Runnable() {
			@Override
			public void run() {
			}
		});
		ValidationObservable.validateSmart(this, mIsDemo)
		                    .map(OmnomObservable.getValidationFunc(this, mErrorHelper, mInternetErrorClickBillListener)).isEmpty()
		                    .subscribe(new Action1<Boolean>() {
			                    @Override
			                    public void call(final Boolean hasNoErrors) {
				                    if(hasNoErrors) {
					                    clearErrors(false);
					                    loadOrders(v);
				                    } else {
					                    startErrorTransition();
					                    getPanelBottom().animate().translationY(200).start();
					                    v.setEnabled(true);
				                    }
			                    }
		                    }, new Action1<Throwable>() {
			                    @Override
			                    public void call(final Throwable throwable) {
				                    startErrorTransition();
				                    mErrorHelper.showInternetError(mInternetErrorClickBillListener);
				                    v.setEnabled(true);
			                    }
		                    });
	}

	protected void startErrorTransition() {
		bgTransitionDrawable.startTransition();
	}

	private void loadOrders(final View v) {
		getPanelBottom().animate().translationY(0).start();
		mOrdersSubscription = AndroidObservable.bindActivity(getActivity(), api.getOrders(mTable.getRestaurantId(), mTable.getId()))
		                                       .subscribe(new Action1<OrdersResponse>() {
			                                       @Override
			                                       public void call(final OrdersResponse ordersResponse) {
				                                       loader.stopProgressAnimation();
				                                       loader.updateProgressMax(new Runnable() {
					                                       @Override
					                                       public void run() {
						                                       v.setEnabled(true);
						                                       if(!ordersResponse.getOrders().isEmpty()) {
							                                       showOrders(ordersResponse.getOrders(), ordersResponse.getRequestId());
						                                       } else {
							                                       startErrorTransition();
							                                       mErrorHelper.showNoOrders(new View.OnClickListener() {
								                                       @Override
								                                       public void onClick(View v) {
									                                       clearErrors(true);
									                                       loader.animateLogoFast(RestaurantHelper.getLogo(mRestaurant),
									                                                              R.drawable.ic_bill_white_normal);
									                                       loader.showProgress(false);
									                                       configureScreen(mRestaurant);
									                                       updateLightProfile(!mIsDemo);
									                                       ViewUtils.setVisible(txtLeave, mIsDemo);
									                                       ViewUtils.setVisible(getPanelBottom(), true);
								                                       }
							                                       });
						                                       }
					                                       }
				                                       });
			                                       }
		                                       }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			                                       @Override
			                                       public void onError(Throwable throwable) {
				                                       v.setEnabled(true);
			                                       }
		                                       });
	}

	protected void updateLightProfile(final boolean visible) {
		updateProfile(R.drawable.ic_profile_white, visible);
	}

	protected void updateDarkProfile(final boolean visible) {
		updateProfile(R.drawable.ic_profile, visible);
	}

	private void updateProfile(final int backgroundResource, final boolean visible) {
		imgProfile.setImageResource(backgroundResource);
		ViewUtils.setVisible(imgProfile, visible);
	}

	protected void hideProfile() {
		ViewUtils.setVisible(imgProfile, false);
	}

	private void showOrders(final List<Order> orders, final String requestId) {
		loader.hideLogo(new Runnable() {
			@Override
			public void run() {
				loader.scaleUp(getResources().getInteger(R.integer.default_animation_duration_medium), new Runnable() {
					@Override
					public void run() {
						OrdersActivity.start(ValidateActivity.this, new ArrayList<Order>(orders), requestId,
						                     mRestaurant.getDecoration().getBackgroundColor(), REQUEST_CODE_ORDERS, mIsDemo);
						if(orders.size() == 1) {
							overridePendingTransition(R.anim.slide_in_down_short, R.anim.nothing);
						}
					}
				});
			}
		});
	}

	public void onWaiter(final View v) {
		final Observable<WaiterCallResponse> observable;
		if(!mWaiterCalled) {
			observable = api.waiterCall(mRestaurant.getId(), mTable.getId());
		} else {
			observable = api.waiterCallStop(mRestaurant.getId(), mTable.getId());
		}
		mWaiterCallSubscribtion = AndroidObservable.bindActivity(this, observable).subscribe(new Action1<WaiterCallResponse>() {
			@Override
			public void call(WaiterCallResponse tableDataResponse) {
				if(tableDataResponse.isSuccess()) {
					mWaiterCalled = !mWaiterCalled;
				}
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			public void onError(Throwable throwable) {
				// TODO:
			}
		});
	}

	@OnClick(R.id.btn_down)
	public void onDownPressed(final View v) {
		final Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		final int height = rect.height();
		final Interpolator interpolator = new DecelerateInterpolator();
		final int duration = 700;
		// imgHolder.animate().translationY(-height).setDuration(duration).setInterpolator(interpolator).start();
		loader.animate().translationY(-height).setDuration(duration).setInterpolator(interpolator).start();
		AnimationUtils.animateAlpha(btnDownPromo, false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_ORDERS && resultCode == RESULT_OK) {
			if(mRestaurant != null) {
				// The following delay is required due to different behavior on different devices.
				// Some of them wait for activity transition animation to finish and then invoke onActivityResult,
				// others perform them in parallel.
				loader.postDelayed(new Runnable() {
					@Override
					public void run() {
						loader.scaleDown(null, new Runnable() {
							@Override
							public void run() {
								ViewUtils.setVisible(getPanelBottom(), true);
								updateLightProfile(!mIsDemo);
								ViewUtils.setVisible(txtLeave, mIsDemo);
								loader.animateLogo(RestaurantHelper.getLogo(mRestaurant), R.drawable.ic_fork_n_knife);
								loader.showLogo();
							}
						});
					}
				}, getResources().getInteger(R.integer.default_animation_duration_short));
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO: Fix when promo will be implemented
		//if(imgHolder.getTranslationY() != 0) {
		//	imgHolder.animate().translationY(0).start();
		//	loader.animate().translationY(0).start();
		//	AnimationUtils.animateAlpha(btnDownPromo, RestaurantHelper.isPromoEnabled(mRestaurant));
		//} else {
		super.onBackPressed();
		//}
	}

	@OnClick(R.id.img_profile)
	protected void onProfile(View v) {
		final int tableNumber = mTable != null ? mTable.getInternalId() : 0;
		final String tableId = mTable != null ? mTable.getId() : null;
		UserProfileActivity.startSliding(this, tableNumber, tableId);
	}

	protected final void onDataLoaded(final Restaurant restaurant, TableDataResponse table) {
		final OmnomApplication app = OmnomApplication.get(getActivity());

		mRestaurant = restaurant;
		mTable = table;

		mPaymentListener.initTableSocket(mTable);

		final String token = app.getAuthToken();
		mUserSubscription = AndroidObservable.bindActivity(this, authenticator.getUser(token)).subscribe(new Action1<UserResponse>() {
			@Override
			public void call(UserResponse userResponse) {
				correctMixpanelTime(userResponse.getTime() == null ? 0 : userResponse.getTime());
				app.cacheUserProfile(new UserProfile(userResponse));
				reportMixPanel(userResponse);
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			public void onError(Throwable throwable) {
				Log.w(TAG, throwable.getMessage());
			}
		});

		mGuestSubscribtion = AndroidObservable.bindActivity(this, api.newGuest(mTable.getRestaurantId(), mTable.getId()))
		                                      .subscribe(new Action1<ResponseBase>() {
			                                      @Override
			                                      public void call(ResponseBase responseBase) {

			                                      }
		                                      }, new Action1<Throwable>() {
			                                      @Override
			                                      public void call(Throwable throwable) {
				                                      Log.w(TAG, throwable.getMessage());
			                                      }
		                                      });

		loader.post(new Runnable() {
			@Override
			public void run() {
				loader.animateLogo(RestaurantHelper.getLogo(restaurant), R.drawable.ic_fork_n_knife,
				                   getResources().getInteger(R.integer.default_animation_duration_short));
			}
		});
		loader.animateColor(RestaurantHelper.getBackgroundColor(restaurant));
		OmnomApplication.getPicasso(this).load(RestaurantHelper.getBackground(restaurant, getResources().getDisplayMetrics()))
		                .into(mTarget);
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				configureScreen(mRestaurant);
				// ViewUtils.setVisible(imgHolder, true);
				updateLightProfile(!mIsDemo);
				ViewUtils.setVisible(txtLeave, mIsDemo);
				ViewUtils.setVisible(getPanelBottom(), true);
				getPanelBottom().animate().translationY(0).setInterpolator(new DecelerateInterpolator())
				                .setDuration(getResources().getInteger(R.integer.default_animation_duration_short)).start();
			}
		});
	}

	/**
	 * Report about user sign up or login
	 */
	private void reportMixPanel(UserResponse userResponse) {
		final UserData user = userResponse.getUser();

		switch(mType) {
			case ConfirmPhoneActivity.TYPE_LOGIN:
				getMixPanelHelper().trackUserLogin(MixPanelHelper.Project.ALL, this, user);
				break;

			case ConfirmPhoneActivity.TYPE_REGISTER:
				getMixPanelHelper().trackUserRegister(MixPanelHelper.Project.ALL, this, user);
				break;

			case TYPE_DEFAULT:
				getMixPanelHelper().trackUserDefault(MixPanelHelper.Project.ALL, this, user);
				break;
		}
	}

	private void configureScreen(final Restaurant restaurant) {
		// FIXME: uncomment the code below when promo is implemented
		final boolean promoEnabled = false; //RestaurantHelper.isPromoEnabled(restaurant);
		// FIXME: uncomment the code below when waiter call is implemented
		final boolean waiterEnabled = false; //RestaurantHelper.isWaiterEnabled(restaurant);

		if(bottomView == null) {
			stubBottomMenu.setLayoutResource(waiterEnabled ? R.layout.layout_bill_waiter : R.layout.layout_bill);
			bottomView = stubBottomMenu.inflate();
		}

		if(waiterEnabled) {
			final View btnWaiter = findById(bottomView, R.id.btn_waiter);
			btnWaiter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					onWaiter(v);
				}
			});
		}

		final View btnBill = findById(bottomView, R.id.btn_bill);
		btnBill.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				onBill(v);
			}
		});

		ViewUtils.setVisible(btnDownPromo, promoEnabled);
		// getPanelBottom().setTranslationY(100);
	}

	public View getPanelBottom() {
		return findById(this, R.id.panel_bottom);
	}

	protected boolean validateDemo() {
		if(mIsDemo) {
			if(mRestaurant == null || mTable == null) {
				loader.startProgressAnimation(10000, new Runnable() {
					@Override
					public void run() {
					}
				});
				api.getDemoTable().subscribe(new Action1<List<DemoTableData>>() {
					@Override
					public void call(final List<DemoTableData> response) {
						final DemoTableData data = response.get(0);
						onDataLoaded(data.getRestaurant(), data.getTable());
					}
				}, onError);
				mFirstRun = false;
			}
			return true;
		}
		return false;
	}
}
