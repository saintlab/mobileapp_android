package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.BuildConfig;
import com.omnom.android.R;
import com.omnom.android.activity.validate.ValidateActivity;
import com.omnom.android.auth.AuthError;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.MenuResponse;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.model.OnTableMixpanelEvent;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.decode.HashDecodeRequest;
import com.omnom.android.restaurateur.model.decode.QrDecodeRequest;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.activity.BaseFragmentActivity;
import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class ValidateActivityCamera extends ValidateActivity {
	public static final String DEVICE_ID_GENYMOTION = "000000000000000";

	private static final String TAG = ValidateActivityCamera.class.getSimpleName();

	private static final String ACTION_LAUNCH_QR = "com.omnom.android.action.launch_qr";

	private static final String ACTION_LAUNCH_HASHCODE = "com.omnom.android.action.launch_hashcode";

	private static final String QR_URL_PATH_PREFIX = "/qr/";

	private static final String SCHEME_OMNOM = "omnom";

	private static final String QUERY_PARAMETER_HASH = "hash";

	private static Intent createIntent(Context context, int animationType, boolean isDemo, int userEnterType) {
		final Intent intent = new Intent(context, ValidateActivityCamera.class);
		intent.putExtra(EXTRA_LOADER_ANIMATION, animationType);
		intent.putExtra(EXTRA_DEMO_MODE, isDemo);
		intent.putExtra(EXTRA_CONFIRM_TYPE, userEnterType);
		return intent;
	}

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType, final int userEnterType) {
		Intent intent = createIntent(context, animationType, false, userEnterType);
		if(context instanceof ConfirmPhoneActivity) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.start(intent, enterAnim, exitAnim, false);
	}

	public static void start(BaseFragmentActivity context, int enterAnim, int exitAnim, int animationType, final int userEnterType) {
		Intent intent = createIntent(context, animationType, false, userEnterType);
		context.start(intent, enterAnim, exitAnim, true);
	}

	public static void start(final BaseFragmentActivity context, final Uri data) {
		final Intent intent = createIntent(context, EXTRA_LOADER_ANIMATION_SCALE_DOWN, false, ValidateActivity.TYPE_DEFAULT);
		intent.setData(data);
		if(data.toString().contains(QR_URL_PATH_PREFIX)) {
			intent.setAction(ACTION_LAUNCH_QR);
		} else {
			intent.setAction(ACTION_LAUNCH_HASHCODE);
		}
		context.start(intent, R.anim.fake_fade_in, R.anim.fake_fade_out_instant, true);
	}

	@Inject
	protected RestaurateurObservableApi api;

	private Subscription mCheckQrSubscribtion;

	private String mQrData;

	private Subscription mValidateSubscribtion;

	private int mOutAnimation;

	@Override
	protected void decode(final boolean startProgressAnimation) {
		clearErrors(true);

		if(isExternalLaunch()) {
			if(ACTION_LAUNCH_HASHCODE.equals(getIntent().getAction())) {
				if(SCHEME_OMNOM.equals(mData.getScheme())) {
					findTable(mData.getQueryParameter(QUERY_PARAMETER_HASH));
				} else {
					findTable(mData.getLastPathSegment());
				}
			} else if(ACTION_LAUNCH_QR.equals(getIntent().getAction())) {
				findTable(mData.toString());
			}
			return;
		}

		if(BuildConfig.DEBUG) {
			//findTable("http://omnom.menu/qr/88cfdcbedcbdf4e4b330824ba9dba92b");

			// fake mehico
			//findTable("http://omnom.menu/qr/a756a5a61fd8edc4d92bb92e8dcd87ea");
			//return;
		}

		if(BuildConfig.DEBUG && AndroidUtils.getDeviceId(this).equals(DEVICE_ID_GENYMOTION)) {
			// findTable("http://www.riston.ru/wishes"); // mehico
			findTable("http://m.2gis.ru/os/"); // mehico
			// findTable("http://omnom.menu/qr/00e7232a4d9d2533e7fa503620c4431b"); // shashlikoff
			return;
		}

		OmnomQRCaptureActivity.start(this, REQUEST_CODE_SCAN_QR);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCheckQrSubscribtion);
		OmnomObservable.unsubscribe(mValidateSubscribtion);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mOutAnimation = intent.getIntExtra(EXTRA_ANIMATION_EXIT, -1);
	}

	@Override
	public void finish() {
		super.finish();
		if(mOutAnimation == EXTRA_ANIMATION_SLIDE_OUT_RIGHT) {
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}
	}

	@Override
	protected void validate() {
		if(validateDemo()) {
			return;
		}
		if(TextUtils.isEmpty(mQrData)) {
			super.validate();
		} else if(mRestaurant == null || mTable == null) {
			clearErrors(true);
			findTable(mQrData);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_SCAN_QR) {
			if(resultCode == RESULT_OK) {
				mViewHelper.startProgressAnimation(getResources().getInteger(R.integer.omnom_validate_duration));
				mQrData = data.getExtras().getString(CaptureActivity.EXTRA_SCANNED_URI);
				findTable(mQrData);
			} else if(resultCode == OmnomQRCaptureActivity.RESULT_RESTAURANT_FOUND) {
				final String requestId = data.getExtras().getString(EXTRA_REQUEST_ID);
				final Restaurant restaurant = data.getExtras().getParcelable(EXTRA_RESTAURANT);
				final Menu menu = data.getExtras().getParcelable(EXTRA_RESTAURANT_MENU);
				handleHashRestaurants(requestId, restaurant, menu);
			} else {
				finish();
			}
		}
	}

	private void findTable(final String tableData) {
		final TableDataResponse[] table = new TableDataResponse[1];

		mValidateSubscribtion = AndroidObservable.bindActivity(this, ValidationObservable.validateSmart(this, mIsDemo)
		                                                                                 .map(OmnomObservable.getValidationFunc(this,
		                                                                                                                        getErrorHelper(),
		                                                                                                                        mInternetErrorClickListener))
		                                                                                 .isEmpty())
		                                         .subscribe(new Action1<Boolean>() {
			                                         @Override
			                                         public void call(Boolean hasNoErrors) {
				                                         if(hasNoErrors) {
					                                         loadTable(tableData);
				                                         } else {
					                                         reportMixPanel(null, OnTableMixpanelEvent.METHOD_QR, table[0]);
					                                         startErrorTransition();
					                                         final View viewById = findViewById(R.id.panel_bottom);
					                                         if(viewById != null) {
						                                         viewById.animate().translationY(200).start();
					                                         }
				                                         }
			                                         }
		                                         }, new Action1<Throwable>() {
			                                         @Override
			                                         public void call(Throwable throwable) {
				                                         startErrorTransition();
				                                         getErrorHelper().showInternetError(mInternetErrorClickListener);
			                                         }
		                                         });

	}

	@Override
	protected void reportMixPanel(final String requestId, final String method, final TableDataResponse tableDataResponse) {
		if(tableDataResponse != null) {
			getMixPanelHelper().track(MixPanelHelper.Project.OMNOM,
			                          OnTableMixpanelEvent.create(requestId, getUserData(),
			                                                      tableDataResponse.getRestaurantId(),
			                                                      tableDataResponse.getId(), method));
		}
	}

	private void loadTable(final String data) {
		final Observable<RestaurantResponse> restaurantObservable;
		if(ACTION_LAUNCH_HASHCODE.equals(getIntent().getAction())) {
			restaurantObservable = api.decode(new HashDecodeRequest(data), mPreloadBackgroundFunction);
		} else {
			restaurantObservable = api.decode(new QrDecodeRequest(data), mPreloadBackgroundFunction);
		}

		mCheckQrSubscribtion = AndroidObservable
				.bindActivity(this, concatMenuObservable(restaurantObservable))
				.subscribe(new Action1<Pair<RestaurantResponse, MenuResponse>>() {
					@Override
					public void call(final Pair<RestaurantResponse, MenuResponse> restaurantResponseMenuResponsePair) {
						final RestaurantResponse decodeResponse = restaurantResponseMenuResponsePair.first;
						if(decodeResponse.hasAuthError()) {
							throw new AuthServiceException(EXTRA_ERROR_WRONG_USERNAME | EXTRA_ERROR_WRONG_PASSWORD,
							                               new AuthError(EXTRA_ERROR_AUTHTOKEN_EXPIRED,
							                                             decodeResponse.getError()));
						}
						bindMenuData();
						if(!TextUtils.isEmpty(decodeResponse.getError())) {
							getErrorHelper().showError(LoaderError.UNKNOWN_QR_CODE, mInternetErrorClickListener);
						} else {
							if(isExternalLaunch()) {
								handleExternalLaunchResult(decodeResponse);
							} else {
								handleDecodeResponse(OnTableMixpanelEvent.METHOD_QR, decodeResponse);
							}
						}
					}
				}, onError);
	}

	protected void handleExternalLaunchResult(final RestaurantResponse decodeResponse) {
		String method = OnTableMixpanelEvent.METHOD_QR;
		if(ACTION_LAUNCH_HASHCODE.equals(getIntent().getAction())) {
			method = OnTableMixpanelEvent.METHOD_HASH;
		}
		if(decodeResponse.hasOnlyRestaurant()) {
			final Restaurant restaurant = decodeResponse.getRestaurants().get(0);
			final TableDataResponse table = RestaurantHelper.getTable(restaurant);
			if(table != null) {
				reportMixPanel(decodeResponse.getRequestId(), method, table);
				onDataLoaded(restaurant, RestaurantHelper.getTable(restaurant), RestaurantHelper.hasOrders(restaurant),
				             decodeResponse.getRequestId());
				// TODO: For debug purposes
				//if(BuildConfig.DEBUG) {
				//onDataLoaded(restaurant, restaurant.tables().get(0), true, decodeResponse.getRequestId());
				//}
			} else if(decodeResponse.hasTables()) {
				RestaurantActivity.start(this, restaurant, true);
			}

			return;
		}
		handleDecodeResponse(method, decodeResponse);
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		updateOrderData(event);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validate;
	}
}