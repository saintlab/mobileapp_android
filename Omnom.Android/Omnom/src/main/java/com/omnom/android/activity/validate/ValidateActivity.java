package com.omnom.android.activity.validate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.AcquiringMailRu;
import com.omnom.android.activity.ConfirmPhoneActivity;
import com.omnom.android.activity.EnteringActivity;
import com.omnom.android.activity.OmnomBaseErrorHandler;
import com.omnom.android.activity.OrdersActivity;
import com.omnom.android.activity.RestaurantActivity;
import com.omnom.android.activity.RestaurantsListActivity;
import com.omnom.android.activity.UserProfileActivity;
import com.omnom.android.activity.ValidateActivityBle18;
import com.omnom.android.activity.ValidateActivityBle21;
import com.omnom.android.activity.ValidateActivityCamera;
import com.omnom.android.activity.ValidateActivityShortcut;
import com.omnom.android.activity.WishActivity;
import com.omnom.android.activity.base.BaseOmnomModeSupportActivity;
import com.omnom.android.activity.helper.OmnomActivityHelper;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.auth.UserData;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.entrance.EntranceDataFactory;
import com.omnom.android.entrance.EntranceDataHelper;
import com.omnom.android.fragment.SearchFragment;
import com.omnom.android.fragment.menu.MenuItemDetailsFragment;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.menu.api.observable.MenuObservableApi;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.MenuResponse;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.OmnomErrorHelper;
import com.omnom.android.mixpanel.model.AppLaunchMixpanelEvent;
import com.omnom.android.notifier.api.observable.NotifierObservableApi;
import com.omnom.android.preferences.PreferenceHelper;
import com.omnom.android.protocol.Protocol;
import com.omnom.android.push.PushNotificationManager;
import com.omnom.android.restaurateur.api.ConfigDataService;
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
import com.omnom.android.service.configuration.ConfigurationResponse;
import com.omnom.android.service.configuration.ConfigurationService;
import com.omnom.android.socket.ActivityPaymentBroadcastReceiver;
import com.omnom.android.socket.PaymentEventIntentFilter;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.activity.BaseFragmentActivity;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.BitmapUtils;
import com.omnom.android.utils.utils.BluetoothUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.OnClick;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.omnom.android.mixpanel.MixPanelHelper.Project.OMNOM;
import static com.omnom.android.mixpanel.MixPanelHelper.Project.OMNOM_ANDROID;

/**
 * Created by Ch3D on 08.10.2014.
 */
public abstract class ValidateActivity extends BaseOmnomModeSupportActivity
		implements FragmentManager.OnBackStackChangedListener, SearchFragment.ItemClickListener<Item>, SearchFragment
		.FragmentCloseListener {

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

	public static final int BACKGROUND_PREVIEW_WIDTH = 50;

	public static final int BACKGROUND_PREVIEW_BLUR_RADIUS = 5;

	private static final String TAG = ValidateActivity.class.getSimpleName();

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

	public static void start(final OmnomActivity context, final int enterAnim,
	                         final int exitAnim, final int animationType, final int userEnterType,
	                         final Uri data, final boolean isApplicationLaunch) {
		Intent intent = createIntent(context.getActivity(), animationType, false, userEnterType, data, isApplicationLaunch);
		context.start(intent, enterAnim, exitAnim, true);
	}

	public static void start(final OmnomActivity context, final int enterAnim,
	                         final int exitAnim, final int animationType, Restaurant restaurant) {
		Intent intent = createIntent(context.getActivity(), animationType, false, ValidateActivity.TYPE_DEFAULT, null, false);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		context.start(intent, enterAnim, exitAnim, true);
	}

	public static void start(final OmnomActivity context, final int enterAnim,
	                         final int exitAnim, final int animationType,
	                         final Restaurant restaurant, final EntranceData entranceData) {
		Intent intent = createIntent(context.getActivity(), animationType, false, ValidateActivity.TYPE_DEFAULT, null, false);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_ENTRANCE_DATA, entranceData);
		context.start(intent, enterAnim, exitAnim, true);
	}

	private static Intent createIntent(final Context context, final int animationType,
	                                   final boolean isDemo, final int userEnterType, final Uri data) {
		final boolean hasBle = BluetoothUtils.hasBleSupport(context);
		final Class validateActivityBleClass = AndroidUtils.isLollipop() ? ValidateActivityBle21.class : ValidateActivityBle18.class;
		final boolean isBleReadyDevice = hasBle & AndroidUtils.isJellyBeanMR2();

		final Intent intent = new Intent(context,
		                                 isBleReadyDevice && data == null ? validateActivityBleClass : ValidateActivityCamera.class);

		intent.putExtra(EXTRA_LOADER_ANIMATION, animationType);
		intent.putExtra(EXTRA_DEMO_MODE, isDemo);
		intent.putExtra(EXTRA_CONFIRM_TYPE, userEnterType);
		if(data != null) {
			intent.setData(data);
			if(data.toString().contains(QR_URL_PATH_PREFIX)) {
				intent.setAction(ACTION_LAUNCH_QR);
			} else {
				intent.setAction(ACTION_LAUNCH_HASHCODE);
			}
		}
		return intent;
	}

	private static Intent createIntent(final Context context, final int animationType,
	                                   final boolean isDemo, final int userEnterType, final Uri data,
	                                   final boolean isApplicationLaunch) {
		Intent intent = createIntent(context, animationType, isDemo, userEnterType, data);
		intent.putExtra(EXTRA_APPLICATION_LAUNCH, isApplicationLaunch);
		return intent;
	}

	protected final View.OnClickListener mInternetErrorClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			decode(true);
		}
	};

	final Target backgroundTarget = new Target() {
		@Override
		public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
			final BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
			AnimationUtils.animateDrawable(findViewById(R.id.root), findViewById(R.id.background),
			                               drawable,
			                               getResources().getInteger(R.integer.default_animation_duration_long));
		}

		@Override
		public void onBitmapFailed(Drawable errorDrawable) {
		}

		@Override
		public void onPrepareLoad(Drawable placeHolderDrawable) {
		}
	};

	@Inject
	protected RestaurateurObservableApi api;

	@Inject
	protected ConfigDataService configApi;

	@Inject
	protected Acquiring mAcquiring;

	@Inject
	protected AuthService authenticator;

	@Inject
	protected MenuObservableApi menuApi;

	@Inject
	protected PushNotificationManager mPushManager;

	@Inject
	protected NotifierObservableApi notifierApi;

	protected boolean mFirstRun = true;

	@Nullable
	protected Restaurant mRestaurant;

	@Nullable
	protected TableDataResponse mTable;

	protected boolean mIsDemo = false;

	protected boolean mSkipViewRendering = false;

	protected Func1<RestaurantResponse, RestaurantResponse> mPreloadBgFunc;

	@Nullable
	protected Menu mMenu;

	protected Uri mData;

	protected ValidateOrderHelper mOrderHelper;

	protected final View.OnClickListener mOnOrderClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onOrder();
		}
	};

	protected ValidateViewHelper mViewHelper;

	Restaurant currentRestaurant;

	final Target previewTarget = new Target() {
		@Override
		public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
			final Bitmap blurredBitmap = BitmapUtils.blur(bitmap, BACKGROUND_PREVIEW_BLUR_RADIUS);
			final BitmapDrawable drawable = new BitmapDrawable(getResources(), blurredBitmap);
			AnimationUtils.animateDrawable(getWindow().getDecorView(), findViewById(R.id.root),
			                               drawable,
			                               getResources().getInteger(R.integer.default_animation_duration_quick));
			OmnomApplication.getPicasso(ValidateActivity.this)
			                .load(RestaurantHelper.getBackground(currentRestaurant,
			                                                     getResources().getDisplayMetrics().widthPixels))
			                .into(backgroundTarget);
		}

		@Override
		public void onBitmapFailed(Drawable errorDrawable) {
		}

		@Override
		public void onPrepareLoad(Drawable placeHolderDrawable) {
		}
	};

	private BroadcastReceiver mPaymentReceiver;

	/**
	 * ConfirmPhoneActivity.TYPE_LOGIN or ConfirmPhoneActivity.TYPE_REGISTER
	 */
	private int mType;

	private boolean mIsApplicationLaunch;

	private int mAnimationType;

	private boolean mWaiterCalled;

	private com.omnom.android.utils.drawable.TransitionDrawable bgTransitionDrawable;

	protected BaseErrorHandler onError = new OmnomBaseErrorHandler(this) {
		@Override
		protected void onThrowable(Throwable throwable) {
			Log.e(TAG, "onError", throwable);
			mViewHelper.loader.stopProgressAnimation(true);
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

			getErrorHelper().showBackendError(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clearErrors(true);
					decode(true);
				}
			});
		}
	};

	protected final View.OnClickListener mInternetErrorClickBillListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBill(findViewById(R.id.btn_bill));
		}
	};

	protected final View.OnClickListener mOnBillClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBill(findViewById(R.id.btn_bill));
		}
	};

	private ConfigurationService configurationService;

	private View.OnClickListener loadConfigsErrorListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			loadConfigs();
		}
	};

	private Subscription mPaymentEventsSubscription;

	private PaymentEventIntentFilter mPaymentFilter;

	protected OmnomErrorHelper getErrorHelper() {return mViewHelper.getErrorHelper();}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mData = getIntent().getData();
		mViewHelper = new ValidateViewHelper(this);
		configurationService = new ConfigurationService(getApplicationContext(), authenticator, configApi, mAcquiring,
		                                                getApp().getAuthToken());

		mPaymentReceiver = new ActivityPaymentBroadcastReceiver(this);
		mPaymentFilter = new PaymentEventIntentFilter(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
	}

	@Override
	protected void handleIntent(Intent intent) {
		super.handleIntent(intent);
		mAnimationType = intent.getIntExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
		mIsDemo = intent.getBooleanExtra(EXTRA_DEMO_MODE, false);
		mSkipViewRendering = intent.getBooleanExtra(EXTRA_SKIP_VIEW_RENDERING, false);
		mType = intent.getIntExtra(EXTRA_CONFIRM_TYPE, TYPE_DEFAULT);
		mIsApplicationLaunch = intent.getBooleanExtra(EXTRA_APPLICATION_LAUNCH, false);
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		startValidation();
	}

	private void startValidation() {
		if(!mSkipViewRendering) {
			if(RestaurantHelper.isBar(mRestaurant) || (EntranceDataHelper.isBar(mEntranceData))) {
				handleBar(mRestaurant, mEntranceData);
				return;
			}

			postDelayed(getResources().getInteger(R.integer.default_animation_duration_quick), new Runnable() {
				@Override
				public void run() {
					validate();
				}
			});
		}
	}

	private void handleBar(final Restaurant restaurant, final EntranceData entranceData) {
		mViewHelper.updateLoader(mRestaurant);
		mViewHelper.startProgressAnimation(getResources().getInteger(R.integer.omnom_validate_duration));

		menuApi.getMenu(restaurant.id()).subscribe(new Action1<MenuResponse>() {
			@Override
			public void call(final MenuResponse menuResponse) {
				mMenu = menuResponse.getMenu();
				ensureEntranceData(entranceData, mRestaurant);
				mViewHelper.setEntranceData(mEntranceData);
				onDataLoaded(restaurant, TableDataResponse.NULL, mEntranceData);
				mSkipViewRendering = true;
			}
		}, onError);

		mFirstRun = false;
	}

	private void ensureEntranceData(final EntranceData entranceData, final Restaurant restaurant) {
		if(entranceData == null) {
			mEntranceData = EntranceDataFactory.create(restaurant);
		} else {
			mEntranceData = entranceData;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mPaymentReceiver, mPaymentFilter);
		mPaymentEventsSubscription = OmnomActivityHelper.processPaymentEvents(getActivity());
		mOrderHelper.updateWishUi();
		mViewHelper.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(mPaymentReceiver);
		unsubscribe(mPaymentEventsSubscription);

		if(mRestaurant == null) {
			mViewHelper.onPause();
		}
	}

	@OnClick(R.id.txt_demo_leave)
	protected void onLeave() {
		onBackPressed();
	}

	public void clearErrors(boolean animateLogo) {
		mViewHelper.hideProfile();
		mViewHelper.clearErrors(mRestaurant, animateLogo);
		if(bgTransitionDrawable.isTransitioned()) {
			bgTransitionDrawable.reverseTransition();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getApp().disconnectTableSocket();
		configurationService.onDestroy();
		mOrderHelper = null;
		mViewHelper.onDestroy();
		mViewHelper = null;
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mFirstRun) {
			switch(mAnimationType) {
				case EXTRA_LOADER_ANIMATION_SCALE_DOWN:
				case EXTRA_LOADER_ANIMATION_FIXED:
					mViewHelper.scaleDown();
					break;

				case EXTRA_LOADER_ANIMATION_SCALE_UP:
					mViewHelper.setSize(0, 0);
					break;

				default:
					mViewHelper.setSize(0, 0);
					break;
			}
		}
	}

	@OnClick(R.id.btn_previous)
	public void onPrevious(View v) {
		mViewHelper.onPrevious();
	}

	@Override
	public void onBackPressed() {
		if(!mViewHelper.onBackPressed()) {
			final Fragment itemDetailsFragment = getSupportFragmentManager().findFragmentByTag(MenuItemDetailsFragment.TAG);
			if(itemDetailsFragment != null) {
				MenuItemDetailsFragment midf = (MenuItemDetailsFragment) itemDetailsFragment;
				midf.onClose();
			} else {
				super.onBackPressed();
			}
		}
	}

	@Override
	public void initUi() {
		getSupportFragmentManager().addOnBackStackChangedListener(this);

		mViewHelper.init();
		mOrderHelper = new ValidateOrderHelper(this, mViewHelper);

		bgTransitionDrawable = new com.omnom.android.utils.drawable.TransitionDrawable(
				getResources().getInteger(R.integer.default_animation_duration_short),
				new Drawable[]{new ColorDrawable(getResources().getColor(R.color.transparent)),
						new ColorDrawable(getResources().getColor(R.color.error_bg_white_transparent))});

		bgTransitionDrawable.setCrossFadeEnabled(true);
		mViewHelper.setBackground(bgTransitionDrawable);

		mPreloadBgFunc = new Func1<RestaurantResponse, RestaurantResponse>() {
			@Override
			public RestaurantResponse call(final RestaurantResponse decodeResponse) {
				final List<Restaurant> restaurants = decodeResponse.getRestaurants();
				if(restaurants.size() == 1) {
					final Restaurant restaurant = restaurants.get(0);
					if(restaurant != null) {
						final String bgImgUrl = RestaurantHelper.getBackground(restaurant, BACKGROUND_PREVIEW_WIDTH);
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

		if(mSkipViewRendering) {
			mFirstRun = false;
			decode(true);
		}
	}

	private void loadConfigs() {
		clearErrors(true);
		final int locationUpdateTimeout = ConfigurationService.LOCATION_UPDATE_TIMEOUT;
		final int validateDuration = getResources().getInteger(R.integer.omnom_validate_duration);
		mViewHelper.startProgressAnimation(locationUpdateTimeout + validateDuration);

		subscribe(configurationService.getConfigurationObservable()
				, new Action1<ConfigurationResponse>() {
			@Override
			public void call(ConfigurationResponse configurationResponse) {
				final UserResponse userResponse = configurationResponse.getUserResponse();
				updateConfiguration(configurationResponse.getConfig());
				correctMixpanelTime(userResponse);
				reportMixPanel(userResponse);
				getApp().cacheUserProfile(new UserProfile(userResponse));

				track(MixPanelHelper.Project.OMNOM,
				      new AppLaunchMixpanelEvent(userResponse.getUser()));
				final boolean hasBle = BluetoothUtils.hasBleSupport(ValidateActivity.this);
				final boolean bleEnabled = BluetoothUtils.isBluetoothEnabled(getActivity());
				if((!hasBle || (hasBle && !bleEnabled)) && !isExternalLaunch()) {
					validateShowRestaurants();
				} else {
					decode(false);
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				Log.e(TAG, "loadConfigs", throwable);
				if(throwable.getCause() instanceof UnknownHostException) {
					getErrorHelper().showInternetError(loadConfigsErrorListener);
				} else {
					getErrorHelper().showUnknownError(loadConfigsErrorListener);
				}
			}
		});
	}

	private void validateShowRestaurants() {
		subscribe(ValidationObservable.validate(ValidateActivity.this)
		                              .map(OmnomObservable.getValidationFunc(
				                              ValidateActivity.this,
				                              getErrorHelper(),
				                              new View.OnClickListener() {
					                              @Override
					                              public void onClick(View v) {
						                              validate();
					                              }
				                              })).isEmpty(),
		          new Action1<Boolean>() {
			          @Override
			          public void call(Boolean hasNoErrors) {
				          if(hasNoErrors) {
					          mViewHelper.showRestaurants();
				          }
			          }
		          }, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						getErrorHelper().showInternetError(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								validate();
							}
						});
					}
				});
	}

	private void updateConfiguration(final Config config) {
		final OmnomApplication app = getApp();

		app.cacheConfig(config);
		if(mAcquiring instanceof AcquiringMailRu) {
			((AcquiringMailRu) mAcquiring).changeEndpoint(config.getAcquiringData().getBaseUrl());
		}
		mPushManager.register();
		getMixPanelHelper().addApi(OMNOM, MixpanelAPI.getInstance(this, config.getTokens().getMixpanelToken()));
		getMixPanelHelper().addApi(OMNOM_ANDROID, MixpanelAPI.getInstance(this, config.getTokens().getMixpanelTokenAndroid()));
	}

	private void correctMixpanelTime(@Nullable final UserResponse userResponse) {
		if(userResponse == null) {
			return;
		}
		final MixPanelHelper mixPanelHelper = getMixPanelHelper();
		if(mixPanelHelper != null) {
			final long timeDiff = TimeUnit.SECONDS.toMillis(userResponse.getServerTime()) - userResponse.getResponseTime();
			mixPanelHelper.setTimeDiff(timeDiff);
		}
	}

	/**
	 * @return <code>true</code> if app was launched by an extrenal qr/link
	 */
	protected boolean isExternalLaunch() {return mData != null;}

	protected void validate() {
		if(mFirstRun || mRestaurant == null) {
			mViewHelper.hideProfile();
			mViewHelper.validate(new Runnable() {
				@Override
				public void run() {
					if(mIsApplicationLaunch) {
						loadConfigs();
					} else {
						decode(true);
					}
				}
			});
		}
		mFirstRun = false;
	}

	protected abstract void decode(boolean startProgressAnimation);

	public void onBill(final View v) {
		final int fragmentsCount = getSupportFragmentManager().getBackStackEntryCount();
		final int startDelay = getResources().getInteger(R.integer.default_animation_duration_quick);
		final int stepDelay = getResources().getInteger(R.integer.default_menu_to_bill_step_duration);
		final int animationSteps = 2;

		Observable.timer(startDelay, stepDelay, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
		          .take(fragmentsCount + animationSteps)
		          .subscribe(new Action1<Long>() {
			          @Override
			          public void call(final Long aLong) {
				          if(aLong < fragmentsCount) {
					          getSupportFragmentManager().popBackStack();
				          } else {
					          if(aLong == fragmentsCount) {
						          mViewHelper.onBillStep1(v);
					          }
					          if(aLong == fragmentsCount + 1) {
						          onBillStep2(v);
					          }
				          }
			          }
		          });
	}

	private void onBillStep2(final View v) {
		v.setEnabled(false);
		mViewHelper.beforeShowOrders();
		ValidationObservable.validateSmart(this, mIsDemo)
		                    .map(OmnomObservable.getValidationFunc(this, getErrorHelper(), mInternetErrorClickBillListener)).isEmpty()
		                    .subscribe(new Action1<Boolean>() {
			                    @Override
			                    public void call(final Boolean hasNoErrors) {
				                    if(hasNoErrors) {
					                    clearErrors(false);
					                    loadOrders(v);
				                    } else {
					                    startErrorTransition();
					                    mViewHelper.translatePanelBottom(200);
					                    v.setEnabled(true);
				                    }
			                    }
		                    }, new Action1<Throwable>() {
			                    @Override
			                    public void call(final Throwable throwable) {
				                    startErrorTransition();
				                    getErrorHelper().showInternetError(mInternetErrorClickBillListener);
				                    v.setEnabled(true);
			                    }
		                    });
	}

	protected void startErrorTransition() {
		bgTransitionDrawable.startTransition();
	}

	private void loadOrders(final View v) {
		mViewHelper.translatePanelBottom(0);
		subscribe(api.getOrders(mTable.getRestaurantId(), mTable.getId())
				, new Action1<OrdersResponse>() {
			@Override
			public void call(final OrdersResponse ordersResponse) {
				mViewHelper.stopProgressAnimation();
				mViewHelper.updateProgressMax(new Runnable() {
					@Override
					public void run() {
						v.setEnabled(true);
						if(!ordersResponse.getOrders().isEmpty()) {
							showOrders(ordersResponse.getOrders(), ordersResponse.getRequestId());
						} else {
							startErrorTransition();
							mViewHelper.onOrderError(mRestaurant, mTable, mIsDemo);
						}
					}
				});
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			public void onError(Throwable throwable) {
				v.setEnabled(true);
				startErrorTransition();
				getErrorHelper().showUnknownError(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mViewHelper.onErrorClose(mRestaurant, mIsDemo);
					}
				});
			}
		});
	}

	protected void showOrders(final List<Order> orders, final String requestId) {
		mViewHelper.showOrders(new Runnable() {
			@Override
			public void run() {
				OrdersActivity.start(ValidateActivity.this, mRestaurant, new ArrayList<Order>(orders), requestId,
				                     mRestaurant.decoration().getBackgroundColor(), REQUEST_CODE_ORDERS, mIsDemo);
				if(orders.size() == 1) {
					overridePendingTransition(R.anim.slide_in_down_short, R.anim.nothing);
				}
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
		subscribe(observable, new Action1<WaiterCallResponse>() {
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
		if(requestCode == REQUEST_CODE_WISH_LIST) {
			if(data != null) {
				final UserOrder resultOrder = data.getParcelableExtra(EXTRA_ORDER);
				mOrderHelper.updateData(resultOrder);
			}
			if(resultCode == WishActivity.RESULT_ORDER_DONE) {
				mOrderHelper.clearOrder();
				collapseSlidingPanelInstant();
			}
			if(resultCode == WishActivity.RESULT_CLEARED) {
				mOrderHelper.clearOrder();
			}
			if((resultCode & WishActivity.RESULT_CLEARED) == WishActivity.RESULT_CLEARED) {
				mOrderHelper.clearOrder();
			}
			if((resultCode & WishActivity.RESULT_BILL) == WishActivity.RESULT_BILL) {
				onBill(findViewById(R.id.btn_bill));
			}
		}
		if(requestCode == REQUEST_CODE_CHANGE_TABLE && resultCode == RESULT_CODE_TABLE_CHANGED) {
			finish();
		}
		if(requestCode == REQUEST_CODE_ORDERS && resultCode == RESULT_OK) {
			if(mRestaurant != null) {
				// The following delay is required due to different behavior on different devices.
				// Some of them wait for activity transition animation to finish and then invoke onActivityResult,
				// others perform them in parallel.
				postDelayed(getResources().getInteger(R.integer.default_animation_duration_short),
				            new Runnable() {
					            @Override
					            public void run() {
						            mViewHelper.onReturnToValidation(mRestaurant, mIsDemo);
					            }
				            });
			}
		}
	}

	@OnClick({R.id.img_profile, R.id.txt_table})
	public void onProfile(View v) {
		final int tableNumber = mTable != null ? mTable.getInternalId() : 0;
		final String tableId = mTable != null ? mTable.getId() : null;
		mViewHelper.onProfile();
		UserProfileActivity.startSliding(this, tableNumber, tableId);
	}

	protected void onDataLoaded(final Restaurant restaurant, @Nullable TableDataResponse table, final EntranceData entranceData) {
		onDataLoaded(restaurant, table, false, StringUtils.EMPTY_STRING, entranceData);
	}

	protected void onDataLoaded(final Restaurant restaurant, final TableDataResponse table,
	                            final boolean forwardToBill,
	                            final String requestId,
	                            final EntranceData entranceData) {

		if(table == null) {
			RestaurantActivity.start(this, restaurant, true);
			return;
		}

		mRestaurant = restaurant;
		mTable = table;

		getApp().connectTableSocket(table);

		ensureEntranceData(entranceData, mRestaurant);
		mViewHelper.setEntranceData(mEntranceData);
		bindMenuData();

		onNewGuest(mTable);
		animateRestaurantBackground(restaurant);
		mViewHelper.onDataLoaded(restaurant, table, forwardToBill, mIsDemo, requestId);
	}

	private void animateRestaurantBackground(final Restaurant restaurant) {
		currentRestaurant = restaurant;
		final String bgImgUrl = RestaurantHelper.getBackground(restaurant, BACKGROUND_PREVIEW_WIDTH);
		if(!TextUtils.isEmpty(bgImgUrl)) {
			OmnomApplication.getPicasso(this)
			                .load(bgImgUrl)
			                .into(previewTarget);
		}
	}

	private void onNewGuest(TableDataResponse table) {
		api.newGuest(table.getRestaurantId(), table.getId()).subscribe(OmnomObservable.emptyOnNext(),
		                                                               OmnomObservable.loggerOnError(TAG));

		notifierApi.tableIn(table.getRestaurantId(), table.getId()).subscribe(OmnomObservable.emptyOnNext(),
		                                                                      OmnomObservable.loggerOnError(TAG));
	}

	/**
	 * Report about user sign up or login
	 */
	private void reportMixPanel(@Nullable final UserResponse userResponse) {
		if(userResponse == null) {
			return;
		}

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

	void configureScreen(final Restaurant restaurant) {
		mViewHelper.configureScreen(restaurant);

		// getPanelBottom().setTranslationY(100);
		mOrderHelper.updateWishUi();
	}

	protected void onOrder() {
		WishActivity.start(this, mRestaurant, mTable, mMenu, mOrderHelper.insureOrder(), mEntranceData, REQUEST_CODE_WISH_LIST);
	}

	protected boolean validateDemo() {
		if(mIsDemo) {
			if(mRestaurant == null || mTable == null) {
				mViewHelper.startProgressAnimation(getResources().getInteger(R.integer.omnom_validate_duration));
				api.getDemoTable().flatMap(new Func1<List<DemoTableData>, Observable<MenuResponse>>() {
					@Override
					public Observable<MenuResponse> call(final List<DemoTableData> demoTableResponse) {
						if(demoTableResponse.size() > 0) {
							final DemoTableData demoTableData = demoTableResponse.get(0);
							if(demoTableData != null && demoTableData.getRestaurant() != null) {
								return menuApi.getMenu(demoTableData.getRestaurant().id());
							}
						}
						return Observable.just(new MenuResponse());
					}
				}, new Func2<List<DemoTableData>, MenuResponse, Pair<List<DemoTableData>, MenuResponse>>() {
					@Override
					public Pair<List<DemoTableData>, MenuResponse> call(final List<DemoTableData> restaurant,
					                                                    final MenuResponse menu) {
						mMenu = menu.getMenu();
						return Pair.create(restaurant, menu);
					}
				}).subscribe(new Action1<Pair<List<DemoTableData>, MenuResponse>>() {
					@Override
					public void call(final Pair<List<DemoTableData>, MenuResponse> listMenuResponsePair) {
						final List<DemoTableData> demoTableResponse = listMenuResponsePair.first;
						final DemoTableData data = demoTableResponse.get(0);
						onDataLoaded(data.getRestaurant(), data.getTable(), mEntranceData);
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

	protected void handleHashRestaurants(final String requestId, final Restaurant restaurant, final Menu menu) {
		// TODO:
	}

	protected void handleRestaurants(final String requestId, final List<Restaurant> restaurants) {
		mViewHelper.handleRestaurants(new Runnable() {
			@Override
			public void run() {
				RestaurantsListActivity.start(ValidateActivity.this, restaurants);
			}
		});
	}

	protected void handleEmptyResponse(final String requestId) {
		mViewHelper.handleRestaurants(new Runnable() {
			@Override
			public void run() {
				RestaurantsListActivity.start(ValidateActivity.this, true);
			}
		});
	}

	protected void handleRestaurant(final String method, final String requestId, final Restaurant restaurant) {
		if(!restaurant.available()) {
			getErrorHelper().showError(LoaderError.RESTAURANT_UNAVAILABLE, mInternetErrorClickListener);
			return;
		}

		// User in already in a restaurant there is no need to send them notification
		final PreferenceHelper preferences = (PreferenceHelper) getPreferences();

		preferences.saveNotificationDetails(this, restaurant.id());
		final TableDataResponse table = RestaurantHelper.getTable(restaurant);
		if(table != null) {
			mViewHelper.handleRestaurants(new Runnable() {
				@Override
				public void run() {
					reportMixPanel(requestId, method, table);
					onDataLoaded(restaurant, table, mEntranceData);
				}
			});
		} else {
			mViewHelper.handleRestaurants(new Runnable() {
				@Override
				public void run() {
					mViewHelper.showProgress(false, true, new Runnable() {
						@Override
						public void run() {
							ValidateActivityShortcut.start(ValidateActivity.this, R.anim.fake_fade_in_instant,
							                               R.anim.fake_fade_out_instant,
							                               EXTRA_LOADER_ANIMATION_FIXED);
							finish();
						}
					});
				}
			});
		}
	}

	protected abstract void reportMixPanel(final String requestId, final String method, final TableDataResponse tableDataResponse);

	public void changeTable() {
		clearErrors(true);
		mViewHelper.onChangeTable();
		postDelayed(850, new Runnable() {
			@Override
			public void run() {
				ValidateActivity.start(ValidateActivity.this, R.anim.fade_in_long, R.anim.fade_out_long, EXTRA_LOADER_ANIMATION_FIXED,
				                       ConfirmPhoneActivity.TYPE_DEFAULT);
			}
		});
	}

	@Override
	public void onBackStackChanged() {
		boolean noFragments = getSupportFragmentManager() != null && getSupportFragmentManager().getBackStackEntryCount() == 0;
		mViewHelper.showBack(noFragments);
		if(noFragments) {
			mViewHelper.resetMenuState();
		}
	}

	protected Observable<Pair<RestaurantResponse, MenuResponse>> concatMenuObservable(final Observable<RestaurantResponse>
			                                                                                  restaurantObservable) {
		final OmnomObservable.ObjectWrapper<RestaurantResponse> mRestaurantResponse = new OmnomObservable.ObjectWrapper<>();
		return restaurantObservable.flatMap(
				new Func1<RestaurantResponse, Observable<MenuResponse>>() {
					@Override
					public Observable<MenuResponse> call(final RestaurantResponse restaurantResponse) {
						mRestaurantResponse.setValue(restaurantResponse);
						if(restaurantResponse.hasOnlyRestaurant()) {
							return RestaurantHelper.getMenuObservable(menuApi, restaurantResponse.getRestaurants().get(0));
						}
						return Observable.just(MenuResponse.EMPTY);
					}
				}, new Func2<RestaurantResponse, MenuResponse, Pair<RestaurantResponse, MenuResponse>>() {
					@Override
					public Pair<RestaurantResponse, MenuResponse> call(final RestaurantResponse restaurant, final MenuResponse menu) {
						mMenu = menu.getMenu();
						return Pair.create(restaurant, menu);
					}
				}).onErrorReturn(new Func1<Throwable, Pair<RestaurantResponse, MenuResponse>>() {
			@Override
			public Pair<RestaurantResponse, MenuResponse> call(final Throwable throwable) {
				return new Pair<>(mRestaurantResponse.getValue(), null);
			}
		});
	}

	protected void bindMenuData() {
		if(RestaurantHelper.isMenuEnabled(mRestaurant) && !mIsDemo) {
			mViewHelper.bindMenuData(mMenu, mOrderHelper);
		} else {
			mViewHelper.hideMenu();
		}
	}

	protected void updateOrderData(final OrderUpdateEvent event) {
		mOrderHelper.updateOrderData(event);
	}

	public View getBottomView() {
		return mViewHelper.bottomView;
	}

	public void collapseSlidingPanel() {
		mViewHelper.collapseSlidingPanel();
	}

	protected void collapseSlidingPanelInstant() {
		bindMenuData();
		mViewHelper.collapseSlidingPanelInstant();
	}

	public SlidingUpPanelLayout.PanelState getSlidingPanelState() {
		return mViewHelper.getSlidingPanelState();
	}

	public void expandSlidingPanel() {
		mViewHelper.expandSlidingPanel();
	}

	public void showSearchFragment() {
		if(mMenu == null || mMenu.items() == null || mMenu.items().items() == null) {
			return;
		}
		Map<String, Item> itemsByName = new LinkedHashMap<String, Item>();
		List<Item> items = new LinkedList<Item>(mMenu.items().items().values());
		Collections.sort(items, new Comparator<Item>() {
			@Override
			public int compare(Item lhs, Item rhs) {
				return lhs.name().compareTo(rhs.name());
			}
		});
		for(Item item : items) {
			itemsByName.put(item.name(), item);
		}
		mViewHelper.showSearchFragment(itemsByName);
	}

	@Override
	public void onFragmentClose() {
		mViewHelper.onSearchFragmentClose();
	}

	@Override
	public void onItemClick(final Item item) {
		mViewHelper.showMenuItemDetails(item);
	}

}