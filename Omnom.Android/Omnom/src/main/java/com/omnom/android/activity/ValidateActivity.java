package com.omnom.android.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.auth.UserData;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.fragment.NoOrdersFragment;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.OmnomErrorHelper;
import com.omnom.android.preferences.PreferenceHelper;
import com.omnom.android.protocol.Protocol;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.WaiterCallResponse;
import com.omnom.android.restaurateur.model.config.Config;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
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
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.BluetoothUtils;
import com.omnom.android.utils.utils.ClickSpan;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.UnknownHostException;
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
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 08.10.2014.
 */
public abstract class ValidateActivity extends BaseOmnomFragmentActivity {

	public static final int REQUEST_CODE_ORDERS = 100;

	/**
	 * Used when there is an active auth token during validation process
	 */
	public static final int TYPE_DEFAULT = -1;

	public static final String ACTION_LAUNCH_QR = "com.omnom.android.action.launch_qr";
	public static final String ACTION_LAUNCH_HASHCODE = "com.omnom.android.action.launch_hashcode";
	public static final String QR_URL_PATH_PREFIX = "/qr/";
	public static final String SCHEME_OMNOM = "omnom";
	public static final String QUERY_PARAMETER_HASH = "hash";

	private static final String TAG = ValidateActivity.class.getSimpleName();

	protected BaseErrorHandler onError = new OmnomBaseErrorHandler(this) {
		@Override
		protected void onThrowable(Throwable throwable) {
			Log.e(TAG, "onError", throwable);
			loader.stopProgressAnimation(true);
			if(throwable instanceof RetrofitError) {
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
				((OmnomApplication) getApplication()).logout();
				EnteringActivity.start(ValidateActivity.this, true);
				return;
			}

			mErrorHelper.showBackendError(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clearErrors(true);
					decode(true);
				}
			});
		}
	};

	public static void startDemo(BaseActivity context, int enterAnim, int exitAnim, int animationType) {
		start(context, enterAnim, exitAnim, animationType, true, -1);
	}

	public static void startDemo(BaseFragmentActivity context, int enterAnim, int exitAnim, int animationType) {
		start(context, enterAnim, exitAnim, animationType, true, -1);
	}

	public static void start(BaseFragmentActivity context, int enterAnim, int exitAnim, int animationType, int userEnterType) {
		start(context, enterAnim, exitAnim, animationType, false, userEnterType);
	}

	private static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType, boolean isDemo,
	                          final int userEnterType) {
		Intent intent = createIntent(context, animationType, isDemo, userEnterType, null);
		if(context instanceof ConfirmPhoneActivity) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		if(!isDemo) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		context.start(intent, enterAnim, exitAnim, !isDemo);
	}

	private static void start(BaseFragmentActivity context, int enterAnim, int exitAnim, int animationType, boolean isDemo,
	                          final int userEnterType) {
		Intent intent = createIntent(context, animationType, isDemo, userEnterType, null);
		if(!isDemo) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		context.start(intent, enterAnim, exitAnim, !isDemo);
	}

	public static void start(final BaseFragmentActivity context, final int enterAnim,
	                         final int exitAnim, final int animationType, final int userEnterType,
	                         final Uri data) {
		Intent intent = createIntent(context, animationType, false, userEnterType, data);
		context.start(intent, enterAnim, exitAnim, true);
	}

	private static Intent createIntent(final Context context, final int animationType,
	                                   final boolean isDemo, final int userEnterType, final Uri data) {
		final boolean hasBle = BluetoothUtils.hasBleSupport(context);
		final Class validateActivityBleClass = AndroidUtils.isLollipop() ? ValidateActivityBle21.class : ValidateActivityBle.class;
		final boolean isBleReadyDevice = hasBle & AndroidUtils.isJellyBeanMR2();

		final Intent intent = new Intent(context, isBleReadyDevice && data == null ? validateActivityBleClass : ValidateActivityCamera.class);

		intent.putExtra(EXTRA_LOADER_ANIMATION, animationType);
		intent.putExtra(EXTRA_DEMO_MODE, isDemo);
		intent.putExtra(EXTRA_CONFIRM_TYPE, userEnterType);
		if (data != null) {
			intent.setData(data);
			if (data.toString().contains(QR_URL_PATH_PREFIX)) {
				intent.setAction(ACTION_LAUNCH_QR);
			} else {
				intent.setAction(ACTION_LAUNCH_HASHCODE);
			}
		}
		return intent;
	}

	protected final View.OnClickListener mInternetErrorClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			decode(true);
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

	@InjectView(R.id.txt_error_additional)
	protected TextView txtErrorAdditional;

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

	@InjectView(R.id.content)
	protected View contentView;

	@InjectViews({R.id.txt_error, R.id.panel_errors})
	protected List<View> errorViews;

	@InjectView(R.id.img_profile)
	protected ImageView imgProfile;

	@InjectView(R.id.btn_previous)
	protected ImageView imgPrevious;

	@InjectView(R.id.txt_demo_leave)
	protected TextView txtLeave;

	@Inject
	protected RestaurateurObservableApi api;

	protected OmnomErrorHelper mErrorHelper;

	protected Target mTarget;

	protected boolean mFirstRun = true;

	@Nullable
	protected Restaurant mRestaurant;

	@Nullable
	protected TableDataResponse mTable;

	protected boolean mIsDemo = false;

	protected boolean mSkipViewRendering = false;

	protected Func1<RestaurantResponse, RestaurantResponse> mPreloadBackgroundFunction;

	/**
	 * ConfirmPhoneActivity.TYPE_LOGIN or ConfirmPhoneActivity.TYPE_REGISTER
	 */
	private int mType;

	protected Uri mData;

	private int mAnimationType;

	private boolean mWaiterCalled;

	private Subscription mOrdersSubscription;

	private Subscription mWaiterCallSubscribtion;

	private Subscription mDataSubscription;

	private View bottomView;

	private com.omnom.android.utils.drawable.TransitionDrawable bgTransitionDrawable;

	private PaymentEventListener mPaymentListener;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mData = getIntent().getData();
	}

	@Override
	protected void handleIntent(Intent intent) {
		mAnimationType = intent.getIntExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
		mIsDemo = intent.getBooleanExtra(EXTRA_DEMO_MODE, false);
		mSkipViewRendering = intent.getBooleanExtra(EXTRA_SKIP_VIEW_RENDERING, false);
		mType = intent.getIntExtra(EXTRA_CONFIRM_TYPE, TYPE_DEFAULT);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		startValidation();
	}

	private void startValidation() {
		if (!mSkipViewRendering) {
			postDelayed(getResources().getInteger(R.integer.default_animation_duration_quick), new Runnable() {
				@Override
				public void run() {
					validate();
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mPaymentListener != null && mTable != null) {
			mPaymentListener.initTableSocket(mTable);
		}
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

	public void clearErrors(boolean animateLogo) {
		hideProfile();
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		ViewUtils.setVisible(txtErrorAdditional, false);
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
		OmnomObservable.unsubscribe(mDataSubscription);
		mPaymentListener.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mFirstRun) {
			switch(mAnimationType) {
				case EXTRA_LOADER_ANIMATION_SCALE_DOWN:
				case EXTRA_LOADER_ANIMATION_FIXED:
					loader.scaleDown();
					break;

				case EXTRA_LOADER_ANIMATION_SCALE_UP:
					loader.setSize(0, 0);
					break;

				default:
					loader.setSize(0, 0);
					break;
			}
		}
	}

	private View.OnClickListener loadConfigsErrorListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			loadConfigs();
		}
	};

	@Override
	public void initUi() {
		loader.setLogo(R.drawable.ic_fork_n_knife);
		loader.setColor(getResources().getColor(R.color.loader_bg));
		mPaymentListener = new PaymentEventListener(this);
		bgTransitionDrawable = new com.omnom.android.utils.drawable.TransitionDrawable(
				getResources().getInteger(R.integer.default_animation_duration_short),
				new Drawable[]{new ColorDrawable(getResources().getColor(R.color.transparent)),
						new ColorDrawable(getResources().getColor(R.color.error_bg_white_transparent))});

		bgTransitionDrawable.setCrossFadeEnabled(true);
		contentView.setBackgroundDrawable(bgTransitionDrawable);

		imgPrevious.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				RestaurantsListActivity.startLeft(ValidateActivity.this);
			}
		});

		btnDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				mErrorHelper.hideError();
				ValidateActivity.startDemo(ValidateActivity.this, R.anim.fake_fade_in_instant, R.anim.fake_fade_out_instant,
				                           EXTRA_LOADER_ANIMATION_SCALE_DOWN);
			}
		});
		mErrorHelper = new OmnomErrorHelper(loader, txtError, btnErrorRepeat, txtErrorRepeat, btnDemo, errorViews);

		mPreloadBackgroundFunction = new Func1<RestaurantResponse, RestaurantResponse>() {
			@Override
			public RestaurantResponse call(final RestaurantResponse decodeResponse) {
				final List<Restaurant> restaurants = decodeResponse.getRestaurants();
				if(restaurants.size() == 1) {
					final Restaurant restaurant = restaurants.get(0);
					if(restaurant != null) {
						final String bgImgUrl = RestaurantHelper.getBackground(restaurant, getResources().getDisplayMetrics());
						if(!TextUtils.isEmpty(bgImgUrl)) {
							try {
								OmnomApplication.getPicasso(getActivity()).load(bgImgUrl).get();
							} catch(IOException e) {
								Log.e(TAG, "unable to load img = " + bgImgUrl);
							}
						}
					}
				}
				return decodeResponse;
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
				va.setDuration(getResources().getInteger(R.integer.default_animation_duration_long));
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

		if (mSkipViewRendering) {
			mFirstRun = false;
			decode(true);
		}
	}

	private void loadConfigs() {
		clearErrors(true);
		loader.startProgressAnimation(getResources().getInteger(R.integer.omnom_validate_duration), new Runnable() {
			@Override
			public void run() {
			}
		});
		mDataSubscription = AndroidObservable.bindActivity(this, getConfigsObservable()).subscribe(new Action1<Boolean>() {
			@Override
			public void call(Boolean success) {
				final ValidateActivity activity = ValidateActivity.this;
				if (!BluetoothUtils.hasBleSupport(activity) && !isExternalLaunch()) {
					loader.stopProgressAnimation();
					loader.updateProgressMax(new Runnable() {
						@Override
						public void run() {
							RestaurantsListActivity.start(activity,true);
						}
					});
				} else {
					decode(false);
				}
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			public void onError(Throwable throwable) {
				if (throwable.getCause() instanceof UnknownHostException) {
					mErrorHelper.showInternetError(loadConfigsErrorListener);
				} else {
					mErrorHelper.showUnknownError(loadConfigsErrorListener);
				}
				Log.e(TAG, "loadConfigs", throwable);
			}
		});
	}

	private Observable<Boolean> getConfigsObservable() {
		final OmnomApplication app = OmnomApplication.get(getActivity());
		return Observable.zip(authenticator.getUser(app.getAuthToken()), api.getConfig(),
				new Func2<UserResponse, Config, Boolean>() {
					@Override
					public Boolean call(UserResponse userResponse, Config config) {
						app.cacheConfig(config);
						app.cacheUserProfile(new UserProfile(userResponse));
						reportMixPanel(userResponse);
						return true;
					}
				}).subscribeOn(Schedulers.io());
	}

	/**
	 * @return <code>true</code> if app was launched by an extrenal qr/link
	 */
	protected boolean isExternalLaunch() {return mData != null;}

	protected void validate() {
		if(mFirstRun || mRestaurant == null) {
			ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
			hideProfile();
			loader.animateLogoFast(R.drawable.ic_fork_n_knife);
			loader.showProgress(false);
			loader.scaleDown(null, new Runnable() {
				@Override
				public void run() {
					loadConfigs();
				}
			});
		}
		if(mTable != null) {
			mPaymentListener.initTableSocket(mTable);
		}
		mFirstRun = false;
	}

	protected abstract void decode(boolean startProgressAnimation);

	public void onBill(final View v) {
		v.setEnabled(false);
		hideProfile();
		ViewUtils.setVisible(imgPrevious, false);
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
							                                       txtErrorAdditional.setText(getString(
									                                       R.string.there_are_no_orders_additional,
									                                       String.valueOf(mTable.getInternalId())));
							                                       AndroidUtils.clickify(txtErrorAdditional,
							                                                             getString(
									                                                             R.string
											                                                             .there_are_no_orders_additional_mark),
							                                                             new ClickSpan
									                                                             .OnClickListener() {
								                                                             @Override
								                                                             public void onClick() {
									                                                             showNoOrdersInfo();
								                                                             }
							                                                             });
							                                       ViewUtils.setVisible(txtErrorAdditional, true);
							                                       final int tableNumber = mTable != null ? mTable.getInternalId() : 0;
							                                       mErrorHelper.showNoOrders(new View.OnClickListener() {
								                                       @Override
								                                       public void onClick(View v) {
									                                       onErrorClose();
								                                       }
							                                       }, tableNumber);
							                                       txtError.setText(StringUtils.EMPTY_STRING);
						                                       }
					                                       }
				                                       });
			                                       }
		                                       }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			                                       @Override
			                                       public void onError(Throwable throwable) {
				                                       v.setEnabled(true);
				                                       startErrorTransition();
				                                       mErrorHelper.showUnknownError(new View.OnClickListener() {
					                                       @Override
					                                       public void onClick(View v) {
														        onErrorClose();
					                                       }
				                                       });
			                                       }
		                                       });
	}

	private void onErrorClose() {
		clearErrors(true);
		ViewUtils.setVisible(txtErrorAdditional, false);
		loader.animateLogoFast(RestaurantHelper.getLogo(mRestaurant),
				R.drawable.ic_bill_white_normal);
		loader.showProgress(false);
		configureScreen(mRestaurant);
		updateLightProfile(!mIsDemo);
		ViewUtils.setVisible(imgPrevious, !mIsDemo);
		ViewUtils.setVisible(txtLeave, mIsDemo);
		ViewUtils.setVisible(getPanelBottom(), true);
	}

	private void showNoOrdersInfo() {
		getSupportFragmentManager().beginTransaction()
		                           .addToBackStack(null)
		                           .setCustomAnimations(R.anim.slide_in_up,
		                                                R.anim.slide_out_down,
		                                                R.anim.slide_in_up,
		                                                R.anim.slide_out_down)
		                           .replace(R.id.fragment_container,
		                                    NoOrdersFragment.newInstance
				                                    (mTable.getInternalId()))
		                           .commit();
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
						                     mRestaurant.decoration().getBackgroundColor(), REQUEST_CODE_ORDERS, mIsDemo);
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
			observable = api.waiterCall(mRestaurant.id(), mTable.getId());
		} else {
			observable = api.waiterCallStop(mRestaurant.id(), mTable.getId());
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_CHANGE_TABLE && resultCode == RESULT_CODE_TABLE_CHANGED) {
			finish();
		}
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
								ViewUtils.setVisible(imgPrevious, !mIsDemo);
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

	@OnClick(R.id.img_profile)
	protected void onProfile(View v) {
		final int tableNumber = mTable != null ? mTable.getInternalId() : 0;
		final String tableId = mTable != null ? mTable.getId() : null;
		UserProfileActivity.startSliding(this, tableNumber, tableId);
	}

	protected void onDataLoaded(final Restaurant restaurant, @Nullable TableDataResponse table) {
		onDataLoaded(restaurant, table, false, StringUtils.EMPTY_STRING);
	}

	protected void onDataLoaded(final Restaurant restaurant, final TableDataResponse table,
	                            final boolean forwardToBill,
	                            final String requestId) {

		if(table == null) {
			RestaurantActivity.start(this, restaurant, true);
			return;
		}

		mRestaurant = restaurant;
		mTable = table;

		mPaymentListener.initTableSocket(mTable);

		onNewGuest(mTable);
		animateRestaurantLogo(restaurant);
		animateRestaurantBackground(restaurant);

		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				configureScreen(mRestaurant);
				updateLightProfile(!mIsDemo);
				ViewUtils.setVisible(imgPrevious, !mIsDemo);
				ViewUtils.setVisible(txtLeave, mIsDemo);
				ViewUtils.setVisible(getPanelBottom(), true);
				getPanelBottom().animate().translationY(0).setInterpolator(new DecelerateInterpolator())
				                .setDuration(getResources().getInteger(R.integer.default_animation_duration_short)).start();
				if(forwardToBill) {
					postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
						@Override
						public void run() {
							showOrders(restaurant.orders(), requestId);
						}
					});
				}
			}
		});
	}

	private void animateRestaurantBackground(final Restaurant restaurant) {
		OmnomApplication.getPicasso(this)
		                .load(RestaurantHelper.getBackground(restaurant, getResources().getDisplayMetrics()))
		                .into(mTarget);
	}

	private void animateRestaurantLogo(final Restaurant restaurant) {
		loader.post(new Runnable() {
			@Override
			public void run() {
				loader.animateLogo(RestaurantHelper.getLogo(restaurant), R.drawable.ic_fork_n_knife,
				                   getResources().getInteger(R.integer.default_animation_duration_short));
			}
		});
		loader.animateColor(RestaurantHelper.getBackgroundColor(restaurant));
	}

	private void onNewGuest(TableDataResponse table) {
		api.newGuest(table.getRestaurantId(), table.getId())
		   .subscribe(OmnomObservable.emptyOnNext(), OmnomObservable.loggerOnError(TAG));
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

	protected final void handleDecodeResponse(final String method, final RestaurantResponse response) {
		final List<Restaurant> restaurants = response.getRestaurants();
		if(restaurants != null) {
			final int size = restaurants.size();
			switch(size) {
				case 0:
					handleEmptyResponse(response.getRequestId());
					break;

				case 1:
					handleRestaurant(method, response.getRequestId(), restaurants.get(0));
					break;

				default:
					handleRestaurants(response.getRequestId(), restaurants);
					break;
			}
		} else {
			// TODO: show error or handle
		}
	}

	protected void handleHashRestaurants(final String requestId, final Restaurant restaurant) {

	}

	protected void handleRestaurants(final String requestId, final List<Restaurant> restaurants) {
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				RestaurantsListActivity.start(ValidateActivity.this, restaurants);
			}
		});
	}

	protected void handleEmptyResponse(final String requestId) {
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				RestaurantsListActivity.start(ValidateActivity.this, true);
			}
		});
	}

	protected void handleRestaurant(final String method, final String requestId, final Restaurant restaurant) {
		// User in already in a restaurant there is no need to send them notification
		final PreferenceHelper preferences = (PreferenceHelper) OmnomApplication.get(getActivity()).getPreferences();
		preferences.saveNotificationDetails(this, restaurant.id());
		final TableDataResponse table = RestaurantHelper.getTable(restaurant);
		if(table != null) {
			loader.stopProgressAnimation();
			loader.updateProgressMax(new Runnable() {
				@Override
				public void run() {
					reportMixPanel(requestId, method, table);
					onDataLoaded(restaurant, table);
				}
			});
		} else {
			loader.stopProgressAnimation();
			loader.updateProgressMax(new Runnable() {
				@Override
				public void run() {
					loader.showProgress(false, true, new Runnable() {
						@Override
						public void run() {
							ValidateActivityShortcut.start(ValidateActivity.this, R.anim.fake_fade_in_instant, R.anim.fake_fade_out_instant,
							                               EXTRA_LOADER_ANIMATION_FIXED);
							finish();
						}
					});
				}
			}, false);
		}
	}

	protected abstract void reportMixPanel(final String requestId, final String method, final TableDataResponse tableDataResponse);

	public void changeTable() {
		clearErrors(true);
		AnimationUtils.animateAlpha(imgPrevious, false);
		bottomView.animate().translationY(bottomView.getHeight());
		loader.animateLogoFast(R.drawable.ic_fork_n_knife);
		AnimationUtils.animateAlpha(imgProfile, false);
		postDelayed(850, new Runnable() {
			@Override
			public void run() {
				ValidateActivity.start(ValidateActivity.this, R.anim.fade_in_long, R.anim.fade_out_long, EXTRA_LOADER_ANIMATION_FIXED,
						ConfirmPhoneActivity.TYPE_DEFAULT);
			}
		});
	}
}
